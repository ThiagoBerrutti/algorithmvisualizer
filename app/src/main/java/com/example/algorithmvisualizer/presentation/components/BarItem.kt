package com.example.algorithmvisualizer.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.algorithmvisualizer.domain.model.ItemStatus

@Composable
fun BarItem(
    value: Int,
    modifier: Modifier = Modifier,
    barModifier: Modifier = Modifier,
    status: ItemStatus,

    ) {
    val barWidth = 40.dp
    val barHeight = remember { 5.dp * value }

    val color = when (status) {
        ItemStatus.Selected -> Color.Blue
        ItemStatus.Static -> Color.Red
        ItemStatus.Partition -> Color.LightGray
        else -> Color.Green
    }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text= "$value",
            overflow = TextOverflow.Clip,
            maxLines = 1,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Box(
            modifier = barModifier
                .height(barHeight)
                .background(color)
        ) {}

    }

}