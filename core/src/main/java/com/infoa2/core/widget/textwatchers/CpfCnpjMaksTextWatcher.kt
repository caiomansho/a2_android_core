package com.infoa2.core.widget.textwatchers

import android.text.Editable
import android.text.TextWatcher
import com.infoa2.core.widget.EditText
import com.infoa2.core.widget.interfaces.ValidatorInputType

/**
 * Created by caio on 21/02/17.
 */

object CpfCnpjMaksTextWatcher {


    private val maskCNPJ = "##.###.###/####-##"
    private val maskCPF = "###.###.###-##"


    fun unmask(s: String): String {
        return s.replace("[^0-9]*".toRegex(), "")
    }

    fun insert(editText: EditText): TextWatcher {
        return object : TextWatcher {
            internal var isUpdating: Boolean = false
            internal var old = ""

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val str = CpfCnpjMaksTextWatcher.unmask(s.toString())
                val mask: String
                val defaultMask = getDefaultMask(str)
                when (str.length) {
                    11 -> mask = maskCPF
                    14 -> mask = maskCNPJ

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
                val str = CpfCnpjMaksTextWatcher.unmask(s.toString())
                val mask: String
                val defaultMask = getDefaultMask(str)
                when (str.length) {
                    11 -> mask = maskCPF
                    14 -> mask = maskCNPJ

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

    private fun getDefaultMask(str: String): String {
        var defaultMask = maskCPF
        if (str.length > 11) {
            defaultMask = maskCNPJ
        }
        return defaultMask
    }

}
