package com.example.algorithmvisualizer.presentation.components

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.algorithmvisualizer.presentation.sort.ItemList
import kotlin.math.max
import kotlin.math.min

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BarChart(
    itemList: ItemList,
    showIndices: Boolean,
    showValues: Boolean,
    modifier: Modifier = Modifier,
) {
    val rItems = remember(itemList) { itemList.items }

    val maxValue = remember(itemList.id) {
        itemList.items.maxOfOrNull { it.value } ?: 100
    }

    val minValue = remember(itemList.id) {
        itemList.items.minOfOrNull { it.value } ?: 0
    }

    val itemCount = itemList.items.size

    BoxWithConstraints(
        modifier
            .fillMaxWidth()
    ) {
        if (itemList.items.isNotEmpty()) {
            fun rCalcHeight(value: Int): Dp {
                val h = calcHeight(
                    value = value,
                    minValue = minValue,
                    maxValue = maxValue,
                    maxHeightDp = maxHeight - 40.dp,
                )
                return h
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

            val spacing =
                max(0f, (totalWidth - (itemWidthPx * itemCount)) / (itemCount - 1)).toDp * factor
            val itemWithSpacingWidth = itemWidthDp + spacing

            val itemsHeight = remember(itemList.id) {
                HashMap<String, Dp>(
                    (itemList.items.map { it.id to rCalcHeight(it.value) }).toMap()
                )
            }


            Column {
                // Gráfico dos Itens
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {

                    items(rItems, key = { item -> item.id }, contentType = { "item" }) { item ->
                        val itemHeight = remember(item.value) { itemsHeight[item.id]!! }

                        BarItem(
                            value = item.value,
                            modifier = Modifier
                                .width(itemWithSpacingWidth)
                                .height(itemHeight)
                                .animateItemPlacement(),
                            barModifier = Modifier
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


private fun calcHeight(
    value: Int,
    minValue: Int,
    maxValue: Int,
    maxHeightDp: Dp,
): Dp {
    val minH = maxHeightDp / (maxValue - minValue) + 40.dp
    return ((maxHeightDp - minH) * (1f * (value - minValue) / (maxValue - minValue))) + minH
}

inline val Int.toDp: Dp
    @Composable get() = with(LocalDensity.current) { this@toDp.toDp() }

inline val Float.toDp: Dp
    @Composable get() = with(LocalDensity.current) { this@toDp.toDp() }

inline val Dp.toPx: Float
    @Composable get() = with(LocalDensity.current) { this@toPx.toPx() }