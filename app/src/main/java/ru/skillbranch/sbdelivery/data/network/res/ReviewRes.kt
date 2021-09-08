package ru.skillbranch.sbdelivery.data.network.res

import java.io.Serializable

data class ReviewRes(
    val name: String,
    val date:Long,
    val rating:Int,
    val message:String
): Serializable