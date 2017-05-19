package com.example.crimemappingapp.fragment;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import com.example.crimemappingapp.utils.DateUtils;

import java.util.Calendar;

@SuppressLint("ValidFragment")
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

    }

    public static View.OnClickListener createDatePickerOnClickListener(final FragmentManager fragmentManager) {
        return createDatePickerOnClickListener(fragmentManager, null);
    }

    public static View.OnClickListener createDatePickerOnClickListener(final FragmentManager fragmentManager, Long dateMillis) {
        return new View.OnClickListener() {
            @SuppressLint("ValidFragment")
            @Override
            public void onClick(final View v) {
                DialogFragment datePickerFragment = new DatePickerFragment() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        ((Button) v).setText(DateUtils.buildDateDisplay(year, month, dayOfMonth));
                    }
                };
                datePickerFragment.show(fragmentManager, "datePicker");
            }
        };
    }
}