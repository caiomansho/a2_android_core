package com.infoa2.core.widget.fragments

/**
 * Created by caio on 11/04/17.
 */

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.Fragment
import android.content.DialogInterface
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.infoa2.core.R
import com.infoa2.core.widget.EditText
import java.util.Calendar

/**
 * A simple [Fragment] subclass.
 */
class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    var calendar: Calendar? = null
    private var editText: EditText? = null


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //Use the current date as the default date in the date picker
        if (calendar == null)
            calendar = Calendar.getInstance()
        val year = calendar!!.get(Calendar.YEAR)
        val month = calendar!!.get(Calendar.MONTH)
        val day = calendar!!.get(Calendar.DAY_OF_MONTH)

        //Create a new DatePickerDialog instance and return it
        /*
            DatePickerDialog Public Constructors - Here we uses first one
            public DatePickerDialog (Context context, DatePickerDialog.OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth)
            public DatePickerDialog (Context context, int theme, DatePickerDialog.OnDateSetListener listener, int year, int monthOfYear, int dayOfMonth)
         */
        val datePickerDialog = DatePickerDialog(activity!!, this, year, month, day)
        datePickerDialog.setButton(
            DialogInterface.BUTTON_NEUTRAL,
            getString(R.string.sou_widgets_clean)
        ) { dialog, which ->
            if (editText != null) {
                editText!!.dateText = null
            }
        }
        return datePickerDialog

    }

    fun show(editText: EditText, fragmentManager: FragmentManager, tag: String) {
        this.editText = editText
        show(fragmentManager, tag)
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        //Do something with the date chosen by the user
        if (calendar == null) {
            calendar = Calendar.getInstance()
        }
        calendar!!.set(year, month, day)
        editText!!.dateText = calendar
    }

}