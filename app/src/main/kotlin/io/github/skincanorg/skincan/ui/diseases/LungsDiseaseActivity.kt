package io.github.skincanorg.skincan.ui.diseases

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import io.github.skincanorg.skincan.R
import io.github.skincanorg.skincan.databinding.ActivityLungsDiseaseBinding
import io.github.skincanorg.skincan.ui.camera.CameraActivity
import io.github.skincanorg.skincan.ui.result.ResultActivity.Companion.FROM

class LungsDiseaseActivity : AppCompatActivity() {
    private val binding: ActivityLungsDiseaseBinding by viewBinding(CreateMethod.INFLATE)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val iv_result_pict = binding.ivResultPict
        val btnCamera = binding.btnCamera // Reference to the button using the binding
        val tvResultStatus = binding.tvResultStatus
        val tv_result_desc = binding.tvResultDesc
        btnCamera.setOnClickListener {
            // Open the CameraActivity
            val intent = Intent(this@LungsDiseaseActivity, CameraActivity::class.java)
            startActivity(intent)
        }


        // Retrieve result string from the intent
        val resultString = intent.getStringExtra("resultString")
        // Set the result string to tvResultStatus TextView
        tvResultStatus.text = resultString

        val suggestionMessage = intent.getStringExtra("suggestionMessage")
        tv_result_desc.text = suggestionMessage
        val imagePath = intent.getStringExtra("imagePath")
        Glide.with(this)
            .load(imagePath)
            .into(iv_result_pict)



    }
}
