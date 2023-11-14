/*
 * Copyright (C) 2022 SkinCan Project
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package io.github.skincanorg.skincan.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import io.github.skincanorg.skincan.Database
import io.github.skincanorg.skincan.R
import io.github.skincanorg.skincan.data.preference.PreferencesHelper
import io.github.skincanorg.skincan.databinding.ActivityMainBinding
import io.github.skincanorg.skincan.lib.Extension.readJson
import io.github.skincanorg.skincan.lib.Extension.toDateTime
import io.github.skincanorg.skincan.ui.OnboardingActivity
import io.github.skincanorg.skincan.ui.auth.AuthViewModel
import io.github.skincanorg.skincan.ui.camera.CameraActivity
import io.github.skincanorg.skincan.ui.diseases.CvdDiseaseActivity
import io.github.skincanorg.skincan.ui.diseases.LungsDiseaseActivity
import io.github.skincanorg.skincan.ui.preference.ProfileActivity
import io.github.skincanorg.skincan.ui.result.ResultActivity
import io.github.skincanorg.skincan.ui.result.ResultListActivity
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: AuthViewModel by viewModels()
    private val binding: ActivityMainBinding by viewBinding(CreateMethod.INFLATE)
//    private val newsAdapter: NewsAdapter by lazy {
//        NewsAdapter(applicationContext.assets.readJson("news.json").getJSONArray("news"))
//    }

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
              //  setupNews()
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
                    startActivity(Intent(this@MainActivity, LungsDiseaseActivity::class.java))
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

                R.id.chat_bot -> {
                    Toast.makeText(this, "Chatbot clicked", Toast.LENGTH_SHORT).show()
                    // startActivity(Intent(this@MainActivity, ResultActivity::class.java))
                    false // Do not highlight
                }

                else -> false
            }
        }
    }
}
