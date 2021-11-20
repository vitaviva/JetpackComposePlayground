package com.github.compose_playground

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toComposeRect
import androidx.compose.ui.graphics.withSave
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.compose_playground.ui.theme.ComposePlaygroundTheme
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabel
import com.google.mlkit.vision.label.ImageLabeler
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposePlaygroundTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    MLKitSample()
                }
            }
        }
    }
}

@Composable
fun MLKitSample() {

    Column(Modifier.fillMaxSize()) {
        var imageLabel by remember { mutableStateOf("") }
        val detctedObject = remember { mutableStateListOf<DetectedObject>() }

        //Load Image
        val context = LocalContext.current
        val bmp = remember(context) {
            context.assetsToBitmap("cat.png")!!
        }


        Canvas(Modifier.aspectRatio(bmp.width.toFloat() / bmp.height.toFloat())) {
            drawIntoCanvas { canvas ->
                canvas.withSave {
                    canvas.scale(size.width / bmp.width)
                    canvas.drawImage(
                        image = bmp.asImageBitmap(), Offset(0f, 0f), Paint()
                    )
                    detctedObject.forEach {
                        canvas.drawRect(
                            it.boundingBox.toComposeRect(),
                            Paint().apply {
                                color = Color.Red.copy(alpha = 0.5f)
                                style = PaintingStyle.Stroke
                                strokeWidth = bmp.width * 0.01f
                            })
                        if (it.labels.isNotEmpty()) {
                            canvas.nativeCanvas.drawText(
                                it.labels.first().text,
                                it.boundingBox.left.toFloat(),
                                it.boundingBox.top.toFloat(),
                                android.graphics.Paint().apply {
                                    color = Color.Green.toArgb()
                                    textSize = bmp.width * 0.05f
                                })
                        }

                    }
                }
            }
        }

//        Image(bitmap = bmp.asImageBitmap(), contentDescription = "")

        val coroutineScope = rememberCoroutineScope()

        Column(Modifier.padding(20.dp)) {
            Button(
                {
                    val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
                    val image = InputImage.fromBitmap(bmp, 0)
                    labeler.process(image).addOnSuccessListener { labels: List<ImageLabel> ->
                        // Task completed successfully
                        imageLabel = labels.scan("") { acc, label ->
                            acc + "${label.text} : ${label.confidence}\n"
                        }.last()
                    }.addOnFailureListener {
                        // Task failed with an exception
                    }
                },
                Modifier.fillMaxWidth()
            ) {
                Text("Image Labeling")
            }

            Spacer(Modifier.height(10.dp))

            Button(
                {
                    val options =
                        ObjectDetectorOptions.Builder()
                            .setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE)
                            .enableMultipleObjects()
//                            .enableClassification()
                            .build()
                    ObjectDetection.getClient(options)
                        .process(InputImage.fromBitmap(bmp, 0))
                        .addOnSuccessListener { detectedObjects ->
                            // Task completed successfully
                            coroutineScope.launch {
                                detctedObject.clear()
                                detctedObject.addAll(getLabels(bmp, detectedObjects).toList())
                            }
                        }
                        .addOnFailureListener { e ->
                            // Task failed with an exception
                            // ...
                        }
                },
                Modifier.fillMaxWidth()
            ) {
                Text("Object Detecting")
            }

            Spacer(Modifier.height(10.dp))

            Button(
                { context.startActivity(Intent(context, CameraActivity::class.java)) },
                Modifier.fillMaxWidth()
            ) {
                Text("Object Tracking")
            }

        }

        Text(imageLabel, Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
    }
}


private fun getLabels(
    bitmap: Bitmap,
    objects: List<DetectedObject>
) = flow {

    val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
    for (obj in objects) {
        val bounds = obj.boundingBox
        val croppedBitmap = Bitmap.createBitmap(
            bitmap,
            bounds.left,
            bounds.top,
            bounds.width(),
            bounds.height()
        )

        emit(
            DetectedObject(
                obj.boundingBox,
                obj.trackingId,
                getLabel(labeler, croppedBitmap).map {
                    //转换为 DetectedObject.Label
                    DetectedObject.Label(it.text, it.confidence, it.index)
                })
        )
    }
}

private suspend fun getLabel(labeler: ImageLabeler, image: Bitmap): List<ImageLabel> =
    suspendCancellableCoroutine { cont ->
        labeler.process(InputImage.fromBitmap(image, 0))
            .addOnSuccessListener { labels ->
                // Task completed successfully
                cont.resume(labels)
            }
    }


private fun Context.assetsToBitmap(fileName: String): Bitmap? =
    assets.open(fileName).use {
        BitmapFactory.decodeStream(it)
    }


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposePlaygroundTheme {
        MLKitSample()
    }
}

