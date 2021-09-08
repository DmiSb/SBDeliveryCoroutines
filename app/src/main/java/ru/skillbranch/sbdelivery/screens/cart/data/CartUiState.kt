package ru.skillbranch.sbdelivery.screens.cart.data

import java.io.Serializable

sealed class CartUiState: Serializable {
    data class Value(val dishes: List<CartItem>) : CartUiState()
    object  Empty : CartUiState()
    object  Loading : CartUiState()
}