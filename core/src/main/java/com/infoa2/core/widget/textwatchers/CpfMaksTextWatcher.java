package com.infoa2.core.widget.textwatchers;

import android.text.Editable;
import android.text.TextWatcher;
import com.infoa2.core.widget.EditText;
import com.infoa2.core.widget.interfaces.ValidatorInputType;

/**
 * Created by caio on 21/02/17.
 */

public abstract class CpfMaksTextWatcher {

    private static final String maskCPF = "###.###.###-##";

    public static String unmask(String s) {
        return s.replaceAll("[^0-9]*", "");
    }

    public static TextWatcher insert(final EditText editText, final ValidatorInputType validatorInputType) {
        return new TextWatcher() {
            boolean isUpdating;
            String old = "";

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = CpfMaksTextWatcher.unmask(s.toString());

                String mascara = "";
                if (isUpdating) {
                    old = str;
                    isUpdating = false;
                    return;
                }
                int i = 0;
                for (char m : maskCPF.toCharArray()) {
                    if ((m != '#' && str.length() > old.length()) || (m != '#' && str.length() < old.length() && str.length() != i)) {
                        mascara += m;
                        continue;
                    }

                    try {
                        mascara += str.charAt(i);
                    } catch (Exception e) {
                        break;
                    }
                    i++;
                }
                isUpdating = true;
                editText.setText(mascara);
                editText.setSelection(editText.getText().toString().length());
                validatorInputType.isValid();

            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            public void afterTextChanged(Editable s) {

            }
        };

    }

    public static TextWatcher insert(final EditText editText) {
        return new TextWatcher() {
            boolean isUpdating;
            String old = "";

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = CpfMaksTextWatcher.unmask(s.toString());

                String mascara = "";
                if (isUpdating) {
                    old = str;
                    isUpdating = false;
                    return;
                }
                int i = 0;
                for (char m : maskCPF.toCharArray()) {
                    if ((m != '#' && str.length() > old.length()) || (m != '#' && str.length() < old.length() && str.length() != i)) {
                        mascara += m;
                        continue;
                    }

                    try {
                        mascara += str.charAt(i);
                    } catch (Exception e) {
                        break;
                    }
                    i++;
                }
                isUpdating = true;
                editText.setText(mascara);
                editText.setSelection(editText.getText().toString().length());

            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            public void afterTextChanged(Editable s) {

            }
        };
    }

}
