package com.github.compose_playground.architecture.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun SearchBarScreen(
    onConfirm: (answer: String) -> Unit
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = "Search in Wanandroid.com")
        var textState by remember { mutableStateOf("") }

        TextField(
            value = textState,
            onValueChange = { textState = it }
        )

        Button(onClick = {
            onConfirm(textState)
        }) {
            Text(text = "Confirm")
        }
    }
}
