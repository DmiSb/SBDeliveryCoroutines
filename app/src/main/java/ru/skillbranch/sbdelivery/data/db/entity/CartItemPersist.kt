package ru.skillbranch.sbdelivery.data.db.entity


import androidx.room.DatabaseView
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItemPersist(
    @PrimaryKey(autoGenerate = true) val id: Long =0,
    val dishId: String,
    val count: Int = 1
)

@DatabaseView(
    """
        SELECT dishId, count,  dish.name AS title, price, image FROM cart_items AS cart
        JOIN dishes AS dish ON dish.id = dishId
    """
)
data class CartItemDbView(
    val dishId: String,
    val image:String,
    val title: String,
    val count:Int,
    val price:Int
)