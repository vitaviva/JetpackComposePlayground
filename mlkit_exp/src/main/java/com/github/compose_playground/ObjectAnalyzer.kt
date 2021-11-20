package com.github.compose_playground

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class ObjectAnalyzer(
    private val coroutineScope: CoroutineScope,
    private val detectedObjects: SnapshotStateList<DetectedObject>
) : ImageAnalysis.Analyzer {
    private val options = ObjectDetectorOptions.Builder()
        .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
        .build()
    private val objectDetector = ObjectDetection.getClient(options)

    @SuppressLint("UnsafeExperimentalUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val frame = InputImage.fromMediaImage(
            imageProxy.image,
            imageProxy.imageInfo.rotationDegrees
        )

        coroutineScope.launch {
            objectDetector.process(frame)
                .addOnSuccessListener { detectedObjects ->
                    // Task completed successfully
                    with(this@ObjectAnalyzer.detectedObjects) {
                        clear()
                        addAll(detectedObjects)
                    }
                }
                .addOnFailureListener { e ->
                    // Task failed with an exception
                    // ...
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }

    }

}