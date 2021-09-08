package ru.skillbranch.sbdelivery.screens.root.logic

interface IEffectHandler<E, M> {
    suspend fun handle(effect: E, commit: (M) -> Unit)
}