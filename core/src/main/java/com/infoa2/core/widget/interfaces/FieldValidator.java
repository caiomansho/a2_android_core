package com.infoa2.core.widget.interfaces;

/**
 * Created by caio on 27/03/17.
 */

public interface FieldValidator {

    Boolean isRequired();
    Boolean isValid();
    void setIsRequired(Boolean isRequired);

}
