package com.fp.devfantasypowerxi.app.view.floatingview

import android.content.Context
import android.os.Build

internal object Util {
    @JvmStatic
    fun dpToPx(context: Context, dp: Float): Int {
        val scale = context.resources.displayMetrics.density
        return Math.round(dp * scale)
    }

    @JvmStatic
    fun hasJellyBean(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
    }

    @JvmStatic
    fun hasLollipop(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
    }
}