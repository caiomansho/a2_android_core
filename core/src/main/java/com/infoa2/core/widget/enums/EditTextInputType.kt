package com.infoa2.core.widget.enums

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.view.View
import com.infoa2.core.R
import com.infoa2.core.widget.EditText
import com.infoa2.core.widget.impl.*
import com.infoa2.core.widget.interfaces.ValidatorInputType
import com.infoa2.core.widget.textwatchers.*
import com.tsongkha.spinnerdatepicker.DatePicker
import com.tsongkha.spinnerdatepicker.DatePickerDialog
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder

import java.util.Calendar
import java.util.GregorianCalendar

/**
 * Created by caio on 27/03/17.
 */

enum class EditTextInputType private constructor(val inputTypeCode: Int) {

    ZIPCODE(0) {
        override fun setupEditText(context: Context, editText: EditText) {
            editText.setKeyListener(DigitsKeyListener.getInstance("0123456789"))
            editText.inputType = InputType.TYPE_CLASS_NUMBER
            editText.setMaxLines(1)
            val validatorInputType = ZipCodeValidatorInputType(context, editText)
            editText.addTextChangedListener(ZipCodeMaksTextWatcher.insert(editText, validatorInputType))
            editText.validatorInputType = validatorInputType
        }

        override fun getDefaultLabel(context: Context): String {
            return context.getString(R.string.sou_widgets_zip_code)

        }
    },
    CPF(1) {
        override fun setupEditText(context: Context, editText: EditText) {
            editText.setKeyListener(DigitsKeyListener.getInstance("0123456789"))
            editText.inputType = InputType.TYPE_CLASS_NUMBER
            editText.setMaxLines(1)
            val validatorInputType = CpfValidatorInputType(context, editText)
            editText.addTextChangedListener(CpfMaksTextWatcher.insert(editText, validatorInputType))
            editText.validatorInputType = validatorInputType

        }

        override fun getDefaultLabel(context: Context): String {
            return context.getString(R.string.sou_widgets_cpf)

        }

    },
    CNPJ(2) {
        override fun setupEditText(context: Context, editText: EditText) {
            editText.setKeyListener(DigitsKeyListener.getInstance("0123456789"))
            editText.inputType = InputType.TYPE_CLASS_NUMBER
            editText.setMaxLines(1)

            val validatorInputType = CnpjValidatorInputType(context, editText)

            editText.addTextChangedListener(CnpjMaksTextWatcher.insert(editText, validatorInputType))
            editText.validatorInputType = validatorInputType
        }

        override fun getDefaultLabel(context: Context): String {
            return context.getString(R.string.sou_widgets_cnpj)

        }

    },
    CPFCNPJ(3) {
        override fun setupEditText(context: Context, editText: EditText) {
            editText.setKeyListener(DigitsKeyListener.getInstance("0123456789"))
            editText.inputType = InputType.TYPE_CLASS_NUMBER
            editText.setMaxLines(1)

            val validatorInputType = CpfCnpjValidatorInputType(context, editText)

            editText.addTextChangedListener(CpfCnpjMaksTextWatcher.insert(editText, validatorInputType))
            editText.validatorInputType = validatorInputType

        }

        override fun getDefaultLabel(context: Context): String {
            return context.getString(R.string.sou_widgets_cpf_cnpj)

        }

    },
    MOBILE(4) {
        override fun setupEditText(context: Context, editText: EditText) {
            editText.setKeyListener(DigitsKeyListener.getInstance("0123456789"))
            editText.inputType = InputType.TYPE_CLASS_NUMBER
            editText.setMaxLines(1)

            val validatorInputType = MobileValidatorInputType(context, editText)

            editText.addTextChangedListener(PhoneMaksTextWatcher.insert(editText, validatorInputType))
            editText.validatorInputType = validatorInputType

        }

        override fun getDefaultLabel(context: Context): String {
            return context.getString(R.string.sou_widgets_mobile)
        }

    },
    PERCENTAGE(5) {
        override fun setupEditText(context: Context, editText: EditText) {
            editText.setKeyListener(DigitsKeyListener.getInstance("0123456789"))
            editText.inputType = InputType.TYPE_CLASS_NUMBER
            editText.setMaxLines(1)
            val validatorInputType = PercentageValidatorInputType(context, editText)
            editText.validatorInputType = validatorInputType
            editText.addTextChangedListener(PercentageMaskTextWatcher.insert(editText, validatorInputType))
        }

        override fun getDefaultLabel(context: Context): String {
            return ""
        }

    },
    CURRENCY(6) {
        override fun setupEditText(context: Context, editText: EditText) {
            editText.setKeyListener(DigitsKeyListener.getInstance("0123456789"))
            editText.inputType = InputType.TYPE_CLASS_NUMBER
            editText.setMaxLines(1)
            val validatorInputType = CurrencyValidatorInputType(context, editText)
            editText.validatorInputType = validatorInputType
            editText.addTextChangedListener(CurrencyMaskTextWatcher.insert(editText, validatorInputType))
        }

        override fun getDefaultLabel(context: Context): String {
            return ""
        }

    },
    NONE(7) {
        override fun setupEditText(context: Context, editText: EditText) {
            val validatorInputType = object : ValidatorInputType {
                override val isValid: Boolean
                    get() {
                        if (!editText.isRequired() || !editText.text!!.toString().isEmpty()) {
                            editText.error = ""
                            return java.lang.Boolean.TRUE

                        } else {
                            return java.lang.Boolean.FALSE
                        }

                    }
            }

            editText.validatorInputType = validatorInputType
            editText.setMaxLines(1)
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                }

                override fun afterTextChanged(s: Editable) {
                    validatorInputType.isValid

                }
            })
        }

        override fun getDefaultLabel(context: Context): String {
            return ""
        }
    },
    EMAIL(8) {
        override fun setupEditText(context: Context, editText: EditText) {
            editText.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            editText.setMaxLines(1)
            val validatorInputType = EmailValidatorInputType(context, editText)
            editText.validatorInputType = validatorInputType
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                }

                override fun afterTextChanged(s: Editable) {
                    validatorInputType.isValid
                }
            })

        }

        override fun getDefaultLabel(context: Context): String {
            return context.getString(R.string.sou_widgets_email)
        }
    },
    DATE(9) {
        override fun setupEditText(context: Context, editText: EditText) {
            editText.inputType = InputType.TYPE_NULL
            editText.setMaxLines(1)
            editText.setOnClickListener {
                val datePickerDialog = editText.datePickerDialog
                if (datePickerDialog != null) {
                    datePickerDialog.show()
                } else {
                    var now: Calendar? = Calendar.getInstance()
                    val spinnerDatePickerDialogBuilder = SpinnerDatePickerDialogBuilder()
                        .context(context)
                        .callback { view, year, monthOfYear, dayOfMonth ->
                            val cal = GregorianCalendar(year, monthOfYear, dayOfMonth)
                            editText.dateText = cal
                        }
                        .spinnerTheme(R.style.NumberPickerStyle)
                        .showTitle(true)
                        .showDaySpinner(true)
                        .maxDate(now!!.get(Calendar.YEAR) + 10, now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))
                        .minDate(1900, 0, 1)
                    now = editText.dateText
                    if (now == null) {
                        now = Calendar.getInstance()
                    }
                    spinnerDatePickerDialogBuilder.defaultDate(
                        now!!.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                    ).build().show()
                }
            }
            editText.editText!!.setOnTouchListener(null)
            editText.editText!!.isFocusableInTouchMode = false
            editText.editText!!.isFocusable = false


            val validatorInputType = object : ValidatorInputType {
                override val isValid: Boolean
                    get() {
                        if (!editText.isRequired() || !editText.text!!.toString().isEmpty()) {
                            editText.error = ""
                            return java.lang.Boolean.TRUE

                        } else {
                            return java.lang.Boolean.FALSE
                        }

                    }
            }

            editText.validatorInputType = validatorInputType
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                }

                override fun afterTextChanged(s: Editable) {
                    validatorInputType.isValid

                }
            })

        }

        override fun getDefaultLabel(context: Context): String {
            return context.getString(R.string.sou_widgets_date)

        }

    },
    TWO_DECIMAL_POINT(6) {
        override fun setupEditText(context: Context, editText: EditText) {
            editText.setKeyListener(DigitsKeyListener.getInstance("0123456789"))
            editText.inputType = InputType.TYPE_CLASS_NUMBER
            editText.setMaxLines(1)
            val validatorInputType = CurrencyValidatorInputType(context, editText)
            editText.validatorInputType = validatorInputType
            editText.addTextChangedListener(TwoDecimalPointMaskTextWatcher.insert(editText, validatorInputType))
        }

        override fun getDefaultLabel(context: Context): String {
            return ""
        }

    };

    abstract fun setupEditText(context: Context, editText: EditText)
    abstract fun getDefaultLabel(context: Context): String

}
