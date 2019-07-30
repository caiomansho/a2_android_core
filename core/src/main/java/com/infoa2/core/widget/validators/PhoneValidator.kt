package com.infoa2.core.widget.validators

import com.google.android.material.textfield.TextInputEditText
import com.infoa2.core.widget.textwatchers.PhoneMaksTextWatcher

/**
 * Created by caio on 30/03/17.
 */

object PhoneValidator {

    fun isPhone(phone: String): Boolean {
        return phone.matches("-?\\d+(\\.\\d+)?".toRegex()) && phone.length >= 7 && phone.length <= 11
    }

    fun isPhone(phone: TextInputEditText): Boolean {
        return isPhone(PhoneMaksTextWatcher.unmask(phone.text!!.toString()))
    }

    fun isMobile(mobile: String): Boolean {
        return mobile.matches("-?\\d+(\\.\\d+)?".toRegex()) && mobile.length >= 10 && mobile.length <= 11
    }

    fun isMobile(phone: TextInputEditText): Boolean {
        return isMobile(PhoneMaksTextWatcher.unmask(phone.text!!.toString()))
    }
}
