package com.infoa2.core.widget.textwatchers;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import com.infoa2.core.widget.EditText;
import com.infoa2.core.widget.interfaces.ValidatorInputType;

import java.text.NumberFormat;


/**
 * Created by caio on 23/02/17.
 */

public class PercentageMaskTextWatcher implements TextWatcher {
    private String current = "";
    private int index;
    private boolean deletingDecimalPoint;
    private boolean deleting = false;
    private final EditText perentageEditText;
    private OnTextChanged onTextChanged;
    private ValidatorInputType validatorInputType;

    public interface OnTextChanged {
        void onTextChanged(String text);
    }

    public PercentageMaskTextWatcher(EditText p_currency) {
        perentageEditText = p_currency;
    }

    public PercentageMaskTextWatcher(EditText p_currency, ValidatorInputType validatorInputType) {
        perentageEditText = p_currency;
        this.validatorInputType = validatorInputType;

    }


    public PercentageMaskTextWatcher(EditText p_currency, OnTextChanged onTextChanged) {
        perentageEditText = p_currency;
        this.onTextChanged = onTextChanged;

    }

    public static PercentageMaskTextWatcher insert(EditText editText) {
        return new PercentageMaskTextWatcher(editText);

    }

    public static TextWatcher insert(EditText editText, ValidatorInputType validatorInputType) {
        return new PercentageMaskTextWatcher(editText, validatorInputType);

    }

    @Override
    public void onTextChanged(
            CharSequence p_s, int p_start, int p_before, int p_count
    ) {
        // nothing to do
    }

    @Override
    public void beforeTextChanged(
            CharSequence p_s, int p_start, int p_count, int p_after
    ) {
        if (p_after > 0) {
            index = p_s.length() - p_start;
        } else {
            index = p_s.length() - p_start - 1;
        }
        if (p_count > 0) {
            deleting = true;

            if (p_s.charAt(p_start) == '.') {
                deletingDecimalPoint = true;
            } else {
                deletingDecimalPoint = false;
            }

        } else {
            deleting = false;

        }

    }

    @Override
    public synchronized void afterTextChanged(Editable p_s) {
        if (!p_s.toString().equals(current)) {
            perentageEditText.removeTextChangedListener(this);
            Log.d("afterTextChanged", String.valueOf(p_s.length()));
            Log.d("afterTextChanged", String.valueOf(index));
            if (deletingDecimalPoint) {
                p_s.delete(p_s.length() - index - 2, p_s.length() - index - 1);

            } else if (deleting && p_s.length() - index - 1 > 0) {
                p_s.delete(p_s.length() - index - 1, p_s.length() - index);

            }
            // Currency char may be retrieved from NumberFormat.getCurrencyInstance()
            String v_text = p_s.toString().replaceAll("[%,.]", "");
            double v_value = 0;
            if (v_text != null && v_text.length() > 0) {
                v_value = Double.parseDouble(v_text);
            }
            // Currency instance may be retrieved from a static member.
            String v_formattedValue = NumberFormat.getCurrencyInstance().format((v_value / 100)).replace("R$", "") + "%";
            current = v_formattedValue;
            perentageEditText.setText(v_formattedValue);
            if (index > v_formattedValue.length()) {
                perentageEditText.setSelection(v_formattedValue.length());
            } else {
                perentageEditText.setSelection(v_formattedValue.length() - index);
            }
            // inlude here anything you may want to do after the formatting is completed.
            perentageEditText.addTextChangedListener(this);

            if (onTextChanged != null) {
                onTextChanged.onTextChanged(v_formattedValue);
            }
            validatorInputType.isValid();

        }
    }

    public static Float toFloat(EditText editText) {
        String value = editText.getText().toString().replaceAll("[%,.]", "");
        if (value.isEmpty()) {
            return new Float(0);

        }
        return Float.valueOf(value) / 100;

    }

    public static Float toFloat(String currency) {
        String value = currency.replaceAll("[%,.]", "");
        if (value.isEmpty()) {
            return new Float(0);

        }
        return Float.valueOf(value) / 100;

    }
}
