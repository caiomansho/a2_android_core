package com.infoa2.core.widget.textwatchers;

import android.text.Editable;
import android.text.TextWatcher;
import com.infoa2.core.widget.EditText;
import com.infoa2.core.widget.interfaces.ValidatorInputType;

/**
 * Created by caio on 21/02/17.
 */

public abstract class PhoneMaksTextWatcher {


    private static final String maskPhone = "(##)####-####";
    private static final String maskPhoneSP = "(##)#####-####";


    public static String unmask(String s) {
        return s.replaceAll("[^0-9]*", "");
    }

    public static TextWatcher insert(final EditText editText) {
        return new TextWatcher() {
            boolean isUpdating;
            String old = "";

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = PhoneMaksTextWatcher.unmask(s.toString());
                String mask;
                String defaultMask = getDefaultMask(str);
                switch (str.length()) {
                    case 10:
                        mask = maskPhone;
                        break;
                    case 11:
                        mask = maskPhoneSP;
                        break;

                    default:
                        mask = defaultMask;
                        break;
                }

                String mascara = "";
                if (isUpdating) {
                    old = str;
                    isUpdating = false;
                    return;
                }
                int i = 0;
                for (char m : mask.toCharArray()) {
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
                editText.setSelection(mascara.length());

            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            public void afterTextChanged(Editable s) {

            }
        };
    }

    public static TextWatcher insert(final EditText editText, final ValidatorInputType validatorInputType) {
        return new TextWatcher() {
            boolean isUpdating;
            String old = "";

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = PhoneMaksTextWatcher.unmask(s.toString());
                String mask;
                String defaultMask = getDefaultMask(str);
                switch (str.length()) {
                    case 10:
                        mask = maskPhone;
                        break;
                    case 11:
                        mask = maskPhoneSP;
                        break;

                    default:
                        mask = defaultMask;
                        break;
                }

                String mascara = "";
                if (isUpdating) {
                    old = str;
                    isUpdating = false;
                    return;
                }
                int i = 0;
                for (char m : mask.toCharArray()) {
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
                editText.setSelection(mascara.length());
                validatorInputType.isValid();

            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            public void afterTextChanged(Editable s) {

            }
        };

    }

    public static String getAreaCode(String str) {
        return str.substring(1,3);
    }

    public static String getPhoneNumber(String str) {
        return str.substring(4);
    }


    private static String getDefaultMask(String str) {
        String defaultMask = maskPhone;
        if (str.length() > 10){
            defaultMask = maskPhoneSP;
        }
        return defaultMask;
    }

}
