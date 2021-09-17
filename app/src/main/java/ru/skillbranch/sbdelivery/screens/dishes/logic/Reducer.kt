package ru.skillbranch.sbdelivery.screens.dishes.logic

import ru.skillbranch.sbdelivery.screens.dishes.data.DishesUiState
import ru.skillbranch.sbdelivery.screens.root.logic.Eff
import ru.skillbranch.sbdelivery.screens.root.logic.NavigateCommand
import ru.skillbranch.sbdelivery.screens.root.logic.RootState
import ru.skillbranch.sbdelivery.screens.root.logic.ScreenState

fun DishesFeature.State.selfReduce(msg: DishesFeature.Msg): Pair<DishesFeature.State, Set<Eff>> =
    when (msg) {
        is DishesFeature.Msg.AddToCart -> this to setOf(
            DishesFeature.Eff.AddToCart(
                msg.id,
                msg.title
            )
        ).toEffs()
        is DishesFeature.Msg.RemoveFromCart -> this to setOf(
            DishesFeature.Eff.RemoveFromCart(
                msg.id,
                msg.title
            )
        ).toEffs()

        is DishesFeature.Msg.ClickDish -> copy(
            isSearch = false,
            suggestions = emptyMap(),
            input = ""
        ) to setOf(
            Eff.Navigate(NavigateCommand.ToDishItem(msg.id, msg.title))
        )

        is DishesFeature.Msg.SearchInput -> copy(input = msg.newInput) to emptySet()

        is DishesFeature.Msg.SearchSubmit -> copy(list = DishesUiState.Loading) to setOf(
            DishesFeature.Eff.SearchDishes(msg.query)
        ).toEffs()

        is DishesFeature.Msg.SearchToggle -> when {
            input.isNotEmpty() && isSearch -> copy(input = "", suggestions = emptyMap()) to setOf(
                DishesFeature.Eff.FindAllDishes
            ).toEffs()
            input.isEmpty() && !isSearch -> copy(isSearch = true) to emptySet()
            else -> copy(isSearch = false, suggestions = emptyMap()) to emptySet()
        }

        is DishesFeature.Msg.ShowDishes -> {
            val dishes =
                if (msg.dishes.isEmpty()) DishesUiState.Empty else DishesUiState.Value(msg.dishes)
            copy(list = dishes, suggestions = emptyMap()) to emptySet()
        }
        is DishesFeature.Msg.ShowError -> copy(list = DishesUiState.Error) to emptySet()
        is DishesFeature.Msg.ShowLoading -> copy(list = DishesUiState.Loading) to emptySet()
        is DishesFeature.Msg.ShowSuggestions -> copy(suggestions = msg.suggestions) to emptySet()

        is DishesFeature.Msg.SuggestionSelect -> {
            copy(suggestions = emptyMap(), input = msg.suggestion) to setOf(DishesFeature.Eff.SearchDishes(msg.suggestion)).toEffs()
        }

        is DishesFeature.Msg.UpdateSuggestionResult -> this to setOf(DishesFeature.Eff.FindSuggestions(msg.query)).toEffs()
    }


fun DishesFeature.State.reduce(root: RootState, msg: DishesFeature.Msg): Pair<RootState, Set<Eff>> {
    val (screenState, eff) = selfReduce(msg)
    return root.changeCurrentScreen<ScreenState.Dishes> { copy(state = screenState) } to eff
}

private fun Set<DishesFeature.Eff>.toEffs(): Set<Eff> = mapTo(HashSet(), Eff::Dishes)