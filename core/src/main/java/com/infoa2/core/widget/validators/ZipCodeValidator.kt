package com.infoa2.core.widget.validators

import com.google.android.material.textfield.TextInputEditText
import com.infoa2.core.widget.textwatchers.ZipCodeMaksTextWatcher

/**
 * Created by caio on 29/03/17.
 */

object ZipCodeValidator {

    fun isZipCode(zipCode: TextInputEditText): Boolean {
        return isZipCode(ZipCodeMaksTextWatcher.unmask(zipCode.text!!.toString()))
    }

    fun isZipCode(zipCode: String): Boolean {
        return zipCode.matches("-?\\d+(\\.\\d+)?".toRegex()) && zipCode.length == 8

    }

}
