package com.github.compose_playground

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview
@Composable
fun AnimateAsStateDemo() {
    var blue by remember { mutableStateOf(true) }
    val color by animateColorAsState(
        if (blue) Blue else Red,
        animationSpec = spring(Spring.StiffnessVeryLow)
    )

    Column(Modifier.padding(16.dp)) {
        Text("AnimateAsStateDemo")
        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { blue = !blue }
        ) {
            Text("Change Color")
        }
        Spacer(Modifier.height(16.dp))
        Box(
            Modifier
                .preferredSize(128.dp)
                .background(color)
        )
    }
}

private sealed class BoxState(val color: Color, val size: Dp) {
    operator fun not() = if (this is Small) Large else Small

    object Small : BoxState(Blue, 64.dp)
    object Large : BoxState(Red, 128.dp)
}

@Preview
@Composable
fun UpdateTransitionDemo() {

    var boxState: BoxState by remember { mutableStateOf(BoxState.Small) }
    val transition = updateTransition(targetState = boxState)

    Column(Modifier.padding(16.dp)) {
        Text("UpdateTransitionDemo")
        Spacer(Modifier.height(16.dp))

        val color by transition.animateColor {
            boxState.color
        }
        val size by transition.animateDp(transitionSpec = {
            if (targetState == BoxState.Large) {
                spring(stiffness = Spring.StiffnessVeryLow)
            } else {
                spring(stiffness = Spring.StiffnessHigh)
            }
        }) {
            boxState.size
        }

        Button(
            onClick = { boxState = !boxState }
        ) {
            Text("Change Color and size")
        }
        Spacer(Modifier.height(16.dp))
        Box(
            Modifier
                .preferredSize(size)
                .background(color)
        )
    }
}


@OptIn(ExperimentalAnimationApi::class)
@Preview
@Composable
fun AnimateVisibilityDemo() {
    var visible by remember { mutableStateOf(true) }

    Column(Modifier.padding(16.dp)) {
        Text("AnimateVisibilityDemo")
        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { visible = !visible }
        ) {
            Text(text = if (visible) "Hide" else "Show")
        }

        Spacer(Modifier.height(16.dp))

        AnimatedVisibility(visible) {
            Box(
                Modifier
                    .preferredSize(128.dp)
                    .background(Blue)
            )
        }
    }
}

@Preview
@Composable
fun AnimateContentSizeDemo() {
    var expend by remember { mutableStateOf(false) }


    Column(Modifier.padding(16.dp)) {
        Text("AnimateContentSizeDemo")
        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { expend = !expend }
        ) {
            Text(if (expend) "Shink" else "Expand")
        }
        Spacer(Modifier.height(16.dp))

        Box(
            Modifier
                .background(Color.LightGray)
                .animateContentSize()
        ) {
            Text(
                text = "animateContentSize() animates its own size when its child modifier (or the child composable if it is already at the tail of the chain) changes size. " +
                        "This allows the parent modifier to observe a smooth size change, resulting in an overall continuous visual change.",
                fontSize = 16.sp,
                textAlign = TextAlign.Justify,
                modifier = Modifier.padding(16.dp),
                maxLines = if (expend) Int.MAX_VALUE else 2
            )
        }
    }
}

private enum class DemoScene {
    Text, Icon
}

@Preview
@Composable
fun CrossfadeDemo() {

    var scene by remember { mutableStateOf(DemoScene.Text) }

    Column(Modifier.padding(16.dp)) {

        Text("AnimateVisibilityDemo")
        Spacer(Modifier.height(16.dp))

        Button(onClick = {
            scene = when (scene) {
                DemoScene.Text -> DemoScene.Icon
                DemoScene.Icon -> DemoScene.Text
            }
        }) {
            Text("toggle")
        }

        Spacer(Modifier.height(16.dp))

        Crossfade(
            current = scene,
            animation = tween(durationMillis = 1000)
        ) {
            when (scene) {
                DemoScene.Text ->
                    Text(text = "Phone", fontSize = 32.sp)
                DemoScene.Icon ->
                    Icon(
                        imageVector = Icons.Default.Phone,
                        null,
                        modifier = Modifier.preferredSize(48.dp)
                    )
            }
        }

    }
}