package com.infoa2.core.widget;


import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import androidx.appcompat.app.AppCompatActivity;
import com.infoa2.core.widget.interfaces.ComponentActions;
import com.infoa2.core.widget.interfaces.FieldValidator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caio on 23/02/17.
 */

public class WidgetUtils {

    private static final String TAG = WidgetUtils.class.getName();
    protected static AppCompatActivity appCompatActivity;
    private static List<FieldValidator> fieldValidators;

    public static void init(AppCompatActivity appCompatActivity){
        WidgetUtils.appCompatActivity = appCompatActivity;
        fieldValidators = new ArrayList<>();
        findAllFieldValidators((ViewGroup) appCompatActivity.findViewById(android.R.id.content));

    }

    public static void init(ViewGroup viewGroup){
        fieldValidators = new ArrayList<>();
        findAllFieldValidators(viewGroup);

    }

    private static void findAllFieldValidators(ViewGroup viewGroup){
        int count = viewGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof ViewGroup && !(view instanceof FieldValidator))
                findAllFieldValidators((ViewGroup) view);
            else if (view instanceof FieldValidator && view.getVisibility() != View.GONE) {
                fieldValidators.add((FieldValidator) view);

            }
        }

    }

    public static Boolean validateComponents() {
        Boolean isValid = Boolean.TRUE;
        for (FieldValidator fieldValidator : fieldValidators) {
            if(!fieldValidator.isValid()){
                isValid = Boolean.FALSE;
                Log.d("Widget Validator", fieldValidator.toString());
            }
        }
        return isValid;

    }

    public static Boolean validateComponents(ScrollView scrollView) {
        Object firstInvalidComponent = null;
        for (FieldValidator fieldValidator : fieldValidators) {
            if(!fieldValidator.isValid()){
                firstInvalidComponent = fieldValidator;
            }
        }
        if(firstInvalidComponent != null ){
            try {
                ((EditText) firstInvalidComponent).getParent().requestChildFocus((View) firstInvalidComponent, (View) firstInvalidComponent);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        return firstInvalidComponent  == null ? Boolean.TRUE : Boolean.FALSE;

    }

    public static void cleanComponents(List<ComponentActions> components) {
        for (ComponentActions componentActions : components) {
            componentActions.cleanComponent();
        }
    }

    public static void disableAndCleanComponents(List<ComponentActions> components, ComponentActions afterComponent) {
        int index = components.indexOf(afterComponent);
        List<ComponentActions> componentsFiltered = components.subList(index, components.size());
        for (ComponentActions componentActions : componentsFiltered) {
            componentActions.cleanComponent();
            componentActions.enableComponent(false);

        }
    }

    public static void disableComponents(List<ComponentActions> components, ComponentActions afterComponent) {
        int index = components.indexOf(afterComponent);
        List<ComponentActions> componentsFiltered = components.subList(index, components.size());
        for (ComponentActions componentActions : componentsFiltered) {
            componentActions.enableComponent(false);
        }
    }
}
