
package io.com.example.healthguard.data.preference

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import io.com.example.healthguard.lib.Util
import javax.inject.Inject
import io.com.example.healthguard.data.preference.PreferenceKeys as Keys

class PreferencesHelper @Inject constructor(@ApplicationContext context: Context) {
    private val prefs: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)
    private val flowPrefs: FlowSharedPreferences = FlowSharedPreferences(prefs)

    fun isDarkMode(context: Context) = flowPrefs.getBoolean(Keys.NIGHT_MODE, Util.isNightModeOn(context))
}
