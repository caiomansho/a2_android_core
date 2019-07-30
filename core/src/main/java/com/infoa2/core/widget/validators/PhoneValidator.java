package com.infoa2.core.widget.validators;

import com.google.android.material.textfield.TextInputEditText;
import com.infoa2.core.widget.textwatchers.PhoneMaksTextWatcher;

/**
 * Created by caio on 30/03/17.
 */

public class PhoneValidator {

    public static Boolean isPhone(String phone){
        return phone.matches("-?\\d+(\\.\\d+)?") && phone.length() >= 7 && phone.length() <= 11;
    }

    public static Boolean isPhone(TextInputEditText phone) {
        return isPhone(PhoneMaksTextWatcher.unmask(phone.getText().toString()));
    }

    public static Boolean isMobile(String mobile){
        return mobile.matches("-?\\d+(\\.\\d+)?") && mobile.length() >= 10 && mobile.length() <= 11;
    }

    public static Boolean isMobile(TextInputEditText phone) {
        return isMobile(PhoneMaksTextWatcher.unmask(phone.getText().toString()));
    }
}
