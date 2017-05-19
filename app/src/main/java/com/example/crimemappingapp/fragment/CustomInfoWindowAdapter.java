package com.example.crimemappingapp.fragment;

import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.crimemappingapp.R;
import com.example.crimemappingapp.activity.CrimeMapActivity;
import com.example.crimemappingapp.utils.Crime;
import com.example.crimemappingapp.utils.CrimeMappingUtils;
import com.example.crimemappingapp.utils.DateUtils;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by klanezurbano on 18/05/2017.
 */

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter, GoogleMap.OnInfoWindowClickListener  {
    private final CrimeMapActivity activity;
    private final boolean isAdmin;

    public CustomInfoWindowAdapter(CrimeMapActivity activity, boolean isAdmin) {
        this.activity = activity;
        this.isAdmin = isAdmin;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        Object tag = marker.getTag();
        if(tag == null) return null;

        View v = LayoutInflater.from(this.activity).inflate(R.layout.info_window_layout, null);

        final Crime crime = (Crime) marker.getTag();

        TextView locationLabel = (TextView) v.findViewById(R.id.locationLabel);
        locationLabel.setText(crime.getLocation());
        TextView crimeLabel = (TextView) v.findViewById(R.id.crimeLabel);
        crimeLabel.setText(crime.getCrimeType().getDisplayName());
        TextView dateHappenedLabel = (TextView) v.findViewById(R.id.dateHappenedLabel);
        dateHappenedLabel.setText(DateUtils.buildDateDisplay(crime.getDateMillis()));

        if(!isAdmin) {
            v.findViewById(R.id.adminPrivilegeContent).setVisibility(View.INVISIBLE);
        }
        return v;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Object obj = marker.getTag();
        if(!isAdmin || obj == null) return;

        Crime crime = (Crime) obj;
        if(CrimeMappingUtils.haveNetworkConnection(this.activity.getBaseContext())) {
            DialogFragment newFragment = EditCrimeFragment.newInstance(R.string.alert_dialog_edit_crime, crime);
            newFragment.show(this.activity.getFragmentManager(), "editCrime");
        } else {
            // TODO alert that needs internet connection
        }
    }
}
