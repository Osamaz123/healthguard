package io.com.example.healthguard.ui.camera

import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.MotionLayout.TransitionListener
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import io.com.example.healthguard.Database
import io.com.example.healthguard.R
import io.com.example.healthguard.databinding.ActivityScannerBinding
import io.com.example.healthguard.lib.Util
import io.com.example.healthguard.ui.diseases.LungsDiseaseActivity
import io.com.example.healthguard.ui.diseases.ModelUtil
import io.com.example.healthguard.ui.diseases.SkinDiseaseActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.tensorflow.lite.Interpreter
import java.io.*
import javax.inject.Inject

@AndroidEntryPoint
class ScannerActivity : AppCompatActivity() {
    private var photoFile: File? = null
    private var shouldLoop = false
    private var looped = true
    private lateinit var image: Bitmap
    private val scope = CoroutineScope(Dispatchers.Main)
    private lateinit var model: Interpreter
    private lateinit var modelType: String


    @Inject
    lateinit var database: Database

    private val binding: ActivityScannerBinding by viewBinding(CreateMethod.INFLATE)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Get the model type from the intent extras
        modelType = intent.getStringExtra("MODEL_TYPE") ?: ""
        model = when (modelType) {
            "skin" -> ModelUtil.loadSkinDiseaseModel(this@ScannerActivity)
             "lung" -> ModelUtil.loadLungDiseaseModel(this@ScannerActivity)
            else -> throw IllegalArgumentException("Invalid model type")
        }

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


    // Perform prediction using TFLite model
    private fun doPrediction(bitmap: Bitmap) {
        val (resultString, suggestionMessage) = when (modelType) {
            "skin" -> ModelUtil.doSkinDiseasePrediction(model, bitmap)
            "lung" -> ModelUtil.doLungDiseasePrediction(model, bitmap)
            else -> throw IllegalArgumentException("Invalid model type")
        }

        // Pass the result string and image path to the appropriate activity
        val intent = Intent(this, if (modelType == "skin") SkinDiseaseActivity::class.java else LungsDiseaseActivity::class.java)
        intent.putExtra("resultString", resultString)
        intent.putExtra("suggestionMessage", suggestionMessage)
        intent.putExtra("imagePath", saveImageToInternalStorage(bitmap)) // Save image to internal storage and pass the path
        startActivity(intent)
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
