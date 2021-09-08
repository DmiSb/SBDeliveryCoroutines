package ru.skillbranch.sbdelivery.screens.cart.logic

import ru.skillbranch.sbdelivery.screens.cart.data.CartUiState
import ru.skillbranch.sbdelivery.screens.cart.data.ConfirmDialogState
import ru.skillbranch.sbdelivery.screens.root.logic.Eff
import ru.skillbranch.sbdelivery.screens.root.logic.NavigateCommand
import ru.skillbranch.sbdelivery.screens.root.logic.RootState
import ru.skillbranch.sbdelivery.screens.root.logic.ScreenState

fun CartFeature.State.selfReduce(msg: CartFeature.Msg): Pair<CartFeature.State, Set<Eff>> =
    when (msg) {
        is CartFeature.Msg.ClickOnDish -> {
            this to setOf(Eff.Navigate(NavigateCommand.ToDishItem(msg.dishId, msg.title)))
        }
        is CartFeature.Msg.DecrementCount -> this to setOf(CartFeature.Eff.DecrementItem(msg.dishId)).toEffs()
        is CartFeature.Msg.IncrementCount -> this to setOf(CartFeature.Eff.IncrementItem(msg.dishId)).toEffs()
        is CartFeature.Msg.HideConfirm -> copy(confirmDialog = ConfirmDialogState.Hide) to emptySet()
        is CartFeature.Msg.RemoveFromCart -> copy(confirmDialog = ConfirmDialogState.Hide) to setOf(
            CartFeature.Eff.RemoveItem(
                msg.dishId,
                (confirmDialog as ConfirmDialogState.Show).title
            )
        ).toEffs()
        is CartFeature.Msg.SendOrder -> this to setOf(CartFeature.Eff.SendOrder(msg.order)).toEffs()
        is CartFeature.Msg.ShowCart -> {
            if (msg.cart.isEmpty()) copy(list = CartUiState.Empty) to emptySet()
            else copy(list = CartUiState.Value(msg.cart)) to emptySet()
        }
        is CartFeature.Msg.ShowConfirm -> copy(confirmDialog = ConfirmDialogState.Show(msg.id, msg.title)) to emptySet()
    }

fun CartFeature.State.reduce(root: RootState, msg: CartFeature.Msg): Pair<RootState, Set<Eff>> {
    val (screenState, effs) = selfReduce(msg)
    return root.changeCurrentScreen<ScreenState.Cart> { copy(state = screenState) } to effs
}

private fun Set<CartFeature.Eff>.toEffs(): Set<Eff> = mapTo(HashSet(), Eff::Cart)