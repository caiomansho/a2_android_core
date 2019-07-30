package com.infoa2.core.widget.validators;

import com.google.android.material.textfield.TextInputEditText;
import com.infoa2.core.widget.textwatchers.ZipCodeMaksTextWatcher;

/**
 * Created by caio on 29/03/17.
 */

public class ZipCodeValidator {

    public static boolean isZipCode(TextInputEditText zipCode){
        return isZipCode(ZipCodeMaksTextWatcher.unmask(zipCode.getText().toString()));
    }

    public static boolean isZipCode(String zipCode){
        return zipCode.matches("-?\\d+(\\.\\d+)?") && zipCode.length() == 8;

    }

}
