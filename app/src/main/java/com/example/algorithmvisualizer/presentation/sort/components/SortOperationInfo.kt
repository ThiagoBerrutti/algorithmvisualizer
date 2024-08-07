package com.example.algorithmvisualizer.presentation.sort.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.algorithmvisualizer.domain.model.ISortOperation
import com.example.algorithmvisualizer.domain.util.SortOperationInfoGenerator


@Composable
fun SortOperationInfo(
    operation: ISortOperation?,
    modifier: Modifier = Modifier,
) {
    val text = remember(operation) {
        operation?.let { SortOperationInfoGenerator.createMessage(operation) } ?: ""
    }

    var expanded by remember { mutableStateOf(true) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .animateContentSize(
                animationSpec = tween(300)
            )
            .clickable { expanded = !expanded }
            .padding(vertical = 12.dp, horizontal = 24.dp),
    ) {
        if (expanded) {
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center,
                modifier = Modifier.height(60.dp)
            )
        }
    }
}
