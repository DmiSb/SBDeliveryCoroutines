package ru.skillbranch.sbdelivery.screens.root.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.screens.root.logic.NavigateCommand

@Composable
fun DefaultToolbar(title: String, cartCount: Int, navigate: (NavigateCommand) -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = title,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        },
        navigationIcon = {
            IconButton(
                onClick = { navigate(NavigateCommand.ToBack) },
                content = {
                    Icon(
                        tint = MaterialTheme.colors.secondary,
                        painter = painterResource(R.drawable.ic_baseline_arrow_back_24),
                        contentDescription = "back"
                    )
                })
        },
        actions = {
            CartButton(cartCount = cartCount, onCartClick = { navigate(NavigateCommand.ToCart) })
        }
    )
}

@Composable
fun CartButton(cartCount: Int, onCartClick: () -> Unit) {
    IconButton(
        onClick = { onCartClick() },
        content = {
            Icon(
                tint = MaterialTheme.colors.secondary,
                painter = painterResource(R.drawable.ic_baseline_shopping_cart_24),
                contentDescription = "Cart"
            )
            if (cartCount > 0) {
                Text(
                    text = "$cartCount",
                    color = MaterialTheme.colors.primary,
                    textAlign = TextAlign.Center,
                    fontSize = 8.sp,
                    modifier = Modifier
                        .offset(10.dp, (-10).dp)
                        .size(12.dp)
                        .background(
                            Color.White, shape = RoundedCornerShape(6.dp)
                        )
                )
            }
        })
}

@Preview
@Composable
fun DefaultToolbarPreview(){
    AppTheme {
        DefaultToolbar(title = "test", 10, {})
    }
}