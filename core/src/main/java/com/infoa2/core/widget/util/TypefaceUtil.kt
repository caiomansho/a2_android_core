package com.infoa2.core.widget.util

import android.content.Context
import android.graphics.Typeface

import java.util.HashMap

/**
 * Created by Rey on 12/23/2014.
 */
object TypefaceUtil {

    private val sCachedFonts = HashMap<String, Typeface>()
    private val PREFIX_ASSET = "asset:"

    /**
     * @param familyName if start with 'asset:' prefix, then load font from asset folder.
     * @return
     */
    fun load(context: Context, familyName: String?, style: Int): Typeface? {
        if (familyName != null && familyName.startsWith(PREFIX_ASSET))
            synchronized(sCachedFonts) {
                try {
                    if (!sCachedFonts.containsKey(familyName)) {
                        val typeface =
                            Typeface.createFromAsset(context.assets, familyName.substring(PREFIX_ASSET.length))
                        sCachedFonts[familyName] = typeface
                        return typeface
                    }
                } catch (e: Exception) {
                    return Typeface.DEFAULT
                }

                return sCachedFonts[familyName]
            }

        return Typeface.create(familyName, style)
    }
}
