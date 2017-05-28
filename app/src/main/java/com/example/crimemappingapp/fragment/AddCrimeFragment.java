package com.example.crimemappingapp.fragment;


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
import android.widget.EditText;
import android.widget.Spinner;

import com.example.crimemappingapp.R;
import com.example.crimemappingapp.activity.CrimeMapActivity;
import com.example.crimemappingapp.utils.Crime;
import com.example.crimemappingapp.utils.CrimeTypes;
import com.example.crimemappingapp.utils.DateUtils;

import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddCrimeFragment extends DialogFragment {

    private Spinner crimeTypeSpinner;
    private Button dateHappenedSpinner;
    private EditText locationField;

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
                .setPositiveButton(R.string.alert_dialog_add,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Crime crime = new Crime();
                                crime.setLocation(locationField.getText().toString());
                                crime.setCrimeType(CrimeTypes.getCrimeType(crimeTypeSpinner.getSelectedItem().toString()));
                                crime.setDateMillis(DateUtils.convertToMillis(dateHappenedSpinner.getText().toString()));
                                ((CrimeMapActivity)getActivity()).doAddCrime(crime);
                            }
                        }
                )
                .setNeutralButton(R.string.alert_dialog_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // do nothing
                            }
                        }
                )
                .create();
    }

    private void initView(View v) {
        locationField = (EditText) v.findViewById(R.id.location_field);

        dateHappenedSpinner = (Button) v.findViewById(R.id.date_button);
        long currDateTime = new Date().getTime();
        dateHappenedSpinner.setOnClickListener(DatePickerFragment.createDatePickerOnClickListener(getFragmentManager(), currDateTime));
        dateHappenedSpinner.setText(DateUtils.buildDateDisplay(currDateTime));

        crimeTypeSpinner = (Spinner) v.findViewById(R.id.crime_type_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, CrimeTypes.getAllDisplayNames());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        crimeTypeSpinner.setAdapter(adapter);
    }

}