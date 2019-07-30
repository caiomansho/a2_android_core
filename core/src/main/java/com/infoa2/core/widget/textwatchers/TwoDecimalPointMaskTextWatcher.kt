package com.infoa2.core.widget.textwatchers

import android.text.Editable
import android.text.TextWatcher
import com.infoa2.core.widget.EditText
import com.infoa2.core.widget.interfaces.ValidatorInputType

import java.text.DecimalFormat

/**
 * Created by caio on 23/02/17.
 */

class TwoDecimalPointMaskTextWatcher : TextWatcher {
    private var current = ""
    private var index: Int = 0
    private var deletingDecimalPoint: Boolean = false
    private val editText: EditText
    private var onTextChanged: OnTextChanged? = null
    private var validatorInputType: ValidatorInputType? = null
    private var v_formattedValue = ""
    private val df = DecimalFormat()

    interface OnTextChanged {
        fun onTextChanged(text: String?)
    }

    constructor(editText: EditText) {
        this.editText = editText
    }

    constructor(editText: EditText, validatorInputType: ValidatorInputType) {
        this.editText = editText
        this.validatorInputType = validatorInputType

    }

    constructor(editText: EditText, onTextChanged: OnTextChanged) {
        this.editText = editText
        this.onTextChanged = onTextChanged

    }


    override fun onTextChanged(
        p_s: CharSequence, p_start: Int, p_before: Int, p_count: Int
    ) {
        // nothing to do
    }

    override fun beforeTextChanged(
        p_s: CharSequence, p_start: Int, p_count: Int, p_after: Int
    ) {
        if (p_after > 0) {
            index = p_s.length - p_start
        } else {
            index = p_s.length - p_start - 1
        }

        if (p_count > 0 && p_s[p_start] == '.') {
            deletingDecimalPoint = true
        } else {
            deletingDecimalPoint = false
        }
    }

    @Synchronized
    override fun afterTextChanged(p_s: Editable) {
        df.applyPattern("#,##0.00")
        if (p_s.toString() != current) {
            editText.removeTextChangedListener(this)
            if (deletingDecimalPoint) {
                p_s.delete(p_s.length - index - 1, p_s.length - index)
            }
            // Currency char may be retrieved from NumberFormat.getCurrencyInstance()
            val v_text = p_s.toString().replace("[^0-9]*".toRegex(), "")
            var v_value = 0.0
            if (v_text != null && v_text.length > 0) {
                v_value = java.lang.Double.parseDouble(v_text)
            }
            // Currency instance may be retrieved from a static member.
            v_formattedValue = df.format(v_value / 100)

            current = v_formattedValue
            editText.setText(v_formattedValue)
            if (index > v_formattedValue!!.length) {
                editText.setSelection(v_formattedValue!!.length)
            } else {
                editText.setSelection(v_formattedValue!!.length - index)
            }
            // inlude here anything you may want to do after the formatting is completed.
            editText.addTextChangedListener(this)

            Thread(Runnable {
                onTextChanged?.onTextChanged(v_formattedValue)

                validatorInputType?.isValid
            })
        }
    }

    companion object {

        fun insert(editText: EditText): TwoDecimalPointMaskTextWatcher {
            return TwoDecimalPointMaskTextWatcher(editText)

        }

        fun insert(editText: EditText, validatorInputType: ValidatorInputType): TextWatcher {
            return TwoDecimalPointMaskTextWatcher(editText, validatorInputType)
        }

        fun toFloat(twoDecimalPoint: EditText): Float {
            return toFloat(twoDecimalPoint.text!!.toString())

        }

        fun toFloat(twoDecimalPoint: String): Float {
            val value = twoDecimalPoint.replace("[,.]".toRegex(), "")
            return if (value.isEmpty()) {
                0f

            } else java.lang.Float.valueOf(value)!! / 100

        }

        fun toDouble(twoDecimalPoint: EditText): Double {
            return toDouble(twoDecimalPoint.text!!.toString())

        }

        fun toDouble(twoDecimalPoint: String): Double {
            val value = twoDecimalPoint.replace("[,.]".toRegex(), "")
            return if (value.isEmpty()) {
                0.0

            } else java.lang.Double.parseDouble(value) / 100

        }
    }
}
