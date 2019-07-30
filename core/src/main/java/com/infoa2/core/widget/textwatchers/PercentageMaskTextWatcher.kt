package com.infoa2.core.widget.textwatchers

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.infoa2.core.widget.EditText
import com.infoa2.core.widget.interfaces.ValidatorInputType

import java.text.NumberFormat


/**
 * Created by caio on 23/02/17.
 */

class PercentageMaskTextWatcher : TextWatcher {
    private var current = ""
    private var index: Int = 0
    private var deletingDecimalPoint: Boolean = false
    private var deleting = false
    private val perentageEditText: EditText
    private lateinit var onTextChanged: OnTextChanged
    private lateinit var validatorInputType: ValidatorInputType

    interface OnTextChanged {
        fun onTextChanged(text: String)
    }

    constructor(p_currency: EditText) {
        perentageEditText = p_currency
    }

    constructor(p_currency: EditText, validatorInputType: ValidatorInputType) {
        perentageEditText = p_currency
        this.validatorInputType = validatorInputType

    }


    constructor(p_currency: EditText, onTextChanged: OnTextChanged) {
        perentageEditText = p_currency
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
        if (p_count > 0) {
            deleting = true

            if (p_s[p_start] == '.') {
                deletingDecimalPoint = true
            } else {
                deletingDecimalPoint = false
            }

        } else {
            deleting = false

        }

    }

    @Synchronized
    override fun afterTextChanged(p_s: Editable) {
        if (p_s.toString() != current) {
            perentageEditText.removeTextChangedListener(this)
            Log.d("afterTextChanged", p_s.length.toString())
            Log.d("afterTextChanged", index.toString())
            if (deletingDecimalPoint) {
                p_s.delete(p_s.length - index - 2, p_s.length - index - 1)

            } else if (deleting && p_s.length - index - 1 > 0) {
                p_s.delete(p_s.length - index - 1, p_s.length - index)

            }
            // Currency char may be retrieved from NumberFormat.getCurrencyInstance()
            val v_text = p_s.toString().replace("[%,.]".toRegex(), "")
            var v_value = 0.0
            if (v_text != null && v_text.length > 0) {
                v_value = java.lang.Double.parseDouble(v_text)
            }
            // Currency instance may be retrieved from a static member.
            val v_formattedValue = NumberFormat.getCurrencyInstance().format(v_value / 100).replace("R$", "") + "%"
            current = v_formattedValue
            perentageEditText.setText(v_formattedValue)
            if (index > v_formattedValue.length) {
                perentageEditText.setSelection(v_formattedValue.length)
            } else {
                perentageEditText.setSelection(v_formattedValue.length - index)
            }
            // inlude here anything you may want to do after the formatting is completed.
            perentageEditText.addTextChangedListener(this)

            onTextChanged?.onTextChanged(v_formattedValue)
            validatorInputType.isValid

        }
    }

    companion object {

        fun insert(editText: EditText): PercentageMaskTextWatcher {
            return PercentageMaskTextWatcher(editText)

        }

        fun insert(editText: EditText, validatorInputType: ValidatorInputType): TextWatcher {
            return PercentageMaskTextWatcher(editText, validatorInputType)

        }

        fun toFloat(editText: EditText): Float {
            val value = editText.text!!.toString().replace("[%,.]".toRegex(), "")
            return if (value.isEmpty()) {
                0f

            } else java.lang.Float.valueOf(value)!! / 100

        }

        fun toFloat(currency: String): Float {
            val value = currency.replace("[%,.]".toRegex(), "")
            return if (value.isEmpty()) {
                0f

            } else java.lang.Float.valueOf(value)!! / 100

        }
    }
}
