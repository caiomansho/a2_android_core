package com.infoa2.core.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.*;
import android.text.method.KeyListener;
import android.text.method.MovementMethod;
import android.text.method.TransformationMethod;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.infoa2.core.R;
import com.infoa2.core.widget.enums.EditTextInputType;
import com.infoa2.core.widget.enums.RequiredTextType;
import com.infoa2.core.widget.interfaces.ComponentActions;
import com.infoa2.core.widget.interfaces.FieldValidator;
import com.infoa2.core.widget.interfaces.ValidatorInputType;
import com.infoa2.core.widget.textwatchers.TwoDecimalPointMaskTextWatcher;
import com.infoa2.core.widget.util.ConfigHelper;
import com.infoa2.core.widget.util.ViewUtil;
import com.infoa2.core.widget.validators.*;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by caio on 24/03/17.
 */

public class EditText extends TextInputLayout implements FieldValidator, ComponentActions {

    private int customErrorMessageId;
    private String customErrorMessage = null;
    private int relatedLabelId;
    private int fieldNotFilledErrorMessageId;
    private int inputId;
    private int relatedLabelTextColor;
    private TextInputEditText editText;
    private EditTextInputType editTextInputType;
    private TextView relatedLabelText;
    private Boolean isRequired = Boolean.FALSE;
    private String requiredText;
    private Boolean isPrefix;
    private Boolean mustBeGreaterThenZero;
    private Calendar dateText;
    private BigDecimal bigDecimal;
    private String nonRequiredText;
    private DatePickerDialog datePickerDialog;
    private ValidatorInputType validatorInputType;
    private RequiredTextType requiredTextType;

    public EditText(Context context) {
        super(context);
    }

    public EditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupAttribute(attrs);
    }

    public EditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupAttribute(attrs);
    }

    public boolean isEmpty(){
        return editText.getText().toString().isEmpty();

    }

    //Setup dos atributos do Layout xml
    private void setupAttribute(AttributeSet attrs) {

        TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.RSEditText,
                0, 0);

        try {
            customErrorMessageId = a.getResourceId(R.styleable.RSEditText_sou_etCustomErrorMessage, 0);
            if (customErrorMessageId == 0) {
                customErrorMessage = ConfigHelper.getStringConfigValue(getContext(), "etCustomErrorMessage");

            }
            if (a.hasValue(R.styleable.RSEditText_sou_etIsRequired)) {
                isRequired = a.getBoolean(R.styleable.RSEditText_sou_etIsRequired, false);

            } else {
                Boolean _isRequired = ConfigHelper.getBooleanConfigValue(getContext(), "etIsRequired");
                if (_isRequired != null) {
                    isRequired = _isRequired;
                } else {
                    isRequired = Boolean.FALSE;
                }
            }

            if(a.hasValue(R.styleable.RSEditText_sou_etRequiredTextType)){
                requiredTextType = RequiredTextType.values()[a.getInt(R.styleable.RSEditText_sou_etRequiredTextType, 0)];

            } else {
                Integer requiredTextTypeIndex = ConfigHelper.getIntConfigValue(getContext(), "etRequiredTextType");
                if (requiredTextTypeIndex != null) {
                    requiredTextType = RequiredTextType.values()[requiredTextTypeIndex];
                } else {
                    requiredTextType = RequiredTextType.REQUIRED;
                }
            }

            fieldNotFilledErrorMessageId = a.getResourceId(R.styleable.RSEditText_sou_etFieldNotFilledErrorMessage, 0);
            relatedLabelId = a.getResourceId(R.styleable.RSEditText_sou_etRelatedLabel, 0);
            inputId = a.getResourceId(R.styleable.RSEditText_sou_etInputId, 0);

            if (a.hasValue(R.styleable.RSEditText_sou_etRequiredText)) {
                requiredText = a.getString(R.styleable.RSEditText_sou_etRequiredText);
                if (requiredText == null || (requiredText != null && requiredText.isEmpty())) {
                    requiredText = "*";

                }

            } else {
                String _requiredText = ConfigHelper.getStringConfigValue(getContext(), "etRequiredText");
                if (_requiredText != null) {
                    requiredText = _requiredText;
                } else {
                    requiredText = "*";
                }
            }

            mustBeGreaterThenZero = a.getBoolean(R.styleable.RSEditText_sou_etMustBeGreaterThenZero, false);
            if (a.hasValue(R.styleable.RSEditText_sou_etIsPrefix)) {
                isPrefix = a.getBoolean(R.styleable.RSEditText_sou_etIsPrefix, true);

            } else {
                Boolean _isPrefix = ConfigHelper.getBooleanConfigValue(getContext(), "etIsPrefix");
                if (_isPrefix != null) {
                    isPrefix = _isPrefix;
                } else {
                    isPrefix = Boolean.TRUE;
                }
            }

            editText = new TextInputEditText(getContext(), attrs);

            if (inputId > 0) {
                editText.setId(inputId);

            } else {
                editText.setId(ViewUtil.generateViewId());

            }

            addView(editText, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            requestLayout();

            addEditTextInputType(EditTextInputType.values()[a.getInt(R.styleable.RSEditText_sou_etValidationType, EditTextInputType.NONE.getInputTypeCode())]);

        } finally {
            a.recycle();
        }
        editText.setHint(null);


    }

    public Boolean isCpf() {
        return CpfValidator.isCpf(editText);

    }

    public Boolean isCnpj() {
        return CnpjValidator.isCNPJ(editText);

    }

    public Boolean isZipCode() {
        return ZipCodeValidator.isZipCode(editText);

    }

    public Boolean isPhone() {
        return PhoneValidator.isPhone(editText);

    }

    public Boolean isMobile() {
        return PhoneValidator.isMobile(editText);

    }

    public Boolean isEmail() {
        return EmailValidator.isEmailValid(editText.getText().toString());

    }


    @Override
    public Boolean isRequired() {
        return isRequired;

    }

    @Override
    public void setIsRequired(Boolean isRequired) {
        this.isRequired = isRequired;
        setupTitleLabel();

    }

    @Override
    public Boolean isValid() {
        if(!isEnabled()){
            return Boolean.TRUE;

        }
        Boolean isValid = Boolean.TRUE;
        if (editText.getText()
                .toString().isEmpty()) {
            if (isRequired) {
                if (fieldNotFilledErrorMessageId > 0) {
                    editText.setError(getContext().getString(fieldNotFilledErrorMessageId));

                } else {
                    editText.setError(getContext().getString(R.string.sou_widgets_field_not_filled));

                }
                isValid = Boolean.FALSE;

            } else {
                return Boolean.TRUE;

            }
        }
        isValid = isValid && validatorInputType.isValid();


        if (this.relatedLabelText != null && relatedLabelTextColor != 0) {
            if (!isValid) {
                relatedLabelText.setTextColor(ContextCompat.getColor(getContext(), android.R.color.holo_red_dark));

            } else {
                relatedLabelText.setTextColor(Color.parseColor("#" + Integer.toHexString(relatedLabelTextColor).substring(2)));

            }
        }
        return isValid;

    }

    public void addEditTextInputType(EditTextInputType editTextInputType) {
        this.editTextInputType = editTextInputType;
        editTextInputType.setupEditText(getContext(), this);

    }

    public void resetState() {
        setError("");

        if (this.relatedLabelText != null && relatedLabelTextColor != 0) {
            relatedLabelText.setTextColor(Color.parseColor("#" + Integer.toHexString(relatedLabelTextColor).substring(2)));
        }

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (relatedLabelId > 0 && relatedLabelText == null) {
            relatedLabelText = (TextView) ((View) getParent()).findViewById(relatedLabelId);
            if(relatedLabelText == null){
                relatedLabelText = (TextView) ((View) getParent().getParent()).findViewById(relatedLabelId);
            }
        }

        if (relatedLabelText != null) {
            nonRequiredText = relatedLabelText.getText() != null && !relatedLabelText.getText().toString().isEmpty()
                    ? relatedLabelText.getText().toString()
                    : editTextInputType.getDefaultLabel(getContext());


        } else {
            nonRequiredText = getHint() != null && !getHint().toString().isEmpty()
                    ? getHint().toString()
                    : editTextInputType.getDefaultLabel(getContext());
        }

        setupTitleLabel();

    }

    @Override
    public void setError(@Nullable final CharSequence error) {
        super.setError(error);
        if (error == null || (error != null && error.length() == 0)) {
            editText.setError(null);
            if (this.relatedLabelText != null && relatedLabelTextColor != 0) {
                relatedLabelText.setTextColor(Color.parseColor("#" + Integer.toHexString(relatedLabelTextColor).substring(2)));
            }
        }
    }

    public Calendar getDateText() {
        return dateText;

    }

    @Override
    public void setOnClickListener(View.OnClickListener onClickListener) {
        editText.setOnClickListener(onClickListener);

    }

    public void setDateText(Calendar dateText) {
        this.dateText = dateText;
        if (dateText != null) {
            editText.setText(getFormatedDate(dateText));

        } else {
            editText.setText("");

        }
    }

    public BigDecimal getBigDecimal() {
        return bigDecimal;
    }

    public void setBigDecimal(BigDecimal bigDecimal) {
        this.bigDecimal = bigDecimal;
        if (bigDecimal != null) {
            if (editTextInputType == EditTextInputType.CURRENCY || editTextInputType == EditTextInputType.PERCENTAGE ||
                    editTextInputType == editTextInputType.TWO_DECIMAL_POINT) {
                bigDecimal = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);

            }
            editText.setText(bigDecimal.toString());

        } else {
            editText.setText("");

        }
    }

    public void setBigDecimal(BigDecimal bigDecimal, int scale) {
        this.bigDecimal = bigDecimal;
        if (bigDecimal != null) {
            bigDecimal = bigDecimal.setScale(scale, BigDecimal.ROUND_HALF_UP);
            editText.setText(bigDecimal.toString());

        } else {
            editText.setText("");

        }
    }

    public void setDouble(Double doubleValue) {
        if (doubleValue != null) {
            if (editTextInputType == EditTextInputType.CURRENCY || editTextInputType == EditTextInputType.PERCENTAGE ||
                    editTextInputType == editTextInputType.TWO_DECIMAL_POINT) {
                editText.setText(BigDecimal.valueOf(doubleValue)
                        .setScale(2, BigDecimal.ROUND_HALF_UP)
                        .toString());
            } else {
                editText.setText(doubleValue.toString());

            }

        } else {
            editText.setText("");

        }
    }


    public void setFloat(Float floatValue) {
        if (floatValue != null) {
            if (editTextInputType == EditTextInputType.CURRENCY || editTextInputType == EditTextInputType.PERCENTAGE ||
                    editTextInputType == editTextInputType.TWO_DECIMAL_POINT) {
                editText.setText(BigDecimal.valueOf(floatValue)
                        .setScale(2, BigDecimal.ROUND_HALF_UP)
                        .toString());
            } else {
                editText.setText(floatValue.toString());

            }

        } else {
            editText.setText("");

        }
    }

    public Float getFloat() {
        return TwoDecimalPointMaskTextWatcher.toFloat(editText.getText().toString());
    }


    public void setInt(Integer intValue) {
        if (intValue != null) {
            if (editTextInputType == EditTextInputType.CURRENCY || editTextInputType == EditTextInputType.PERCENTAGE) {
                editText.setText(BigDecimal.valueOf(intValue)
                        .setScale(2, BigDecimal.ROUND_HALF_UP)
                        .toString());
            } else {
                editText.setText(intValue.toString());

            }

        } else {
            editText.setText("");

        }
    }


    public void setDouble(Double doubleValue, int scale) {
        if (doubleValue != null) {
            editText.setText(BigDecimal.valueOf(doubleValue)
                    .setScale(scale, BigDecimal.ROUND_HALF_UP)
                    .toString());

        } else {
            editText.setText("");

        }
    }

    //retorna a data formatada em formato brasileiro
    private String getFormatedDate(Calendar data) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            return sdf.format(data.getTime());

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    private void setupTitleLabel() {

        if ((isRequired && requiredTextType == RequiredTextType.REQUIRED) || (!isRequired && requiredTextType == RequiredTextType.NOTREQUIRED)) {
            if (relatedLabelText != null) {
                relatedLabelTextColor = relatedLabelText.getCurrentTextColor();
                String title = relatedLabelText.getText() != null && !relatedLabelText.getText().toString().isEmpty()
                        ? relatedLabelText.getText().toString()
                        : editTextInputType.getDefaultLabel(getContext());

                title = title.replace(requiredText, "");
                if (isPrefix) {
                    relatedLabelText.setText(String.format("%s%s", requiredText, title));

                } else {
                    relatedLabelText.setText(String.format("%s%s", title, requiredText));

                }
                setHint("");

            } else {
                String hint = getHint() != null && !getHint().toString().isEmpty()
                        ? getHint().toString()
                        : editTextInputType.getDefaultLabel(getContext());
                hint = hint.replace(requiredText, "");
                if (isPrefix) {
                    setHint(String.format("%s%s", requiredText, hint));

                } else {
                    setHint(String.format("%s%s", hint, requiredText));

                }
            }
        } else {

            if (relatedLabelText != null) {
                relatedLabelText.setText(nonRequiredText);
                setHint("");

            } else {
                String hint = getHint() != null && !getHint().toString().isEmpty()
                        ? getHint().toString()
                        : editTextInputType.getDefaultLabel(getContext());
                hint = hint.replace(requiredText, "");
                setHint(hint);

            }

        }
    }

    public Float toFloat() throws NumberFormatException, Exception {
        String value = editText.getText().toString().replaceAll("[^0-9]*", "");
        return value.isEmpty() ? new Float(0.0F) : Float.valueOf(Float.valueOf(value).floatValue() / 100.0F);

    }

    public Double toDouble() throws NumberFormatException, Exception {
        String value = editText.getText().toString().replaceAll("[^0-9]*", "");
        return value.isEmpty() ? 0.0 : Double.valueOf(Double.valueOf(value) / 100.0);

    }

    @Override
    public void enableComponent(boolean enabled) {
        setEnabled(enabled);

    }

    @Override
    public void cleanComponent() {
        editText.setText("");
        resetState();

    }

    //getter setter
    public ValidatorInputType getValidatorInputType() {
        return validatorInputType;
    }

    public Boolean getMustBeGreaterThenZero() {
        return mustBeGreaterThenZero;
    }

    public void setValidatorInputType(ValidatorInputType validatorInputType) {
        this.validatorInputType = validatorInputType;

    }

    public DatePickerDialog getDatePickerDialog() {
        return datePickerDialog;
    }

    public void setDatePickerDialog(DatePickerDialog datePickerDialog) {
        this.datePickerDialog = datePickerDialog;
    }


    @Nullable
    @Override
    public TextInputEditText getEditText() {
        return editText;
    }

    public int getCustomErrorMessageId() {
        return customErrorMessageId;
    }

    //EditText methods
    public Editable getText() {
        return editText.getText();

    }

    public void addTextChangedListener(TextWatcher watcher) {
        editText.addTextChangedListener(watcher);

    }

    public void setSelection(int start, int stop) {
        editText.setSelection(start, stop);
    }

    //EditText methods
    public void setSelection(int index) {
        editText.setSelection(index);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        editText.setEnabled(enabled);
    }

    /**
     * @return the base paint used for the text.  Please use this only to
     * consult the Paint's properties and not to change them.
     */
    public TextPaint getPaint() {
        return editText.getPaint();
    }

    /**
     * @return the flags on the Paint being used to display the text.
     * @see android.graphics.Paint#getFlags
     */
    public int getPaintFlags() {
        return editText.getPaintFlags();
    }

    /**
     * Get the private type of the content.
     *
     * @see EditorInfo#privateImeOptions
     */
    public String getPrivateImeOptions() {
        return editText.getPrivateImeOptions();
    }

    /**
     * Convenience for {@link android.text.Selection#getSelectionEnd}.
     */
    public int getSelectionEnd() {
        return editText.getSelectionEnd();
    }

    /**
     * Convenience for {@link android.text.Selection#getSelectionStart}.
     */
    public int getSelectionStart() {
        return editText.getSelectionStart();
    }

    /**
     * @return the color of the shadow layer
     * @attr ref android.R.styleable#TextView_shadowColor
     * @see #setShadowLayer(float, float, float, int)
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public int getShadowColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            return editText.getShadowColor();

        return 0;
    }

    /**
     * @return the horizontal offset of the shadow layer
     * @attr ref android.R.styleable#TextView_shadowDx
     * @see #setShadowLayer(float, float, float, int)
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public float getShadowDx() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            return editText.getShadowDx();

        return 0;
    }

    /**
     * @return the vertical offset of the shadow layer
     * @attr ref android.R.styleable#TextView_shadowDy
     * @see #setShadowLayer(float, float, float, int)
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public float getShadowDy() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            return editText.getShadowDy();

        return 0;
    }


    /**
     * Returns whether the soft input method will be made visible when this
     * TextView gets focused. The default is true.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public final boolean getShowSoftInputOnFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return editText.getShowSoftInputOnFocus();
        return true;
    }

    /**
     * Gets the text colors for the different states (normal, selected, focused) of the TextView.
     *
     * @attr ref android.R.styleable#TextView_textColor
     * @see #setTextColor(ColorStateList)
     * @see #setTextColor(int)
     */
    public final ColorStateList getTextColors() {
        return editText.getTextColors();
    }

    /**
     * Get the default {@link Locale} of the text in this TextView.
     *
     * @return the default {@link Locale} of the text in this TextView.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public Locale getTextLocale() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            return editText.getTextLocale();

        return Locale.getDefault();
    }

    /**
     * @return the extent by which text is currently being stretched
     * horizontally.  This will usually be 1.
     */
    public float getTextScaleX() {
        return editText.getTextScaleX();
    }

    /**
     * @return the size (in pixels) of the default text size in this TextView.
     */
    public float getTextSize() {
        return editText.getTextSize();
    }

    /**
     * Returns the total bottom padding of the view, including the bottom
     * Drawable if any, the extra space to keep more than maxLines
     * from showing, and the vertical offset for gravity, if any.
     */
    public int getTotalPaddingBottom() {
        return getPaddingBottom() + editText.getTotalPaddingBottom();
    }

    /**
     * Returns the total end padding of the view, including the end
     * Drawable if any.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public int getTotalPaddingEnd() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            return getPaddingEnd() + editText.getTotalPaddingEnd();

        return getTotalPaddingRight();
    }

    /**
     * Returns the total left padding of the view, including the left
     * Drawable if any.
     */
    public int getTotalPaddingLeft() {
        return getPaddingLeft() + editText.getTotalPaddingLeft();
    }

    /**
     * Returns the total right padding of the view, including the right
     * Drawable if any.
     */
    public int getTotalPaddingRight() {
        return getPaddingRight() + editText.getTotalPaddingRight();
    }

    /**
     * Returns the total start padding of the view, including the start
     * Drawable if any.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public int getTotalPaddingStart() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            return getPaddingStart() + editText.getTotalPaddingStart();

        return getTotalPaddingLeft();
    }

    /**
     * Returns the total top padding of the view, including the top
     * Drawable if any, the extra space to keep more than maxLines
     * from showing, and the vertical offset for gravity, if any.
     */
    public int getTotalPaddingTop() {
        return getPaddingTop() + editText.getTotalPaddingTop();
    }

    /**
     * @return the current transformation method for this TextView.
     * This will frequently be null except for single-line and password
     * fields.
     * @attr ref android.R.styleable#TextView_password
     * @attr ref android.R.styleable#TextView_singleLine
     */
    public final TransformationMethod getTransformationMethod() {
        return editText.getTransformationMethod();
    }

    /**
     * @return the current typeface and style in which the text is being
     * displayed.
     * @attr ref android.R.styleable#TextView_fontFamily
     * @attr ref android.R.styleable#TextView_typeface
     * @attr ref android.R.styleable#TextView_textStyle
     * @see #setTypeface(Typeface)
     */
    public Typeface getTypeface() {
        return editText.getTypeface();
    }

    /**
     * Returns the list of URLSpans attached to the text
     * (by {@link android.text.util.Linkify} or otherwise) if any.  You can call
     * {@link URLSpan#getURL} on them to find where they link to
     * or use {@link android.text.Spanned#getSpanStart} and {@link android.text.Spanned#getSpanEnd}
     * to find the region of the text they are attached to.
     */
    public URLSpan[] getUrls() {
        return editText.getUrls();
    }

    @Override
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean hasOverlappingRendering() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && editText.hasOverlappingRendering();
    }

    /**
     * Return true iff there is a selection inside this text view.
     */
    public boolean hasSelection() {
        return editText.hasSelection();
    }

    /**
     * @return whether or not the cursor is visible (assuming this TextView is editable)
     * @attr ref android.R.styleable#TextView_cursorVisible
     * @see #setCursorVisible(boolean)
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean isCursorVisible() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN || editText.isCursorVisible();
    }

    /**
     * Returns whether this text view is a current input method target.  The
     * default implementation just checks with {@link android.view.inputmethod.InputMethodManager}.
     */
    public boolean isInputMethodTarget() {
        return editText.isInputMethodTarget();
    }

    /**
     * Return whether or not suggestions are enabled on this TextView. The suggestions are generated
     * by the IME or by the spell checker as the user types. This is done by adding
     * {@link android.text.style.SuggestionSpan}s to the text.
     * <p>
     * When suggestions are enabled (default), this list of suggestions will be displayed when the
     * user asks for them on these parts of the text. This value depends on the inputType of this
     * TextView.
     * <p>
     * The class of the input type must be {@link android.text.InputType#TYPE_CLASS_TEXT}.
     * <p>
     * In addition, the type variation must be one of
     * {@link android.text.InputType#TYPE_TEXT_VARIATION_NORMAL},
     * {@link android.text.InputType#TYPE_TEXT_VARIATION_EMAIL_SUBJECT},
     * {@link android.text.InputType#TYPE_TEXT_VARIATION_LONG_MESSAGE},
     * {@link android.text.InputType#TYPE_TEXT_VARIATION_SHORT_MESSAGE} or
     * {@link android.text.InputType#TYPE_TEXT_VARIATION_WEB_EDIT_TEXT}.
     * <p>
     * And finally, the {@link android.text.InputType#TYPE_TEXT_FLAG_NO_SUGGESTIONS} flag must <i>not</i> be set.
     *
     * @return true if the suggestions popup window is enabled, based on the inputType.
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public boolean isSuggestionsEnabled() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && editText.isSuggestionsEnabled();
    }

    /**
     * Returns the state of the {@code textIsSelectable} flag (See
     * {@link #setTextIsSelectable setTextIsSelectable()}). Although you have to set this flag
     * to allow users to select and copy text in a non-editable TextView, the content of an
     * {@link EditText} can always be selected, independently of the value of this flag.
     * <p>
     *
     * @return True if the text displayed in this TextView can be selected by the user.
     * @attr ref android.R.styleable#TextView_textIsSelectable
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public boolean isTextSelectable() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB || editText.isTextSelectable();
    }

    /**
     * Returns the length, in characters, of the text managed by this TextView
     */
    public int length() {
        return editText.length();
    }


    /**
     * Removes the specified TextWatcher from the list of those whose
     * methods are called
     * whenever this TextView's text changes.
     */
    public void removeTextChangedListener(TextWatcher watcher) {
        editText.removeTextChangedListener(watcher);
    }

    /**
     * Sets the properties of this field to transform input to ALL CAPS
     * display. This may use a "small caps" formatting if available.
     * This setting will be ignored if this field is editable or selectable.
     * <p>
     * This call replaces the current transformation method. Disabling this
     * will not necessarily restore the previous behavior from before this
     * was enabled.
     *
     * @attr ref android.R.styleable#TextView_textAllCaps
     * @see #setTransformationMethod(TransformationMethod)
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void setAllCaps(boolean allCaps) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            editText.setAllCaps(allCaps);
    }

    /**
     * Sets the autolink mask of the text.  See {@link
     * android.text.util.Linkify#ALL Linkify.ALL} and peers for
     * possible values.
     *
     * @attr ref android.R.styleable#TextView_autoLink
     */
    public final void setAutoLinkMask(int mask) {
        editText.setAutoLinkMask(mask);
    }

    /**
     * Set whether the cursor is visible. The default is true. Note that this property only
     * makes sense for editable TextView.
     *
     * @attr ref android.R.styleable#TextView_cursorVisible
     * @see #isCursorVisible()
     */
    public void setCursorVisible(boolean visible) {
        editText.setCursorVisible(visible);
    }

    /**
     * If provided, this ActionMode.Callback will be used to create the ActionMode when text
     * selection is initiated in this View.
     * <p>
     * The standard implementation populates the menu with a subset of Select All, Cut, Copy and
     * Paste actions, depending on what this View supports.
     * <p>
     * A custom implementation can add new entries in the default menu in its
     * {@link ActionMode.Callback#onPrepareActionMode(ActionMode, android.view.Menu)} method. The
     * default actions can also be removed from the menu using {@link android.view.Menu#removeItem(int)} and
     * passing {@link android.R.id#selectAll}, {@link android.R.id#cut}, {@link android.R.id#copy}
     * or {@link android.R.id#paste} ids as parameters.
     * <p>
     * Returning false from
     * {@link ActionMode.Callback#onCreateActionMode(ActionMode, android.view.Menu)} will prevent
     * the action mode from being started.
     * <p>
     * Action click events should be handled by the custom implementation of
     * {@link ActionMode.Callback#onActionItemClicked(ActionMode, android.view.MenuItem)}.
     * <p>
     * Note that text selection mode is not started when a TextView receives focus and the
     * {@link android.R.attr#selectAllOnFocus} flag has been set. The content is highlighted in
     * that case, to allow for quick replacement.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void setCustomSelectionActionModeCallback(ActionMode.Callback actionModeCallback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            editText.setCustomSelectionActionModeCallback(actionModeCallback);
    }

    /**
     * Sets the Factory used to create new Editables.
     */
    public final void setEditableFactory(Editable.Factory factory) {
        editText.setEditableFactory(factory);
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
    public void setElegantTextHeight(boolean elegant) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            editText.setElegantTextHeight(elegant);
    }

    /**
     * @return the minimum width of the TextView, expressed in ems or -1 if the minimum width
     * @attr ref android.R.styleable#TextView_minEms
     * @see #setMinEms(int)
     * @see #setEms(int)
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public int getMinEms() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            return editText.getMinEms();

        return -1;
    }

    /**
     * @return the maximum width of the TextView, expressed in ems or -1 if the maximum width
     * @attr ref android.R.styleable#TextView_maxEms
     * @see #setMaxEms(int)
     * @see #setEms(int)
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public int getMaxEms() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            return editText.getMaxEms();

        return -1;
    }

    /**
     * Makes the TextView exactly this many ems wide
     *
     * @attr ref android.R.styleable#TextView_ems
     * @see #setMaxEms(int)
     * @see #setMinEms(int)
     * @see #getMinEms()
     * @see #getMaxEms()
     */
    public void setEms(int ems) {
        editText.setEms(ems);
    }

    /**
     * If this TextView contains editable content, extract a portion of it
     * based on the information in <var>request</var> in to <var>outText</var>.
     *
     * @return Returns true if the text was successfully extracted, else false.
     */
    public boolean extractText(ExtractedTextRequest request, ExtractedText outText) {
        return editText.extractText(request, outText);
    }

    /**
     * Apply to this text view the given extracted text, as previously
     * returned by {@link #extractText(ExtractedTextRequest, ExtractedText)}.
     */
    public void setExtractedText(ExtractedText text) {
        editText.setExtractedText(text);
    }

    /**
     * Sets the list of input filters that will be used if the buffer is
     * Editable. Has no effect otherwise.
     *
     * @attr ref android.R.styleable#TextView_maxLength
     */
    public void setFilters(InputFilter[] filters) {
        editText.setFilters(filters);
    }

    /**
     * @return the currently set font feature settings.  Default is null.
     * @see #setFontFeatureSettings(String)
     * @see android.graphics.Paint#setFontFeatureSettings
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public String getFontFeatureSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return editText.getFontFeatureSettings();
        return null;
    }

    /**
     * Sets font feature settings.  The format is the same as the CSS
     * font-feature-settings attribute:
     * http://dev.w3.org/csswg/css-fonts/#propdef-font-feature-settings
     *
     * @param fontFeatureSettings font feature settings represented as CSS compatible string
     * @attr ref android.R.styleable#TextView_fontFeatureSettings
     * @see #getFontFeatureSettings()
     * @see android.graphics.Paint#getFontFeatureSettings
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setFontFeatureSettings(String fontFeatureSettings) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            editText.setFontFeatureSettings(fontFeatureSettings);
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
     *                    entire text data: true to include it, false to not.
     * @attr ref android.R.styleable#TextView_freezesText
     */
    public void setFreezesText(boolean freezesText) {
        editText.setFreezesText(freezesText);
    }

    /**
     * Sets the horizontal alignment of the text and the
     * vertical gravity that will be used when there is extra space
     * in the TextView beyond what is required for the text itself.
     *
     * @attr ref android.R.styleable#TextView_gravity
     * @see Gravity
     */
    public void setGravity(int gravity) {
        editText.setGravity(gravity);
    }

    /**
     * Sets the color used to display the selection highlight.
     *
     * @attr ref android.R.styleable#TextView_textColorHighlight
     */
    public void setHighlightColor(int color) {
        editText.setHighlightColor(color);
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
     * @see #setHintTextColor(ColorStateList)
     * @see #setHintTextColor(int)
     * @see #setTextColor(ColorStateList)
     * @see #setLinkTextColor(ColorStateList)
     */
    public final ColorStateList getHintTextColors() {
        return editText.getHintTextColors();
    }

    /**
     * Sets the color of the hint text.
     *
     * @attr ref android.R.styleable#TextView_textColorHint
     * @see #getHintTextColors()
     * @see #setHintTextColor(int)
     * @see #setTextColor(ColorStateList)
     * @see #setLinkTextColor(ColorStateList)
     */
    public final void setHintTextColor(ColorStateList colors) {
        editText.setHintTextColor(colors);
    }

    /**
     * Sets the color of the hint text for all the states (disabled, focussed, selected...) of this
     * TextView.
     *
     * @attr ref android.R.styleable#TextView_textColorHint
     * @see #setHintTextColor(ColorStateList)
     * @see #getHintTextColors()
     * @see #setTextColor(int)
     */
    public final void setHintTextColor(int color) {
        editText.setHintTextColor(color);
    }

    /**
     * Sets whether the text should be allowed to be wider than the
     * View is.  If false, it will be wrapped to the width of the View.
     *
     * @attr ref android.R.styleable#TextView_scrollHorizontally
     */
    public void setHorizontallyScrolling(boolean whether) {
        editText.setHorizontallyScrolling(whether);
    }

    /**
     * Get the IME action label previous set with {@link #setImeActionLabel}.
     *
     * @see #setImeActionLabel
     * @see EditorInfo
     */
    public CharSequence getImeActionLabel() {
        return editText.getImeActionLabel();
    }

    /**
     * Get the IME action ID previous set with {@link #setImeActionLabel}.
     *
     * @see #setImeActionLabel
     * @see EditorInfo
     */
    public int getImeActionId() {
        return editText.getImeActionId();
    }

    /**
     * Change the custom IME action associated with the text view, which
     * will be reported to an IME with {@link EditorInfo#actionLabel}
     * and {@link EditorInfo#actionId} when it has focus.
     *
     * @attr ref android.R.styleable#TextView_imeActionLabel
     * @attr ref android.R.styleable#TextView_imeActionId
     * @see #getImeActionLabel
     * @see #getImeActionId
     * @see EditorInfo
     */
    public void setImeActionLabel(CharSequence label, int actionId) {
        editText.setImeActionLabel(label, actionId);
    }

    /**
     * Change the editor type integer associated with the text view, which
     * will be reported to an IME with {@link EditorInfo#imeOptions} when it
     * has focus.
     *
     * @attr ref android.R.styleable#TextView_imeOptions
     * @see #getImeOptions
     * @see EditorInfo
     */

    public void setImeOptions(int imeOptions) {
        editText.setImeOptions(imeOptions);
    }

    /**
     * Get the type of the IME editor.
     *
     * @see #setImeOptions(int)
     * @see EditorInfo
     */
    public int getImeOptions() {
        return editText.getImeOptions();
    }

    /**
     * Set whether the TextView includes extra top and bottom padding to make
     * room for accents that go above the normal ascent and descent.
     * The default is true.
     *
     * @attr ref android.R.styleable#TextView_includeFontPadding
     * @see #getIncludeFontPadding()
     */
    public void setIncludeFontPadding(boolean includepad) {
        editText.setIncludeFontPadding(includepad);
    }

    /**
     * Gets whether the TextView includes extra top and bottom padding to make
     * room for accents that go above the normal ascent and descent.
     *
     * @attr ref android.R.styleable#TextView_includeFontPadding
     * @see #setIncludeFontPadding(boolean)
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean getIncludeFontPadding() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && editText.getIncludeFontPadding();
    }

    /**
     * Set the extra input data of the text, which is the
     * {@link EditorInfo#extras TextBoxAttribute.extras}
     * Bundle that will be filled in when creating an input connection.  The
     * given integer is the resource ID of an XML resource holding an
     *
     * @attr ref android.R.styleable#TextView_editorExtras
     * @see #getInputExtras(boolean)
     * @see EditorInfo#extras
     */
    public void setInputExtras(int xmlResId) throws XmlPullParserException, IOException {
        editText.setInputExtras(xmlResId);
    }

    /**
     * Retrieve the input extras currently associated with the text view, which
     * can be viewed as well as modified.
     *
     * @param create If true, the extras will be created if they don't already
     *               exist.  Otherwise, null will be returned if none have been created.
     * @attr ref android.R.styleable#TextView_editorExtras
     * @see #setInputExtras(int)
     * @see EditorInfo#extras
     */
    public Bundle getInputExtras(boolean create) {
        return editText.getInputExtras(create);
    }

    /**
     * Set the type of the content with a constant as defined for {@link EditorInfo#inputType}. This
     * will take care of changing the key listener, by calling {@link #setKeyListener(KeyListener)},
     * to match the given content type.  If the given content type is {@link EditorInfo#TYPE_NULL}
     * then a soft keyboard will not be displayed for this text view.
     * <p>
     * Note that the maximum number of displayed lines (see {@link #setMaxLines(int)}) will be
     * modified if you change the {@link EditorInfo#TYPE_TEXT_FLAG_MULTI_LINE} flag of the input
     * type.
     *
     * @attr ref android.R.styleable#TextView_inputType
     * @see #getInputType()
     * @see #setRawInputType(int)
     * @see android.text.InputType
     */
    public void setInputType(int type) {
        editText.setInputType(type);
    }

    /**
     * Get the type of the editable content.
     *
     * @see #setInputType(int)
     * @see android.text.InputType
     */
    public int getInputType() {
        return editText.getInputType();
    }

    /**
     * Sets the key listener to be used with this TextView.  This can be null
     * to disallow user input.  Note that this method has significant and
     * subtle interactions with soft keyboards and other input method:
     * see {@link KeyListener#getInputType() KeyListener.getContentType()}
     * for important details.  Calling this method will replace the current
     * content type of the text view with the content type returned by the
     * key listener.
     * <p>
     * Be warned that if you want a TextView with a key listener or movement
     * method not to be focusable, or if you want a TextView without a
     * key listener or movement method to be focusable, you must call
     * {@link #setFocusable} again after calling this to get the focusability
     * back the way you want it.
     *
     * @attr ref android.R.styleable#TextView_numeric
     * @attr ref android.R.styleable#TextView_digits
     * @attr ref android.R.styleable#TextView_phoneNumber
     * @attr ref android.R.styleable#TextView_inputMethod
     * @attr ref android.R.styleable#TextView_capitalize
     * @attr ref android.R.styleable#TextView_autoText
     */
    public void setKeyListener(KeyListener input) {
        editText.setKeyListener(input);

    }

    /**
     * @return the extent by which text is currently being letter-spaced.
     * This will normally be 0.
     * @see #setLetterSpacing(float)
     * @see android.graphics.Paint#setLetterSpacing
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public float getLetterSpacing() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? editText.getLetterSpacing() : 0;
    }

    /**
     * Sets text letter-spacing.  The value is in 'EM' units.  Typical values
     * for slight expansion will be around 0.05.  Negative values tighten text.
     *
     * @attr ref android.R.styleable#TextView_letterSpacing
     * @see #getLetterSpacing()
     * @see android.graphics.Paint#getLetterSpacing
     */
    public void setLetterSpacing(float letterSpacing) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            editText.setLetterSpacing(letterSpacing);
    }

    /**
     * Sets line spacing for this TextView.  Each line will have its height
     * multiplied by <code>mult</code> and have <code>add</code> added to it.
     *
     * @attr ref android.R.styleable#TextView_lineSpacingExtra
     * @attr ref android.R.styleable#TextView_lineSpacingMultiplier
     */
    public void setLineSpacing(float add, float mult) {
        editText.setLineSpacing(add, mult);
    }

    /**
     * Makes the TextView exactly this many lines tall.
     * <p>
     * Note that setting this value overrides any other (minimum / maximum) number of lines or
     * height setting. A single line TextView will set this value to 1.
     *
     * @attr ref android.R.styleable#TextView_lines
     */
    public void setLines(int lines) {
        editText.setLines(lines);
    }

    /**
     * Sets the color of links in the text.
     *
     * @attr ref android.R.styleable#TextView_textColorLink
     * @see #setLinkTextColor(int)
     * @see #getLinkTextColors()
     * @see #setTextColor(ColorStateList)
     * @see #setHintTextColor(ColorStateList)
     */
    public final void setLinkTextColor(ColorStateList colors) {
        editText.setLinkTextColor(colors);
    }

    /**
     * @return the list of colors used to paint the links in the text, for the different states of
     * this TextView
     * @attr ref android.R.styleable#TextView_textColorLink
     * @see #setLinkTextColor(ColorStateList)
     * @see #setLinkTextColor(int)
     */
    public final ColorStateList getLinkTextColors() {
        return editText.getLinkTextColors();
    }

    /**
     * Sets the color of links in the text.
     *
     * @attr ref android.R.styleable#TextView_textColorLink
     * @see #setLinkTextColor(int)
     * @see #getLinkTextColors()
     * @see #setTextColor(ColorStateList)
     * @see #setHintTextColor(ColorStateList)
     */
    public final void setLinkTextColor(int color) {
        editText.setLinkTextColor(color);
    }

    /**
     * Sets whether the movement method will automatically be set to
     * {@link android.text.method.LinkMovementMethod} if {@link #setAutoLinkMask} has been
     * set to nonzero and links are detected in {@link #setText}.
     * The default is true.
     *
     * @attr ref android.R.styleable#TextView_linksClickable
     */
    public final void setLinksClickable(boolean whether) {
        editText.setLinksClickable(whether);
    }

    /**
     * Sets how many times to repeat the marquee animation. Only applied if the
     * TextView has marquee enabled. Set to -1 to repeat indefinitely.
     *
     * @attr ref android.R.styleable#TextView_marqueeRepeatLimit
     * @see #getMarqueeRepeatLimit()
     */
    public void setMarqueeRepeatLimit(int marqueeLimit) {
        editText.setMarqueeRepeatLimit(marqueeLimit);
    }

    /**
     * Gets the number of times the marquee animation is repeated. Only meaningful if the
     * TextView has marquee enabled.
     *
     * @return the number of times the marquee animation is repeated. -1 if the animation
     * repeats indefinitely
     * @attr ref android.R.styleable#TextView_marqueeRepeatLimit
     * @see #setMarqueeRepeatLimit(int)
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public int getMarqueeRepeatLimit() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            return editText.getMarqueeRepeatLimit();

        return -1;
    }

    /**
     * Makes the TextView at most this many ems wide
     *
     * @attr ref android.R.styleable#TextView_maxEms
     */
    public void setMaxEms(int maxems) {
        editText.setMaxEms(maxems);
    }

    /**
     * Makes the TextView at most this many pixels tall.  This option is mutually exclusive with the
     * {@link #setMaxLines(int)} method.
     * <p>
     * Setting this value overrides any other (maximum) number of lines setting.
     *
     * @attr ref android.R.styleable#TextView_maxHeight
     */
    public void setMaxHeight(int maxHeight) {
        editText.setMaxHeight(maxHeight);
    }

    /**
     * Makes the TextView at most this many lines tall.
     * <p>
     * Setting this value overrides any other (maximum) height setting.
     *
     * @attr ref android.R.styleable#TextView_maxLines
     */
    public void setMaxLines(int maxlines) {
        editText.setMaxLines(maxlines);
    }

    /**
     * Makes the TextView at most this many pixels wide
     *
     * @attr ref android.R.styleable#TextView_maxWidth
     */
    public void setMaxWidth(int maxpixels) {
        editText.setMaxWidth(maxpixels);
    }

    /**
     * Makes the TextView at least this many ems wide
     *
     * @attr ref android.R.styleable#TextView_minEms
     */
    public void setMinEms(int minems) {
        editText.setMinEms(minems);
    }

    /**
     * Makes the TextView at least this many pixels tall.
     * <p>
     * Setting this value overrides any other (minimum) number of lines setting.
     *
     * @attr ref android.R.styleable#TextView_minHeight
     */
    public void setMinHeight(int minHeight) {
        editText.setMinHeight(minHeight);
    }

    /**
     * Makes the TextView at least this many lines tall.
     * <p>
     * Setting this value overrides any other (minimum) height setting. A single line TextView will
     * set this value to 1.
     *
     * @attr ref android.R.styleable#TextView_minLines
     * @see #getMinLines()
     */
    public void setMinLines(int minlines) {
        editText.setMinLines(minlines);
    }

    /**
     * @return the minimum number of lines displayed in this TextView, or -1 if the minimum
     * height was set in pixels instead using {@link #setMinHeight(int) or #setDividerHeight(int)}.
     * @attr ref android.R.styleable#TextView_minLines
     * @see #setMinLines(int)
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public int getMinLines() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            return editText.getMinLines();

        return -1;
    }

    /**
     * Makes the TextView at least this many pixels wide
     *
     * @attr ref android.R.styleable#TextView_minWidth
     */
    public void setMinWidth(int minpixels) {
        editText.setMinWidth(minpixels);
    }

    /**
     * Sets the movement method (arrow key handler) to be used for
     * this TextView.  This can be null to disallow using the arrow keys
     * to move the cursor or scroll the view.
     * <p>
     * Be warned that if you want a TextView with a key listener or movement
     * method not to be focusable, or if you want a TextView without a
     * key listener or movement method to be focusable, you must call
     * {@link #setFocusable} again after calling this to get the focusability
     * back the way you want it.
     */
    public final void setMovementMethod(MovementMethod movement) {
        editText.setMovementMethod(movement);
    }

    /**
     * Set a special listener to be called when an action is performed
     * on the text view.  This will be called when the enter key is pressed,
     * or when an action supplied to the IME is selected by the user.  Setting
     * this means that the normal hard key event will not insert a newline
     * into the text view, even if it is multi-line; holding down the ALT
     * modifier will, however, allow the user to insert a newline character.
     */
    public void setOnEditorActionListener(TextView.OnEditorActionListener l) {
        editText.setOnEditorActionListener(l);
    }

    /**
     * Register a callback to be invoked when a hardware key is pressed in this view.
     * Key presses in software input methods will generally not trigger the methods of
     * this listener.
     *
     * @param l the key listener to attach to this view
     */
    @Override
    public void setOnKeyListener(View.OnKeyListener l) {
        editText.setOnKeyListener(l);
    }

    /**
     * Register a callback to be invoked when focus of this view changed.
     *
     * @param l The callback that will run.
     */
    @Override
    public void setOnFocusChangeListener(View.OnFocusChangeListener l) {
        editText.setOnFocusChangeListener(l);
    }

    /**
     * Sets the Factory used to create new Spannables.
     */
    public final void setSpannableFactory(Spannable.Factory factory) {
        editText.setSpannableFactory(factory);
    }

    public final void setText(int resid) {
        editText.setText(resid);
    }

    public final void setText(char[] text, int start, int len) {
        editText.setText(text, start, len);
    }

    public final void setText(int resid, TextView.BufferType type) {
        editText.setText(resid, type);
    }

    public final void setText(CharSequence text) {
        editText.setText(text);
    }


}
