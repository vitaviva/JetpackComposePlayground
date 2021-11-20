package com.github.compose_playground

import android.Manifest
import android.os.Bundle
import android.util.Size
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toComposeRect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.github.compose_playground.ui.theme.ComposePlaygroundTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.objects.DetectedObject


class CameraActivity : AppCompatActivity() {

    @ExperimentalPermissionsApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposePlaygroundTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    val permission = Manifest.permission.CAMERA
                    val permissionState = rememberPermissionState(permission)

                    LaunchedEffect(Unit) {
                        permissionState.launchPermissionRequest()
                    }

                    PermissionRequired(
                        permissionState = permissionState,
                        {}, {}, {
                            val detectedObjects = mutableStateListOf<DetectedObject>()

                            Box {

                                CameraPreview(detectedObjects)
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    drawIntoCanvas { canvas ->
                                        detectedObjects.forEach {
                                            canvas.scale(size.width / 480, size.height / 640)
                                            canvas.drawRect(
                                                it.boundingBox.toComposeRect(),
                                                Paint().apply {
                                                    color = Color.Red
                                                    style = PaintingStyle.Stroke
                                                    strokeWidth = 5f
                                                })
                                            canvas.nativeCanvas.drawText(
                                                "TrackingId_${it.trackingId}",
                                                it.boundingBox.left.toFloat(),
                                                it.boundingBox.top.toFloat(),
                                                android.graphics.Paint().apply {
                                                    color = Color.Green.toArgb()
                                                    textSize = 20f
                                                })
                                        }

                                    }

                                }
                            }

                        }
                    )

                }
            }
        }
    }
}


@Composable
private fun CameraPreview(detectedObjects: SnapshotStateList<DetectedObject>) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    val coroutineScope = rememberCoroutineScope()
    val objectAnalyzer = remember { ObjectAnalyzer(coroutineScope, detectedObjects) }

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val executor = ContextCompat.getMainExecutor(ctx)

            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(executor, objectAnalyzer)
                }

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build()

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalyzer
                )
            }, executor)
            previewView
        },
        modifier = Modifier.fillMaxSize(),
    )

}

