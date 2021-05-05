package enyciaa.composelist.playground.architecture

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview

const val TAG = "Compose"


@Preview
@Composable
fun Foo() {

    var text by remember { mutableStateOf("") }

    Log.d(TAG, "Foo")
    Button(onClick = {
        text = "$text $text"
    }.also { Log.d(TAG, "Button") }) {
        Log.d(TAG, "Button content lambda")
        Wrapper {
            Text(text).also { Log.d(TAG, "Text") }
        }
    }

    SideEffect {

    }
}


@Composable
fun Wrapper(content: @Composable () -> Unit) {
    Log.d(TAG, "Wrapper recomposing")
    Box {
        Log.d(TAG, "Box")
        content()
    }
}