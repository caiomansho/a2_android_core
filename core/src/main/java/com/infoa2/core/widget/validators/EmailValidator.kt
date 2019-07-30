package com.infoa2.core.widget.validators

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Created by caio on 11/04/17.
 */

object EmailValidator {

    fun isEmailValid(email: String?): Boolean {
        if (email == null || email.trim { it <= ' ' }.length == 0)
            return false

        val emailPattern =
            "\\b(^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@([A-Za-z0-9-])+(\\.[A-Za-z0-9-]+)*((\\.[A-Za-z0-9]{2,})|(\\.[A-Za-z0-9]{2,}\\.[A-Za-z0-9]{2,}))$)\\b"
        val pattern = Pattern.compile(emailPattern, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(email)
        return matcher.matches()

    }

}
