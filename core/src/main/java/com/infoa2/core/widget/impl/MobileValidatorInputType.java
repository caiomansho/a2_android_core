package com.infoa2.core.widget.impl;

import android.content.Context;
import com.infoa2.core.R;
import com.infoa2.core.widget.EditText;
import com.infoa2.core.widget.interfaces.ValidatorInputType;

/**
 * Created by caiomansho on 05/04/2017.
 */

public class MobileValidatorInputType implements ValidatorInputType {

    private EditText editText;
    private Context context;

    public MobileValidatorInputType(Context context, EditText editText){
        this.editText = editText;
        this.context = context;

    }

    @Override
    public Boolean isValid() {
        if (!editText.getText().toString().isEmpty() && !editText.isMobile()) {
            if (editText.getCustomErrorMessageId() != 0) {
                editText.setErrorEnabled(true);
                editText. setError(context.getString(editText.getCustomErrorMessageId()));

            } else {
                editText. setError(context.getString(R.string.sou_widgets_invalid_mobile_number));

            }
            return Boolean.FALSE;

        } else if(editText.getText().toString().isEmpty()) {
            editText. setError(null);
            editText.setErrorEnabled(false);
            return Boolean.FALSE;

        } else {
            editText. setError(null);
            editText.setErrorEnabled(false);
            return Boolean.TRUE;

        }
    }

}
