package ru.skillbranch.sbdelivery.screens.root.logic

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.skillbranch.sbdelivery.screens.cart.logic.CartFeature
import ru.skillbranch.sbdelivery.screens.cart.logic.reduce
import ru.skillbranch.sbdelivery.screens.dish.logic.DishFeature
import ru.skillbranch.sbdelivery.screens.dish.logic.reduce
import ru.skillbranch.sbdelivery.screens.dishes.logic.DishesFeature
import ru.skillbranch.sbdelivery.screens.dishes.logic.reduce
import java.io.Serializable

object RootFeature {
    private fun initialState(): RootState = RootState(
        screens = mapOf(
            DishesFeature.route to ScreenState.Dishes(DishesFeature.initialState()),
            DishFeature.route to ScreenState.Dish(DishFeature.initialState()),
            CartFeature.route to ScreenState.Cart(CartFeature.initialState()),
        ),
        currentRoute = DishesFeature.route
    )

    private fun initialEffects(): Set<Eff> =
        DishesFeature.initialEffects().mapTo(HashSet(), Eff::Dishes) + Eff.SyncCounter

    private val _state: MutableStateFlow<RootState> = MutableStateFlow(initialState())
    val state
        get() = _state.asStateFlow()
    private lateinit var _scope: CoroutineScope

    private val mutations: MutableSharedFlow<Msg> = MutableSharedFlow()

    fun mutate(mutation: Msg) {
        _scope.launch {
            mutations.emit(mutation)
        }
    }

    fun listen(scope: CoroutineScope, effDispatcher: IEffectHandler<Eff, Msg>, initState: RootState?) {
        Log.e("RootFeature", "Start listen init state: $initState")
        _scope = scope
        _scope.launch {
            mutations
                .onEach { Log.e("DemoEffHandler", "MUTATION $it") }
                .scan((initState ?: initialState()) to initialEffects()) { (s, _), m -> reduceDispatcher(s, m) }
                .collect { (s, eff) ->
                    _state.emit(s)
                    eff.forEach {
                        launch {
                            effDispatcher.handle(it, RootFeature::mutate)
                        }
                    }
                }
        }
    }

    private fun reduceDispatcher(root: RootState, msg: Msg): Pair<RootState, Set<Eff>> =
        when {
            msg is Msg.Dishes && root.current is ScreenState.Dishes -> root.current.state.reduce(
                root,
                msg.msg
            )

            msg is Msg.Dish && root.current is ScreenState.Dish -> root.current.state.reduce(
                root,
                msg.msg
            )

            msg is Msg.Cart && root.current is ScreenState.Cart -> root.current.state.reduce(
                root,
                msg.msg
            )

            //root mutations
            msg is Msg.UpdateCartCount -> root.copy(cartCount = msg.count) to emptySet()
            //navigation
            msg is Msg.Navigate -> root.reduceNavigate(msg.cmd)

            else -> root to emptySet()
        }
}

data class RootState(
    val screens: Map<String, ScreenState>,
    val currentRoute: String,
    val backstack: List<ScreenState> = emptyList(),
    val cartCount: Int = 0
) : Serializable {
    val current: ScreenState = checkNotNull(screens[currentRoute])

    fun <T : ScreenState> changeCurrentScreen(block: T.() -> T): RootState {
        val newScreen = (current as? T)?.block()
        val newScreens = if (newScreen != null) screens.toMutableMap().also { mutScreens ->
            mutScreens[currentRoute] = newScreen
        } else screens
        return copy(screens = newScreens)
    }
}

sealed class ScreenState(
    val route: String,
    val title: String
) : Serializable {
    data class Dishes(val state: DishesFeature.State) :
        ScreenState(DishesFeature.route, "Все блюда")

    data class Dish(val state: DishFeature.State) :
        ScreenState(DishFeature.route, state.title)

    data class Cart(val state: CartFeature.State) :
        ScreenState(CartFeature.route, "Корзина")
}

sealed class Msg {
    data class Dishes(val msg: DishesFeature.Msg) : Msg()
    data class Dish(val msg: DishFeature.Msg) : Msg()
    data class Cart(val msg: CartFeature.Msg) : Msg()

    //Navigation in root state level
    data class Navigate(val cmd: NavigateCommand) : Msg()

    //Root mutation
    data class UpdateCartCount(val count: Int) : Msg()

}

sealed class Eff {

    data class Dishes(val eff: DishesFeature.Eff) : Eff()
    data class Dish(val eff: DishFeature.Eff) : Eff()
    data class Cart(val eff: CartFeature.Eff) : Eff()

    sealed class Notification(open val message: String) : Eff() {
        data class Text(override val message: String) : Notification(message)
        data class Action(override val message: String, val label: String, val action: Msg) :
            Notification(message)

        data class Error(
            override val message: String,
            val label: String? = null,
            val action: Msg? = null
        ) : Notification(message)
    }

    //root effects
    object SyncCounter : Eff()

    data class Navigate(val cmd: NavigateCommand) : Eff()
    data class Cmd(val cmd: Command) : Eff()
}

sealed class NavigateCommand {
    data class ToDishItem(val id: String, val title: String) : NavigateCommand()
    object ToBack : NavigateCommand()
    object ToCart : NavigateCommand()
}

sealed class Command {
    object Finish : Command()
    //Android specific commands finish() startForResult, etc
}


