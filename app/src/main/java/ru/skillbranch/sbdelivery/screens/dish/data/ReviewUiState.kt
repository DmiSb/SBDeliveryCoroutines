package ru.skillbranch.sbdelivery.screens.dish.data

import ru.skillbranch.sbdelivery.data.network.res.ReviewRes
import java.io.Serializable

sealed class ReviewUiState : Serializable {
    object Loading : ReviewUiState()
    data class Value(val list: List<ReviewRes>) : ReviewUiState()
    data class ValueWithLoading(val list: List<ReviewRes>) : ReviewUiState()
    object Empty : ReviewUiState()
}