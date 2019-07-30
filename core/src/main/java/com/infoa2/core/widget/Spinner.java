package com.infoa2.core.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import com.infoa2.core.R;
import com.infoa2.core.widget.enums.RequiredTextType;
import com.infoa2.core.widget.interfaces.ComponentActions;
import com.infoa2.core.widget.interfaces.FieldValidator;
import com.google.android.material.textfield.TextInputLayout;
import com.infoa2.core.widget.interfaces.ValidatorInputType;
import com.infoa2.core.widget.util.ConfigHelper;
import com.infoa2.core.widget.util.ViewUtil;

import java.util.List;

/**
 * Created by caio on 30/03/17.
 */

public class Spinner extends TextInputLayout
        implements FieldValidator, ComponentActions {

    private int customErrorMessageId;
    private String customErrorMessage = null;
    private int relatedLabelId = 0;
    private int inputId;
    private int relatedLabelTextColor;
    //    private int styleId;
    private TextView relatedLabelText;
    private Boolean isRequired = Boolean.FALSE;
    private String requiredText;
    private Boolean isPrefix;
    private int defaultSelectedIndex;
    private android.widget.Spinner spinner;
    private int spThemeId;
    private int checkedIndex = -1;
    private int textColor;
    private int textColorHint;

    private ArrayAdapter adapter;

    private SpinnerValidatorInputType validatorInputType;
    private RequiredTextType requiredTextType;

    public Spinner(Context context) {
        super(context);
    }

    public Spinner(Context context, AttributeSet attrs) {
        super(context);
        setupAttribute(attrs);
    }

    public Spinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context);
        setupAttribute(attrs);
    }

    private void setupAttribute(AttributeSet attrs) {
        int[] attrsArray = new int[]{
                android.R.attr.id // 0
        };
        TypedArray ta = getContext().obtainStyledAttributes(attrs, attrsArray);
        int id = ta.getResourceId(0 /* index of attribute in attrsArray */, View.NO_ID);
        ta.recycle();
        setId(id);
        setLayoutParams(new ViewGroup.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.RSSpinner,
                0, 0);

        try {
            customErrorMessageId = a.getResourceId(R.styleable.RSSpinner_sou_spCustomErrorMessage, 0);
            if (customErrorMessageId == 0) {
                customErrorMessage = ConfigHelper.getStringConfigValue(getContext(), "customErrorMessage");

            }

            if (a.hasValue(R.styleable.RSSpinner_sou_spDefaultSelectedIndex)) {
                defaultSelectedIndex = a.getInt(R.styleable.RSSpinner_sou_spDefaultSelectedIndex, -1);

            } else {
                Integer _defaultSelectedIndex = ConfigHelper.getIntConfigValue(getContext(), "spDefaultSelectedIndex");
                if (_defaultSelectedIndex != null) {
                    defaultSelectedIndex = _defaultSelectedIndex;
                } else {
                    defaultSelectedIndex = -1;
                }

            }

            if (a.hasValue(R.styleable.RSSpinner_sou_spRequiredTextType)) {
                requiredTextType = RequiredTextType.values()[a.getInt(R.styleable.RSSpinner_sou_spRequiredTextType, 0)];

            } else {
                Integer requiredTextTypeIndex = ConfigHelper.getIntConfigValue(getContext(), "spRequiredTextType");
                if (requiredTextTypeIndex != null) {
                    requiredTextType = RequiredTextType.values()[requiredTextTypeIndex];
                } else {
                    requiredTextType = RequiredTextType.REQUIRED;
                }
            }

            if (a.hasValue(R.styleable.RSSpinner_sou_spIsRequired)) {
                isRequired = a.getBoolean(R.styleable.RSSpinner_sou_spIsRequired, false);

            } else {
                Boolean _isRequired = ConfigHelper.getBooleanConfigValue(getContext(), "spIsRequired");
                if (_isRequired != null) {
                    isRequired = _isRequired;
                } else {
                    isRequired = Boolean.FALSE;
                }
            }

            if (a.hasValue(R.styleable.RSSpinner_sou_spRequiredText)) {
                requiredText = a.getString(R.styleable.RSSpinner_sou_spRequiredText);
                if (requiredText == null || (requiredText != null && requiredText.isEmpty())) {
                    requiredText = "*";

                }


            } else {
                String _requiredText = ConfigHelper.getStringConfigValue(getContext(), "spRequiredText");
                if (_requiredText != null) {
                    requiredText = _requiredText;
                } else {
                    requiredText = "*";
                }
            }

            relatedLabelId = a.getResourceId(R.styleable.RSSpinner_sou_spRelatedLabel, 0);
            spThemeId = a.getResourceId(R.styleable.RSSpinner_sou_spRelatedLabel, 0);
            inputId = a.getResourceId(R.styleable.RSSpinner_sou_spInputId, 0);

            textColor = a.getResourceId(R.styleable.RSSpinner_sou_spTextColor, 0);
            textColorHint = a.getResourceId(R.styleable.RSSpinner_sou_spTextColorHint, 0);


            if (textColor == 0) {
                String colorName = ConfigHelper.getStringConfigValue(getContext(), "spTextColor");
                textColor = getResources().getIdentifier(colorName, "color", getContext().getApplicationContext().getPackageName());

            }

            if (textColorHint == 0) {
                String colorName = ConfigHelper.getStringConfigValue(getContext(), "spTextColorHint");
                textColorHint = getResources().getIdentifier(colorName, "color", getContext().getApplicationContext().getPackageName());
            }

            if (requiredText == null || (requiredText != null && requiredText.isEmpty())) {
                requiredText = "*";

            }

            if (a.hasValue(R.styleable.RSSpinner_sou_spIsPrefix)) {
                isPrefix = a.getBoolean(R.styleable.RSSpinner_sou_spIsPrefix, true);

            } else {
                Boolean _isPrefix = ConfigHelper.getBooleanConfigValue(getContext(), "isPrefix");
                if (_isPrefix != null) {
                    isPrefix = _isPrefix;
                } else {
                    isPrefix = Boolean.TRUE;
                }
            }

            if (spThemeId == 0) {
                spinner = new android.widget.Spinner(getContext(), attrs);

            } else {
                spinner = new android.widget.Spinner(getContext(), attrs);

            }

            if (isRequired) {
                validatorInputType = new SpinnerValidatorInputType();

            }

            if (inputId > 0) {
                spinner.setId(inputId);

            } else {
                spinner.setId(ViewUtil.generateViewId());

            }

            addView(spinner, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));


        } finally {
            a.recycle();
        }

        requestLayout();
        spinner.setFocusable(true); // can be done in XML preferrable
//        spinner.setFocusableInTouchMode(true);

    }

    @Override
    public Boolean isRequired() {
        return isRequired;

    }

    @Override
    public Boolean isValid() {
        if (validatorInputType != null) {
            return validatorInputType.isValid();
        }
        return Boolean.TRUE;

    }

    @Override
    public void setIsRequired(Boolean isRequired) {
        if (isRequired) {
            validatorInputType = new SpinnerValidatorInputType();

        } else {
            validatorInputType = null;

        }
        this.isRequired = isRequired;
        setupTitleLabel();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (relatedLabelId > 0 && relatedLabelText == null) {
            relatedLabelText = (TextView) ((View) getParent()).findViewById(relatedLabelId);

            if (relatedLabelText == null) {
                relatedLabelText = (TextView) ((View) getParent().getParent()).findViewById(relatedLabelId);

            }
            if (relatedLabelText != null) {
                relatedLabelTextColor = relatedLabelText.getCurrentTextColor();
            }
            if ((spinner.getPrompt() == null ||
                    (spinner.getPrompt() != null && spinner.getPrompt().toString().isEmpty()))
                    && relatedLabelText.getText() != null
                    && !relatedLabelText.getText().toString().isEmpty()) {
                spinner.setPrompt(relatedLabelText.getText().toString());

            }
        }

        setupTitleLabel();

    }

    private void setupTitleLabel() {
        if ((isRequired && requiredTextType == RequiredTextType.REQUIRED) || (!isRequired && requiredTextType == RequiredTextType.NOTREQUIRED)) {
            if (relatedLabelText != null) {
                String title = relatedLabelText.getText() != null && !relatedLabelText.getText().toString().isEmpty()
                        ? relatedLabelText.getText().toString()
                        : "";
                title = title.replace(requiredText, "");
                if (isPrefix) {
                    relatedLabelText.setText(String.format("%s%s", requiredText, title));

                } else {
                    relatedLabelText.setText(String.format("%s%s", title, requiredText));

                }
            }
        } else {
            if (relatedLabelText != null) {
                String title = relatedLabelText.getText() != null && !relatedLabelText.getText().toString().isEmpty()
                        ? relatedLabelText.getText().toString()
                        : "";
                relatedLabelText.setText(title.replace(requiredText, ""));

            }
        }
    }

    public void setOnItemSelectedListener(final AdapterView.OnItemSelectedListener listener) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            private boolean firstExec = true;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setError(null);
                if (Spinner.this.validatorInputType != null && !this.firstExec) {
                    Spinner.this.validatorInputType.isValid();
                }

                if (position > 0) {
                    this.firstExec = false;
                }

                listener.onItemSelected(parent, view, position, id);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                listener.onNothingSelected(parent);

            }
        });

    }

    public void setAdapter(SpinnerAdapter adapter) {
        spinner.setAdapter(adapter);
    }

    public int getSelectedItemPosition() {
        return spinner.getSelectedItemPosition();

    }

    public void setSelection(int position) {
        if (position < spinner.getCount()) {
            spinner.setSelection(position);
        }
        isValid();
    }

    public void resetState() {
        setError("");

        if (this.relatedLabelText != null && relatedLabelTextColor != 0) {
            relatedLabelText.setTextColor(Color.parseColor("#" + Integer.toHexString(relatedLabelTextColor)));
        }

    }

    public <T> void setupAdapter(List<T> items) {

        adapter = new ArrayAdapter<T>(spinner.getContext(),
                android.R.layout.simple_spinner_item, items) {

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                if (textColorHint > 0) {
                    View view = super.getDropDownView(position, convertView, parent);
                    TextView tv = (TextView) view;
                    if (position == 0) {
                        // Set the hint text color gray
                        tv.setTextColor(ContextCompat.getColor(getContext(), textColorHint));
                    } else {
                        tv.setTextColor(ContextCompat.getColor(getContext(), textColor));
                    }
                }
                return super.getDropDownView(position, convertView, parent);

            }

        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        if (checkedIndex > -1) {
            spinner.setSelection(checkedIndex);

        } else if (defaultSelectedIndex > -1) {
            spinner.setSelection(defaultSelectedIndex);

        }

        final AdapterView.OnItemSelectedListener listener = spinner.getOnItemSelectedListener();
        if (listener == null) {
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                boolean firstExec = true;

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (validatorInputType != null && !firstExec) {
                        validatorInputType.isValid();

                    }
                    if (position > 0) {
                        firstExec = false;

                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        } else {
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                boolean firstExec = true;

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (validatorInputType != null && !firstExec) {
                        validatorInputType.isValid();

                    }
                    if (position > 0) {
                        firstExec = false;

                    }
                    listener.onItemSelected(parent, view, position, id);

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        spinner.setEnabled(enabled);
    }


    @Override
    public void enableComponent(boolean enabled) {
        setEnabled(enabled);

    }

    @Override
    public void cleanComponent() {
        spinner.setSelection(0);
        resetState();

    }

    static class SavedState extends View.BaseSavedState {

        int position;
        boolean showDropdown;

        SavedState(Parcelable superState) {
            super(superState);
        }

        SavedState(Parcel source, ClassLoader loader) {
            super(source, loader);
        }

        ;

        /**
         * Constructor called from {@link #CREATOR}
         */
        SavedState(Parcel in) {
            super(in);
            position = in.readInt();
            showDropdown = in.readByte() != 0;
        }

        @Override
        public void writeToParcel(@NonNull Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(position);
            out.writeByte((byte) (showDropdown ? 1 : 0));
        }

        @Override
        public String toString() {
            return "AbsSpinner.SavedState{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " position=" + position
                    + " showDropdown=" + showDropdown + "}";
        }

        //
//        public static final Creator<SavedState> CREATOR
//                = new Creator<SavedState>() {
//            public SavedState createFromParcel(Parcel in) {
//                return new SavedState(in);
//            }
//
//            public SavedState[] newArray(int size) {
//                return new SavedState[size];
//            }
//        };
//
        public static final ClassLoaderCreator<SavedState> CREATOR
                = new ClassLoaderCreator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel source, ClassLoader loader) {
                return new SavedState(source, loader);
            }

            @Override
            public SavedState createFromParcel(Parcel source) {
                return new SavedState(source);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };


    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.position = getSelectedItemPosition();
        return ss;

    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        checkedIndex = ss.position;
        setSelection(ss.position);

    }

    //getter setter
    public SpinnerValidatorInputType getValidatorInputType() {
        return validatorInputType;
    }

    public android.widget.Spinner getSpinner() {
        return spinner;
    }

    public ArrayAdapter getAdapter() {
        return adapter;
    }

    public void setValidatorInputType(SpinnerValidatorInputType validatorInputType) {
        this.validatorInputType = validatorInputType;

    }

    public int getCustomErrorMessageId() {
        return customErrorMessageId;
    }

    public class SpinnerValidatorInputType implements ValidatorInputType {

        @Override
        public Boolean isValid() {

            Boolean isValid = getSelectedItemPosition() == 0 ? Boolean.FALSE : Boolean.TRUE;
            if (relatedLabelText != null) {
                if (!isValid && relatedLabelTextColor != 0) {
                    relatedLabelText.setTextColor(ContextCompat.getColor(getContext(), android.R.color.holo_red_dark));
                    setError(getContext().getString(R.string.sou_widgets_select_option));

                } else {
                    relatedLabelText.setTextColor(relatedLabelTextColor);
                    setError(null);

                }
            }
            return isValid;
        }

    }

}
