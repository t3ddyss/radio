package com.t3ddyss.radio.utilities

import android.content.Context
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat

fun Context.getThemeColor(@AttrRes res: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(res, typedValue, true)
    return typedValue.data
}

fun Int.toColorFilter() = BlendModeColorFilterCompat
    .createBlendModeColorFilterCompat(this, BlendModeCompat.SRC_ATOP)