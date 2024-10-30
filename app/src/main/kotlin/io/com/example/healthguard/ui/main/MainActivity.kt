
package io.com.example.healthguard.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import io.com.example.healthguard.Database
import io.com.example.healthguard.R
import io.com.example.healthguard.data.preference.PreferencesHelper
import io.com.example.healthguard.databinding.ActivityMainBinding
import io.com.example.healthguard.ui.OnboardingActivity
import io.com.example.healthguard.ui.auth.AuthViewModel
import io.com.example.healthguard.ui.camera.CameraActivity
import io.com.example.healthguard.ui.diseases.CvdDiseaseActivity
import io.com.example.healthguard.ui.preference.ProfileActivity
import io.com.example.healthguard.ui.vitalsigns.VitalSignsActivity
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: AuthViewModel by viewModels()
    private val binding: ActivityMainBinding by viewBinding(CreateMethod.INFLATE)


    @Inject
    lateinit var prefs: PreferencesHelper

    @Inject
    lateinit var database: Database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_SkinCan)

        viewModel.authState.observe(this) {
            if (!it) {
                startActivity(Intent(this@MainActivity, OnboardingActivity::class.java))
                finishAffinity()
            } else {
                setupLayout()
                setupCoreFunctions()
                setupBottomNavigation()
            }
        }
    }

    private fun setupLayout() {
        binding.apply {
            setContentView(root)
            userName.text = viewModel.getUser().displayName
        }
    }

    private fun setupCoreFunctions() {
        binding.apply {
            btnProfile.setOnClickListener {
                startActivity(Intent(this@MainActivity, ProfileActivity::class.java))
            }

            cvdCard.apply {
                root.setOnClickListener {
                    startActivity(Intent(this@MainActivity, CvdDiseaseActivity::class.java))
                }
            }
            lungsDiseaseCard.apply {
                root.setOnClickListener {
                   startActivity(Intent(this@MainActivity, CameraActivity::class.java).apply {
                        putExtra("MODEL_TYPE", "lung")
                    })
                }
            }

            skinDiseaseCard.apply {
                root.setOnClickListener {
                    startActivity(Intent(this@MainActivity, CameraActivity::class.java).apply {
                        putExtra("MODEL_TYPE", "skin")
                    })
                }
            }

        }
    }



    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    binding.contentWrapper.smoothScrollTo(0, 0)
                    true
                }



                R.id.vital_signs -> {
                    startActivity(Intent(this@MainActivity, VitalSignsActivity::class.java))
                    false
                }

                else -> false
            }
        }
    }
}
