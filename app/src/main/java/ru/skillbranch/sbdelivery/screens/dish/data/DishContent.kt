package ru.skillbranch.sbdelivery.screens.dish.data

import java.io.Serializable

data class DishContent(
    val id: String,
    val image: String,
    val title: String,
    val description: String,
    val price: Int,
    val oldPrice: Int?
): Serializable