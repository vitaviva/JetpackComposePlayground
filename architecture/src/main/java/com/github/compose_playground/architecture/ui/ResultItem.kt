package com.github.compose_playground.architecture.ui

import android.graphics.Color
import android.text.Html
import android.widget.TextView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.compose_playground.architecture.util.highlight


@Composable
fun ResultItem(title: String, desc: String = "", keyWorlds: String) {

    Column(Modifier.padding(top = 5.dp, bottom = 5.dp)) {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth(),
            factory = {
                TextView(it).apply {
                    textSize = 16f
                    setTextColor(Color.BLUE)
                    text = highlight(title, keyWorlds)
                }
            }) {

        }

        AndroidView(
            modifier = Modifier
                .fillMaxWidth(),
            factory = {
                TextView(it).apply {
                    textSize = 12f
                    text = Html.fromHtml(desc)
                }
            }) {

        }

//        Text(
//            text = desc,
//            fontSize = 12.sp,
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(top = 2.dp)
//
//        )
    }


}

@Preview
@Composable
fun PreviewResultItem() {
    Column {

        ResultItem("I Love Java", "Java is so cool", keyWorlds = "java")
        ResultItem("I Love JavaScript", keyWorlds = "java")
        ResultItem("I Love JavaServerPage", keyWorlds = "java")
    }
}