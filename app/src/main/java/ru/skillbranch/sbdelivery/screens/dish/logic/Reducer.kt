package ru.skillbranch.sbdelivery.screens.dish.logic

import ru.skillbranch.sbdelivery.screens.dish.data.DishUiState
import ru.skillbranch.sbdelivery.screens.dish.data.ReviewUiState
import ru.skillbranch.sbdelivery.screens.root.logic.Eff
import ru.skillbranch.sbdelivery.screens.root.logic.RootState
import ru.skillbranch.sbdelivery.screens.root.logic.ScreenState

fun DishFeature.State.selfReduce(msg: DishFeature.Msg) : Pair<DishFeature.State, Set<Eff>> =
    when(msg){
        is DishFeature.Msg.AddToCart ->
            copy(count = 1) to setOf(DishFeature.Eff.AddToCart(msg.id, msg.count)).toEffs()
        is DishFeature.Msg.DecrementCount -> {
            if (count <= 1) this to emptySet()
            else copy(count = count - 1) to emptySet()
        }
        is DishFeature.Msg.HideReviewDialog ->
            copy(isReviewDialog = false) to emptySet()
        is DishFeature.Msg.IncrementCount ->
            copy(count = count + 1) to emptySet()
        is DishFeature.Msg.SendReview -> {
            val reviewList = if (reviews is ReviewUiState.Empty) emptyList()
            else (reviews as ReviewUiState.Value).list
            copy(isReviewDialog = false, reviews = ReviewUiState.ValueWithLoading(reviewList)) to
                    setOf(DishFeature.Eff.SendReview(msg.dishId, msg.rating, msg.review, reviewList)).toEffs()
        }
        is DishFeature.Msg.ShowDish ->
            copy(content = DishUiState.Value(msg.dish)) to emptySet()
        is DishFeature.Msg.ShowReviewDialog ->
            copy(isReviewDialog = true) to emptySet()
        is DishFeature.Msg.ShowReviews ->
            if (msg.reviews.isNotEmpty()) copy(reviews = ReviewUiState.Value(msg.reviews)) to emptySet()
            else copy(reviews = ReviewUiState.Empty) to emptySet()
        is DishFeature.Msg.ReviewsEmpty -> copy(reviews = ReviewUiState.Empty) to emptySet()
        is DishFeature.Msg.ToggleLike -> copy(isLiked = !isLiked) to emptySet()
    }

fun  DishFeature.State.reduce(root: RootState, msg: DishFeature.Msg) : Pair<RootState, Set<Eff>> {
    val (screenState, effs) = selfReduce(msg)
    return root.changeCurrentScreen<ScreenState.Dish> { copy(state = screenState) } to effs
}

private fun Set<DishFeature.Eff>.toEffs(): Set<Eff> = mapTo(HashSet(), Eff::Dish)