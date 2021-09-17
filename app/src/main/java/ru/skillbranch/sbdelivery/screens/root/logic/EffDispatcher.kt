package ru.skillbranch.sbdelivery.screens.root.logic

import android.util.Log
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import ru.skillbranch.sbdelivery.repository.RootRepository
import ru.skillbranch.sbdelivery.screens.cart.logic.CartEffHandler
import ru.skillbranch.sbdelivery.screens.dish.logic.DishEffHandler
import ru.skillbranch.sbdelivery.screens.dishes.logic.DishesEffHandler
import javax.inject.Inject

class EffDispatcher @Inject constructor(
    private val dishesHandler: DishesEffHandler,
    private val dishHandler: DishEffHandler,
    private val cartHandler: CartEffHandler,

    private val rootRepository : RootRepository,

    //Channels for ui (ui effects) and android command
    private val _notifyChannel: Channel<Eff.Notification>,
    private val _commandChannel: Channel<Command>

) : IEffectHandler<Eff, Msg> {

    val notifications = _notifyChannel.receiveAsFlow()
    val androidCommands = _commandChannel.receiveAsFlow()

    override suspend fun handle(effect: Eff, commit: (Msg) -> Unit) {
        Log.e("EffDispatcher", "EFF $effect")


        when (effect) {
            is Eff.Dishes -> dishesHandler.handle(effect.eff, commit)
            is Eff.Dish -> dishHandler.handle(effect.eff, commit)
            is Eff.Cart -> cartHandler.handle(effect.eff, commit)

            //root effects handle
            is Eff.SyncCounter -> {
                val count = rootRepository.cartCount()
                commit(Msg.UpdateCartCount(count))
            }

            is Eff.Notification -> _notifyChannel.send(effect)
            is Eff.Navigate -> {
                //handle navigate
                //to downstream and handle in activity layer
                //or handle in state layer
                commit(Msg.Navigate(effect.cmd))
            }
            is Eff.Cmd -> _commandChannel.send(effect.cmd)

            is Eff.AddToCart -> {
                rootRepository.addDishToCart(effect.id)
                _notifyChannel.send(
                    Eff.Notification.Action(
                        "${effect.title} успешно добавлен в корзину",
                        label = "Отмена",
                        action = Msg.RemoveFromCart(effect.id, effect.title)
                    )
                )
            }
        }
    }
}
