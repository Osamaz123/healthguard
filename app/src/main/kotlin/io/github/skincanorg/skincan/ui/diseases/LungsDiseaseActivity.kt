package io.github.skincanorg.skincan.ui.diseases

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import io.github.skincanorg.skincan.R
import io.github.skincanorg.skincan.databinding.ActivityLungsDiseaseBinding
import io.github.skincanorg.skincan.ui.camera.CameraActivity
import io.github.skincanorg.skincan.ui.result.ResultActivity.Companion.FROM

class LungsDiseaseActivity : AppCompatActivity() {
    private val binding: ActivityLungsDiseaseBinding by viewBinding(CreateMethod.INFLATE)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val btnCamera = binding.btnCamera // Reference to the button using the binding

        btnCamera.setOnClickListener {
            // Open the CameraActivity
            val intent = Intent(this@LungsDiseaseActivity, CameraActivity::class.java)
            startActivity(intent)
        }

        // Add any other code related to your activity here.
    }
}
