/*
 * Copyright (C) 2022 SkinCan Project
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package io.com.example.healthguard.ui.preference

import android.content.Intent
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import io.com.example.healthguard.R
import io.com.example.healthguard.lib.PreferenceExtension.iconRes
import io.com.example.healthguard.lib.PreferenceExtension.newScreen
import io.com.example.healthguard.lib.PreferenceExtension.preference

class ProfileFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceScreen = preferenceManager.newScreen {
            preference {
                key = "account"
                title = "Account Settings"
                iconRes = R.drawable.ic_avatar
            }
            preference {
                key = "preferences"
                title = "Preferences"
                iconRes = R.drawable.ic_settings
                intent = Intent(requireActivity(), PreferenceActivity::class.java)
            }
            preference {
                key = "about"
                title = "About"
                iconRes = R.drawable.ic_info
                // TODO: Add about page
                // TODO: Move Licenses button to About page
               // intent = Intent(requireActivity(), LicensesActivity::class.java)
            }
        }
    }
}
