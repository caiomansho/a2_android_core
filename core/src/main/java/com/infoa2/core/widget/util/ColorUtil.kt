package com.infoa2.core.widget.util

import android.graphics.Color

object ColorUtil {

    private fun getMiddleValue(prev: Int, next: Int, factor: Float): Int {
        return Math.round(prev + (next - prev) * factor)
    }

    fun getMiddleColor(prevColor: Int, curColor: Int, factor: Float): Int {
        if (prevColor == curColor)
            return curColor

        if (factor == 0f)
            return prevColor
        else if (factor == 1f)
            return curColor

        val a = getMiddleValue(Color.alpha(prevColor), Color.alpha(curColor), factor)
        val r = getMiddleValue(Color.red(prevColor), Color.red(curColor), factor)
        val g = getMiddleValue(Color.green(prevColor), Color.green(curColor), factor)
        val b = getMiddleValue(Color.blue(prevColor), Color.blue(curColor), factor)

        return Color.argb(a, r, g, b)
    }

    fun getColor(baseColor: Int, alphaPercent: Float): Int {
        val alpha = Math.round(Color.alpha(baseColor) * alphaPercent)

        return baseColor and 0x00FFFFFF or (alpha shl 24)
    }
}
