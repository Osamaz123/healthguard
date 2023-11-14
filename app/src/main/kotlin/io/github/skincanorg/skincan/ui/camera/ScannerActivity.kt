/*
 * Copyright (C) 2022 SkinCan Project
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package io.github.skincanorg.skincan.ui.camera

import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.MotionLayout.TransitionListener
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import dagger.hilt.android.AndroidEntryPoint
import io.github.skincanorg.skincan.Database
import io.github.skincanorg.skincan.R
import io.github.skincanorg.skincan.databinding.ActivityScannerBinding
import io.github.skincanorg.skincan.lib.Util
import io.github.skincanorg.skincan.ui.diseases.LungsDiseaseActivity
import io.github.skincanorg.skincan.ui.result.ResultActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.TensorProcessor
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.label.TensorLabel
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import javax.inject.Inject

@AndroidEntryPoint
class ScannerActivity : AppCompatActivity() {
    private var photoFile: File? = null
    private var shouldLoop = false
    private var looped = true
    private lateinit var image: Bitmap
    private lateinit var tflite: Interpreter
    private val scope = CoroutineScope(Dispatchers.Main)
    // Define the number of classes in your model
    private  val NUM_CLASSES = 7 // Replace with the actual number of classes in your model

    // Define the class labels
    private val classLabels = arrayOf(
        "actinic keratosis",
        "basal cell carcinoma",
        "nevus",
        "melanoma",
        "vascular lesion",
        "dermatofibroma",
        "benign keratosis"
    )

    @Inject
    lateinit var database: Database

    private val binding: ActivityScannerBinding by viewBinding(CreateMethod.INFLATE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.apply {
            photoFile = intent.extras?.get("IMG_FILE") as File?
            if (photoFile != null) {
                image = Util.processBitmap(
                    BitmapFactory.decodeFile(photoFile!!.path),
                    photoFile!!,
                )

                Glide.with(ivImagePreview)
                    .load(image)
                    .into(ivImagePreview)
            } else {
                finish()
            }

            root.setTransitionListener(
                object : TransitionListener {
                    override fun onTransitionCompleted(motionLayout: MotionLayout, currentId: Int) {
                        if (shouldLoop) {
                            if (!looped)
                                motionLayout.transitionToState(R.id.loop_start)
                            else
                                motionLayout.transitionToState(R.id.loop_end)
                            looped = !looped
                        }
                    }

                    override fun onTransitionStarted(motionLayout: MotionLayout?, startId: Int, endId: Int) {}

                    override fun onTransitionChange(
                        motionLayout: MotionLayout?,
                        startId: Int,
                        endId: Int,
                        progress: Float,
                    ) {
                    }

                    override fun onTransitionTrigger(
                        motionLayout: MotionLayout?,
                        triggerId: Int,
                        positive: Boolean,
                        progress: Float,
                    ) {
                    }

                },
            )
            // Initialize the TFLite interpreter
            tflite = Interpreter(loadModelFile())

            btnScan.setOnClickListener {
                shouldLoop = true
                root.transitionToState(R.id.loop_start, 1)


                // Introduce a delay before calling doPrediction
                scope.launch {
                    delay(2000) // Adjust the delay duration as needed (in milliseconds)
                    doPrediction(image)
                }
            }

            btnCancel.setOnClickListener {
                finish()
            }
        }


    }
    private fun loadModelFile(): MappedByteBuffer {
        val modelFilename = "model12345_quantized.tflite" // Replace with your actual model file name
        val assetFileDescriptor = assets.openFd(modelFilename)
        val inputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val declaredLength = assetFileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    // Perform prediction using TFLite model
    private fun doPrediction(bitmap: Bitmap) {
        // Preprocess the input image
        val inputTensor = preprocessImage(bitmap)

        // Run inference
        val outputTensor = TensorBuffer.createFixedSize(intArrayOf(1, NUM_CLASSES), DataType.FLOAT32)
        tflite.run(inputTensor.buffer, outputTensor.buffer.rewind())

        // Get the predicted class index
        val predictedClassIndex = outputTensor.intArray[0]

        // Get the predicted class name
        val predictedClassName = classLabels[predictedClassIndex]

        // Get the confidence score
        val confidenceScore = outputTensor.floatArray[0]

        val resultString = "Predicted class: $predictedClassName\nConfidence: ${confidenceScore * 100}%"

        // Suggestive message based on the prediction
        val suggestionMessage = when {
            confidenceScore >= 0.8 -> "High confidence! This looks like a $predictedClassName. We recommend consulting a healthcare professional for further evaluation."
            confidenceScore >= 0.5 -> "Moderate confidence. It could be a $predictedClassName. It's advisable to seek a medical opinion for confirmation."
            else -> "Low confidence. The result is inconclusive. We recommend consulting a healthcare professional for a more accurate assessment."
        }
        // Pass the result string and image path to LungsDiseaseActivity
        val intent = Intent(this, LungsDiseaseActivity::class.java)
        intent.putExtra("resultString", resultString)
        intent.putExtra("suggestionMessage", suggestionMessage)
        intent.putExtra("imagePath", saveImageToInternalStorage(bitmap)) // Save image to internal storage and pass the path
        startActivity(intent)
    }

    private fun preprocessImage(bitmap: Bitmap): TensorImage {
        // Create TFLite input tensor from the bitmap
        var inputTensor = TensorImage(DataType.FLOAT32)
        inputTensor.load(bitmap)

        // Preprocess the input image
        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR))
            .add(NormalizeOp(0f, 255f))
            .build()

        inputTensor = imageProcessor.process(inputTensor)

        return inputTensor
    }
    private fun saveImageToInternalStorage(bitmap: Bitmap): String {
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir("images", MODE_PRIVATE)
        file = File(file, "${System.currentTimeMillis()}.jpg")

        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return file.absolutePath
    }


    override fun onDestroy() {
        super.onDestroy()
        // Cancel the coroutine scope to avoid potential memory leaks
        scope.cancel()
    }



}
