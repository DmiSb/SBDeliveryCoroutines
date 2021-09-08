package ru.skillbranch.sbdelivery.screens.cart.data

import java.io.Serializable

data class CartItem (
    val id:String,
    val image:String,
    val title:String,
    val count: Int,
    val price:Int
):Serializable