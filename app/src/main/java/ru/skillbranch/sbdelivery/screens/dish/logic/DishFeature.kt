package ru.skillbranch.sbdelivery.screens.dish.logic

import ru.skillbranch.sbdelivery.data.network.res.ReviewRes
import ru.skillbranch.sbdelivery.screens.dish.data.DishContent
import ru.skillbranch.sbdelivery.screens.dish.data.DishUiState
import ru.skillbranch.sbdelivery.screens.dish.data.ReviewUiState
import java.io.Serializable

object DishFeature {
    const val route = "dish"

    fun initialState() : State = State("", "")
    fun initialEffects(id:String) : Set<Eff> =  setOf(Eff.LoadDish(id), Eff.LoadReviews(id))

    data class State(
        val id: String,
        val title: String,
        val isReviewDialog: Boolean = false,
        val reviews: ReviewUiState = ReviewUiState.Loading,
        val content: DishUiState = DishUiState.Loading,
        val count: Int = 1,
        val rating: Float = 0f,
        val isLiked: Boolean = false
    ): Serializable

    sealed class Eff {
        data class LoadDish(val dishId: String) : Eff()
        data class LoadReviews(val dishId: String) : Eff()
        data class AddToCart(val id: String, val count: Int) : Eff()
        data class SendReview(
            val id: String,
            val rating: Int,
            val review: String,
            val currReviews: List<ReviewRes> = mutableListOf()
        ) : Eff()
        object Terminate: Eff()
    }

    sealed class Msg{
        object ToggleLike : Msg()
        object IncrementCount : Msg()
        object DecrementCount : Msg()
        object ShowReviewDialog : Msg()
        object HideReviewDialog : Msg()
        object ReviewsEmpty : Msg()
        data class SendReview(val dishId:String, val rating: Int, val review:String) : Msg()
        data class ShowDish(val dish: DishContent) : Msg()
        data class AddToCart(val id: String, val count: Int) : Msg()
        data class ShowReviews(val reviews: List<ReviewRes>) : Msg()
    }
}