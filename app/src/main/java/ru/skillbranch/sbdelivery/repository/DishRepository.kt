package ru.skillbranch.sbdelivery.repository

import ru.skillbranch.sbdelivery.data.db.dao.CartDao
import ru.skillbranch.sbdelivery.data.db.dao.DishesDao
import ru.skillbranch.sbdelivery.data.db.entity.CartItemPersist
import ru.skillbranch.sbdelivery.data.network.RestService
import ru.skillbranch.sbdelivery.data.network.req.ReviewReq
import ru.skillbranch.sbdelivery.data.network.res.ReviewRes
import ru.skillbranch.sbdelivery.data.toDishContent
import ru.skillbranch.sbdelivery.screens.dish.data.DishContent
import javax.inject.Inject

interface IDishRepository {
    suspend fun findDish(id: String): DishContent
    suspend fun addToCart(id: String, count: Int)
    suspend fun cartCount(): Int
    suspend fun loadReviews(dishId: String): List<ReviewRes>
    suspend fun sendReview(id: String, rating: Int, review: String): ReviewRes
}

class DishRepository @Inject constructor(
    private val api: RestService,
    private val dishesDao: DishesDao,
    private val cartDao: CartDao,
) : IDishRepository {
    override suspend fun findDish(id: String): DishContent = dishesDao.findDish(id).toDishContent()

    override suspend fun addToCart(id: String, count: Int) {
        val dishCount = cartDao.dishCount(id) ?: 0
        if (dishCount > 0) cartDao.updateItemCount(id, dishCount + count)
        else cartDao.addItem(CartItemPersist(dishId = id, count = count))
    }

    override suspend fun cartCount(): Int = cartDao.cartCount() ?: 0

    override suspend fun loadReviews(dishId: String): List<ReviewRes> {
        val reviews = mutableListOf<ReviewRes>()
        var offset = 0
        while (true) {
            val resp = api.getReviews(dishId, offset * 10, 10)
            if (resp.isSuccessful) {
                offset++
                reviews.addAll(resp.body()!!)
            } else break
        }
        return reviews
    }

    override suspend fun sendReview(id: String, rating: Int, review: String): ReviewRes =
        api.sendReview(id, ReviewReq(rating, review))
}