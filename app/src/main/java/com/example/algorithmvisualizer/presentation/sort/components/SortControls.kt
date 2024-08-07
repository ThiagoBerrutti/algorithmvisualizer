package com.example.algorithmvisualizer.presentation.sort.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.algorithmvisualizer.R


@Composable
fun SortControls(
    onPrevStep: () -> Unit,
    onPlay: () -> Unit,
    onNextStep: () -> Unit,
    onResetClick: () -> Unit,
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
) {
    val rOnPlay = remember { { onPlay() } }

    val iconButtonColors = IconButtonDefaults.filledIconButtonColors()
    val pauseColors: IconButtonColors = IconButtonDefaults.filledIconButtonColors(
        containerColor = MaterialTheme.colorScheme.error,
        contentColor = MaterialTheme.colorScheme.onError
    )

    Column(
        modifier = modifier.padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.wrapContentHeight()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Reset")
                Spacer(Modifier.height(4.dp))
                IconButton(
                    onClick = onResetClick,
                    colors = iconButtonColors,
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(painterResource(id = R.drawable.ic_rotate_cw), "prev")
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Prev")
                Spacer(Modifier.height(4.dp))
                IconButton(
                    onClick = onPrevStep,
                    colors = iconButtonColors,
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(painterResource(id = R.drawable.ic_chevron_left), "prev")
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val text: String
                val colors = if (isPlaying) pauseColors else iconButtonColors

                val icon = if (isPlaying) {
                    text = "Pause"
                    R.drawable.ic_pause
                } else {
                    text = "Play"
                    R.drawable.ic_play
                }

                Text(text)
                Spacer(Modifier.height(4.dp))
                IconButton(
                    onClick = rOnPlay,
                    colors = colors,
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(painterResource(id = icon), "play")
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Next")
                Spacer(Modifier.height(4.dp))
                IconButton(
                    onClick = onNextStep,
                    colors = iconButtonColors,
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(painterResource(id = R.drawable.ic_chevron_right), "next")
                }
            }
        }
    }
}
