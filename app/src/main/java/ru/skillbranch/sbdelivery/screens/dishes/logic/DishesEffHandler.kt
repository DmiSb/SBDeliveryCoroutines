package ru.skillbranch.sbdelivery.screens.dishes.logic

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import ru.skillbranch.sbdelivery.repository.DishesRepository
import ru.skillbranch.sbdelivery.screens.root.logic.Eff
import ru.skillbranch.sbdelivery.screens.root.logic.IEffectHandler
import ru.skillbranch.sbdelivery.screens.root.logic.Msg
import javax.inject.Inject

class DishesEffHandler @Inject constructor(
    private val repository: DishesRepository,
    private val notifyChannel: Channel<Eff.Notification>,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : IEffectHandler<DishesFeature.Eff, Msg> {
    override suspend fun handle(effect: DishesFeature.Eff, commit: (Msg) -> Unit) {
        when (effect) {
            is DishesFeature.Eff.AddToCart -> {
                repository.addDishToCart(effect.id)
                val count = repository.cartCount()
                commit(Msg.UpdateCartCount(count))
                notifyChannel.send(
                    Eff.Notification.Action(
                        message = "${effect.title} добавлен в корзину",
                        label = "Отмена",
                        action = Msg.Dishes(
                            DishesFeature.Msg.RemoveFromCart(
                                effect.id,
                                effect.title
                            )
                        )
                    )
                )
            }

            is DishesFeature.Eff.RemoveFromCart -> {
                repository.removeDishFromCart(effect.id)
                val count = repository.cartCount()
                commit(Msg.UpdateCartCount(count))
                notifyChannel.send(Eff.Notification.Text("${effect.title} удален из корзины"))
            }

            is DishesFeature.Eff.FindAllDishes -> {
                commit(DishesFeature.Msg.ShowLoading.toMsg())
                val dishes = repository.findDishes()
                commit(DishesFeature.Msg.ShowDishes(dishes).toMsg())
            }

            is DishesFeature.Eff.FindSuggestions -> {
                val suggestions = repository.findSuggestions(effect.query)
                commit(DishesFeature.Msg.ShowSuggestions(suggestions).toMsg())
            }


            is DishesFeature.Eff.SearchDishes -> {
                delay(3000)
                commit(DishesFeature.Msg.ShowLoading.toMsg())
                val dishes = repository.searchDishes(effect.query)
                commit(DishesFeature.Msg.ShowDishes(dishes).toMsg())
            }

            is DishesFeature.Eff.SyncDishes -> {
                commit(DishesFeature.Msg.ShowLoading.toMsg())
                val isEmpty = repository.isEmptyDishes()
                if (isEmpty) repository.syncDishes()
                val dishes = repository.findDishes()
                commit(DishesFeature.Msg.ShowDishes(dishes).toMsg())
            }
        }
    }

    private fun DishesFeature.Msg.toMsg() = Msg.Dishes(this)
}