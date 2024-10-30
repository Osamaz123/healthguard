package io.com.example.healthguard.ui.diseases

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import io.com.example.healthguard.databinding.ActivityLungsDiseaseBinding
import io.com.example.healthguard.ui.camera.CameraActivity
import io.com.example.healthguard.ui.main.MainActivity

class SkinDiseaseActivity : AppCompatActivity() {
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
            val intent = Intent(this@SkinDiseaseActivity, CameraActivity::class.java)
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

    override fun onBackPressed() {
        // Navigate to the main activity when the back button is pressed
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()


    }
}
