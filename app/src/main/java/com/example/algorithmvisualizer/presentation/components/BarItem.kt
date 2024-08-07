package com.example.algorithmvisualizer.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.example.algorithmvisualizer.domain.model.ItemStatus

@Composable
fun BarItem(
    value: Int,
    showValue: Boolean,
    modifier: Modifier = Modifier,
    barModifier: Modifier = Modifier,
    status: ItemStatus,
) {

    val color = when (status) {
        ItemStatus.Selected -> Color(0xff302681)
        ItemStatus.Static -> Color(0xffffcb00)
        ItemStatus.Partition -> Color(0xff009440)
        else -> Color.LightGray
    }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (showValue) {
            Text(
                text = "$value",
                overflow = TextOverflow.Clip,
                maxLines = 1,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Box(
            modifier = barModifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(color)
        )
    }

}