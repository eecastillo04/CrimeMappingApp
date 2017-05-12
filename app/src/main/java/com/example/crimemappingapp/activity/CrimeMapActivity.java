package com.example.crimemappingapp.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;

import com.example.crimemappingapp.R;
import com.example.crimemappingapp.fragment.AddCrimeFragment;
import com.example.crimemappingapp.fragment.DatePickerFragment;
import com.example.crimemappingapp.utils.DatabaseHelper;
import com.example.crimemappingapp.utils.DateUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class CrimeMapActivity extends AppCompatActivity implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;

    private Location mCurrentLocation;

    private GoogleMap mMap;
    private boolean isMapReady;

    private boolean isAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_map);

        isAdmin = getIntent().getExtras().getBoolean("isAdmin");

        // TEMP CRIME TYPE SPINNER
        Spinner crimeTypeSpinner = (Spinner) findViewById(R.id.crime_type_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, DatabaseHelper.retrieveAllCrimeTypes());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        crimeTypeSpinner.setAdapter(adapter);

        // TEMP FROM YEAR SPINNER
        Button fromYearSpinner = (Button) findViewById(R.id.from_date_button);
        fromYearSpinner.setOnClickListener(DatePickerFragment.createDatePickerOnClickListener(getFragmentManager()));

        // TEMP FROM YEAR SPINNER
        Button toYearSpinner = (Button) findViewById(R.id.to_date_button);
        toYearSpinner.setOnClickListener(DatePickerFragment.createDatePickerOnClickListener(getFragmentManager()));

        Button addCrimeButton = (Button) findViewById(R.id.add_crime_button);
        addCrimeButton.setVisibility(isAdmin ? View.VISIBLE: View.INVISIBLE);
        addCrimeButton.setOnClickListener(createAddCrimeOnClickListener());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
            return;
        }

        markCurrentLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 200: {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    markCurrentLocation();
                }
            }
        }
    }

    private void markCurrentLocation() throws SecurityException {
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (isMapReady && mCurrentLocation != null) {
            LatLng myLaLn = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

            CameraPosition camPos = new CameraPosition.Builder().target(myLaLn)
                    .zoom(15)
                    .bearing(45)
                    .tilt(0)
                    .build();

            CameraUpdate camUpd3 = CameraUpdateFactory.newCameraPosition(camPos);

            mMap.addMarker(new MarkerOptions().position(myLaLn).title("Current Location"));

            mMap.moveCamera(camUpd3);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        isMapReady = true;
        mMap = googleMap;
        mGoogleApiClient.connect();
    }

    private View.OnClickListener createAddCrimeOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = AddCrimeFragment.newInstance(
                        R.string.alert_dialog_add_crime);
                newFragment.show(getFragmentManager(), "addCrime");
            }
        };
    }

    public void doPositiveClick() {
    }

    public void doNegativeClick() {
    }
}
