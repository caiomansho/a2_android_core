package com.infoa2.core.widget.impl

import android.content.Context
import com.infoa2.core.R
import com.infoa2.core.widget.EditText
import com.infoa2.core.widget.interfaces.ValidatorInputType

/**
 * Created by caiomansho on 05/04/2017.
 */

class ZipCodeValidatorInputType(private val context: Context, private val editText: EditText) : ValidatorInputType {

    override val isValid: Boolean
        get() {
            if (!editText.text!!.toString().isEmpty() && !editText.isZipCode) {
                editText.isErrorEnabled = true
                if (editText.customErrorMessageId != 0) {
                    editText.error = context.getString(editText.customErrorMessageId)

                } else {
                    editText.error = context.getString(R.string.sou_widgets_invalid_zip_code)

                }
                return java.lang.Boolean.FALSE

            } else if (editText.text!!.toString().isEmpty()) {
                editText.error = null
                editText.isErrorEnabled = false
                return java.lang.Boolean.FALSE

            } else {
                editText.error = null
                editText.isErrorEnabled = false
                return java.lang.Boolean.TRUE

            }
        }

}
