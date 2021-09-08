package ru.skillbranch.sbdelivery.data

import ru.skillbranch.sbdelivery.data.db.entity.CartItemDbView
import ru.skillbranch.sbdelivery.data.db.entity.DishPersist
import ru.skillbranch.sbdelivery.data.network.res.DishRes
import ru.skillbranch.sbdelivery.screens.cart.data.CartItem
import ru.skillbranch.sbdelivery.screens.dish.data.DishContent
import ru.skillbranch.sbdelivery.screens.dishes.data.DishItem

fun DishRes.toDishPersist() : DishPersist = DishPersist(
    id,
    name,
    description ?: "",
    image ?: "",
    oldPrice,
    price,
    rating ?: 0f,
    likes ?: 0,
    category,
    commentsCount ?: 0,
    active,
    createdAt,
    updatedAt
)

fun DishPersist.toDishItem() : DishItem = DishItem(id, image, "$price", title=name)

fun DishPersist.toDishContent() : DishContent =
    DishContent(
        id = id,
        image = image,
        title = name,
        description = description,
        price = price,
        oldPrice = oldPrice
    )

fun CartItemDbView.toCartItem() : CartItem = CartItem(dishId, image, title, count, price)