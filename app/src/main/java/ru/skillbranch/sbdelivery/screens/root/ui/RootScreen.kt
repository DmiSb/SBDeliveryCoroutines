package ru.skillbranch.sbdelivery.screens.root.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarResult
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.collect
import ru.skillbranch.sbdelivery.RootViewModel
import ru.skillbranch.sbdelivery.screens.cart.ui.CartScreen
import ru.skillbranch.sbdelivery.screens.dish.ui.DishScreen
import ru.skillbranch.sbdelivery.screens.dishes.ui.DishesScreen
import ru.skillbranch.sbdelivery.screens.dishes.ui.DishesToolbar
import ru.skillbranch.sbdelivery.screens.root.logic.Eff
import ru.skillbranch.sbdelivery.screens.root.logic.Msg
import ru.skillbranch.sbdelivery.screens.root.logic.RootState
import ru.skillbranch.sbdelivery.screens.root.logic.ScreenState

@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@Composable
fun RootScreen(vm: RootViewModel) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(scope) {
        vm.dispatcher.notifications
            .collect { notification -> renderNotification(notification, scaffoldState, vm::accept) }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar={ AppbarHost(vm) },
        content = { ContentHost(vm) }
    )
}


@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
fun ContentHost(vm: RootViewModel) {
    val state: RootState by vm.feature.state.collectAsState()
    val screen: ScreenState = state.current

    Navigation(currentScreen = screen, modifier = Modifier.fillMaxSize()) { currentScreen ->
        when (currentScreen) {
            is ScreenState.Dishes -> DishesScreen(currentScreen.state, { vm.accept(Msg.Dishes(it)) })
            is ScreenState.Dish -> DishScreen(currentScreen.state, { vm.accept(Msg.Dish(it)) })
            is ScreenState.Cart -> CartScreen(currentScreen.state, { vm.accept(Msg.Cart(it)) })
        }
    }

}


@Composable
fun Navigation(
    currentScreen: ScreenState,
    modifier: Modifier = Modifier,
    content: @Composable (ScreenState) -> Unit
){
    val restorableStateHolder = rememberSaveableStateHolder()

    Box(modifier){
        restorableStateHolder.SaveableStateProvider(key = currentScreen.route + currentScreen.title) {
            content(currentScreen)
        }
    }

}

@ExperimentalComposeUiApi
@Composable
fun AppbarHost(vm: RootViewModel) {
    val state: RootState by vm.feature.state.collectAsState()
    when (val screen: ScreenState = state.current) {
        is ScreenState.Dishes -> DishesToolbar(
            state = screen.state,
            cartCount = state.cartCount,
            accept = { vm.accept(Msg.Dishes(it)) },
            navigate = vm::navigate)

        else -> DefaultToolbar(title = screen.title, cartCount = state.cartCount, navigate =  vm::navigate  )
    }
}

private suspend fun renderNotification(
    notification: Eff.Notification,
    scaffoldState: ScaffoldState,
    accept: (Msg) -> Unit
) {
    val result = when(notification){
        is Eff.Notification.Text -> scaffoldState.snackbarHostState.showSnackbar(notification.message)
        is Eff.Notification.Action -> {
            val (message, label) = notification
            scaffoldState.snackbarHostState.showSnackbar(message, label)
        }
        is Eff.Notification.Error -> {
            val (message, label) = notification
            scaffoldState.snackbarHostState.showSnackbar(message, label)
        }
    }

    when(result){
        SnackbarResult.Dismissed -> { /*Nothing*/ }
        SnackbarResult.ActionPerformed -> {
            when(notification){
                is Eff.Notification.Action -> accept(notification.action)
                is Eff.Notification.Error -> notification.action?.let { accept(it) }
                else  ->  { /*Nothing*/ }
            }
        }
    }
}
