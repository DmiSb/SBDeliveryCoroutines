package ru.skillbranch.sbdelivery.screens.dish.data

import java.io.Serializable

sealed class DishUiState : Serializable {
    object Loading : DishUiState()
    data class Value(val data: DishContent) : DishUiState()
}
