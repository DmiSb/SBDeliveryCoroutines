package ru.skillbranch.sbdelivery.screens.dishes.logic

import ru.skillbranch.sbdelivery.screens.dishes.data.DishItem
import ru.skillbranch.sbdelivery.screens.dishes.data.DishesUiState
import java.io.Serializable

object DishesFeature {
    fun initialState(): State  = State()
    fun initialEffects(): Set<Eff>  = setOf(Eff.SyncDishes)

    val route: String = "dishes"

    data class State(
        val input:String = "",
        val isSearch:Boolean = false,
        val suggestions: Map<String, Int> = emptyMap(),
        val list: DishesUiState = DishesUiState.Loading
    ): Serializable

    sealed class Msg{
        data class SearchInput(val newInput:String) : Msg()
        data class ShowDishes(val dishes:List<DishItem>):Msg()
        data class SearchSubmit(val query:String):Msg()
        data class ClickDish(val id:String, val title:String) : Msg()
        data class AddToCart(val id:String, val title:String) : Msg()
        data class RemoveFromCart(val id:String, val title:String) : Msg()
        data class UpdateSuggestionResult( val query:String) : Msg()
        data class ShowSuggestions( val suggestions:Map<String, Int>) : Msg()
        data class SuggestionSelect( val suggestion : String) : Msg()

        object SearchToggle: Msg()
        object ShowError: Msg()
        object ShowLoading: Msg()
    }

    sealed class Eff{
        data class SearchDishes(val query:String):Eff()
        data class AddToCart(val id:String, val title:String) : Eff()
        data class RemoveFromCart(val id:String, val title:String) : Eff()
        data class FindSuggestions(val query:String) : Eff()

        object SyncDishes : Eff()
        object FindAllDishes : Eff()
    }
}