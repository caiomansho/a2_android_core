package com.infoa2.core.widget.textwatchers

import android.text.Editable
import android.text.TextWatcher
import com.infoa2.core.widget.EditText
import com.infoa2.core.widget.interfaces.ValidatorInputType

/**
 * Created by caio on 21/02/17.
 */

object PhoneMaksTextWatcher {


    private val maskPhone = "(##)####-####"
    private val maskPhoneSP = "(##)#####-####"


    fun unmask(s: String): String {
        return s.replace("[^0-9]*".toRegex(), "")
    }

    fun insert(editText: EditText): TextWatcher {
        return object : TextWatcher {
            internal var isUpdating: Boolean = false
            internal var old = ""

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val str = PhoneMaksTextWatcher.unmask(s.toString())
                val mask: String
                val defaultMask = getDefaultMask(str)
                when (str.length) {
                    10 -> mask = maskPhone
                    11 -> mask = maskPhoneSP

                    else -> mask = defaultMask
                }

                var mascara = ""
                if (isUpdating) {
                    old = str
                    isUpdating = false
                    return
                }
                var i = 0
                for (m in mask.toCharArray()) {
                    if (m != '#' && str.length > old.length || m != '#' && str.length < old.length && str.length != i) {
                        mascara += m
                        continue
                    }

                    try {
                        mascara += str[i]
                    } catch (e: Exception) {
                        break
                    }

                    i++
                }
                isUpdating = true
                editText.setText(mascara)
                editText.setSelection(mascara.length)

            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int,
                after: Int
            ) {

            }

            override fun afterTextChanged(s: Editable) {

            }
        }
    }

    fun insert(editText: EditText, validatorInputType: ValidatorInputType): TextWatcher {
        return object : TextWatcher {
            internal var isUpdating: Boolean = false
            internal var old = ""

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val str = PhoneMaksTextWatcher.unmask(s.toString())
                val mask: String
                val defaultMask = getDefaultMask(str)
                when (str.length) {
                    10 -> mask = maskPhone
                    11 -> mask = maskPhoneSP

                    else -> mask = defaultMask
                }

                var mascara = ""
                if (isUpdating) {
                    old = str
                    isUpdating = false
                    return
                }
                var i = 0
                for (m in mask.toCharArray()) {
                    if (m != '#' && str.length > old.length || m != '#' && str.length < old.length && str.length != i) {
                        mascara += m
                        continue
                    }

                    try {
                        mascara += str[i]
                    } catch (e: Exception) {
                        break
                    }

                    i++
                }
                isUpdating = true
                editText.setText(mascara)
                editText.setSelection(mascara.length)
                validatorInputType.isValid

            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int,
                after: Int
            ) {

            }

            override fun afterTextChanged(s: Editable) {

            }
        }

    }

    fun getAreaCode(str: String): String {
        return str.substring(1, 3)
    }

    fun getPhoneNumber(str: String): String {
        return str.substring(4)
    }


    private fun getDefaultMask(str: String): String {
        var defaultMask = maskPhone
        if (str.length > 10) {
            defaultMask = maskPhoneSP
        }
        return defaultMask
    }

}
