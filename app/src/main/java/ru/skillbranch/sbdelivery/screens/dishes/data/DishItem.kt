package ru.skillbranch.sbdelivery.screens.dishes.data

import java.io.Serializable

data class DishItem(
    val id: String,
    val image: String,
    val price: String,
    val title: String,
): Serializable