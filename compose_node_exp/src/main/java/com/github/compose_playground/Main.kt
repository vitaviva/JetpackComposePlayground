package com.github.compose_playground

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.runtime.AbstractApplier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.Composition
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DefaultMonotonicFrameClock
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(InternalCoroutinesApi::class)
fun runApp(context: Context): FrameLayout {
    val composer = Recomposer(Dispatchers.Main)

    GlobalSnapshotManager.ensureStarted()
    val mainScope = MainScope()
    mainScope.launch(start = CoroutineStart.UNDISPATCHED) {
        withContext(coroutineContext + DefaultMonotonicFrameClock) {
            composer.runRecomposeAndApplyChanges()
        }
    }
    mainScope.launch {
        composer.state.collect {
            println("composer:$it")
        }
    }

    val rootDocument = FrameLayout(context)
    Composition(ViewApplier(rootDocument), composer).apply {
        setContent {
            CompositionLocalProvider(localContext provides context) {
                AndroidViewApp()
            }
        }
    }
    return rootDocument
}



@Composable
private fun AndroidViewApp() {
    var count by remember { mutableStateOf(1) }
    LinearLayout {
        TextView(
            text = "Count of Android TextViews : $count",
        )
        repeat(count) {
            TextView(
                text = "Android TextView: $it ",
                onClick = {
                    count++
                }
            )
        }
    }
}

val localContext = compositionLocalOf<Context> { TODO() }

@Composable
fun TextView(
    text: String,
    onClick: () -> Unit = {}
) {
    val context = localContext.current
    ComposeNode<TextView, ViewApplier>(
        factory = {
            TextView(context)
        },
        update = {
            set(text) {
                this.text = text
            }
            set(onClick) {
                setOnClickListener { onClick() }
            }
        },
    )
}

@Composable
fun LinearLayout(children: @Composable () -> Unit) {
    val context = localContext.current
    ComposeNode<LinearLayout, ViewApplier>(
        factory = {
            LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
            }
        },
        update = {},
        content = children,
    )
}

class ViewApplier(val view: FrameLayout) : AbstractApplier<View>(view) {
    override fun onClear() {
        (view as? ViewGroup)?.removeAllViews()
    }

    override fun insertBottomUp(index: Int, instance: View) {
        (current as? ViewGroup)?.addView(instance, index)
    }

    override fun insertTopDown(index: Int, instance: View) {
    }

    override fun move(from: Int, to: Int, count: Int) {
        // NOT Supported
        TODO()
    }

    override fun remove(index: Int, count: Int) {
        (view as? ViewGroup)?.removeViews(index, count)
    }
}

