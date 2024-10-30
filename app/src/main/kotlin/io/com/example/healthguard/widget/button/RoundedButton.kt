

package io.com.example.healthguard.widget.button

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatButton
import io.com.example.healthguard.R
import io.com.example.healthguard.lib.Extension.dp
import io.com.example.healthguard.lib.Extension.sp
import io.com.example.healthguard.lib.Util.getDrawableWithAttrTint

open class RoundedButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int? = null,
) : AppCompatButton(context, attrs, defStyleAttr ?: android.R.attr.buttonStyle) {
    init {
        background = getDrawableWithAttrTint(context, R.drawable.bg_rounded_ripple, R.attr.colorPrimary)
        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.colorOnPrimary, typedValue, true)
        this.setTextColor(typedValue.data)
        this.setPadding(
            paddingLeft + 16.dp(context),
            paddingTop + 6.dp(context),
            paddingRight + 16.dp(context),
            paddingBottom + 6.dp(context),
        )
        textSize = 18.sp(context)
    }
}
