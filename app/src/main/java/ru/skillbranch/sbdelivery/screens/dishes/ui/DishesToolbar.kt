package ru.skillbranch.sbdelivery.screens.dishes.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.screens.dishes.logic.DishesFeature
import ru.skillbranch.sbdelivery.screens.root.logic.NavigateCommand
import ru.skillbranch.sbdelivery.screens.root.ui.AppTheme
import ru.skillbranch.sbdelivery.screens.root.ui.CartButton

@ExperimentalComposeUiApi
@Composable
fun DishesToolbar(
    state: DishesFeature.State,
    cartCount: Int,
    accept: (DishesFeature.Msg) -> Unit,
    navigate: (NavigateCommand) -> Unit
) {

    val scope = rememberCoroutineScope()
    val inputFlow: MutableSharedFlow<String> = remember { MutableSharedFlow() }
    LaunchedEffect(key1 = state.isSearch ) {
        inputFlow
            .debounce(500)
            .collect {
                Log.e("DishesToolbar", "collect")
                accept(DishesFeature.Msg.UpdateSuggestionResult(it))
            }
    }

    SearchToolbar(
        input = state.input,
        cartCount = cartCount,
        isSearch = state.isSearch,
        title = "Все блюда",
        suggestions = state.suggestions,
        onInput = {
            accept(DishesFeature.Msg.SearchInput(it))
            scope.launch { inputFlow.emit(it) }
        },
        onSubmit = { accept(DishesFeature.Msg.SearchSubmit(it)) },
        onSuggestionClick = { accept(DishesFeature.Msg.SuggestionSelect(it)) },
        onSearchToggle = { accept(DishesFeature.Msg.SearchToggle) },
        onCartClick = { navigate(NavigateCommand.ToCart) }
    )
}

@ExperimentalComposeUiApi
@Composable
fun SearchToolbar(
    title: String,
    input: String,
    cartCount: Int = 0,
    isSearch: Boolean = false,
    suggestions: Map<String, Int> = emptyMap(),
    onInput: (query: String) -> Unit,
    onSubmit: (query: String) -> Unit,
    onSuggestionClick: (query: String) -> Unit,
    onSearchToggle: () -> Unit,
    onCartClick: () -> Unit
) {
    Column {
        TopAppBar(
            title = {
                if (!isSearch) Text(
                    text = title,
                    color = MaterialTheme.colors.onPrimary
                ) else CustomSearchField(
                    input = input,
                    placeholder = "Поиск",
                    onInput = onInput,
                    onSubmit = onSubmit
                )
            },
            actions = {
                IconButton(
                    onClick = { onSearchToggle() },
                    content = {
                        Icon(
                            tint = if (!isSearch) MaterialTheme.colors.secondary else MaterialTheme.colors.onPrimary,
                            painter = painterResource(if (!isSearch) R.drawable.ic_search_dishes else R.drawable.ic_baseline_close_24),
                            contentDescription = null
                        )
                    })
                CartButton(cartCount = cartCount, onCartClick = onCartClick)
            }
        )
        if (suggestions.isNotEmpty()) {
            BoxWithConstraints(
                contentAlignment = Alignment.CenterEnd,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .wrapContentSize(align = Alignment.TopStart, unbounded = true)
                        .width(maxWidth)
                ) {
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        suggestions.forEach {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSuggestionClick(it.key) }
                                    .padding(16.dp, vertical = 4.dp)) {
                                Text(
                                    text = it.key,
                                    color = MaterialTheme.colors.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "${it.value}",
                                    color = MaterialTheme.colors.onSurface,
                                    textAlign = TextAlign.Center,
                                    fontSize = 12.sp,
                                    modifier = Modifier
                                        .defaultMinSize(minWidth = 16.dp)
                                        .background(
                                            MaterialTheme.colors.secondary,
                                            RoundedCornerShape(50),
                                        )
                                        .padding(horizontal = 4.dp)
                                )
                            }

                        }
                    }
                }
            }
        }
    }
}

@ExperimentalComposeUiApi
@Composable
private fun CustomSearchField(
    input: String,
    placeholder: String = "Поиск",
    onInput: ((query: String) -> Unit)? = null,
    onSubmit: ((query: String) -> Unit)? = null
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current


    //TOD request focus
    /* DisposableEffect(Unit) {
         focusRequester.requestFocus()
         onDispose { }
     }*/

    val decoratedPlaceholder: @Composable ((Modifier) -> Unit)? =
        if (input.isEmpty()) {
            @Composable {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.alpha(0.6f)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_search_dishes),
                        contentDescription = "Search Icon",
                        tint = MaterialTheme.colors.onPrimary,
                        modifier = Modifier.padding(end = 8.dp)
                    )

                    Text(
                        text = placeholder,
                        color = MaterialTheme.colors.onPrimary,
                        fontSize = 16.sp
                    )
                }
            }
        } else null

    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.weight(1f), content = {
            decoratedPlaceholder?.invoke(Modifier.fillMaxWidth())

            BasicTextField(
                value = input,
                onValueChange = { onInput?.invoke(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                singleLine = true,
                textStyle = TextStyle.Default.copy(
                    fontSize = 16.sp,
                    color = MaterialTheme.colors.onPrimary
                ),
                cursorBrush = SolidColor(MaterialTheme.colors.secondary),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Search,
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        onSubmit?.invoke(input)
                        keyboardController?.hideSoftwareKeyboard()
                    },
                ),
            )
        })
    }
}

@ExperimentalComposeUiApi
@Preview
@Composable
fun IdleToolbarPreview() {
    AppTheme {
        DishesToolbar(DishesFeature.State(), 5, {}, {})
    }
}

@ExperimentalComposeUiApi
@Preview
@Composable
fun SearchToolbarPreview() {
    AppTheme {
        DishesToolbar(DishesFeature.State(input = "search test", isSearch = true), 0, {}, {})
    }
}

@ExperimentalComposeUiApi
@Preview
@Composable
fun SuggestionsToolbarPreview() {
    AppTheme {
        Box(Modifier.height(160.dp)) {
            DishesToolbar(
                DishesFeature.State(
                    input = "search test",
                    isSearch = true,
                    suggestions = mapOf("test" to 4, "search" to 2)
                ), 0, {}, {})
        }

    }

}