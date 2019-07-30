package com.infoa2.core.widget.enums;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.View;
import com.infoa2.core.R;
import com.infoa2.core.widget.EditText;
import com.infoa2.core.widget.impl.*;
import com.infoa2.core.widget.interfaces.ValidatorInputType;
import com.infoa2.core.widget.textwatchers.*;
import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by caio on 27/03/17.
 */

public enum EditTextInputType {

    ZIPCODE(0) {
        @Override
        public void setupEditText(Context context, final EditText editText) {
            editText.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            editText.setMaxLines(1);
            ZipCodeValidatorInputType validatorInputType =  new ZipCodeValidatorInputType(context, editText);
            editText.addTextChangedListener(ZipCodeMaksTextWatcher.insert(editText, validatorInputType));
            editText.setValidatorInputType(validatorInputType);
        }

        @Override
        public String getDefaultLabel(Context context) {
            return context.getString(R.string.sou_widgets_zip_code);

        }
    },
    CPF(1){
        @Override
        public void setupEditText(Context context, EditText editText) {
            editText.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            editText.setMaxLines(1);
            CpfValidatorInputType validatorInputType =  new CpfValidatorInputType(context, editText) ;
;
            editText.addTextChangedListener(CpfMaksTextWatcher.insert(editText, validatorInputType));
            editText.setValidatorInputType(validatorInputType);

        }

        @Override
        public String getDefaultLabel(Context context) {
            return context.getString(R.string.sou_widgets_cpf);

        }

    },
    CNPJ(2){
        @Override
        public void setupEditText(Context context, EditText editText) {
            editText.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            editText.setMaxLines(1);

            CnpjValidatorInputType validatorInputType =  new CnpjValidatorInputType(context, editText);

            editText.addTextChangedListener(CnpjMaksTextWatcher.insert(editText, validatorInputType));
            editText.setValidatorInputType(validatorInputType);
        }

        @Override
        public String getDefaultLabel(Context context) {
            return context.getString(R.string.sou_widgets_cnpj);

        }

    },
    CPFCNPJ(3){
        @Override
        public void setupEditText(Context context, EditText editText) {
            editText.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            editText.setMaxLines(1);

            CpfCnpjValidatorInputType validatorInputType =  new CpfCnpjValidatorInputType(context, editText);

            editText.addTextChangedListener(CpfCnpjMaksTextWatcher.insert(editText, validatorInputType));
            editText.setValidatorInputType(validatorInputType);

        }

        @Override
        public String getDefaultLabel(Context context) {
            return context.getString(R.string.sou_widgets_cpf_cnpj);

        }

    },
    MOBILE(4){
        @Override
        public void setupEditText(Context context, EditText editText) {
            editText.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            editText.setMaxLines(1);

            MobileValidatorInputType validatorInputType =  new MobileValidatorInputType(context, editText);

            editText.addTextChangedListener(PhoneMaksTextWatcher.insert(editText, validatorInputType));
            editText.setValidatorInputType(validatorInputType);

        }

        @Override
        public String getDefaultLabel(Context context) {
            return context.getString(R.string.sou_widgets_mobile);
        }

    },
    PERCENTAGE(5){
        @Override
        public void setupEditText(Context context, EditText editText) {
            editText.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            editText.setMaxLines(1);
            PercentageValidatorInputType validatorInputType = new PercentageValidatorInputType(context, editText);
            editText.setValidatorInputType(validatorInputType);
            editText.addTextChangedListener(PercentageMaskTextWatcher.insert(editText, validatorInputType));
        }

        @Override
        public String getDefaultLabel(Context context) {
            return "";
        }

    },
    CURRENCY(6){
        @Override
        public void setupEditText(Context context, EditText editText){
            editText.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            editText.setMaxLines(1);
            CurrencyValidatorInputType validatorInputType = new CurrencyValidatorInputType(context, editText);
            editText.setValidatorInputType(validatorInputType);
            editText.addTextChangedListener(CurrencyMaskTextWatcher.insert(editText, validatorInputType));
        }

        @Override
        public String getDefaultLabel(Context context) {
            return "";
        }

    },
    NONE(7){
        @Override
        public void setupEditText(Context context, final EditText editText) {
            final ValidatorInputType validatorInputType = new ValidatorInputType(){
                @Override
                public Boolean isValid() {
                    if(!editText.isRequired() || !editText.getText().toString().isEmpty()){
                        editText. setError("");
                        return Boolean.TRUE;

                    } else {
                        return Boolean.FALSE;
                    }

                }
            };

            editText.setValidatorInputType(validatorInputType);
            editText.setMaxLines(1);
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    validatorInputType.isValid();

                }
            });
        }

        @Override
        public String getDefaultLabel(Context context) {
            return "";
        }
    },
    EMAIL(8){
        @Override
        public void setupEditText(Context context, EditText editText) {
            editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            editText.setMaxLines(1);
            final EmailValidatorInputType validatorInputType = new EmailValidatorInputType(context, editText);
            editText.setValidatorInputType(validatorInputType);
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    validatorInputType.isValid();
                }
            });

        }

        @Override
        public String getDefaultLabel(Context context) {
            return context.getString(R.string.sou_widgets_email);
        }
    },
    DATE(9){
        @Override
        public void setupEditText(final Context context, final EditText editText) {
            editText.setInputType(InputType.TYPE_NULL);
            editText.setMaxLines(1);
            editText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatePickerDialog datePickerDialog = editText.getDatePickerDialog();
                    if(datePickerDialog != null){
                        datePickerDialog.show();
                    } else {
                        Calendar now = Calendar.getInstance();
                        SpinnerDatePickerDialogBuilder spinnerDatePickerDialogBuilder = new SpinnerDatePickerDialogBuilder()
                                .context(context)
                                .callback(new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                        Calendar cal = new GregorianCalendar(year, monthOfYear, dayOfMonth);
                                        editText.setDateText(cal);

                                    }
                                })
                                .spinnerTheme(R.style.NumberPickerStyle)
                                .showTitle(true)
                                .showDaySpinner(true)
                                .maxDate(now.get(Calendar.YEAR) + 10, now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))
                                .minDate(1900, 0, 1);
                        now = editText.getDateText();
                        if(now == null){
                            now = Calendar.getInstance();
                        }
                        spinnerDatePickerDialogBuilder.defaultDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)).build().show();
                    }

                }
            });
            editText.getEditText().setOnTouchListener(null);
            editText.getEditText().setFocusableInTouchMode(false);
            editText.getEditText().setFocusable(false);


            final ValidatorInputType validatorInputType = new ValidatorInputType(){
                @Override
                public Boolean isValid() {
                    if(!editText.isRequired() || !editText.getText().toString().isEmpty()){
                        editText. setError("");
                        return Boolean.TRUE;

                    } else {
                        return Boolean.FALSE;
                    }

                }
            };

            editText.setValidatorInputType(validatorInputType);
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    validatorInputType.isValid();

                }
            });

        }

        @Override
        public String getDefaultLabel(Context context) {
            return context.getString(R.string.sou_widgets_date);

        }
        
    },
    TWO_DECIMAL_POINT(6){
        @Override
        public void setupEditText(Context context, EditText editText){
            editText.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            editText.setMaxLines(1);
            CurrencyValidatorInputType validatorInputType = new CurrencyValidatorInputType(context, editText);
            editText.setValidatorInputType(validatorInputType);
            editText.addTextChangedListener(TwoDecimalPointMaskTextWatcher.insert(editText, validatorInputType));
        }

        @Override
        public String getDefaultLabel(Context context) {
            return "";
        }

    };

    private int inputTypeCode;

    EditTextInputType(int inputTypeCode) {
        this.inputTypeCode = inputTypeCode;
    }

    public int getInputTypeCode(){
        return inputTypeCode;

    }

    public abstract void setupEditText(Context context, EditText editText);
    public abstract String getDefaultLabel(Context context);

}
