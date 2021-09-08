package ru.skillbranch.sbdelivery.screens.dish.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.data.network.res.ReviewRes
import ru.skillbranch.sbdelivery.screens.dish.data.ReviewUiState
import ru.skillbranch.sbdelivery.screens.dish.logic.DishFeature
import ru.skillbranch.sbdelivery.screens.root.ui.AppTheme
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DishReviews(reviews: ReviewUiState, rating: Float, accept: (DishFeature.Msg) -> Unit) {
    when (reviews) {
        is ReviewUiState.Loading -> Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            CircularProgressIndicator(color = MaterialTheme.colors.secondary)
        }
        is ReviewUiState.Value -> Reviews(
            reviews = reviews.list,
            rating = rating,
            onAddReview = { /*TODO*/ },
            modifier = Modifier
                .fillMaxWidth()
        )
        ReviewUiState.Empty -> Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.height(112.dp)
        ) {
            Text(text = "Отзывов о этом товаре пока нет.\n Но вы можете быть первым")
        }
    }
}


@Composable
fun Reviews(
    reviews: List<ReviewRes>,
    modifier: Modifier = Modifier,
    rating: Float = 0f,
    onAddReview: () -> Unit
) {
    val lightBgColor = Color(android.graphics.Color.parseColor("#33313B"))
    Column(modifier = modifier.background(MaterialTheme.colors.surface)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Отзывы",
                color = MaterialTheme.colors.onPrimary,
                fontSize = 18.sp,
                style = TextStyle(fontWeight = FontWeight.Bold)
            )

            if (rating > 0) {
                Spacer(modifier = Modifier.width(16.dp))
                Icon(
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colors.secondary,
                    painter = painterResource(R.drawable.ic_baseline_star_24),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "$rating/5",
                    color = MaterialTheme.colors.onPrimary,
                    fontSize = 18.sp,
                    style = TextStyle(fontWeight = FontWeight.Bold)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            TextButton(
                onClick = { onAddReview() },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = lightBgColor,
                    contentColor = androidx.compose.ui.graphics.Color.White
                ),
            ) {
                Text("Добавить отзыв")
            }
        }
        Column(modifier = Modifier.padding(8.dp)) {
            for (item in reviews) {
                ReviewItem(review = item)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun ReviewItem(review: ReviewRes, modifier: Modifier = Modifier) {
    val lightBgColor = Color(android.graphics.Color.parseColor("#33313B"))
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = lightBgColor,
        elevation = 2.dp
    ) {
        Column(modifier = modifier.padding(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val date = SimpleDateFormat("dd.MM.yy").run { format(Date(review.date)) }
                Text(
                    text = "${review.name}, $date",
                    modifier = Modifier.weight(1f),
                    fontSize = 14.sp,
                    color = MaterialTheme.colors.onPrimary,
                    style = TextStyle(fontWeight = FontWeight.Bold),
                )

                Row {
                    for (i in 1..review.rating) {
                        Icon(
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colors.secondary,
                            painter = painterResource(R.drawable.ic_baseline_star_24),
                            contentDescription = null
                        )
                    }
                }
            }
            Text(
                fontSize = 12.sp,
                color = MaterialTheme.colors.onBackground,
                style = TextStyle(fontWeight = FontWeight.ExtraLight),
                text = review.message,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }

    }
}

@Preview
@Composable
fun ReviewsPreview() {
    AppTheme {
        Reviews(reviews = emptyList(), rating = 2f, onAddReview = {})
    }
}

@Preview
@Composable
fun ReviewItemPreview() {
    AppTheme {
        ReviewItem(
            review = ReviewRes(
                "Екатерина",
                Date().time,
                2,
                "Lorem ipsum Lorem ipsum Lorem ipsum Lorem ipsum Lorem ipsum"
            )
        )
    }
}
