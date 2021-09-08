package ru.skillbranch.sbdelivery.screens.cart.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.screens.cart.data.CartItem
import ru.skillbranch.sbdelivery.screens.root.ui.AppTheme

@OptIn(ExperimentalCoilApi::class)
@Composable
fun CartListItem(
    dish: CartItem,
    onProductClick: (dishId: String, title: String) -> Unit,
    onIncrement: (dishId: String) -> Unit,
    onDecrement: (dishId: String) -> Unit,
    onRemove: (dishId: String, title:String) -> Unit,
) {
    Column(modifier = Modifier.padding(start = 16.dp, top = 16.dp)) {
        ConstraintLayout {

            val (title, poster, price, stepper) = createRefs()

            val painter = rememberImagePainter(
                data = dish.image,
                builder = {
                    crossfade(true)
                    placeholder(R.drawable.img_empty_place_holder)
                    error(R.drawable.img_empty_place_holder)
                }
            )
            Image(
                painter  = painter,
                contentDescription = dish.title,
                contentScale = if(painter.state is ImagePainter.State.Success) ContentScale.Crop else ContentScale.Inside,
                modifier = Modifier
                    .height(80.dp)
                    .aspectRatio(1f)
                    .clickable { onProductClick(dish.id, dish.title) }
                    .constrainAs(poster) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        bottom.linkTo(parent.bottom)
                    }
                    .clip(RoundedCornerShape(8.dp))
            )

            Text(
                fontSize = 18.sp,
                color = MaterialTheme.colors.onPrimary,
                text = dish.title,
                style = TextStyle(fontWeight = FontWeight.Bold),
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(title) {
                        top.linkTo(poster.top)
                        start.linkTo(poster.end, margin = 16.dp)
                        end.linkTo(parent.end, margin = 16.dp)
                        width = Dimension.preferredWrapContent
                    }
            )

            CartStepper(
                value = dish.count,
                onIncrement = { onIncrement(dish.id) },
                onDecrement = { onDecrement(dish.id) },
                onRemove = { onRemove(dish.id, dish.title) },
                modifier = Modifier
                    .constrainAs(stepper) {
                        start.linkTo(poster.end, margin = 16.dp)
                        bottom.linkTo(poster.bottom)
                    }
            )

            Text(
                fontSize = 18.sp,
                color = MaterialTheme.colors.secondary,
                text = "${dish.price} Р",
                textAlign = TextAlign.End,
                style = TextStyle(fontWeight = FontWeight.Bold),
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(price) {
                        top.linkTo(stepper.top)
                        bottom.linkTo(stepper.bottom)
                        start.linkTo(stepper.end, margin = 16.dp)
                        end.linkTo(parent.end, margin = 16.dp)
                        width = Dimension.preferredWrapContent
                    }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Divider(color = MaterialTheme.colors.onBackground.copy(alpha = 0.2f))
    }
}

@Composable
fun CartStepper(
    value: Int,
    modifier: Modifier = Modifier,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    onRemove: () -> Unit
) {
    Row(modifier
        .height(32.dp)
        .animateContentSize()
    ){
        Row(
            modifier = modifier
                .height(32.dp)
                .border(
                    0.dp,
                    MaterialTheme.colors.onBackground,
                    shape = RoundedCornerShape(4.dp)
                )
                .clip(RoundedCornerShape(4.dp)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if(value > 1){
                IconButton(
                    onClick = { onDecrement.invoke() },
                    content = {
                        Icon(
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colors.secondary,
                            painter = painterResource(R.drawable.ic_baseline_remove_24),
                            contentDescription = null
                        )
                    },
                    modifier = Modifier
                        .width(30.dp)
                        .fillMaxHeight()
                        .border(
                            0.dp,
                            MaterialTheme.colors.onBackground
                        )
                        .clipToBounds()
                )
            }

            Text(
                text = "$value",
                fontSize = 18.sp,
                style = TextStyle(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colors.secondary,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp)
            )

            IconButton(
                onClick = { onIncrement.invoke() },
                content = {
                    Icon(
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colors.secondary,
                        painter = painterResource(R.drawable.ic_baseline_add_24),
                        contentDescription = null
                    )
                },
                modifier = Modifier
                    .width(30.dp)
                    .fillMaxHeight()
                    .border(
                        0.dp,
                        MaterialTheme.colors.onBackground
                    )
                    .clipToBounds()
            )
        }

        if(value == 1 ){
            Spacer(modifier = Modifier.width(16.dp))
            IconButton(
                onClick = { onRemove.invoke() },
                content = {
                    Icon(
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colors.secondary,
                        painter = painterResource(R.drawable.ic_baseline_delete_24),
                        contentDescription = null
                    )
                },
                modifier = Modifier
                    .width(30.dp)
                    .border(
                        0.dp,
                        MaterialTheme.colors.onBackground,
                        shape = RoundedCornerShape(4.dp)
                    )

            )
        }
    }

}

@Preview
@Composable
fun CartStepperRemovePreview(){
    AppTheme {
        CartStepper(value = 1, onDecrement = {}, onIncrement = {}, onRemove = {})
    }
}

@Preview
@Composable
fun CartStepperIdlePreview(){
    AppTheme {
        CartStepper(value = 2, onDecrement = {}, onIncrement = {}, onRemove = {})
    }
}

@Preview
@Composable
fun CartItemPreview(){
    val dish = CartItem("0","", "test", 2, 100)
    AppTheme {
        CartListItem(
            dish = dish,
            onProductClick = { a, b -> },
            onIncrement = {},
            onDecrement = {},
            onRemove = { a, b -> })
    }
}
