package com.infoa2.core.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.core.content.ContextCompat;
import androidx.customview.view.AbsSavedState;
import com.google.android.material.textfield.TextInputLayout;
import com.infoa2.core.R;
import com.infoa2.core.widget.enums.RequiredTextType;
import com.infoa2.core.widget.interfaces.ComponentActions;
import com.infoa2.core.widget.interfaces.FieldValidator;
import com.infoa2.core.widget.interfaces.ValidatorInputType;
import com.infoa2.core.widget.util.ConfigHelper;
import com.infoa2.core.widget.util.ViewUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caio on 31/03/17.
 */

public class RadioGroup extends TextInputLayout implements FieldValidator, ComponentActions {

    private int customErrorMessageId;
    private String customErrorMessage = null;
    private int relatedLabelId = 0;
    private int inputId = 0;
    private int relatedLabelTextColor;
    private Integer minRadioWidthLength;
    private TextView relatedLabelText;
    private Boolean isRequired = Boolean.FALSE;
    private String requiredText;
    private Boolean isPrefix;
    private Boolean rgEqualWeight = Boolean.FALSE;
    private int rgDefaultSelectedIndex;
    private android.widget.RadioGroup radioGroup;
    private int checkedIndex = -1;
    private List<AppCompatRadioButton> radioButtons = new ArrayList<AppCompatRadioButton>();
    private String nonRequiredText;
    private int textColor;
    private AttributeSet attrs;

    private ValidatorInputType validatorInputType;
    private RequiredTextType requiredTextType;

    public RadioGroup(Context context) {
        super(context);
    }

    public RadioGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupAttribute(attrs);
    }

    public RadioGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupAttribute(attrs);
    }

    //Setup dos atributos do Layout xml
    private void setupAttribute(AttributeSet attrs) {
        int[] attrsArray = new int[]{
                android.R.attr.id // 0
        };
        this.attrs = attrs;

        TypedArray ta = getContext().obtainStyledAttributes(attrs, attrsArray);
        int id = ta.getResourceId(0 /* index of attribute in attrsArray */, View.NO_ID);
        ta.recycle();
        setId(id);
        setLayoutParams(new ViewGroup.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.RSRadioGroup,
                0, 0);


        try {
            customErrorMessageId = a.getResourceId(R.styleable.RSRadioGroup_sou_rgCustomErrorMessage, 0);
            if (customErrorMessageId == 0) {
                customErrorMessage = ConfigHelper.getStringConfigValue(getContext(), "customErrorMessage");

            }

            if (a.hasValue(R.styleable.RSRadioGroup_sou_rgDefaultSelectedIndex)) {
                rgDefaultSelectedIndex = a.getInt(R.styleable.RSRadioGroup_sou_rgDefaultSelectedIndex, -1);

            } else {
                Integer _rgDefaultSelectedIndex = ConfigHelper.getIntConfigValue(getContext(), "rgDefaultSelectedIndex");
                if (_rgDefaultSelectedIndex != null) {
                    rgDefaultSelectedIndex = _rgDefaultSelectedIndex;
                } else {
                    rgDefaultSelectedIndex = -1;
                }

            }

            textColor = a.getResourceId(R.styleable.RSRadioGroup_sou_rgTextColor, 0);


            if (a.hasValue(R.styleable.RSRadioGroup_sou_rgMinRadioWidth)) {
                minRadioWidthLength = a.getDimensionPixelSize(R.styleable.RSRadioGroup_sou_rgMinRadioWidth, 0);
            }

            if (a.hasValue(R.styleable.RSRadioGroup_sou_rgIsRequired)) {
                isRequired = a.getBoolean(R.styleable.RSRadioGroup_sou_rgIsRequired, false);

            } else {
                Boolean _isRequired = ConfigHelper.getBooleanConfigValue(getContext(), "rgIsRequired");
                if (_isRequired != null) {
                    isRequired = _isRequired;
                } else {
                    isRequired = Boolean.FALSE;
                }
            }

            if (a.hasValue(R.styleable.RSRadioGroup_sou_rgRequiredTextType)) {
                requiredTextType = RequiredTextType.values()[a.getInt(R.styleable.RSRadioGroup_sou_rgRequiredTextType, 0)];

            } else {
                Integer requiredTextTypeIndex = ConfigHelper.getIntConfigValue(getContext(), "rgRequiredTextType");
                if (requiredTextTypeIndex != null) {
                    requiredTextType = RequiredTextType.values()[requiredTextTypeIndex];
                } else {
                    requiredTextType = RequiredTextType.REQUIRED;
                }
            }

            rgEqualWeight = a.getBoolean(R.styleable.RSRadioGroup_sou_rgEqualWeight, false);

            validatorInputType = new ValidatorInputType() {
                @Override
                public Boolean isValid() {
                    if (isRequired) {
                        return getCheckedIndex() > -1;

                    }
                    return Boolean.TRUE;

                }
            };

            relatedLabelId = a.getResourceId(R.styleable.RSRadioGroup_sou_rgRelatedLabel, 0);
            inputId = a.getResourceId(R.styleable.RSRadioGroup_sou_rgInputId, 0);

            if (a.hasValue(R.styleable.RSRadioGroup_sou_rgRequiredText)) {
                requiredText = a.getString(R.styleable.RSRadioGroup_sou_rgRequiredText);
                if (requiredText == null || (requiredText != null && requiredText.isEmpty())) {
                    requiredText = "*";

                }

            } else {
                String _requiredText = ConfigHelper.getStringConfigValue(getContext(), "rgRequiredText");
                if (_requiredText != null) {
                    requiredText = _requiredText;
                } else {
                    requiredText = "*";
                }
            }

            if (a.hasValue(R.styleable.RSRadioGroup_sou_rgIsPrefix)) {
                isPrefix = a.getBoolean(R.styleable.RSRadioGroup_sou_rgIsPrefix, true);

            } else {
                Boolean _isPrefix = ConfigHelper.getBooleanConfigValue(getContext(), "isPrefix");
                if (_isPrefix != null) {
                    isPrefix = _isPrefix;
                } else {
                    isPrefix = Boolean.TRUE;
                }
            }
            radioGroup = new android.widget.RadioGroup(getContext(), attrs);

            if (inputId > 0) {
                radioGroup.setId(inputId);

            } else {
                radioGroup.setId(ViewUtil.generateViewId());

            }
            radioGroup.setOnCheckedChangeListener(new android.widget.RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(android.widget.RadioGroup group, int checkedId) {
                    isValid();
                }
            });

            addView(radioGroup, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));


        } finally {
            a.recycle();
        }

        requestLayout();

    }

    public boolean isChecked() {
        if (radioButtons == null) {
            return false;
        }
        for (AppCompatRadioButton radioButton : radioButtons) {
            if (radioButton.isChecked()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Boolean isRequired() {
        return isRequired;

    }

    @Override
    public Boolean isValid() {
        if (!isEnabled()) {
            return Boolean.TRUE;
        }


        Boolean isValid = Boolean.TRUE;
        if (validatorInputType != null) {
            isValid = validatorInputType.isValid();

        }

        if (this.relatedLabelText != null && relatedLabelTextColor != 0) {
            if (!isValid) {
                relatedLabelText.setTextColor(ContextCompat.getColor(getContext(), android.R.color.holo_red_dark));

            } else {
                relatedLabelText.setTextColor(relatedLabelTextColor);

            }
        }
        return isValid;

    }

    @Override
    public void setIsRequired(Boolean isRequired) {
        this.isRequired = isRequired;
        setupTitleLabel();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (relatedLabelId > 0 && relatedLabelText == null) {
            relatedLabelText = (TextView) ((View) getParent()).findViewById(relatedLabelId);
            if (relatedLabelText != null) {
                relatedLabelTextColor = relatedLabelText.getCurrentTextColor();
            }
        }

        if (relatedLabelText != null) {
            nonRequiredText = relatedLabelText.getText() != null && !relatedLabelText.getText().toString().isEmpty()
                    ? relatedLabelText.getText().toString() : "";


        } else {
            nonRequiredText = getHint() != null && !getHint().toString().isEmpty()
                    ? getHint().toString() : "";
        }

        setupTitleLabel();

    }

    private void setupTitleLabel() {

        if ((isRequired && requiredTextType == RequiredTextType.REQUIRED) || (!isRequired && requiredTextType == RequiredTextType.NOTREQUIRED)) {
            if (relatedLabelText != null) {
                String title = relatedLabelText.getText() != null && !relatedLabelText.getText().toString().isEmpty()
                        ? relatedLabelText.getText().toString()
                        : "";
                title = title.replace("*", "");
                if (isPrefix) {
                    relatedLabelText.setText(String.format("%s%s", requiredText, title));

                } else {
                    relatedLabelText.setText(String.format("%s%s", title, requiredText));

                }

            } else {

                String hint = getHint() != null && !getHint().toString().isEmpty()
                        ? getHint().toString()
                        : "";
                hint = hint.replace("*", "");
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
                setHint(nonRequiredText);

            }

        }
    }


    @Override
    public void enableComponent(boolean enabled) {
        setEnabled(enabled);

    }

    @Override
    public void cleanComponent() {
        clearCheck();
        resetState();

    }

    public void resetState() {
        setError("");

        if (this.relatedLabelText != null && relatedLabelTextColor != 0) {
            relatedLabelText.setTextColor(Color.parseColor("#" + Integer.toHexString(relatedLabelTextColor).substring(2)));
        }

    }

    public <T> void setupRadioButtons(List<T> items) {
        radioButtons = new ArrayList<AppCompatRadioButton>();
        AppCompatRadioButton radioButton;

        for (T t : items) {
            if (rgEqualWeight) {
                radioButton = new AppCompatRadioButton(getContext(), attrs);
                if (radioGroup.getOrientation() == RadioGroup.HORIZONTAL) {
                    radioButton.setLayoutParams(new android.widget.RadioGroup.LayoutParams(
                            0, android.widget.RadioGroup.LayoutParams.MATCH_PARENT, 1));
                } else {
                    radioButton.setLayoutParams(new android.widget.RadioGroup.LayoutParams(
                            android.widget.RadioGroup.LayoutParams.MATCH_PARENT, 0, 1));
                }

            } else {
                radioButton = new AppCompatRadioButton(getContext());
                if (minRadioWidthLength != null) {
                    radioButton.setLayoutParams(new RadioGroup.LayoutParams(minRadioWidthLength, RadioGroup.LayoutParams.MATCH_PARENT));

                }

            }
            if (textColor != 0) {
                radioButton.setTextColor(ContextCompat.getColor(getContext(), textColor));
            }
            radioButtons.add(radioButton);
            radioButton.setText(t.toString());
            radioGroup.addView(radioButton);

        }
        if (checkedIndex > -1) {
            checkByIndex(checkedIndex);

        } else if (rgDefaultSelectedIndex > -1) {
            checkByIndex(rgDefaultSelectedIndex);
        }
    }


    public <T> void setupRadioButtons(T... items) {
        radioButtons = new ArrayList<AppCompatRadioButton>();
        AppCompatRadioButton radioButton;
        for (T t : items) {
            if (rgEqualWeight) {
                radioButton = new AppCompatRadioButton(getContext(), attrs);
                if (radioGroup.getOrientation() == RadioGroup.HORIZONTAL) {
                    radioButton.setLayoutParams(new android.widget.RadioGroup.LayoutParams(
                            0, android.widget.RadioGroup.LayoutParams.MATCH_PARENT, 1));
                } else {
                    radioButton.setLayoutParams(new android.widget.RadioGroup.LayoutParams(
                            android.widget.RadioGroup.LayoutParams.MATCH_PARENT, 0, 1));
                }

            } else {
                radioButton = new AppCompatRadioButton(getContext());
                if (minRadioWidthLength != null) {
                    radioButton.setLayoutParams(new RadioGroup.LayoutParams(minRadioWidthLength, RadioGroup.LayoutParams.MATCH_PARENT));

                }

            }
            if (textColor != 0) {
                radioButton.setTextColor(ContextCompat.getColor(getContext(), textColor));
            }
            radioButtons.add(radioButton);
            radioButton.setText(t.toString());
            radioGroup.addView(radioButton);

        }
        if (checkedIndex > -1) {
            checkByIndex(checkedIndex);

        } else if (rgDefaultSelectedIndex > -1) {
            checkByIndex(rgDefaultSelectedIndex);
        }
    }


    static class SavedState extends AbsSavedState {
        CharSequence error;
        Integer index;

        SavedState(Parcelable superState) {
            super(superState);
        }

        SavedState(Parcel source, ClassLoader loader) {
            super(source, loader);
            error = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(source);
        }

        @Override
        public  void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            TextUtils.writeToParcel(error, dest, flags);
        }

        @Override
        public String toString() {
            return "TextInputLayout.SavedState{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " error="
                    + error
                    + "}";
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.ClassLoaderCreator<SavedState>() {
                    @Override
                    public  SavedState createFromParcel(Parcel in, ClassLoader loader) {
                        return new SavedState(in, loader);
                    }

                    @Override
                    public  SavedState createFromParcel(Parcel in) {
                        return new SavedState(in, null);
                    }

                    @Override
                    public  SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        RadioGroup.SavedState ss = new RadioGroup.SavedState(superState);
        ss.index = getCheckedIndex();
        return ss;

    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        RadioGroup.SavedState ss = (RadioGroup.SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        if(ss.index != null) {
            checkedIndex = ss.index;
        }
        //checkByIndex(ss.index);

    }

    public boolean isChecked(int index) throws IndexOutOfBoundsException {
        if (radioButtons != null) {
            return radioButtons.get(index).isChecked();
        }
        return false;
    }


    public void checkByIndex(int index) {
        if (index < radioButtons.size() && index >= 0) {
            RadioButton radioButton = radioButtons.get(index);
            radioGroup.check(radioButton.getId());
        }
    }

    public int getCheckedRadioButtonId() {
        return radioGroup.getCheckedRadioButtonId();

    }

    public int getCheckedIndex() {
        int index = -1;
        for (int x = 0; x < radioButtons.size(); x++) {
            if (radioButtons.get(x).isChecked()) {
                index = x;
            }

        }
        return index;

    }

    public void setOnCheckedChangeListener(final android.widget.RadioGroup.OnCheckedChangeListener onCheckedChangeListener) {
        radioGroup.setOnCheckedChangeListener(new android.widget.RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(android.widget.RadioGroup group, int checkedId) {
                isValid();
                onCheckedChangeListener.onCheckedChanged(group, checkedId);
            }
        });
    }

    public void clearCheck() {
        if (radioGroup != null) {
            radioGroup.clearCheck();
        }

    }

    public void check(int id) {
        radioGroup.check(id);
    }

    public List<AppCompatRadioButton> getRadioButtons() {
        return radioButtons;
    }


}
