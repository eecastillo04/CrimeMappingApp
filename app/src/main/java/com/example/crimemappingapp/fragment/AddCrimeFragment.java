package com.example.crimemappingapp.fragment;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;

import com.example.crimemappingapp.R;
import com.example.crimemappingapp.activity.CrimeMapActivity;
import com.example.crimemappingapp.utils.DatabaseHelper;
import com.example.crimemappingapp.utils.DateUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddCrimeFragment extends DialogFragment {

    public static AddCrimeFragment newInstance(int title) {
        AddCrimeFragment frag = new AddCrimeFragment();
        Bundle args = new Bundle();
        args.putInt("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = getArguments().getInt("title");

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_add_crime, null);

        initView(v);

        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setView(v)
                .setPositiveButton(R.string.alert_dialog_save,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                ((CrimeMapActivity)getActivity()).doPositiveClick();
                            }
                        }
                )
                .setNegativeButton(R.string.alert_dialog_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                ((CrimeMapActivity)getActivity()).doNegativeClick();
                            }
                        }
                )
                .create();
    }

    private void initView(View v) {
        Spinner crimeTypeSpinner = (Spinner) v.findViewById(R.id.crime_type_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, DatabaseHelper.retrieveAllCrimeTypes());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        crimeTypeSpinner.setAdapter(adapter);

        Button dateHappenedSpinner = (Button) v.findViewById(R.id.date_button);
        dateHappenedSpinner.setOnClickListener(DatePickerFragment.createDatePickerOnClickListener(getFragmentManager()));
    }
}
