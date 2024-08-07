package com.example.algorithmvisualizer.presentation.sort.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.algorithmvisualizer.domain.model.SortAlgorithmName


@Composable
fun AlgorithmNameDialog(
    onAlgorithmChange: (SortAlgorithmName) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    onCompletion: () -> Unit = {},
) {
    Dialog(
        onDismissRequest = { onDismissRequest() },
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(4.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Black),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SortAlgorithmName.entries.forEach {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onAlgorithmChange(it)
                            onCompletion()
                        }
                ) {
                    Text(
                        text = it.name,
                        fontSize = 32.sp,
                        color = Color.White,
                        modifier = Modifier.padding(16.dp)
                    )
                }

            }
        }
    }
}
