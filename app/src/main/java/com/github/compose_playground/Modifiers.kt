package com.github.compose_playground

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.compose_playground.ui.theme.typography


@Preview(showBackground = true)
@Composable
fun Plain() {
    Row(modifier = Modifier.fillMaxWidth()) {
        Image(
                modifier = Modifier.size(40.dp),
                bitmap = imageResource(id = R.drawable.miku),
                contentDescription = null, // decorative
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(text = name, maxLines = 1)
            Text(text = desc, maxLines = 1)
        }
        Text("Follow", Modifier.padding(6.dp))
    }

}

@Preview(showBackground = true)
@Composable
fun Decorated() {
    Row(modifier = Modifier
            .fillMaxWidth()
            .preferredHeightIn(min = 64.dp)
            .padding(8.dp)
            .border(1.dp, Color.LightGray, RoundedCornerShape(4.dp))
            .padding(8.dp)

    ) {

        Avatar(modifier = Modifier
                .padding(4.dp)
                .align(Alignment.CenterVertically)
        )

        Info(Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
        )

        FollowBtn(Modifier.align(Alignment.CenterVertically)
        )
    }
}

@Composable
fun Info(modifier: Modifier) {
    Column(modifier = modifier) {
        Text(
                text = name,
                color = Color.Black,
                maxLines = 1,
                style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        letterSpacing = 0.15.sp
                )
        )
        Text(
                text = desc,
                color = Color.Black.copy(alpha = 0.75f),
                maxLines = 1,
                style = TextStyle( // here
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        letterSpacing = 0.25.sp
                )
        )
    }
}

@Composable
fun Avatar(modifier: Modifier) {

    Image(
            modifier = modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .border(
                            shape = CircleShape,
                            border = BorderStroke(
                                    width = 2.dp,
                                    brush = Brush.linearGradient(
                                            colors = listOf(blue, teal200, green200, orange),
                                            start = Offset(0f, 0f),
                                            end = Offset(100f, 100f)
                                    )
                            )
                    )
                    .border(
                            shape = CircleShape,
                            border = BorderStroke(4.dp, SolidColor(Color.White))
                    ),
            bitmap = imageResource(id = R.drawable.miku),
            contentDescription = null, // decorative
    )

}

@Composable
fun FollowBtn(modifier: Modifier) {
    val backgroundShape: Shape = RoundedCornerShape(4.dp)

    Text(
            text = "Follow",
            style = typography.body1.copy(color = Color.White),
            textAlign = TextAlign.Center,
            modifier =
            modifier
                    .preferredWidth(80.dp)
                    .clickable(onClick = {})
                    .shadow(3.dp, shape = backgroundShape)
                    .clip(backgroundShape)
                    .background(
                            brush = Brush.verticalGradient(
                                    colors = listOf(
                                            Red500,
                                            orange700,
                                    ),
                                    startY = 0f,
                                    endY = 80f
                            )
                    )
                    .padding(6.dp)
    )
}

val purple = Color(0xFF833AB4)
val purple700 = Color(0xFF512DA8)
val purple200 = Color(0xFFBB86FC)
val purple500 = Color(0xFF6200EE)
val orange200 = Color(0xFFff7961)
val orange500 = Color(0xFFf44336)
val orange700 = Color(0xFFba000d)
val green200 = Color(0xffa5d6a7)
val green500 = Color(0xff4caf50)
val green700 = Color(0xff388e3c)
val teal200 = Color(0xff80deea)
val Red500 = Color(0xFFEE1D52)
val blue = Color(0xFF5851DB)
val orange = Color(0xFFF56040)
val yellow = Color(0xFFFCAF45)


val name = "Hatsune Miku"
val desc = "World is Mine 、メルト"