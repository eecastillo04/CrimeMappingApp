package com.example.crimemappingapp.activity;

import android.Manifest;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.crimemappingapp.R;
import com.example.crimemappingapp.fragment.EditCrimeFragment;
import com.example.crimemappingapp.fragment.CustomInfoWindowAdapter;
import com.example.crimemappingapp.fragment.DatePickerFragment;
import com.example.crimemappingapp.utils.Crime;
import com.example.crimemappingapp.utils.CrimeMappingUtils;
import com.example.crimemappingapp.utils.CrimeTypes;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.R.id.list;

public class CrimeMapActivity extends AppCompatActivity implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;

    private Location mCurrentLocation;

    private GoogleMap mMap;
    private boolean isMapReady;

    private boolean isAdmin;

    private static final int READ_REQUEST_CODE = 42;

    private List<FetchLatLongFromService> latLngFetcherList = new ArrayList<>();
    private List<Crime> importCrimeList = new ArrayList<>();
    private Spinner crimeTypeSpinner;
    private Button fromYearSpinner;
    private Button toYearSpinner;

    private Map<Integer, Marker> crimeMarkersMap = new HashMap<>();
    private Map<Integer, Crime> visibleCrimesMap = new HashMap<>();
    private boolean isEnabledHeatmap;
    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_map);

        isAdmin = getIntent().getExtras().getBoolean("isAdmin");

        crimeTypeSpinner = (Spinner) findViewById(R.id.crime_type_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, CrimeTypes.getAllDisplayNames());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        crimeTypeSpinner.setAdapter(adapter);

        fromYearSpinner = (Button) findViewById(R.id.from_date_button);
        fromYearSpinner.setOnClickListener(DatePickerFragment.createDatePickerOnClickListener(getFragmentManager()));

        toYearSpinner = (Button) findViewById(R.id.to_date_button);
        toYearSpinner.setOnClickListener(DatePickerFragment.createDatePickerOnClickListener(getFragmentManager()));
        
        Button searchButton = (Button) findViewById(R.id.search_button);
        searchButton.setOnClickListener(createSearchCrimesOnClickListener());

        Button importButton = (Button) findViewById(R.id.import_button);
        importButton.setOnClickListener(createImportOnClickListener());

        Button clearMapButton = (Button) findViewById(R.id.clear_map_button);
        clearMapButton.setOnClickListener(createClearMapOnClickListener());

        Button heatmapButton = (Button) findViewById(R.id.heatmap_button);
        heatmapButton.setOnClickListener(createHeatmapOnClickListener());

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
        CustomInfoWindowAdapter infoWindowAdapter = new CustomInfoWindowAdapter(this, isAdmin);
        mMap.setInfoWindowAdapter(infoWindowAdapter);
        mMap.setOnInfoWindowClickListener(infoWindowAdapter);
        mGoogleApiClient.connect();
    }

    private View.OnClickListener createSearchCrimesOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    List<Crime> crimeList = DatabaseHelper.retrieveAllCrimes(
                            CrimeTypes.getCrimeTypeId(crimeTypeSpinner.getSelectedItem().toString()),
                            DateUtils.convertToMillis(fromYearSpinner.getText().toString()),
                            DateUtils.convertToMillis(toYearSpinner.getText().toString())
                    );
                    markCrimesOnMap(crimeList);
                } catch(Exception e) {
                    // TODO alert check input requirements for searching
                }
            }
        };
    }

    private View.OnClickListener createClearMapOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(Marker marker: crimeMarkersMap.values()) {
                    marker.remove();
                }

                visibleCrimesMap.clear();
                crimeMarkersMap.clear();
            }
        };
    }

    private View.OnClickListener createHeatmapOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEnabledHeatmap) {
                    mOverlay.remove();
                } else {
                    List<LatLng> latLngList = new ArrayList<>();

                    for(Crime crime: visibleCrimesMap.values()) {
                        latLngList.add(crime.getLatLng());
                    }

                    // Create a heat map tile provider, passing it the latlngs of the police stations.
                    mProvider = new HeatmapTileProvider.Builder()
                            .data(latLngList)
                            .build();
                    // Add a tile overlay to the map, using the heat map tile provider.
                    mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
                }
                
                isEnabledHeatmap = !isEnabledHeatmap;
            }
        };
    }

    private View.OnClickListener createImportOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performFileSearch();
            }
        };
    }

    private void markCrimesOnMap(List<Crime> crimeList) {
        for(Crime crime: crimeList) {
            int crimeId = crime.getId();
            if(visibleCrimesMap.containsKey(crimeId)) continue;

            MarkerOptions options = new MarkerOptions();
            options.position(crime.getLatLng());
            options.icon(getMarkerIcon(crime.getCrimeType().getHexColor()));

            Marker marker = mMap.addMarker(options);
            marker.setTag(crime);

            visibleCrimesMap.put(crimeId, crime);
            crimeMarkersMap.put(crimeId, marker);
        }
    }

    // method definition
    public BitmapDescriptor getMarkerIcon(String color) {
        float[] hsv = new float[3];
        Color.colorToHSV(Color.parseColor(color), hsv);
        return BitmapDescriptorFactory.defaultMarker(hsv[0]);
    }

    /**
     * Fires an intent to spin up the "file chooser" UI and select an image.
     */
    public void performFileSearch() {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("*/*");

        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                try {
                    String crimeDataString = readTextFromUri(uri);
                    if(CrimeMappingUtils.haveNetworkConnection(getBaseContext())) {
                        parseCrimes(crimeDataString);
                        new ImportParsedCrimesToDB().execute();
                    } else {
                        // TODO alert that internet connection is required
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String readTextFromUri(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        int count = 0;
        while ((line = reader.readLine()) != null) {
            if(count != 0) {
                stringBuilder.append(line);
                stringBuilder.append("\n");
            }
            count++;
        }
        return stringBuilder.toString();
    }

    private List<Crime> parseCrimes(String crimeDataString) throws IOException {
        List<Crime> crimeList = new ArrayList<>();
        String[] crimeDataStringLines = crimeDataString.split("\n");
        for(String crimeDataStringLine: crimeDataStringLines) {
            String[] crimeData = crimeDataStringLine.split("\t");
            String crimeTypeName = crimeData[0];
            String location = crimeData[1];
            String dateString = crimeData[2];

            Crime crime = new Crime();
            crime.setCrimeType(CrimeTypes.getCrimeType(crimeTypeName));
            crime.setDateMillis(DateUtils.convertToMillis(dateString));
            crime.setLocation(location);
            FetchLatLongFromService latLngFetcher = new FetchLatLongFromService(crime);
            latLngFetcherList.add(latLngFetcher);
            latLngFetcher.execute();
        }

        return crimeList;
    }

    public class ImportParsedCrimesToDB extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            // check if fetching latlng is already done
            while(!latLngFetcherList.isEmpty());

            DatabaseHelper.insertCrimeList(importCrimeList);

            return null;
        }
    }

    public class FetchLatLongFromService extends
            AsyncTask<Void, Void, StringBuilder> {
        Crime crime;

        public FetchLatLongFromService(Crime crime) {
            this.crime = crime;

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            this.cancel(true);
        }

        @Override
        protected StringBuilder doInBackground(Void... params) {
            try {
                HttpURLConnection conn = null;
                StringBuilder jsonResults = new StringBuilder();
                String googleMapUrl = "http://maps.googleapis.com/maps/api/geocode/json?address="
                        + this.crime.getLocation().replaceAll("\\s", "") + "&sensor=false";

                URL url = new URL(googleMapUrl);
                conn = (HttpURLConnection) url.openConnection();
                InputStreamReader in = new InputStreamReader(
                        conn.getInputStream());
                int read;
                char[] buff = new char[1024];
                while ((read = in.read(buff)) != -1) {
                    jsonResults.append(buff, 0, read);
                }
                return jsonResults;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(StringBuilder result) {
            super.onPostExecute(result);
            LatLng point = null;
            try {
                JSONObject jsonObj = new JSONObject(result.toString());
                JSONArray resultJsonArray = jsonObj.getJSONArray("results");

                // Extract the Place descriptions from the results
                // resultList = new ArrayList<String>(resultJsonArray.length());

                JSONObject before_geometry_jsonObj = resultJsonArray
                        .getJSONObject(0);

                JSONObject geometry_jsonObj = before_geometry_jsonObj
                        .getJSONObject("geometry");

                JSONObject location_jsonObj = geometry_jsonObj
                        .getJSONObject("location");

                String lat_helper = location_jsonObj.getString("lat");
                double lat = Double.valueOf(lat_helper);


                String lng_helper = location_jsonObj.getString("lng");
                double lng = Double.valueOf(lng_helper);


                point = new LatLng(lat, lng);

            } catch (JSONException e) {
                try {
                    point = getLocationFromAddress(crime.getLocation());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

            if(point != null) {
                crime.setLatLng(point);
                importCrimeList.add(crime);
            }

            latLngFetcherList.remove(this);
        }
    }

    private LatLng getLocationFromAddress(String strAddress) throws IOException {
        Geocoder coder = new Geocoder(this);
        List<Address> address;
        LatLng p1 = null;

        address = coder.getFromLocationName(strAddress,5);
        if (address==null) {
            return null;
        }
        Address location=address.get(0);
        location.getLatitude();
        location.getLongitude();

        p1 = new LatLng(location.getLatitude() * 1E6,
                location.getLongitude() * 1E6);
        return p1;
    }

    public void doPositiveClick(Crime crime) {
        DatabaseHelper.updateCrime(crime.getId(), crime.getCrimeType().getId(), crime.getDateMillis());
    }

    public void doNegativeClick(int crimeId) {
        visibleCrimesMap.remove(crimeId);
        crimeMarkersMap.get(crimeId).remove();
        crimeMarkersMap.remove(crimeId);
        DatabaseHelper.deleteCrime(crimeId);
    }

}
