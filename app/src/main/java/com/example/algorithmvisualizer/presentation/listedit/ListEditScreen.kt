package com.example.algorithmvisualizer.presentation.listedit

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.twotone.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ListEditRoute(
    onBackClick: () -> Unit,
    viewModel: ListEditViewModel = hiltViewModel(),
) {

    val rOnBackClick = remember { onBackClick }

    val onSaveValuesClick: () -> Unit = remember {
        viewModel::onSaveClick
    }

    ListEditScreen(
        onBackClick = rOnBackClick,
        onSaveClick = onSaveValuesClick,
        viewModel = viewModel
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListEditScreen(
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    viewModel: ListEditViewModel,
) {

    val state by viewModel.listEditUiState.collectAsState()

    val onAddItem: (value: Int) -> Unit = viewModel::onAddItem
    val onRandomListItems: (size: Int) -> Unit = viewModel::onRandomListItems


    Surface {
        Column(
            Modifier
                .padding(16.dp)
                .fillMaxSize()
                .border(1.dp, Color.Red),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.TwoTone.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier.size(32.dp)
                    )
                }
                Text(
                    "Save",
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clickable { onSaveClick() },
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 24.sp
                )
            }

            when (val currentState = state) {
                is ListEditUiState.Success -> {
                    val list =currentState.list
                    list.let { listNotNull ->
                        ListEditControls(onAddItem, onRandomListItems, listNotNull.size)
                        Spacer(modifier = Modifier.height(24.dp))
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(56.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.padding(24.dp)
                        ) {
                            val chunks = listNotNull.chunked(5)
                            chunks.forEach { chunk ->
                                items(chunk, key = { it.key }) {
                                    val onDelete = remember { { viewModel.onDeleteItem(it.key) } }
                                    ListItem(
                                        value = it.value,
                                        modifier = Modifier
                                            .clickable { onDelete() }
                                            .animateItemPlacement()
                                    )
                                }
                            }
                        }
                    }
                }
                else ->{
                    Column(Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center) {
                        ProgressIndicatorDemo()
                    }
                }
            }
        }
    }
}


@Composable
fun ListItem(value: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(80.dp, 48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "$value", color = MaterialTheme.colorScheme.onSecondaryContainer)
    }
}

@Composable
fun ListEditControls(
    onAddItem: (Int) -> Unit,
    onRandomListItems: (Int) -> Unit,
    initialSize: Int?,
    modifier: Modifier = Modifier,
) {
    var text by remember {
        mutableStateOf("")
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current


    var randomSize by remember { mutableStateOf(initialSize?.toString() ?: "") }

    val rOnAddItem = remember {
        {
            if (text.isDigitsOnly() && text.isNotEmpty()) {
                onAddItem(text.toInt())
                text = ""
            }
        }
    }
    val rOnRandomItemsClick = remember {
        {
            if (randomSize.isDigitsOnly() && randomSize.isNotEmpty()) {
                onRandomListItems(randomSize.toInt())
            }
        }
    }


    var radioState by remember { mutableIntStateOf(1) }
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.selectable(
                selected = radioState == 0,
                onClick = { radioState = 0 },
                role = Role.RadioButton
            )
        ) {
            RadioButton(selected = radioState == 0, onClick = null)
            Text(text = "Add value")
        }

        Spacer(Modifier.width(20.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.selectable(
                selected = radioState == 1,
                onClick = { radioState = 1 },
                role = Role.RadioButton
            )
        ) {
            RadioButton(selected = radioState == 1, onClick = null)
            Text(text = "Random value")
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Gray),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Column(
            Modifier.padding(20.dp),

            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (radioState == 0) {
                Row(horizontalArrangement = Arrangement.Start) {
                    Column {
                        Text("Add new Value", fontSize = 18.sp)
                        Spacer(Modifier.height(4.dp))
                        TextField(
                            value = text,
                            onValueChange = { v -> text = v.filter { c -> c.isDigit() } },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Go
                            ),
                            keyboardActions = KeyboardActions(
                                onGo = { rOnAddItem() },
                            ),
                            trailingIcon = {
                                IconButton(
                                    onClick = rOnAddItem,
                                    colors = IconButtonDefaults.filledIconButtonColors(
                                        containerColor = MaterialTheme.colorScheme.inverseSurface,
                                        contentColor = MaterialTheme.colorScheme.inverseOnSurface
                                    ),
                                    enabled = text.isNotEmpty() && text.isDigitsOnly()
                                ) {
                                    Icon(Icons.Default.Add, "")
                                }
                            }
                        )
                    }
                }
            } else {
                Row(horizontalArrangement = Arrangement.Start) {
                    Column {
                        Text("Size", fontSize = 18.sp)
                        Spacer(Modifier.height(4.dp))
                        TextField(
                            value = randomSize,
                            onValueChange = { v ->
                                val newSize = v.filter { c -> c.isDigit() }
                                randomSize = if (newSize.toIntOrNull()?.compareTo(0) == -1) {
                                    "0"
                                } else {
                                    newSize
                                }
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Go
                            ),
                            keyboardActions = KeyboardActions(
                                onGo = {
                                    rOnRandomItemsClick()
                                    keyboardController?.hide()

//                                    focusRequester.freeFocus()
                                    focusManager.clearFocus()
                                },
                            ),

                            modifier = Modifier
                                .width(120.dp)
//                                .focusRequester(focusRequester)
                            ,
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        rOnRandomItemsClick()

                                        focusManager.clearFocus()
                                    },
                                    colors = IconButtonDefaults.filledIconButtonColors(
                                        containerColor = MaterialTheme.colorScheme.inverseSurface,
                                        contentColor = MaterialTheme.colorScheme.inverseOnSurface
                                    ),
                                    enabled = randomSize.isNotEmpty() && randomSize.isDigitsOnly()
                                ) {
                                    Icon(Icons.Default.Add, "")
                                }
                            }
                        )
//                        TextField(
//                            value = randomSize,
//                            onValueChange = { v ->
//                                randomSize = v.filter { c -> c.isDigit() }
//                            },
//                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//                            modifier = Modifier.width(120.dp)
//                        )
                    }
                }
            }
        }
    }
}


@Composable
fun ProgressIndicatorDemo() {
    val t = rememberInfiniteTransition("")
    val p by t.animateFloat(initialValue = 0f, targetValue = 1f ,
        animationSpec =  infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress = p,
            strokeWidth = 4.dp,
        )
    }

//    CircularProgressIndicator(progress)
}

