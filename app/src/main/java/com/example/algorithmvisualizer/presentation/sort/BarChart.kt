package com.example.algorithmvisualizer.presentation.sort

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.algorithmvisualizer.domain.model.Item
import com.example.algorithmvisualizer.presentation.components.BarItem
import kotlin.math.max
import kotlin.math.min

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BarChart(
    items: List<Item>,
    showIndices: Boolean,
    showValues: Boolean,
    modifier: Modifier = Modifier,
) {

    val rItems = remember(items) { items }
    val maxValue = remember(items.size) {
        items.maxOfOrNull { it.value } ?: 100
    }

    val itemCount = items.size

    BoxWithConstraints(modifier.fillMaxWidth()) {
        if (items.isNotEmpty()) {
            val rCalcHeight = remember(maxValue, maxHeight) {
                { value: Int ->
                    calcHeight(
                        value,
                        maxValue,
                        maxHeight
                    )
                }
            }

            val factor = .3f

            val cMinWidthPx = 1f
            val cMaxWidth = 40.dp

            val cMinWidthDp = cMinWidthPx.toDp

            val totalWidth = constraints.maxWidth
            val availableWidth = (totalWidth.toDp - cMinWidthDp * itemCount).toPx
            val maxItemWidth = cMaxWidth.toPx
            val minItemWidth = cMinWidthDp.toPx

            val itemWidthPx = remember(itemCount, availableWidth) {
                max(
                    minItemWidth,
                    min(
                        maxItemWidth,
                        availableWidth / (itemCount + (itemCount - 1) * factor)
                    )
                )
            }

            val itemWidthDp = itemWidthPx.toDp

            val spacing = max(0f, (totalWidth - (itemWidthPx * itemCount)) / (itemCount - 1)).toDp
            val itemWithSpacingWidth = itemWidthDp+spacing

            LaunchedEffect(itemCount) {
                Log.d("WIDTH_TEST","maxwidth between: ${minItemWidth} and the min(${maxItemWidth}, ${availableWidth / (itemCount + (itemCount - 1) * factor)});")
                Log.d("WIDTH_TEST","itemWidth: $itemWidthDp; itemWithSpacingWidth: $itemWithSpacingWidth")
            }


            Column() {
                // Gráfico dos Itens
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    items(rItems, key = { item -> item.id }) { item ->
                        val itemHeight = rCalcHeight(item.value)
                        BarItem(
                            value = item.value,
                            modifier = Modifier
                                .width(itemWithSpacingWidth)
                                .animateItemPlacement(),
                            barModifier = Modifier
                                .height(itemHeight)
                                .width(itemWidthDp),
                            showValue = showValues,
                            status = item.status
                        )
                    }
                }


                // Índices abaixo das barras
                if (showIndices) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        for (n in 0..rItems.lastIndex) {
                            Text(
                                text = "$n",
                                modifier = Modifier.width(itemWidthDp),
                                textAlign = TextAlign.Center,
                                fontSize = 16.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}


private fun calcHeight(value: Int, maxValue: Int, maxHeightDp: Dp): Dp =
    maxHeightDp * value / maxValue * 1f

inline val Int.toDp: Dp
    @Composable get() = with(LocalDensity.current) { this@toDp.toDp() }

inline val Float.toDp: Dp
    @Composable get() = with(LocalDensity.current) { this@toDp.toDp() }

inline val Dp.toPx: Float
    @Composable get() = with(LocalDensity.current) { this@toPx.toPx() }