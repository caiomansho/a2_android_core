package com.infoa2.core.widget

import android.content.Context
import android.graphics.Color
import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.content.ContextCompat
import androidx.customview.view.AbsSavedState
import com.google.android.material.textfield.TextInputLayout
import com.infoa2.core.R
import com.infoa2.core.widget.enums.RequiredTextType
import com.infoa2.core.widget.interfaces.ComponentActions
import com.infoa2.core.widget.interfaces.FieldValidator
import com.infoa2.core.widget.interfaces.ValidatorInputType
import com.infoa2.core.widget.util.ConfigHelper
import com.infoa2.core.widget.util.ViewUtil
import java.util.ArrayList

/**
 * Created by caio on 31/03/17.
 */

class RadioGroup : TextInputLayout, FieldValidator, ComponentActions {

    private var customErrorMessageId: Int = 0
    private var customErrorMessage: String? = null
    private var relatedLabelId = 0
    private var inputId = 0
    private var relatedLabelTextColor: Int = 0
    private var minRadioWidthLength: Int? = null
    private var relatedLabelText: TextView? = null

    private var _isRequired = false

    private var requiredText: String? = null
    private var isPrefix: Boolean? = null
    private var rgEqualWeight: Boolean? = java.lang.Boolean.FALSE
    private var rgDefaultSelectedIndex: Int = 0
    private var radioGroup: android.widget.RadioGroup? = null
    private var checkedIndex = -1
    private var radioButtons: MutableList<AppCompatRadioButton>? = ArrayList()
    private var nonRequiredText: String? = null
    private var textColor: Int = 0
    private var attrs: AttributeSet? = null

    private var validatorInputType: ValidatorInputType? = null
    private var requiredTextType: RequiredTextType? = null

    val isChecked: Boolean
        get() {
            if (radioButtons == null) {
                return false
            }
            for (radioButton in radioButtons!!) {
                if (radioButton.isChecked) {
                    return true
                }
            }
            return false
        }

    override fun isValid(): Boolean {
        if (!isEnabled) {
            return java.lang.Boolean.TRUE
        }


        var isValid = true
        if (validatorInputType != null) {
            isValid = validatorInputType!!.isValid

        }

        if (this.relatedLabelText != null && relatedLabelTextColor != 0) {
            if (!isValid) {
                relatedLabelText!!.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark))

            } else {
                relatedLabelText!!.setTextColor(relatedLabelTextColor)

            }
        }
        return isValid

    }

    val checkedRadioButtonId: Int
        get() = radioGroup!!.checkedRadioButtonId

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setupAttribute(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setupAttribute(attrs)
    }

    override fun isRequired(): Boolean {
        return _isRequired
    }

    fun setIsRequired(value: Boolean) {
        this._isRequired = value
        setupTitleLabel()
    }

    //Setup dos atributos do Layout xml
    private fun setupAttribute(attrs: AttributeSet) {
        val attrsArray = intArrayOf(
            android.R.attr.id // 0
        )
        this.attrs = attrs

        val ta = context.obtainStyledAttributes(attrs, attrsArray)
        val id = ta.getResourceId(0 /* index of attribute in attrsArray */, View.NO_ID)
        ta.recycle()
        setId(id)
        layoutParams =
            ViewGroup.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        val a = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.RSRadioGroup,
            0, 0
        )


        try {
            customErrorMessageId = a.getResourceId(R.styleable.RSRadioGroup_sou_rgCustomErrorMessage, 0)
            if (customErrorMessageId == 0) {
                customErrorMessage = ConfigHelper.getStringConfigValue(context, "customErrorMessage")

            }

            if (a.hasValue(R.styleable.RSRadioGroup_sou_rgDefaultSelectedIndex)) {
                rgDefaultSelectedIndex = a.getInt(R.styleable.RSRadioGroup_sou_rgDefaultSelectedIndex, -1)

            } else {
                val _rgDefaultSelectedIndex = ConfigHelper.getIntConfigValue(context, "rgDefaultSelectedIndex")
                if (_rgDefaultSelectedIndex != null) {
                    rgDefaultSelectedIndex = _rgDefaultSelectedIndex
                } else {
                    rgDefaultSelectedIndex = -1
                }

            }

            textColor = a.getResourceId(R.styleable.RSRadioGroup_sou_rgTextColor, 0)


            if (a.hasValue(R.styleable.RSRadioGroup_sou_rgMinRadioWidth)) {
                minRadioWidthLength = a.getDimensionPixelSize(R.styleable.RSRadioGroup_sou_rgMinRadioWidth, 0)
            }

            if (a.hasValue(R.styleable.RSRadioGroup_sou_rgIsRequired)) {
                setIsRequired(a.getBoolean(R.styleable.RSRadioGroup_sou_rgIsRequired, false))

            } else {
                val _isRequired = ConfigHelper.getBooleanConfigValue(context, "rgIsRequired")
                if (_isRequired != null) {
                    setIsRequired(_isRequired)
                } else {
                    setIsRequired(false)
                }
            }

            if (a.hasValue(R.styleable.RSRadioGroup_sou_rgRequiredTextType)) {
                requiredTextType =
                    RequiredTextType.values()[a.getInt(R.styleable.RSRadioGroup_sou_rgRequiredTextType, 0)]

            } else {
                val requiredTextTypeIndex = ConfigHelper.getIntConfigValue(context, "rgRequiredTextType")
                if (requiredTextTypeIndex != null) {
                    requiredTextType = RequiredTextType.values()[requiredTextTypeIndex]
                } else {
                    requiredTextType = RequiredTextType.REQUIRED
                }
            }

            rgEqualWeight = a.getBoolean(R.styleable.RSRadioGroup_sou_rgEqualWeight, false)

            validatorInputType = object : ValidatorInputType {
                override val isValid: Boolean
                    get() = if (_isRequired!!) {
                        getCheckedIndex() > -1

                    } else java.lang.Boolean.TRUE
            }

            relatedLabelId = a.getResourceId(R.styleable.RSRadioGroup_sou_rgRelatedLabel, 0)
            inputId = a.getResourceId(R.styleable.RSRadioGroup_sou_rgInputId, 0)

            if (a.hasValue(R.styleable.RSRadioGroup_sou_rgRequiredText)) {
                requiredText = a.getString(R.styleable.RSRadioGroup_sou_rgRequiredText)
                if (requiredText == null || requiredText != null && requiredText!!.isEmpty()) {
                    requiredText = "*"

                }

            } else {
                val _requiredText = ConfigHelper.getStringConfigValue(context, "rgRequiredText")
                if (_requiredText != null) {
                    requiredText = _requiredText
                } else {
                    requiredText = "*"
                }
            }

            if (a.hasValue(R.styleable.RSRadioGroup_sou_rgIsPrefix)) {
                isPrefix = a.getBoolean(R.styleable.RSRadioGroup_sou_rgIsPrefix, true)

            } else {
                val _isPrefix = ConfigHelper.getBooleanConfigValue(context, "isPrefix")
                if (_isPrefix != null) {
                    isPrefix = _isPrefix
                } else {
                    isPrefix = java.lang.Boolean.TRUE
                }
            }
            radioGroup = android.widget.RadioGroup(context, attrs)

            if (inputId > 0) {
                radioGroup!!.id = inputId

            } else {
                radioGroup!!.id = ViewUtil.generateViewId()

            }
            radioGroup!!.setOnCheckedChangeListener { group, checkedId -> isValid() }

            addView(
                radioGroup,
                LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            )


        } finally {
            a.recycle()
        }

        requestLayout()

    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (relatedLabelId > 0 && relatedLabelText == null) {
            relatedLabelText = (parent as View).findViewById<View>(relatedLabelId) as TextView
            if (relatedLabelText != null) {
                relatedLabelTextColor = relatedLabelText!!.currentTextColor
            }
        }

        if (relatedLabelText != null) {
            nonRequiredText = if (relatedLabelText!!.text != null && !relatedLabelText!!.text.toString().isEmpty())
                relatedLabelText!!.text.toString()
            else
                ""


        } else {
            nonRequiredText = if (hint != null && !hint!!.toString().isEmpty())
                hint!!.toString()
            else
                ""
        }

        setupTitleLabel()

    }

    private fun setupTitleLabel() {

        if (_isRequired!! && requiredTextType == RequiredTextType.REQUIRED || !_isRequired && requiredTextType == RequiredTextType.NOTREQUIRED) {
            if (relatedLabelText != null) {
                var title = if (relatedLabelText!!.text != null && !relatedLabelText!!.text.toString().isEmpty())
                    relatedLabelText!!.text.toString()
                else
                    ""
                title = title.replace("*", "")
                if (isPrefix!!) {
                    relatedLabelText!!.setText(String.format("%s%s", requiredText, title))

                } else {
                    relatedLabelText!!.setText(String.format("%s%s", title, requiredText))

                }

            } else {

                var hint = if (hint != null && !hint!!.toString().isEmpty())
                    hint!!.toString()
                else
                    ""
                hint = hint.replace("*", "")
                if (isPrefix!!) {
                    setHint(String.format("%s%s", requiredText, hint))

                } else {
                    setHint(String.format("%s%s", hint, requiredText))

                }
            }
        } else {

            if (relatedLabelText != null) {
                relatedLabelText!!.text = nonRequiredText
                hint = ""

            } else {
                hint = nonRequiredText

            }

        }
    }


    override fun enableComponent(enabled: Boolean) {
        isEnabled = enabled

    }

    override fun cleanComponent() {
        clearCheck()
        resetState()

    }

    fun resetState() {
        error = ""

        if (this.relatedLabelText != null && relatedLabelTextColor != 0) {
            relatedLabelText!!.setTextColor(
                Color.parseColor(
                    "#" + Integer.toHexString(relatedLabelTextColor).substring(
                        2
                    )
                )
            )
        }

    }

    fun <T> setupRadioButtons(items: List<T>) {
        radioButtons = ArrayList()
        var radioButton: AppCompatRadioButton

        for (t in items) {
            if (rgEqualWeight!!) {
                radioButton = AppCompatRadioButton(context, attrs)
                if (radioGroup!!.orientation == android.widget.RadioGroup.HORIZONTAL) {
                    radioButton.layoutParams = android.widget.RadioGroup.LayoutParams(
                        0, android.widget.RadioGroup.LayoutParams.MATCH_PARENT, 1f
                    )
                } else {
                    radioButton.layoutParams = android.widget.RadioGroup.LayoutParams(
                        android.widget.RadioGroup.LayoutParams.MATCH_PARENT, 0, 1f
                    )
                }

            } else {
                radioButton = AppCompatRadioButton(context)
                if (minRadioWidthLength != null) {
                    radioButton.layoutParams =
                        android.widget.RadioGroup.LayoutParams(
                            minRadioWidthLength!!,
                            android.widget.RadioGroup.LayoutParams.MATCH_PARENT
                        )

                }

            }
            if (textColor != 0) {
                radioButton.setTextColor(ContextCompat.getColor(context, textColor))
            }
            radioButtons!!.add(radioButton)
            radioButton.text = t.toString()
            radioGroup!!.addView(radioButton)

        }
        if (checkedIndex > -1) {
            checkByIndex(checkedIndex)

        } else if (rgDefaultSelectedIndex > -1) {
            checkByIndex(rgDefaultSelectedIndex)
        }
    }


    fun <T> setupRadioButtons(vararg items: T) {
        radioButtons = ArrayList()
        var radioButton: AppCompatRadioButton
        for (t in items) {
            if (rgEqualWeight!!) {
                radioButton = AppCompatRadioButton(context, attrs)
                if (radioGroup!!.orientation == android.widget.RadioGroup.HORIZONTAL) {
                    radioButton.layoutParams = android.widget.RadioGroup.LayoutParams(
                        0, android.widget.RadioGroup.LayoutParams.MATCH_PARENT, 1f
                    )
                } else {
                    radioButton.layoutParams = android.widget.RadioGroup.LayoutParams(
                        android.widget.RadioGroup.LayoutParams.MATCH_PARENT, 0, 1f
                    )
                }

            } else {
                radioButton = AppCompatRadioButton(context)
                if (minRadioWidthLength != null) {
                    radioButton.layoutParams =
                        android.widget.RadioGroup.LayoutParams(
                            minRadioWidthLength!!,
                            android.widget.RadioGroup.LayoutParams.MATCH_PARENT
                        )

                }

            }
            if (textColor != 0) {
                radioButton.setTextColor(ContextCompat.getColor(context, textColor))
            }
            radioButtons!!.add(radioButton)
            radioButton.text = t.toString()
            radioGroup!!.addView(radioButton)

        }
        if (checkedIndex > -1) {
            checkByIndex(checkedIndex)

        } else if (rgDefaultSelectedIndex > -1) {
            checkByIndex(rgDefaultSelectedIndex)
        }
    }

    @Throws(IndexOutOfBoundsException::class)
    fun isChecked(index: Int): Boolean {
        return if (radioButtons != null) {
            radioButtons!![index].isChecked
        } else false
    }


    fun checkByIndex(index: Int) {
        if (index < radioButtons!!.size && index >= 0) {
            val radioButton = radioButtons!![index]
            radioGroup!!.check(radioButton.id)
        }
    }

    fun getCheckedIndex(): Int {
        var index = -1
        for (x in radioButtons!!.indices) {
            if (radioButtons!![x].isChecked) {
                index = x
            }

        }
        return index

    }

    fun setOnCheckedChangeListener(onCheckedChangeListener: android.widget.RadioGroup.OnCheckedChangeListener) {
        radioGroup!!.setOnCheckedChangeListener { group, checkedId ->
            isValid()
            onCheckedChangeListener.onCheckedChanged(group, checkedId)
        }
    }

    fun clearCheck() {
        if (radioGroup != null) {
            radioGroup!!.clearCheck()
        }

    }

    fun check(id: Int) {
        radioGroup!!.check(id)
    }

    fun getRadioButtons(): List<AppCompatRadioButton>? {
        return radioButtons
    }


}
