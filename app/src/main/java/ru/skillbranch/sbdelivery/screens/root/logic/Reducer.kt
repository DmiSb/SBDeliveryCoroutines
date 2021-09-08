package ru.skillbranch.sbdelivery.screens.root.logic

import ru.skillbranch.sbdelivery.screens.cart.logic.CartFeature
import ru.skillbranch.sbdelivery.screens.dish.logic.DishFeature

fun RootState.reduceNavigate(msg: NavigateCommand): Pair<RootState, Set<Eff>> {

    val navEffs : Set<Eff> = when(current){
        is ScreenState.Dish -> setOf(Eff.Dish(DishFeature.Eff.Terminate))
        else -> emptySet()
    }

    return when (msg) {
        is NavigateCommand.ToBack -> {
            val newBackstack = backstack.dropLast(1)
            val newScreen = backstack.lastOrNull()
            if (newScreen == null) this to setOf(Eff.Cmd(Command.Finish))
            else {
                val newScreens = screens.toMutableMap()
                    .also { mutableScreens -> mutableScreens[newScreen.route] = newScreen }
                copy(
                    screens = newScreens,
                    backstack = newBackstack,
                    currentRoute = newScreen.route
                ) to emptySet()
            }
        }

        is NavigateCommand.ToCart -> {
            //return if on cart screen (single top)
            if(current.route === CartFeature.route) return this to emptySet()
            val newBackstack = backstack.plus(current)
            var newState = copy(currentRoute = CartFeature.route, backstack = newBackstack)
            newState = newState.changeCurrentScreen<ScreenState.Cart> {
                copy(state = CartFeature.initialState())
            }
            val newEffs = CartFeature.initialEffects().mapTo(HashSet(), Eff::Cart)
            newState to newEffs
        }

        is NavigateCommand.ToDishItem -> {
            val newBackstack = backstack.plus(current)
            var newState = copy(currentRoute = DishFeature.route, backstack = newBackstack)
            newState = newState.changeCurrentScreen<ScreenState.Dish> {
                copy(
                    state = DishFeature.State(
                        id = msg.id,
                        title = msg.title
                    )
                )
            }
            val newEffs = DishFeature.initialEffects(msg.id).mapTo(HashSet(), Eff::Dish)
            newState to newEffs
        }
    }.run { first to second.plus(navEffs) }
}