package com.example.algorithmvisualizer.presentation.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.algorithmvisualizer.navigation.AlgoVisNavHost

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AlgoVisApp(appState: AlgoVisAppState) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { BottomNavigationBar(navController = appState.navController) }) {
//        Box(Modifier.padding(it))

            AlgoVisNavHost(appState = appState)

    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController, modifier: Modifier = Modifier) {
    NavigationBar {
        val buttonModifier = Modifier
//            .clip(RectangleShape)
//            .weight(1f)
//            .fillMaxSize()
//            .border(1.dp, Color.Red)
            .size(120.dp)
//            .border(1.dp,Color.Red)
        val iconContainerModifier = Modifier
//            .clip(RectangleShape)
            .fillMaxSize(0.8f)
//            .border(1.dp,Color.Green)

        val iconModifier=
        Modifier

//            .border(1.dp,Color.Yellow)
//        .size(24.dp)
//            .padding(2.dp)

        NavigationBarItem(
            label = {Text("First")},
            selected = false,
            onClick = { },
            icon = {
                    Icon(Icons.Outlined.FavoriteBorder,"", modifier=iconModifier)
//                Box(modifier = iconContainerModifier, contentAlignment = Alignment.Center) {
//                }
            },
            modifier = buttonModifier
        )
        NavigationBarItem(label = {Text("Second")},selected = false, onClick = { },
            icon = {
                    Icon(Icons.Outlined.Notifications, "", modifier=iconModifier)
//                Box(modifier = iconContainerModifier, contentAlignment = Alignment.Center) {
//                }
            }, modifier = buttonModifier
        )

        NavigationBarItem(label = {Text("Third")},selected = false, onClick = { },
            icon = {
                    Icon(Icons.Outlined.Call, "", modifier=iconModifier)
//                Box(modifier = iconContainerModifier, contentAlignment = Alignment.Center) {
//                }
            }, modifier = buttonModifier
        )
//        NavigationBarItem(selected = false, onClick = {}, icon = { Icon(Icons.Outlined.Call, "") },modifier=buttonModifier)
//        NavigationBarItem(selected = false, onClick = { }, icon = { Icon(Icons.Outlined.Menu, "") })//,modifier=buttonModifier)
    }

}

