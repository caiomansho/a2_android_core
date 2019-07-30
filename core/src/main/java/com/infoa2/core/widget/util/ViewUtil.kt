package com.infoa2.core.widget.util

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View

import java.util.concurrent.atomic.AtomicInteger

object ViewUtil {

    val FRAME_DURATION = (1000 / 60).toLong()

    private val sNextGeneratedId = AtomicInteger(1)

    @SuppressLint("NewApi")
    fun generateViewId(): Int {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            while (true) {
                val result = sNextGeneratedId.get()
                // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
                var newValue = result + 1
                if (newValue > 0x00FFFFFF)
                    newValue = 1 // Roll over to 1, not 0.
                if (sNextGeneratedId.compareAndSet(result, newValue))
                    return result
            }
        } else
            return View.generateViewId()
    }

    fun hasState(states: IntArray?, state: Int): Boolean {
        if (states == null)
            return false

        for (state1 in states)
            if (state1 == state)
                return true

        return false
    }

    fun setBackground(v: View, drawable: Drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            v.background = drawable
        else
            v.setBackgroundDrawable(drawable)
    }


}
