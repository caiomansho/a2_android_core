package com.infoa2.core.widget

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import com.infoa2.core.R
import com.infoa2.core.widget.enums.RequiredTextType
import com.infoa2.core.widget.interfaces.ComponentActions
import com.infoa2.core.widget.interfaces.FieldValidator
import com.google.android.material.textfield.TextInputLayout
import com.infoa2.core.widget.interfaces.ValidatorInputType
import com.infoa2.core.widget.util.ConfigHelper
import com.infoa2.core.widget.util.ViewUtil

/**
 * Created by caio on 30/03/17.
 */

class Spinner : TextInputLayout, FieldValidator, ComponentActions {

    var customErrorMessageId: Int = 0
        private set
    private var customErrorMessage: String? = null
    private var relatedLabelId = 0
    private var inputId: Int = 0
    private var relatedLabelTextColor: Int = 0
    private var relatedLabelText: TextView? = null
    private var _isRequired = false
    private var requiredText: String? = null
    private var isPrefix: Boolean? = null
    private var defaultSelectedIndex: Int = 0
    var spinner: android.widget.Spinner? = null
        private set
    private var spThemeId: Int = 0
    private var checkedIndex = -1
    private var textColor: Int = 0
    private var textColorHint: Int = 0

    var adapter: ArrayAdapter<*>? = null
        private set

    //getter setter
    var validatorInputType: SpinnerValidatorInputType? = null
    private var requiredTextType: RequiredTextType? = null

    val selectedItemPosition: Int
        get() = spinner!!.selectedItemPosition

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context) {
        setupAttribute(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context) {
        setupAttribute(attrs)
    }

    override fun isRequired(): Boolean {
        return _isRequired
    }

    fun setIsRequired(value: Boolean) {
        if (value) {
            validatorInputType = SpinnerValidatorInputType()

        } else {
            validatorInputType = null

        }
        this._isRequired = value
        setupTitleLabel()
    }

    override fun isValid(): Boolean {
        return if (validatorInputType != null) {
            validatorInputType!!.isValid
        } else true
    }

    private fun setupAttribute(attrs: AttributeSet) {
        val attrsArray = intArrayOf(
            android.R.attr.id // 0
        )
        val ta = context.obtainStyledAttributes(attrs, attrsArray)
        val id = ta.getResourceId(0 /* index of attribute in attrsArray */, View.NO_ID)
        ta.recycle()
        setId(id)
        layoutParams =
            ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)

        val a = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.RSSpinner,
            0, 0
        )

        try {
            customErrorMessageId = a.getResourceId(R.styleable.RSSpinner_sou_spCustomErrorMessage, 0)
            if (customErrorMessageId == 0) {
                customErrorMessage = ConfigHelper.getStringConfigValue(context, "customErrorMessage")

            }

            if (a.hasValue(R.styleable.RSSpinner_sou_spDefaultSelectedIndex)) {
                defaultSelectedIndex = a.getInt(R.styleable.RSSpinner_sou_spDefaultSelectedIndex, -1)

            } else {
                val _defaultSelectedIndex = ConfigHelper.getIntConfigValue(context, "spDefaultSelectedIndex")
                if (_defaultSelectedIndex != null) {
                    defaultSelectedIndex = _defaultSelectedIndex
                } else {
                    defaultSelectedIndex = -1
                }

            }

            if (a.hasValue(R.styleable.RSSpinner_sou_spRequiredTextType)) {
                requiredTextType = RequiredTextType.values()[a.getInt(R.styleable.RSSpinner_sou_spRequiredTextType, 0)]

            } else {
                val requiredTextTypeIndex = ConfigHelper.getIntConfigValue(context, "spRequiredTextType")
                if (requiredTextTypeIndex != null) {
                    requiredTextType = RequiredTextType.values()[requiredTextTypeIndex]
                } else {
                    requiredTextType = RequiredTextType.REQUIRED
                }
            }

            if (a.hasValue(R.styleable.RSSpinner_sou_spIsRequired)) {
                setIsRequired(a.getBoolean(R.styleable.RSSpinner_sou_spIsRequired, false))

            } else {
                val _isRequired = ConfigHelper.getBooleanConfigValue(context, "spIsRequired")
                if (_isRequired != null) {
                    setIsRequired(_isRequired)
                } else {
                    setIsRequired(false)
                }
            }

            if (a.hasValue(R.styleable.RSSpinner_sou_spRequiredText)) {
                requiredText = a.getString(R.styleable.RSSpinner_sou_spRequiredText)
                if (requiredText == null || requiredText != null && requiredText!!.isEmpty()) {
                    requiredText = "*"

                }


            } else {
                val _requiredText = ConfigHelper.getStringConfigValue(context, "spRequiredText")
                if (_requiredText != null) {
                    requiredText = _requiredText
                } else {
                    requiredText = "*"
                }
            }

            relatedLabelId = a.getResourceId(R.styleable.RSSpinner_sou_spRelatedLabel, 0)
            spThemeId = a.getResourceId(R.styleable.RSSpinner_sou_spRelatedLabel, 0)
            inputId = a.getResourceId(R.styleable.RSSpinner_sou_spInputId, 0)

            textColor = a.getResourceId(R.styleable.RSSpinner_sou_spTextColor, 0)
            textColorHint = a.getResourceId(R.styleable.RSSpinner_sou_spTextColorHint, 0)


            if (textColor == 0) {
                val colorName = ConfigHelper.getStringConfigValue(context, "spTextColor")
                textColor = resources.getIdentifier(colorName, "color", context.applicationContext.packageName)

            }

            if (textColorHint == 0) {
                val colorName = ConfigHelper.getStringConfigValue(context, "spTextColorHint")
                textColorHint = resources.getIdentifier(colorName, "color", context.applicationContext.packageName)
            }

            if (requiredText == null || requiredText != null && requiredText!!.isEmpty()) {
                requiredText = "*"

            }

            if (a.hasValue(R.styleable.RSSpinner_sou_spIsPrefix)) {
                isPrefix = a.getBoolean(R.styleable.RSSpinner_sou_spIsPrefix, true)

            } else {
                val _isPrefix = ConfigHelper.getBooleanConfigValue(context, "isPrefix")
                if (_isPrefix != null) {
                    isPrefix = _isPrefix
                } else {
                    isPrefix = java.lang.Boolean.TRUE
                }
            }

            if (spThemeId == 0) {
                spinner = android.widget.Spinner(context, attrs)

            } else {
                spinner = android.widget.Spinner(context, attrs)

            }

            if (_isRequired!!) {
                validatorInputType = SpinnerValidatorInputType()

            }

            if (inputId > 0) {
                spinner!!.id = inputId

            } else {
                spinner!!.id = ViewUtil.generateViewId()

            }

            addView(
                spinner,
                LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            )


        } finally {
            a.recycle()
        }

        requestLayout()
        spinner!!.isFocusable = true // can be done in XML preferrable
        //        spinner.setFocusableInTouchMode(true);

    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (relatedLabelId > 0 && relatedLabelText == null) {
            relatedLabelText = (parent as View).findViewById<View>(relatedLabelId) as TextView

            if (relatedLabelText == null) {
                relatedLabelText = (parent.parent as View).findViewById<View>(relatedLabelId) as TextView

            }
            if (relatedLabelText != null) {
                relatedLabelTextColor = relatedLabelText!!.currentTextColor
            }
            if ((spinner!!.prompt == null || spinner!!.prompt != null && spinner!!.prompt.toString().isEmpty())
                && relatedLabelText!!.text != null
                && !relatedLabelText!!.text.toString().isEmpty()
            ) {
                spinner!!.prompt = relatedLabelText!!.text.toString()

            }
        }

        setupTitleLabel()

    }

    private fun setupTitleLabel() {
        if (_isRequired!! && requiredTextType == RequiredTextType.REQUIRED || (!_isRequired)!! && requiredTextType == RequiredTextType.NOTREQUIRED) {
            if (relatedLabelText != null) {
                var title = if (relatedLabelText!!.text != null && !relatedLabelText!!.text.toString().isEmpty())
                    relatedLabelText!!.text.toString()
                else
                    ""
                title = title.replace(requiredText!!, "")
                if (isPrefix!!) {
                    relatedLabelText!!.setText(String.format("%s%s", requiredText, title))

                } else {
                    relatedLabelText!!.setText(String.format("%s%s", title, requiredText))

                }
            }
        } else {
            if (relatedLabelText != null) {
                val title = if (relatedLabelText!!.text != null && !relatedLabelText!!.text.toString().isEmpty())
                    relatedLabelText!!.text.toString()
                else
                    ""
                relatedLabelText!!.setText(title.replace(requiredText!!, ""))

            }
        }
    }

    fun setOnItemSelectedListener(listener: AdapterView.OnItemSelectedListener) {
        spinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            private var firstExec = true

            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                error = null
                if (this@Spinner.validatorInputType != null && !this.firstExec) {
                    this@Spinner.validatorInputType!!.isValid
                }

                if (position > 0) {
                    this.firstExec = false
                }

                listener.onItemSelected(parent, view, position, id)

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                listener.onNothingSelected(parent)

            }
        }

    }

    fun setAdapter(adapter: SpinnerAdapter) {
        spinner!!.adapter = adapter
    }

    fun setSelection(position: Int) {
        if (position < spinner!!.count) {
            spinner!!.setSelection(position)
        }
        isValid()
    }

    fun resetState() {
        error = ""

        if (this.relatedLabelText != null && relatedLabelTextColor != 0) {
            relatedLabelText!!.setTextColor(Color.parseColor("#" + Integer.toHexString(relatedLabelTextColor)))
        }

    }

    fun <T> setupAdapter(items: List<T>) {

        adapter = object : ArrayAdapter<T>(
            spinner!!.context,
            android.R.layout.simple_spinner_item, items
        ) {

            override fun getDropDownView(
                position: Int, convertView: View?,
                parent: ViewGroup
            ): View {
                if (textColorHint > 0) {
                    val view = super.getDropDownView(position, convertView, parent)
                    val tv = view as TextView
                    if (position == 0) {
                        // Set the hint text color gray
                        tv.setTextColor(ContextCompat.getColor(context, textColorHint))
                    } else {
                        tv.setTextColor(ContextCompat.getColor(context, textColor))
                    }
                }
                return super.getDropDownView(position, convertView, parent)

            }

        }

        adapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner!!.adapter = adapter
        if (checkedIndex > -1) {
            spinner!!.setSelection(checkedIndex)

        } else if (defaultSelectedIndex > -1) {
            spinner!!.setSelection(defaultSelectedIndex)

        }

        val listener = spinner!!.onItemSelectedListener
        if (listener == null) {
            spinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

                internal var firstExec = true

                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    if (validatorInputType != null && !firstExec) {
                        validatorInputType!!.isValid

                    }
                    if (position > 0) {
                        firstExec = false

                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }

        } else {
            spinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

                internal var firstExec = true

                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    if (validatorInputType != null && !firstExec) {
                        validatorInputType!!.isValid

                    }
                    if (position > 0) {
                        firstExec = false

                    }
                    listener.onItemSelected(parent, view, position, id)

                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }

        }
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        spinner!!.isEnabled = enabled
    }


    override fun enableComponent(enabled: Boolean) {
        isEnabled = enabled

    }

    override fun cleanComponent() {
        spinner!!.setSelection(0)
        resetState()

    }

    inner class SpinnerValidatorInputType : ValidatorInputType {

        override val isValid: Boolean
            get() {

                val isValid = if (selectedItemPosition == 0) java.lang.Boolean.FALSE else java.lang.Boolean.TRUE
                if (relatedLabelText != null) {
                    if (!isValid && relatedLabelTextColor != 0) {
                        relatedLabelText!!.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark))
                        error = context.getString(R.string.sou_widgets_select_option)

                    } else {
                        relatedLabelText!!.setTextColor(relatedLabelTextColor)
                        error = null

                    }
                }
                return isValid
            }

    }

}
