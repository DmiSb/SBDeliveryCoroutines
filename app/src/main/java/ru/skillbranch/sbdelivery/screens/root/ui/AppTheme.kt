package ru.skillbranch.sbdelivery.screens.root.ui

import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import ru.skillbranch.sbdelivery.R

@Composable
fun AppTheme(content: @Composable ()->Unit){
    MaterialTheme(
        colors = Colors(
            primary= colorResource(R.color.colorPrimary),
            primaryVariant= colorResource(R.color.colorPrimaryVariant),
            secondary = colorResource(R.color.colorSecondary),
            secondaryVariant = colorResource(R.color.colorSecondaryVariant),
            background = colorResource(R.color.colorBackground),
            surface = colorResource(R.color.colorSurface),
            error =  colorResource(R.color.colorError),
            onPrimary =  colorResource(R.color.colorOnPrimary),
            onSecondary= colorResource(R.color.colorOnSecondary),
            onBackground= colorResource(R.color.colorOnBackground),
            onSurface = colorResource(R.color.colorOnSurface),
            onError = colorResource(R.color.colorOnError),
            isLight = true
        ),
        content = content
    )
}