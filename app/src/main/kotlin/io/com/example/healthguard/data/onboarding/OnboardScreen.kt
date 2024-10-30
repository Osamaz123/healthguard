

package io.com.example.healthguard.data.onboarding

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class OnboardScreen(
    @DrawableRes
    val image: Int,
    @StringRes
    val title: Int,
    @StringRes
    val subtitle: Int,
)
