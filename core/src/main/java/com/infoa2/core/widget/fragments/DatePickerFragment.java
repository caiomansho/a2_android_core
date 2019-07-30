package com.infoa2.core.widget.fragments;

/**
 * Created by caio on 11/04/17.
 */

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.DatePicker;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import com.infoa2.core.R;
import com.infoa2.core.widget.EditText;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private Calendar calendar;
    private EditText editText;

    public DatePickerFragment(){}


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Use the current date as the default date in the date picker
        if(calendar == null)
            calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        //Create a new DatePickerDialog instance and return it
        /*
            DatePickerDialog Public Constructors - Here we uses first one
            public DatePickerDialog (Context context, DatePickerDialog.OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth)
            public DatePickerDialog (Context context, int theme, DatePickerDialog.OnDateSetListener listener, int year, int monthOfYear, int dayOfMonth)
         */
        DatePickerDialog datePickerDialog =  new DatePickerDialog(getActivity(), this, year, month, day);
        datePickerDialog.setButton(DialogInterface.BUTTON_NEUTRAL, getString(R.string.sou_widgets_clean), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(editText != null){
                    editText.setDateText(null);
                }

            }
        });
        return datePickerDialog;

    }

    public void show(EditText editText, FragmentManager fragmentManager, String tag){
        this.editText = editText;
        show(fragmentManager, tag);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        //Do something with the date chosen by the user
        if(calendar == null){
            calendar = Calendar.getInstance();
        }
        calendar.set(year, month, day);
        editText.setDateText(calendar);
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar){
        this.calendar = calendar;

    }

}