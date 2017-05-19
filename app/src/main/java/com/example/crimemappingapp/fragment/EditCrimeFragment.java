package com.example.crimemappingapp.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.crimemappingapp.R;
import com.example.crimemappingapp.activity.CrimeMapActivity;
import com.example.crimemappingapp.utils.Crime;
import com.example.crimemappingapp.utils.CrimeTypes;
import com.example.crimemappingapp.utils.DateUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditCrimeFragment extends DialogFragment {

    private Spinner crimeTypeSpinner;
    private Button dateHappenedSpinner;

    public static EditCrimeFragment newInstance(int title, Crime crime) {
        EditCrimeFragment frag = new EditCrimeFragment();
        Bundle args = new Bundle();
        args.putInt("title", title);
        args.putString("location", crime.getLocation());
        args.putString("crimeType", crime.getCrimeType().getDisplayName());
        args.putLong("dateMillis", crime.getDateMillis());
        args.putInt("crimeId", crime.getId());
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = getArguments().getInt("title");
        String location = getArguments().getString("location");
        String crimeType = getArguments().getString("crimeType");
        long dateMillis = getArguments().getLong("dateMillis");
        final int crimeId = getArguments().getInt("crimeId");

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_edit_crime, null);

        initView(v, location, crimeType, dateMillis);

        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setView(v)
                .setPositiveButton(R.string.alert_dialog_save,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Crime crime = new Crime();
                                crime.setId(crimeId);
                                crime.setCrimeType(CrimeTypes.getCrimeType(crimeTypeSpinner.getSelectedItem().toString()));
                                crime.setDateMillis(DateUtils.convertToMillis(dateHappenedSpinner.getText().toString()));
                                ((CrimeMapActivity)getActivity()).doPositiveClick(crime);
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
                .setNegativeButton(R.string.alert_dialog_delete,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                ((CrimeMapActivity)getActivity()).doNegativeClick(crimeId);
                            }
                        }
                )
                .create();
    }

    private void initView(View v, String location, String crimeType, long dateMillis) {
        EditText locationField = (EditText) v.findViewById(R.id.location_field);
        locationField.setText(location);

        dateHappenedSpinner = (Button) v.findViewById(R.id.date_button);
        dateHappenedSpinner.setOnClickListener(DatePickerFragment.createDatePickerOnClickListener(getFragmentManager(), dateMillis));
        dateHappenedSpinner.setText(DateUtils.buildDateDisplay(dateMillis));

        crimeTypeSpinner = (Spinner) v.findViewById(R.id.crime_type_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, CrimeTypes.getAllDisplayNames());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        crimeTypeSpinner.setAdapter(adapter);
    }

}
