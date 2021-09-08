package ru.skillbranch.sbdelivery.screens.cart.data

import java.io.Serializable

sealed class ConfirmDialogState : Serializable {
    data class Show(val id: String, val title:String) : ConfirmDialogState()
    object Hide : ConfirmDialogState()
}