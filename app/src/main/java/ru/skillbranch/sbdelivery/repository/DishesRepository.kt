package ru.skillbranch.sbdelivery.repository

import ru.skillbranch.sbdelivery.data.db.dao.CartDao
import ru.skillbranch.sbdelivery.data.db.dao.DishesDao
import ru.skillbranch.sbdelivery.data.db.entity.CartItemPersist
import ru.skillbranch.sbdelivery.data.network.RestService
import ru.skillbranch.sbdelivery.data.network.res.DishRes
import ru.skillbranch.sbdelivery.data.toDishItem
import ru.skillbranch.sbdelivery.data.toDishPersist
import ru.skillbranch.sbdelivery.screens.dishes.data.DishItem
import java.util.*
import javax.inject.Inject

interface IDishesRepository {
    suspend fun searchDishes(query: String): List<DishItem>
    suspend fun isEmptyDishes(): Boolean
    suspend fun syncDishes()
    suspend fun findDishes(): List<DishItem>
    suspend fun findSuggestions(query: String): Map<String, Int>
    suspend fun addDishToCart(id: String)
    suspend fun removeDishFromCart(dishId: String)
    suspend fun cartCount(): Int
}

class DishesRepository @Inject constructor(
    private val api: RestService,
    private val dishesDao: DishesDao,
    private val cartDao: CartDao
) : IDishesRepository {
    override suspend fun searchDishes(query: String): List<DishItem> {
        return if (query.isEmpty()) findDishes()
        else dishesDao.findDishesFrom(query)
            .map { it.toDishItem() }
    }

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
        dishesDao.insertDishes(dishes.map { it.toDishPersist() })
    }

    override suspend fun findDishes(): List<DishItem> =
        dishesDao.findAllDishes().map { it.toDishItem() }

    override suspend fun findSuggestions(query: String): Map<String, Int> {
        val listDishes = searchDishes(query)

        return listDishes
            .map {
                it.title.replace("[.,!?\"-]".toRegex(), "")
                    .lowercase(Locale.getDefault())
                    .split(" ")
            }
            .flatten().filter { it.contains(query, true) }
            .groupingBy { it }.eachCount()

    /*val suggs = mutableMapOf<String, Int>()
        if (query.isBlank()) return suggs

        else dishesDao.findDishesFrom(query)
            .map { dish ->
                var j = 0
                var start: Int; var end : Int
                var shortName: String; var badLast = false
                while (j < dish.name.length) {
                    j = dish.name.indexOf(query.trim(), j, ignoreCase = true)
                    if (j == -1) break

                    start = j
                    while (start > 0) {
                        if (dish.name[start - 1] == ' ' || dish.name[start - 1] == ','
                            || dish.name[start - 1] == '"' || dish.name[start - 1] == '.'
                            || dish.name[start - 1] == ';') break
                        start--
                    }

                    end = j + query.trim().length
                    while (end < dish.name.length) {
                        if (dish.name[end] == ' ' || dish.name[end] == ',' || dish.name[end] == '"'
                            || dish.name[end] == '.' || dish.name[end] == ';')
                        {
                            if (end == dish.name.length - 1) {
                                end++
                                badLast = true
                            }
                            break
                        }
                        end++
                    }
                    j = end

                    shortName = if (end == dish.name.length) {
                        if (badLast) dish.name.substring(start, end - 1)
                        else
                            dish.name.substring(start)
                    } else {
                        dish.name.substring(start, end)
                    }
                    suggs.merge(shortName.lowercase(), 1) { i: Int, _: Int -> i + 1 }
                }
            }
        return suggs*/
    }

    override suspend fun addDishToCart(id: String) {
        val count = cartDao.dishCount(id) ?: 0
        if (count > 0) cartDao.updateItemCount(id, count.inc())
        else cartDao.addItem(CartItemPersist(dishId = id))
    }

    override suspend fun cartCount(): Int = cartDao.cartCount() ?: 0

    override suspend fun removeDishFromCart(dishId: String) {
        val count = cartDao.dishCount(dishId) ?: 0
        if (count > 1) cartDao.decrementItemCount(dishId)
        else cartDao.removeItem(dishId)
    }
}