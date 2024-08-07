package com.example.algorithmvisualizer.presentation.sort.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SortSteps(
    step: Int,
    onSliderValueChange: (Float) -> Unit,
    onStepClick: () -> Unit,
    getOperationSize: () -> Int,
    modifier: Modifier = Modifier,
) {
    val opSize = getOperationSize()
    val stepText = remember(step) { "$step" }
    val sliderPosition by remember(opSize, step) {
        mutableFloatStateOf(
            if (opSize == 0) {
                0f
            } else {
                (step.toFloat() / opSize)
            }
        )
    }

    Column(modifier) {
        Slider(
            value = sliderPosition,
            onValueChange = onSliderValueChange,
            steps = opSize,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onStepClick() }
        ) {
            Text("Step: ", fontSize = 18.sp)
            Spacer(Modifier.width(8.dp))
            Text(text = stepText, fontWeight = FontWeight.Medium, fontSize = 24.sp)
        }
    }

}