package com.infoa2.core.widget.impl;

import android.content.Context;
import com.infoa2.core.R;
import com.infoa2.core.widget.EditText;
import com.infoa2.core.widget.interfaces.ValidatorInputType;
import com.infoa2.core.widget.textwatchers.CpfCnpjMaksTextWatcher;

/**
 * Created by caiomansho on 05/04/2017.
 */

public class CpfCnpjValidatorInputType implements ValidatorInputType {

    private EditText editText;
    private Context context;

    public CpfCnpjValidatorInputType(Context context, EditText editText){
        this.editText = editText;
        this.context = context;

    }

    @Override
    public Boolean isValid() {
        if (!editText.getText().toString().isEmpty()
                && (((CpfCnpjMaksTextWatcher.unmask(editText.getText().toString()).length() > 11 && !editText.isCnpj())
                || (CpfCnpjMaksTextWatcher.unmask(editText.getText().toString()).length() <= 11
                && !editText.isCpf())))) {
            editText.setErrorEnabled(true);
            if (editText.getCustomErrorMessageId() != 0) {
                editText. setError(context.getString(editText.getCustomErrorMessageId()));

            } else {
                editText. setError(context.getString(R.string.sou_widgets_invalid_cpf_cnpj));

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
