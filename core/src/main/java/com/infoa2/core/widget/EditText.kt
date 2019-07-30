package com.infoa2.core.widget

import android.annotation.TargetApi
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.*
import android.text.method.KeyListener
import android.text.method.MovementMethod
import android.text.method.TransformationMethod
import android.text.style.URLSpan
import android.util.AttributeSet
import android.view.ActionMode
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.ExtractedText
import android.view.inputmethod.ExtractedTextRequest
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.infoa2.core.R
import com.infoa2.core.widget.enums.EditTextInputType
import com.infoa2.core.widget.enums.RequiredTextType
import com.infoa2.core.widget.interfaces.ComponentActions
import com.infoa2.core.widget.interfaces.FieldValidator
import com.infoa2.core.widget.interfaces.ValidatorInputType
import com.infoa2.core.widget.textwatchers.TwoDecimalPointMaskTextWatcher
import com.infoa2.core.widget.util.ConfigHelper
import com.infoa2.core.widget.util.ViewUtil
import com.infoa2.core.widget.validators.*
import com.tsongkha.spinnerdatepicker.DatePickerDialog
import org.xmlpull.v1.XmlPullParserException

import java.io.IOException
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Created by caio on 24/03/17.
 */

class EditText : TextInputLayout, FieldValidator, ComponentActions {

    var customErrorMessageId: Int = 0
        private set
    private var customErrorMessage: String? = null
    private var relatedLabelId: Int = 0
    private var fieldNotFilledErrorMessageId: Int = 0
    private var inputId: Int = 0
    private var relatedLabelTextColor: Int = 0
    private var editText: TextInputEditText? = null
    private var editTextInputType: EditTextInputType? = null
    private var relatedLabelText: TextView? = null
    var isRequired
        get() = _isRequired
        set(value) {
            this._isRequired = value
            setupTitleLabel()
        }
    private var _isRequired = false
    private var requiredText: String? = null
    private var isPrefix: Boolean? = null
    var mustBeGreaterThenZero: Boolean? = null
        private set
    var dateText: Calendar? = null
        set(dateText) {
            field = dateText
            if (dateText != null) {
                editText!!.setText(getFormatedDate(dateText))

            } else {
                editText!!.setText("")

            }
        }
    private var bigDecimal: BigDecimal? = null
    private var nonRequiredText: String? = null
    var datePickerDialog: DatePickerDialog? = null
    //getter setter
    var validatorInputType: ValidatorInputType? = null
    private var requiredTextType: RequiredTextType? = null

    val isEmpty: Boolean
        get() = editText!!.text!!.toString().isEmpty()

    val isCpf: Boolean
        get() = CpfValidator.isCpf(editText!!)

    val isCnpj: Boolean
        get() = CnpjValidator.isCNPJ(editText!!)

    val isZipCode: Boolean
        get() = ZipCodeValidator.isZipCode(editText!!)

    val isPhone: Boolean
        get() = PhoneValidator.isPhone(editText!!)

    val isMobile: Boolean
        get() = PhoneValidator.isMobile(editText!!)

    val isEmail: Boolean
        get() = EmailValidator.isEmailValid(editText!!.text!!.toString())

    override val isValid: Boolean?
        get() {
            if (!isEnabled) {
                return java.lang.Boolean.TRUE

            }
            var isValid: Boolean? = java.lang.Boolean.TRUE
            if (editText!!.text!!
                    .toString().isEmpty()
            ) {
                if (isRequired!!) {
                    if (fieldNotFilledErrorMessageId > 0) {
                        editText!!.error = context.getString(fieldNotFilledErrorMessageId)

                    } else {
                        editText!!.error = context.getString(R.string.sou_widgets_field_not_filled)

                    }
                    isValid = java.lang.Boolean.FALSE

                } else {
                    return java.lang.Boolean.TRUE

                }
            }
            isValid = isValid!! && validatorInputType!!.isValid!!


            if (this.relatedLabelText != null && relatedLabelTextColor != 0) {
                if (!isValid) {
                    relatedLabelText!!.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark))

                } else {
                    relatedLabelText!!.setTextColor(
                        Color.parseColor(
                            "#" + Integer.toHexString(relatedLabelTextColor).substring(
                                2
                            )
                        )
                    )

                }
            }
            return isValid

        }

    var float: Float?
        get() = TwoDecimalPointMaskTextWatcher.toFloat(editText!!.text!!.toString())
        set(floatValue) = if (floatValue != null) {
            if (editTextInputType === EditTextInputType.CURRENCY || editTextInputType === EditTextInputType.PERCENTAGE ||
                editTextInputType === EditTextInputType.TWO_DECIMAL_POINT
            ) {
                editText!!.setText(
                    BigDecimal.valueOf(floatValue.toDouble())
                        .setScale(2, BigDecimal.ROUND_HALF_UP)
                        .toString()
                )
            } else {
                editText!!.setText(floatValue.toString())

            }

        } else {
            editText!!.setText("")

        }

    //EditText methods
    val text: Editable?
        get() = editText!!.text

    /**
     * @return the base paint used for the text.  Please use this only to
     * consult the Paint's properties and not to change them.
     */
    val paint: TextPaint
        get() = editText!!.paint

    /**
     * @return the flags on the Paint being used to display the text.
     * @see android.graphics.Paint.getFlags
     */
    val paintFlags: Int
        get() = editText!!.paintFlags

    /**
     * Get the private type of the content.
     *
     * @see EditorInfo.privateImeOptions
     */
    val privateImeOptions: String
        get() = editText!!.privateImeOptions

    /**
     * Convenience for [android.text.Selection.getSelectionEnd].
     */
    val selectionEnd: Int
        get() = editText!!.selectionEnd

    /**
     * Convenience for [android.text.Selection.getSelectionStart].
     */
    val selectionStart: Int
        get() = editText!!.selectionStart

    /**
     * @return the color of the shadow layer
     * @attr ref android.R.styleable#TextView_shadowColor
     * @see .setShadowLayer
     */
    val shadowColor: Int
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) editText!!.shadowColor else 0

    /**
     * @return the horizontal offset of the shadow layer
     * @attr ref android.R.styleable#TextView_shadowDx
     * @see .setShadowLayer
     */
    val shadowDx: Float
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) editText!!.shadowDx else 0f

    /**
     * @return the vertical offset of the shadow layer
     * @attr ref android.R.styleable#TextView_shadowDy
     * @see .setShadowLayer
     */
    val shadowDy: Float
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) editText!!.shadowDy else 0f


    /**
     * Returns whether the soft input method will be made visible when this
     * TextView gets focused. The default is true.
     */
    val showSoftInputOnFocus: Boolean
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) editText!!.showSoftInputOnFocus else true

    /**
     * Gets the text colors for the different states (normal, selected, focused) of the TextView.
     *
     * @attr ref android.R.styleable#TextView_textColor
     * @see .setTextColor
     * @see .setTextColor
     */
    val textColors: ColorStateList
        get() = editText!!.textColors

    /**
     * Get the default [Locale] of the text in this TextView.
     *
     * @return the default [Locale] of the text in this TextView.
     */
    val textLocale: Locale
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) editText!!.textLocale else Locale.getDefault()

    /**
     * @return the extent by which text is currently being stretched
     * horizontally.  This will usually be 1.
     */
    val textScaleX: Float
        get() = editText!!.textScaleX

    /**
     * @return the size (in pixels) of the default text size in this TextView.
     */
    val textSize: Float
        get() = editText!!.textSize

    /**
     * Returns the total bottom padding of the view, including the bottom
     * Drawable if any, the extra space to keep more than maxLines
     * from showing, and the vertical offset for gravity, if any.
     */
    val totalPaddingBottom: Int
        get() = paddingBottom + editText!!.totalPaddingBottom

    /**
     * Returns the total end padding of the view, including the end
     * Drawable if any.
     */
    val totalPaddingEnd: Int
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) paddingEnd + editText!!.totalPaddingEnd else totalPaddingRight

    /**
     * Returns the total left padding of the view, including the left
     * Drawable if any.
     */
    val totalPaddingLeft: Int
        get() = paddingLeft + editText!!.totalPaddingLeft

    /**
     * Returns the total right padding of the view, including the right
     * Drawable if any.
     */
    val totalPaddingRight: Int
        get() = paddingRight + editText!!.totalPaddingRight

    /**
     * Returns the total start padding of the view, including the start
     * Drawable if any.
     */
    val totalPaddingStart: Int
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) paddingStart + editText!!.totalPaddingStart else totalPaddingLeft

    /**
     * Returns the total top padding of the view, including the top
     * Drawable if any, the extra space to keep more than maxLines
     * from showing, and the vertical offset for gravity, if any.
     */
    val totalPaddingTop: Int
        get() = paddingTop + editText!!.totalPaddingTop

    /**
     * @return the current transformation method for this TextView.
     * This will frequently be null except for single-line and password
     * fields.
     * @attr ref android.R.styleable#TextView_password
     * @attr ref android.R.styleable#TextView_singleLine
     */
    val transformationMethod: TransformationMethod
        get() = editText!!.transformationMethod

    /**
     * Returns the list of URLSpans attached to the text
     * (by [android.text.util.Linkify] or otherwise) if any.  You can call
     * [URLSpan.getURL] on them to find where they link to
     * or use [android.text.Spanned.getSpanStart] and [android.text.Spanned.getSpanEnd]
     * to find the region of the text they are attached to.
     */
    val urls: Array<URLSpan>
        get() = editText!!.urls

    /**
     * @return whether or not the cursor is visible (assuming this TextView is editable)
     * @attr ref android.R.styleable#TextView_cursorVisible
     * @see .setCursorVisible
     */
    /**
     * Set whether the cursor is visible. The default is true. Note that this property only
     * makes sense for editable TextView.
     *
     * @attr ref android.R.styleable#TextView_cursorVisible
     * @see .isCursorVisible
     */
    var isCursorVisible: Boolean
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        get() = Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN || editText!!.isCursorVisible
        set(visible) {
            editText!!.isCursorVisible = visible
        }

    /**
     * Returns whether this text view is a current input method target.  The
     * default implementation just checks with [android.view.inputmethod.InputMethodManager].
     */
    val isInputMethodTarget: Boolean
        get() = editText!!.isInputMethodTarget

    /**
     * Return whether or not suggestions are enabled on this TextView. The suggestions are generated
     * by the IME or by the spell checker as the user types. This is done by adding
     * [android.text.style.SuggestionSpan]s to the text.
     *
     *
     * When suggestions are enabled (default), this list of suggestions will be displayed when the
     * user asks for them on these parts of the text. This value depends on the inputType of this
     * TextView.
     *
     *
     * The class of the input type must be [android.text.InputType.TYPE_CLASS_TEXT].
     *
     *
     * In addition, the type variation must be one of
     * [android.text.InputType.TYPE_TEXT_VARIATION_NORMAL],
     * [android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_SUBJECT],
     * [android.text.InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE],
     * [android.text.InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE] or
     * [android.text.InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT].
     *
     *
     * And finally, the [android.text.InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS] flag must *not* be set.
     *
     * @return true if the suggestions popup window is enabled, based on the inputType.
     */
    val isSuggestionsEnabled: Boolean
        @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && editText!!.isSuggestionsEnabled

    /**
     * Returns the state of the `textIsSelectable` flag (See
     * [setTextIsSelectable()][.setTextIsSelectable]). Although you have to set this flag
     * to allow users to select and copy text in a non-editable TextView, the content of an
     * [EditText] can always be selected, independently of the value of this flag.
     *
     *
     *
     * @return True if the text displayed in this TextView can be selected by the user.
     * @attr ref android.R.styleable#TextView_textIsSelectable
     */
    val isTextSelectable: Boolean
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        get() = Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB || editText!!.isTextSelectable

    /**
     * @return the minimum width of the TextView, expressed in ems or -1 if the minimum width
     * @attr ref android.R.styleable#TextView_minEms
     * @see .setMinEms
     * @see .setEms
     */
    /**
     * Makes the TextView at least this many ems wide
     *
     * @attr ref android.R.styleable#TextView_minEms
     */
    var minEms: Int
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) editText!!.minEms else -1
        set(minems) {
            editText!!.minEms = minems
        }

    /**
     * @return the maximum width of the TextView, expressed in ems or -1 if the maximum width
     * @attr ref android.R.styleable#TextView_maxEms
     * @see .setMaxEms
     * @see .setEms
     */
    /**
     * Makes the TextView at most this many ems wide
     *
     * @attr ref android.R.styleable#TextView_maxEms
     */
    var maxEms: Int
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) editText!!.maxEms else -1
        set(maxems) {
            editText!!.maxEms = maxems
        }

    /**
     * @return the currently set font feature settings.  Default is null.
     * @see .setFontFeatureSettings
     * @see android.graphics.Paint.setFontFeatureSettings
     */
    /**
     * Sets font feature settings.  The format is the same as the CSS
     * font-feature-settings attribute:
     * http://dev.w3.org/csswg/css-fonts/#propdef-font-feature-settings
     *
     * @param fontFeatureSettings font feature settings represented as CSS compatible string
     * @attr ref android.R.styleable#TextView_fontFeatureSettings
     * @see .getFontFeatureSettings
     * @see android.graphics.Paint.getFontFeatureSettings
     */
    var fontFeatureSettings: String?
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) editText!!.fontFeatureSettings else null
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        set(fontFeatureSettings) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                editText!!.fontFeatureSettings = fontFeatureSettings
        }
    //
    //    /**
    //     * Sets the text to be displayed when the text of the TextView is empty.
    //     * Null means to use the normal empty text. The hint does not currently
    //     * participate in determining the size of the view.
    //     *
    //     * @attr ref android.R.styleable#TextView_hint
    //     */
    //    public void setHint (CharSequence hint){
    //        editText.setHint(hint);
    //    }
    //
    //    /**
    //     * Sets the text to be displayed when the text of the TextView is empty,
    //     * from a resource.
    //     *
    //     * @attr ref android.R.styleable#TextView_hint
    //     */
    //    public void setHint (int resid){
    //        editText.setHint(resid);
    //
    //    }

    /**
     * @return the color of the hint text, for the different states of this TextView.
     * @attr ref android.R.styleable#TextView_textColorHint
     * @see .setHintTextColor
     * @see .setHintTextColor
     * @see .setTextColor
     * @see .setLinkTextColor
     */
    val hintTextColors: ColorStateList
        get() = editText!!.hintTextColors

    /**
     * Get the IME action label previous set with [.setImeActionLabel].
     *
     * @see .setImeActionLabel
     *
     * @see EditorInfo
     */
    val imeActionLabel: CharSequence
        get() = editText!!.imeActionLabel

    /**
     * Get the IME action ID previous set with [.setImeActionLabel].
     *
     * @see .setImeActionLabel
     *
     * @see EditorInfo
     */
    val imeActionId: Int
        get() = editText!!.imeActionId

    /**
     * Get the type of the IME editor.
     *
     * @see .setImeOptions
     * @see EditorInfo
     */
    /**
     * Change the editor type integer associated with the text view, which
     * will be reported to an IME with [EditorInfo.imeOptions] when it
     * has focus.
     *
     * @attr ref android.R.styleable#TextView_imeOptions
     * @see .getImeOptions
     *
     * @see EditorInfo
     */

    var imeOptions: Int
        get() = editText!!.imeOptions
        set(imeOptions) {
            editText!!.imeOptions = imeOptions
        }

    /**
     * Gets whether the TextView includes extra top and bottom padding to make
     * room for accents that go above the normal ascent and descent.
     *
     * @attr ref android.R.styleable#TextView_includeFontPadding
     * @see .setIncludeFontPadding
     */
    /**
     * Set whether the TextView includes extra top and bottom padding to make
     * room for accents that go above the normal ascent and descent.
     * The default is true.
     *
     * @attr ref android.R.styleable#TextView_includeFontPadding
     * @see .getIncludeFontPadding
     */
    var includeFontPadding: Boolean
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && editText!!.includeFontPadding
        set(includepad) {
            editText!!.includeFontPadding = includepad
        }

    /**
     * Get the type of the editable content.
     *
     * @see .setInputType
     * @see android.text.InputType
     */
    /**
     * Set the type of the content with a constant as defined for [EditorInfo.inputType]. This
     * will take care of changing the key listener, by calling [.setKeyListener],
     * to match the given content type.  If the given content type is [EditorInfo.TYPE_NULL]
     * then a soft keyboard will not be displayed for this text view.
     *
     *
     * Note that the maximum number of displayed lines (see [.setMaxLines]) will be
     * modified if you change the [EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE] flag of the input
     * type.
     *
     * @attr ref android.R.styleable#TextView_inputType
     * @see .getInputType
     * @see .setRawInputType
     * @see android.text.InputType
     */
    var inputType: Int
        get() = editText!!.inputType
        set(type) {
            editText!!.inputType = type
        }

    /**
     * Gets the number of times the marquee animation is repeated. Only meaningful if the
     * TextView has marquee enabled.
     *
     * @return the number of times the marquee animation is repeated. -1 if the animation
     * repeats indefinitely
     * @attr ref android.R.styleable#TextView_marqueeRepeatLimit
     * @see .setMarqueeRepeatLimit
     */
    /**
     * Sets how many times to repeat the marquee animation. Only applied if the
     * TextView has marquee enabled. Set to -1 to repeat indefinitely.
     *
     * @attr ref android.R.styleable#TextView_marqueeRepeatLimit
     * @see .getMarqueeRepeatLimit
     */
    var marqueeRepeatLimit: Int
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) editText!!.marqueeRepeatLimit else -1
        set(marqueeLimit) {
            editText!!.marqueeRepeatLimit = marqueeLimit
        }

    /**
     * @return the minimum number of lines displayed in this TextView, or -1 if the minimum
     * height was set in pixels instead using [or #setDividerHeight(int)][.setMinHeight].
     * @attr ref android.R.styleable#TextView_minLines
     * @see .setMinLines
     */
    /**
     * Makes the TextView at least this many lines tall.
     *
     *
     * Setting this value overrides any other (minimum) height setting. A single line TextView will
     * set this value to 1.
     *
     * @attr ref android.R.styleable#TextView_minLines
     * @see .getMinLines
     */
    var minLines: Int
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) editText!!.minLines else -1
        set(minlines) {
            editText!!.minLines = minlines
        }

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setupAttribute(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setupAttribute(attrs)
    }

    //Setup dos atributos do Layout xml
    private fun setupAttribute(attrs: AttributeSet) {

        val a = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.RSEditText,
            0, 0
        )

        try {
            customErrorMessageId = a.getResourceId(R.styleable.RSEditText_sou_etCustomErrorMessage, 0)
            if (customErrorMessageId == 0) {
                customErrorMessage = ConfigHelper.getStringConfigValue(context, "etCustomErrorMessage")

            }
            if (a.hasValue(R.styleable.RSEditText_sou_etIsRequired)) {
                isRequired = a.getBoolean(R.styleable.RSEditText_sou_etIsRequired, false)

            } else {
                val _isRequired = ConfigHelper.getBooleanConfigValue(context, "etIsRequired")
                if (_isRequired != null) {
                    isRequired = _isRequired
                } else {
                    isRequired = java.lang.Boolean.FALSE
                }
            }

            if (a.hasValue(R.styleable.RSEditText_sou_etRequiredTextType)) {
                requiredTextType = RequiredTextType.values()[a.getInt(R.styleable.RSEditText_sou_etRequiredTextType, 0)]

            } else {
                val requiredTextTypeIndex = ConfigHelper.getIntConfigValue(context, "etRequiredTextType")
                if (requiredTextTypeIndex != null) {
                    requiredTextType = RequiredTextType.values()[requiredTextTypeIndex]
                } else {
                    requiredTextType = RequiredTextType.REQUIRED
                }
            }

            fieldNotFilledErrorMessageId = a.getResourceId(R.styleable.RSEditText_sou_etFieldNotFilledErrorMessage, 0)
            relatedLabelId = a.getResourceId(R.styleable.RSEditText_sou_etRelatedLabel, 0)
            inputId = a.getResourceId(R.styleable.RSEditText_sou_etInputId, 0)

            if (a.hasValue(R.styleable.RSEditText_sou_etRequiredText)) {
                requiredText = a.getString(R.styleable.RSEditText_sou_etRequiredText)
                if (requiredText == null || requiredText != null && requiredText!!.isEmpty()) {
                    requiredText = "*"

                }

            } else {
                val _requiredText = ConfigHelper.getStringConfigValue(context, "etRequiredText")
                if (_requiredText != null) {
                    requiredText = _requiredText
                } else {
                    requiredText = "*"
                }
            }

            mustBeGreaterThenZero = a.getBoolean(R.styleable.RSEditText_sou_etMustBeGreaterThenZero, false)
            if (a.hasValue(R.styleable.RSEditText_sou_etIsPrefix)) {
                isPrefix = a.getBoolean(R.styleable.RSEditText_sou_etIsPrefix, true)

            } else {
                val _isPrefix = ConfigHelper.getBooleanConfigValue(context, "etIsPrefix")
                if (_isPrefix != null) {
                    isPrefix = _isPrefix
                } else {
                    isPrefix = java.lang.Boolean.TRUE
                }
            }

            editText = TextInputEditText(context, attrs)

            if (inputId > 0) {
                editText!!.id = inputId

            } else {
                editText!!.id = ViewUtil.generateViewId()

            }

            addView(
                editText,
                LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            )

            requestLayout()

            addEditTextInputType(
                EditTextInputType.values()[a.getInt(
                    R.styleable.RSEditText_sou_etValidationType,
                    EditTextInputType.NONE.inputTypeCode
                )]
            )

        } finally {
            a.recycle()
        }
        editText!!.hint = null


    }

    fun addEditTextInputType(editTextInputType: EditTextInputType) {
        this.editTextInputType = editTextInputType
        editTextInputType.setupEditText(context, this)

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

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (relatedLabelId > 0 && relatedLabelText == null) {
            relatedLabelText = (parent as View).findViewById<View>(relatedLabelId) as TextView
            if (relatedLabelText == null) {
                relatedLabelText = (parent.parent as View).findViewById<View>(relatedLabelId) as TextView
            }
        }

        if (relatedLabelText != null) {
            nonRequiredText = if (relatedLabelText!!.text != null && !relatedLabelText!!.text.toString().isEmpty())
                relatedLabelText!!.text.toString()
            else
                editTextInputType!!.getDefaultLabel(context)


        } else {
            nonRequiredText = if (hint != null && !hint!!.toString().isEmpty())
                hint!!.toString()
            else
                editTextInputType!!.getDefaultLabel(context)
        }

        setupTitleLabel()

    }

    override fun setError(error: CharSequence?) {
        super.setError(error)
        if (error == null || error != null && error.length == 0) {
            editText!!.error = null
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
    }

    override fun setOnClickListener(onClickListener: View.OnClickListener?) {
        editText!!.setOnClickListener(onClickListener)

    }

    fun getBigDecimal(): BigDecimal? {
        return bigDecimal
    }

    fun setBigDecimal(bigDecimal: BigDecimal?) {
        var bigDecimal = bigDecimal
        this.bigDecimal = bigDecimal
        if (bigDecimal != null) {
            if (editTextInputType === EditTextInputType.CURRENCY || editTextInputType === EditTextInputType.PERCENTAGE ||
                editTextInputType === EditTextInputType.TWO_DECIMAL_POINT
            ) {
                bigDecimal = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP)

            }
            editText!!.setText(bigDecimal!!.toString())

        } else {
            editText!!.setText("")

        }
    }

    fun setBigDecimal(bigDecimal: BigDecimal?, scale: Int) {
        var bigDecimal = bigDecimal
        this.bigDecimal = bigDecimal
        if (bigDecimal != null) {
            bigDecimal = bigDecimal.setScale(scale, BigDecimal.ROUND_HALF_UP)
            editText!!.setText(bigDecimal!!.toString())

        } else {
            editText!!.setText("")

        }
    }

    fun setDouble(doubleValue: Double?) {
        if (doubleValue != null) {
            if (editTextInputType === EditTextInputType.CURRENCY || editTextInputType === EditTextInputType.PERCENTAGE ||
                editTextInputType === EditTextInputType.TWO_DECIMAL_POINT
            ) {
                editText!!.setText(
                    BigDecimal.valueOf(doubleValue)
                        .setScale(2, BigDecimal.ROUND_HALF_UP)
                        .toString()
                )
            } else {
                editText!!.setText(doubleValue.toString())

            }

        } else {
            editText!!.setText("")

        }
    }


    fun setInt(intValue: Int?) {
        if (intValue != null) {
            if (editTextInputType === EditTextInputType.CURRENCY || editTextInputType === EditTextInputType.PERCENTAGE) {
                editText!!.setText(
                    BigDecimal.valueOf(intValue.toLong())
                        .setScale(2, BigDecimal.ROUND_HALF_UP)
                        .toString()
                )
            } else {
                editText!!.setText(intValue.toString())

            }

        } else {
            editText!!.setText("")

        }
    }


    fun setDouble(doubleValue: Double?, scale: Int) {
        if (doubleValue != null) {
            editText!!.setText(
                BigDecimal.valueOf(doubleValue)
                    .setScale(scale, BigDecimal.ROUND_HALF_UP)
                    .toString()
            )

        } else {
            editText!!.setText("")

        }
    }

    //retorna a data formatada em formato brasileiro
    private fun getFormatedDate(data: Calendar): String {
        try {
            val sdf = SimpleDateFormat("dd/MM/yyyy")
            return sdf.format(data.time)

        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }

    }

    private fun setupTitleLabel() {

        if (isRequired!! && requiredTextType == RequiredTextType.REQUIRED || (!isRequired)!! && requiredTextType == RequiredTextType.NOTREQUIRED) {
            if (relatedLabelText != null) {
                relatedLabelTextColor = relatedLabelText!!.currentTextColor
                var title = if (relatedLabelText!!.text != null && !relatedLabelText!!.text.toString().isEmpty())
                    relatedLabelText!!.text.toString()
                else
                    editTextInputType!!.getDefaultLabel(context)

                title = title.replace(requiredText!!, "")
                if (isPrefix!!) {
                    relatedLabelText!!.setText(String.format("%s%s", requiredText, title))

                } else {
                    relatedLabelText!!.setText(String.format("%s%s", title, requiredText))

                }
                hint = ""

            } else {
                var hint = if (hint != null && !hint!!.toString().isEmpty())
                    hint!!.toString()
                else
                    editTextInputType!!.getDefaultLabel(context)
                hint = hint.replace(requiredText!!, "")
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
                var hint = if (hint != null && !hint!!.toString().isEmpty())
                    hint!!.toString()
                else
                    editTextInputType!!.getDefaultLabel(context)
                hint = hint.replace(requiredText!!, "")
                setHint(hint)

            }

        }
    }

    @Throws(NumberFormatException::class, Exception::class)
    fun toFloat(): Float? {
        val value = editText!!.text!!.toString().replace("[^0-9]*".toRegex(), "")
        return if (value.isEmpty()) 0.0f else java.lang.Float.valueOf(java.lang.Float.valueOf(value)!!.toFloat() / 100.0f)

    }

    @Throws(NumberFormatException::class, Exception::class)
    fun toDouble(): Double? {
        val value = editText!!.text!!.toString().replace("[^0-9]*".toRegex(), "")
        return if (value.isEmpty()) 0.0 else java.lang.Double.valueOf(java.lang.Double.valueOf(value)!! / 100.0)

    }

    override fun enableComponent(enabled: Boolean) {
        isEnabled = enabled

    }

    override fun cleanComponent() {
        editText!!.setText("")
        resetState()

    }


    override fun getEditText(): TextInputEditText? {
        return editText
    }

    fun addTextChangedListener(watcher: TextWatcher) {
        editText!!.addTextChangedListener(watcher)

    }

    fun setSelection(start: Int, stop: Int) {
        editText!!.setSelection(start, stop)
    }

    //EditText methods
    fun setSelection(index: Int) {
        editText!!.setSelection(index)
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        editText!!.isEnabled = enabled
    }

    /**
     * @return the current typeface and style in which the text is being
     * displayed.
     * @attr ref android.R.styleable#TextView_fontFamily
     * @attr ref android.R.styleable#TextView_typeface
     * @attr ref android.R.styleable#TextView_textStyle
     * @see .setTypeface
     */
    override fun getTypeface(): Typeface? {
        return editText!!.typeface
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun hasOverlappingRendering(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && editText!!.hasOverlappingRendering()
    }

    /**
     * Return true iff there is a selection inside this text view.
     */
    fun hasSelection(): Boolean {
        return editText!!.hasSelection()
    }

    /**
     * Returns the length, in characters, of the text managed by this TextView
     */
    fun length(): Int {
        return editText!!.length()
    }


    /**
     * Removes the specified TextWatcher from the list of those whose
     * methods are called
     * whenever this TextView's text changes.
     */
    fun removeTextChangedListener(watcher: TextWatcher) {
        editText!!.removeTextChangedListener(watcher)
    }

    /**
     * Sets the properties of this field to transform input to ALL CAPS
     * display. This may use a "small caps" formatting if available.
     * This setting will be ignored if this field is editable or selectable.
     *
     *
     * This call replaces the current transformation method. Disabling this
     * will not necessarily restore the previous behavior from before this
     * was enabled.
     *
     * @attr ref android.R.styleable#TextView_textAllCaps
     * @see .setTransformationMethod
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    fun setAllCaps(allCaps: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            editText!!.isAllCaps = allCaps
    }

    /**
     * Sets the autolink mask of the text.  See [ ][android.text.util.Linkify.ALL] and peers for
     * possible values.
     *
     * @attr ref android.R.styleable#TextView_autoLink
     */
    fun setAutoLinkMask(mask: Int) {
        editText!!.autoLinkMask = mask
    }

    /**
     * If provided, this ActionMode.Callback will be used to create the ActionMode when text
     * selection is initiated in this View.
     *
     *
     * The standard implementation populates the menu with a subset of Select All, Cut, Copy and
     * Paste actions, depending on what this View supports.
     *
     *
     * A custom implementation can add new entries in the default menu in its
     * [ActionMode.Callback.onPrepareActionMode] method. The
     * default actions can also be removed from the menu using [android.view.Menu.removeItem] and
     * passing [android.R.id.selectAll], [android.R.id.cut], [android.R.id.copy]
     * or [android.R.id.paste] ids as parameters.
     *
     *
     * Returning false from
     * [ActionMode.Callback.onCreateActionMode] will prevent
     * the action mode from being started.
     *
     *
     * Action click events should be handled by the custom implementation of
     * [ActionMode.Callback.onActionItemClicked].
     *
     *
     * Note that text selection mode is not started when a TextView receives focus and the
     * [android.R.attr.selectAllOnFocus] flag has been set. The content is highlighted in
     * that case, to allow for quick replacement.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    fun setCustomSelectionActionModeCallback(actionModeCallback: ActionMode.Callback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            editText!!.customSelectionActionModeCallback = actionModeCallback
    }

    /**
     * Sets the Factory used to create new Editables.
     */
    fun setEditableFactory(factory: Editable.Factory) {
        editText!!.setEditableFactory(factory)
    }

    /**
     * Set the TextView's elegant height metrics flag. This setting selects font
     * variants that have not been compacted to fit Latin-based vertical
     * metrics, and also increases top and bottom bounds to provide more space.
     *
     * @param elegant set the paint's elegant metrics flag.
     * @attr ref android.R.styleable#TextView_elegantTextHeight
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun setElegantTextHeight(elegant: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            editText!!.isElegantTextHeight = elegant
    }

    /**
     * Makes the TextView exactly this many ems wide
     *
     * @attr ref android.R.styleable#TextView_ems
     * @see .setMaxEms
     * @see .setMinEms
     * @see .getMinEms
     * @see .getMaxEms
     */
    fun setEms(ems: Int) {
        editText!!.setEms(ems)
    }

    /**
     * If this TextView contains editable content, extract a portion of it
     * based on the information in <var>request</var> in to <var>outText</var>.
     *
     * @return Returns true if the text was successfully extracted, else false.
     */
    fun extractText(request: ExtractedTextRequest, outText: ExtractedText): Boolean {
        return editText!!.extractText(request, outText)
    }

    /**
     * Apply to this text view the given extracted text, as previously
     * returned by [.extractText].
     */
    fun setExtractedText(text: ExtractedText) {
        editText!!.setExtractedText(text)
    }

    /**
     * Sets the list of input filters that will be used if the buffer is
     * Editable. Has no effect otherwise.
     *
     * @attr ref android.R.styleable#TextView_maxLength
     */
    fun setFilters(filters: Array<InputFilter>) {
        editText!!.filters = filters
    }

    /**
     * Control whether this text view saves its entire text contents when
     * freezing to an icicle, in addition to dynamic state such as cursor
     * position.  By default this is false, not saving the text.  Set to true
     * if the text in the text view is not being saved somewhere else in
     * persistent storage (such as in a content provider) so that if the
     * view is later thawed the user will not lose their data.
     *
     * @param freezesText Controls whether a frozen icicle should include the
     * entire text data: true to include it, false to not.
     * @attr ref android.R.styleable#TextView_freezesText
     */
    fun setFreezesText(freezesText: Boolean) {
        editText!!.freezesText = freezesText
    }

    /**
     * Sets the horizontal alignment of the text and the
     * vertical gravity that will be used when there is extra space
     * in the TextView beyond what is required for the text itself.
     *
     * @attr ref android.R.styleable#TextView_gravity
     * @see Gravity
     */
    override fun setGravity(gravity: Int) {
        editText!!.gravity = gravity
    }

    /**
     * Sets the color used to display the selection highlight.
     *
     * @attr ref android.R.styleable#TextView_textColorHighlight
     */
    fun setHighlightColor(color: Int) {
        editText!!.highlightColor = color
    }

    /**
     * Sets the color of the hint text.
     *
     * @attr ref android.R.styleable#TextView_textColorHint
     * @see .getHintTextColors
     * @see .setHintTextColor
     * @see .setTextColor
     * @see .setLinkTextColor
     */
    fun setHintTextColor(colors: ColorStateList) {
        editText!!.setHintTextColor(colors)
    }

    /**
     * Sets the color of the hint text for all the states (disabled, focussed, selected...) of this
     * TextView.
     *
     * @attr ref android.R.styleable#TextView_textColorHint
     * @see .setHintTextColor
     * @see .getHintTextColors
     * @see .setTextColor
     */
    fun setHintTextColor(color: Int) {
        editText!!.setHintTextColor(color)
    }

    /**
     * Sets whether the text should be allowed to be wider than the
     * View is.  If false, it will be wrapped to the width of the View.
     *
     * @attr ref android.R.styleable#TextView_scrollHorizontally
     */
    fun setHorizontallyScrolling(whether: Boolean) {
        editText!!.setHorizontallyScrolling(whether)
    }

    /**
     * Change the custom IME action associated with the text view, which
     * will be reported to an IME with [EditorInfo.actionLabel]
     * and [EditorInfo.actionId] when it has focus.
     *
     * @attr ref android.R.styleable#TextView_imeActionLabel
     * @attr ref android.R.styleable#TextView_imeActionId
     * @see .getImeActionLabel
     *
     * @see .getImeActionId
     *
     * @see EditorInfo
     */
    fun setImeActionLabel(label: CharSequence, actionId: Int) {
        editText!!.setImeActionLabel(label, actionId)
    }

    /**
     * Set the extra input data of the text, which is the
     * [TextBoxAttribute.extras][EditorInfo.extras]
     * Bundle that will be filled in when creating an input connection.  The
     * given integer is the resource ID of an XML resource holding an
     *
     * @attr ref android.R.styleable#TextView_editorExtras
     * @see .getInputExtras
     * @see EditorInfo.extras
     */
    @Throws(XmlPullParserException::class, IOException::class)
    fun setInputExtras(xmlResId: Int) {
        editText!!.setInputExtras(xmlResId)
    }

    /**
     * Retrieve the input extras currently associated with the text view, which
     * can be viewed as well as modified.
     *
     * @param create If true, the extras will be created if they don't already
     * exist.  Otherwise, null will be returned if none have been created.
     * @attr ref android.R.styleable#TextView_editorExtras
     * @see .setInputExtras
     * @see EditorInfo.extras
     */
    fun getInputExtras(create: Boolean): Bundle {
        return editText!!.getInputExtras(create)
    }

    /**
     * Sets the key listener to be used with this TextView.  This can be null
     * to disallow user input.  Note that this method has significant and
     * subtle interactions with soft keyboards and other input method:
     * see [KeyListener.getContentType()][KeyListener.getInputType]
     * for important details.  Calling this method will replace the current
     * content type of the text view with the content type returned by the
     * key listener.
     *
     *
     * Be warned that if you want a TextView with a key listener or movement
     * method not to be focusable, or if you want a TextView without a
     * key listener or movement method to be focusable, you must call
     * [.setFocusable] again after calling this to get the focusability
     * back the way you want it.
     *
     * @attr ref android.R.styleable#TextView_numeric
     * @attr ref android.R.styleable#TextView_digits
     * @attr ref android.R.styleable#TextView_phoneNumber
     * @attr ref android.R.styleable#TextView_inputMethod
     * @attr ref android.R.styleable#TextView_capitalize
     * @attr ref android.R.styleable#TextView_autoText
     */
    fun setKeyListener(input: KeyListener) {
        editText!!.keyListener = input

    }

    /**
     * Sets line spacing for this TextView.  Each line will have its height
     * multiplied by `mult` and have `add` added to it.
     *
     * @attr ref android.R.styleable#TextView_lineSpacingExtra
     * @attr ref android.R.styleable#TextView_lineSpacingMultiplier
     */
    fun setLineSpacing(add: Float, mult: Float) {
        editText!!.setLineSpacing(add, mult)
    }

    /**
     * Makes the TextView exactly this many lines tall.
     *
     *
     * Note that setting this value overrides any other (minimum / maximum) number of lines or
     * height setting. A single line TextView will set this value to 1.
     *
     * @attr ref android.R.styleable#TextView_lines
     */
    fun setLines(lines: Int) {
        editText!!.setLines(lines)
    }

    /**
     * Sets the color of links in the text.
     *
     * @attr ref android.R.styleable#TextView_textColorLink
     * @see .setLinkTextColor
     * @see .getLinkTextColors
     * @see .setTextColor
     * @see .setHintTextColor
     */
    fun setLinkTextColor(colors: ColorStateList) {
        editText!!.setLinkTextColor(colors)
    }

    /**
     * Sets the color of links in the text.
     *
     * @attr ref android.R.styleable#TextView_textColorLink
     * @see .setLinkTextColor
     * @see .getLinkTextColors
     * @see .setTextColor
     * @see .setHintTextColor
     */
    fun setLinkTextColor(color: Int) {
        editText!!.setLinkTextColor(color)
    }

    /**
     * Sets whether the movement method will automatically be set to
     * [android.text.method.LinkMovementMethod] if [.setAutoLinkMask] has been
     * set to nonzero and links are detected in [.setText].
     * The default is true.
     *
     * @attr ref android.R.styleable#TextView_linksClickable
     */
    fun setLinksClickable(whether: Boolean) {
        editText!!.linksClickable = whether
    }

    /**
     * Makes the TextView at most this many pixels tall.  This option is mutually exclusive with the
     * [.setMaxLines] method.
     *
     *
     * Setting this value overrides any other (maximum) number of lines setting.
     *
     * @attr ref android.R.styleable#TextView_maxHeight
     */
    fun setMaxHeight(maxHeight: Int) {
        editText!!.maxHeight = maxHeight
    }

    /**
     * Makes the TextView at most this many lines tall.
     *
     *
     * Setting this value overrides any other (maximum) height setting.
     *
     * @attr ref android.R.styleable#TextView_maxLines
     */
    fun setMaxLines(maxlines: Int) {
        editText!!.maxLines = maxlines
    }

    /**
     * Makes the TextView at most this many pixels wide
     *
     * @attr ref android.R.styleable#TextView_maxWidth
     */
    fun setMaxWidth(maxpixels: Int) {
        editText!!.maxWidth = maxpixels
    }

    /**
     * Makes the TextView at least this many pixels tall.
     *
     *
     * Setting this value overrides any other (minimum) number of lines setting.
     *
     * @attr ref android.R.styleable#TextView_minHeight
     */
    fun setMinHeight(minHeight: Int) {
        editText!!.minHeight = minHeight
    }

    /**
     * Makes the TextView at least this many pixels wide
     *
     * @attr ref android.R.styleable#TextView_minWidth
     */
    fun setMinWidth(minpixels: Int) {
        editText!!.minWidth = minpixels
    }

    /**
     * Sets the movement method (arrow key handler) to be used for
     * this TextView.  This can be null to disallow using the arrow keys
     * to move the cursor or scroll the view.
     *
     *
     * Be warned that if you want a TextView with a key listener or movement
     * method not to be focusable, or if you want a TextView without a
     * key listener or movement method to be focusable, you must call
     * [.setFocusable] again after calling this to get the focusability
     * back the way you want it.
     */
    fun setMovementMethod(movement: MovementMethod) {
        editText!!.movementMethod = movement
    }

    /**
     * Set a special listener to be called when an action is performed
     * on the text view.  This will be called when the enter key is pressed,
     * or when an action supplied to the IME is selected by the user.  Setting
     * this means that the normal hard key event will not insert a newline
     * into the text view, even if it is multi-line; holding down the ALT
     * modifier will, however, allow the user to insert a newline character.
     */
    fun setOnEditorActionListener(l: TextView.OnEditorActionListener) {
        editText!!.setOnEditorActionListener(l)
    }

    /**
     * Register a callback to be invoked when a hardware key is pressed in this view.
     * Key presses in software input methods will generally not trigger the methods of
     * this listener.
     *
     * @param l the key listener to attach to this view
     */
    override fun setOnKeyListener(l: View.OnKeyListener) {
        editText!!.setOnKeyListener(l)
    }

    /**
     * Register a callback to be invoked when focus of this view changed.
     *
     * @param l The callback that will run.
     */
    override fun setOnFocusChangeListener(l: View.OnFocusChangeListener) {
        editText!!.onFocusChangeListener = l
    }

    /**
     * Sets the Factory used to create new Spannables.
     */
    fun setSpannableFactory(factory: Spannable.Factory) {
        editText!!.setSpannableFactory(factory)
    }

    fun setText(resid: Int) {
        editText!!.setText(resid)
    }

    fun setText(text: CharArray, start: Int, len: Int) {
        editText!!.setText(text, start, len)
    }

    fun setText(resid: Int, type: TextView.BufferType) {
        editText!!.setText(resid, type)
    }

    fun setText(text: CharSequence) {
        editText!!.setText(text)
    }


}
