package io.com.example.healthguard.ui.diseases

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

object ModelUtil {
    private lateinit var skinDiseaseModel: Interpreter
    private lateinit var lungDiseaseModel: Interpreter

    private val NUM_CLASSES = 7 // Number of classes in the skin disease model
    private val classLabels = arrayOf(
        "actinic keratosis",
        "basal cell carcinoma",
        "nevus",
        "melanoma",
        "vascular lesion",
        "dermatofibroma",
        "benign keratosis"
    )

    private val LUNG_NUM_CLASSES = 3 // Number of classes in the lung disease model
    private val lungClassLabels = arrayOf(
        "bengin_lung_cancer",
        "malignant_lung_cancer",
        "normal_lung"
    )


    fun loadSkinDiseaseModel(context: Context): Interpreter {
        val modelFilename = "model12345_quantized.tflite"
        val modelBuffer = loadModelFile(context, modelFilename)
        skinDiseaseModel = Interpreter(modelBuffer)
        return skinDiseaseModel
    }

    fun loadLungDiseaseModel(context: Context): Interpreter {
        val modelFilename = "lung_cancer_model.tflite" // Change this to your actual model filename
        val modelBuffer = loadModelFile(context, modelFilename)
        lungDiseaseModel = Interpreter(modelBuffer)
        return lungDiseaseModel
    }
    private fun loadModelFile(context: Context, modelFilename: String): MappedByteBuffer {
        val assetFileDescriptor = context.assets.openFd(modelFilename)
        val inputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val declaredLength = assetFileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }



    fun doSkinDiseasePrediction(model: Interpreter, image: Bitmap): Pair<String, String> {
        // Preprocess the input image
        val inputTensor = preprocessImage(image)

        // Run inference
        val outputTensor = TensorBuffer.createFixedSize(intArrayOf(1, NUM_CLASSES), DataType.FLOAT32)
        model.run(inputTensor.buffer, outputTensor.buffer.rewind())

        // Find the index of the maximum probability
        val maxProbabilityIndex = outputTensor.floatArray.indexOfFirst { it == outputTensor.floatArray.maxOrNull() }

        // Check if the index is valid
        val predictedClassIndex = if (maxProbabilityIndex != -1) maxProbabilityIndex else 0

        // Get the predicted class name
        val predictedClassName = classLabels[predictedClassIndex]

        // Get the confidence score
        val confidenceScore = outputTensor.floatArray[predictedClassIndex] ?: 0.0f

        val resultString = "Predicted class: $predictedClassName\nConfidence: ${confidenceScore * 100}%"

        // Suggestive message based on the prediction
        val suggestionMessage = when {
            confidenceScore >= 0.8 -> "High confidence! This looks like a $predictedClassName. We recommend consulting a healthcare professional for further evaluation."
            confidenceScore >= 0.5 -> "Moderate confidence. It could be a $predictedClassName. It's advisable to seek a medical opinion for confirmation."
            else -> "Low confidence. The result is inconclusive. We recommend consulting a healthcare professional for a more accurate assessment."
        }

        return Pair(resultString, suggestionMessage)
    }

    fun doLungDiseasePrediction(model: Interpreter, image: Bitmap): Pair<String, String> {
        // Preprocess the input image
        val inputTensor = preprocessImage(image)

        // Run inference
        val outputTensor = TensorBuffer.createFixedSize(intArrayOf(1, LUNG_NUM_CLASSES), DataType.FLOAT32)
        model.run(inputTensor.buffer, outputTensor.buffer.rewind())

        // Find the index of the maximum probability
        val maxProbabilityIndex = outputTensor.floatArray.indexOfFirst { it == outputTensor.floatArray.maxOrNull() }

        // Check if the index is valid
        val predictedClassIndex = if (maxProbabilityIndex != -1) maxProbabilityIndex else 0

        // Get the predicted class name
        val predictedClassName = lungClassLabels[predictedClassIndex]

        // Get the confidence score
        val confidenceScore = outputTensor.floatArray[predictedClassIndex] ?: 0.0f

        val resultString = "Predicted class: $predictedClassName\nConfidence: ${confidenceScore * 100}%"

        // Suggestive message based on the prediction
        val suggestionMessage = when {
            confidenceScore >= 0.8 -> "High confidence! This looks like $predictedClassName. We recommend consulting a healthcare professional for further evaluation."
            confidenceScore >= 0.5 -> "Moderate confidence. It could be $predictedClassName. It's advisable to seek a medical opinion for confirmation."
            else -> "Low confidence. The result is inconclusive. We recommend consulting a healthcare professional for a more accurate assessment."
        }

        return Pair(resultString, suggestionMessage)
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
}
