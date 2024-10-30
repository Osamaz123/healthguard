/*
 * Copyright (C) 2022 SkinCan Project
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package io.com.example.healthguard.ui.preference

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import io.com.example.healthguard.R
import io.com.example.healthguard.data.preference.PreferenceKeys
import io.com.example.healthguard.lib.PreferenceExtension.newScreen
import io.com.example.healthguard.lib.PreferenceExtension.switchPreference
import io.com.example.healthguard.lib.PreferenceExtension.titleRes
import io.com.example.healthguard.lib.Util

class PreferenceFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceScreen = preferenceManager.newScreen {
            switchPreference {
                key = PreferenceKeys.NIGHT_MODE
                titleRes = R.string.dark_mode
                isChecked = Util.isNightModeOn(context)
                widgetLayoutResource = R.layout.preference_material_switch
            }
        }
    }
}
