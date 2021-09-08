package ru.skillbranch.sbdelivery.repository

import ru.skillbranch.sbdelivery.data.db.dao.CartDao
import ru.skillbranch.sbdelivery.data.db.dao.DishesDao
import ru.skillbranch.sbdelivery.data.db.entity.CartItemPersist
import ru.skillbranch.sbdelivery.data.network.RestService
import ru.skillbranch.sbdelivery.data.network.res.DishRes
import ru.skillbranch.sbdelivery.data.toDishPersist
import javax.inject.Inject

interface IRootRepository {
    suspend fun cartCount(): Int
    suspend fun isEmptyDishes(): Boolean
    suspend fun syncDishes()
    suspend fun addDishToCart(id: String)
    suspend fun removeDishFromCart(dishId: String)
}

class RootRepository @Inject constructor(
    private val api: RestService,
    private val cartDao: CartDao,
    private val dishesDao: DishesDao
) : IRootRepository{
    override suspend fun cartCount(): Int = cartDao.cartCount() ?: 0

    override suspend fun isEmptyDishes(): Boolean = dishesDao.dishesCounts() == 0

    override suspend fun syncDishes() {
        val dishes = mutableListOf<DishRes>()
        var offset = 0
        while (true) {
            val res = api.getDishes(offset * 10, 10)
            if (res.isSuccessful) {
                offset++
                dishes.addAll(res.body()!!)
            } else break
        }
        dishes.map { it.toDishPersist() }
            .also { dishesDao.insertDishes(it) }
    }

    override suspend fun addDishToCart(id: String) {
        val count = cartDao.dishCount(id) ?: 0
        if (count > 0) cartDao.updateItemCount(id, count.inc())
        else cartDao.addItem(CartItemPersist(dishId = id))
    }

    override suspend fun removeDishFromCart(dishId: String) {
        val count = cartDao.dishCount(dishId) ?: 0
        if (count > 1) cartDao.decrementItemCount(dishId)
        else cartDao.removeItem(dishId)
    }

}