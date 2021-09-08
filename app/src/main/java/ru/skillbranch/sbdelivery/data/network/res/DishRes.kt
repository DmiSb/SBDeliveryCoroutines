package ru.skillbranch.sbdelivery.data.network.res

data class DishRes(
    val id: String,
    val name: String,
    val description: String?,
    val image: String?,
    val oldPrice: Int?,
    val price: Int,
    val rating: Float?,
    val likes: Int?,
    val category: String,
    val commentsCount: Int?,
    val active: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)