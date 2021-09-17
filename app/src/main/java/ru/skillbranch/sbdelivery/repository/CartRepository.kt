package ru.skillbranch.sbdelivery.repository

import ru.skillbranch.sbdelivery.data.db.dao.CartDao
import ru.skillbranch.sbdelivery.data.network.RestService
import ru.skillbranch.sbdelivery.data.toCartItem
import ru.skillbranch.sbdelivery.screens.cart.data.CartItem
import javax.inject.Inject

interface ICartRepository {
    suspend fun loadItems(): List<CartItem>
    suspend fun incrementItem(dishId: String)
    suspend fun decrementItem(dishId: String)
    suspend fun removeItem(dishId: String)
    suspend fun clearCart()
}

class CartRepository @Inject constructor(
    private val api: RestService,
    private val cartDao: CartDao
) : ICartRepository {

    override suspend fun loadItems(): List<CartItem> = cartDao.findCartItems()
        .map{ it.toCartItem() }

    override suspend fun incrementItem(dishId: String)  = cartDao.incrementItemCount(dishId)

    override suspend fun decrementItem(dishId: String) = cartDao.decrementItemCount(dishId)

    override suspend fun removeItem(dishId: String) {
        cartDao.removeItem(dishId)
    }

    override suspend fun clearCart() {
        cartDao.clearCart()
    }
}