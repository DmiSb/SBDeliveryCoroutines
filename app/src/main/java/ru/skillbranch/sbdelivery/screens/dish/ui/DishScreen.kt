package ru.skillbranch.sbdelivery.screens.dish.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.screens.dish.data.DishUiState
import ru.skillbranch.sbdelivery.screens.dish.logic.DishFeature
import ru.skillbranch.sbdelivery.screens.root.ui.AppTheme

@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@Composable
fun DishScreen(state: DishFeature.State, accept: (DishFeature.Msg) -> Unit) {
    when (state.content) {
        is DishUiState.Value -> {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                DishContent(dish = state.content.data, count = state.count, accept = accept)
                DishReviews(reviews = state.reviews, rating = state.rating, accept = accept)
            }

        }

        is DishUiState.Loading -> Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator(color = MaterialTheme.colors.secondary)
        }

    }

    if (state.isReviewDialog) ReviewDialog(state.id, accept)
}

@Composable
fun ReviewDialog(dishId: String, accept: (DishFeature.Msg) -> Unit) {

    var rating by rememberSaveable { mutableStateOf(0) }
    var review by rememberSaveable { mutableStateOf("") }

    Dialog(
        onDismissRequest = { accept(DishFeature.Msg.HideReviewDialog) }) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = Color.White,
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Оставить отзыв",
                        color = MaterialTheme.colors.primary,
                        fontSize = 18.sp,
                        style = TextStyle(fontWeight = FontWeight.Bold),
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { accept(DishFeature.Msg.HideReviewDialog) },
                        modifier = Modifier.size(18.dp)
                    ) {
                        Icon(
                            tint = MaterialTheme.colors.onBackground,
                            painter = painterResource(R.drawable.ic_baseline_close_24),
                            contentDescription = null
                        )
                    }
                }
                RatingBar(
                    value = rating,
                    onChoose = { rating = it },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = review,
                    onValueChange = { review = it },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        textColor = MaterialTheme.colors.primary,
                        focusedBorderColor = MaterialTheme.colors.secondary,
                        unfocusedBorderColor = MaterialTheme.colors.onBackground
                    ),
                    placeholder = {
                        Text(
                            "Напишите отзыв",
                            color = MaterialTheme.colors.onBackground
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(
                    onClick = { accept(DishFeature.Msg.SendReview(dishId, rating, review)) },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.secondary,
                        contentColor = MaterialTheme.colors.onSecondary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        "Оставить отзыв",
                        style = TextStyle(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@Composable
fun RatingBar(value: Int = 0, maxValue: Int = 5, onChoose: (Int) -> Unit, modifier: Modifier) {
    Row(horizontalArrangement = Arrangement.Center, modifier = modifier) {
        for (item in 0..maxValue.dec()) {
            IconButton(onClick = { onChoose(item.inc()) }) {
                Icon(
                    tint = MaterialTheme.colors.secondary,
                    painter = painterResource(if (item.inc() <= value) R.drawable.ic_baseline_star_24 else R.drawable.ic_baseline_star_border_24),
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
@Preview
fun RatingPreview() {
    AppTheme {
        RatingBar(value = 1, onChoose = {}, modifier = Modifier.fillMaxWidth())
    }
}
