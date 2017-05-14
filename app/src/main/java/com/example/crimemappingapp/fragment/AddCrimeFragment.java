package com.example.crimemappingapp.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import com.example.crimemappingapp.R;
import com.example.crimemappingapp.activity.CrimeMapActivity;
import com.example.crimemappingapp.utils.DatabaseHelper;
import com.example.crimemappingapp.utils.PlaceDetailsJSONParser;
import com.example.crimemappingapp.utils.PlaceJSONParser;
import com.google.android.gms.maps.SupportMapFragment;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddCrimeFragment extends DialogFragment {

    AppCompatAutoCompleteTextView atvPlaces;

    DownloadTask placesDownloadTask;
    DownloadTask placeDetailsDownloadTask;
    ParserTask placesParserTask;
    ParserTask placeDetailsParserTask;

    final int PLACES=0;
    final int PLACES_DETAILS=1;

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
        // Getting a reference to the AppCompatAutoCompleteTextView
        atvPlaces = (AppCompatAutoCompleteTextView) v.findViewById(R.id.atv_places);
        atvPlaces.setThreshold(1);


        // Adding textchange listener
        atvPlaces.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Creating a DownloadTask to download Google Places matching "s"
                placesDownloadTask = new DownloadTask(PLACES);

                // Getting url to the Google Places Autocomplete api
                String url = getAutoCompleteUrl(s.toString());

                // Start downloading Google Places
                // This causes to execute doInBackground() of DownloadTask class
                placesDownloadTask.execute(url);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });

        // Setting an item click listener for the AutoCompleteTextView dropdown list
        atvPlaces.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int index,
                                    long id) {

                ListView lv = (ListView) arg0;
                SimpleAdapter adapter = (SimpleAdapter) arg0.getAdapter();

                HashMap<String, String> hm = (HashMap<String, String>) adapter.getItem(index);

                // Creating a DownloadTask to download Places details of the selected place
                placeDetailsDownloadTask = new DownloadTask(PLACES_DETAILS);

                // Getting url to the Google Places details api
                String url = getPlaceDetailsUrl(hm.get("reference"));

                // Start downloading Google Place Details
                // This causes to execute doInBackground() of DownloadTask class
                placeDetailsDownloadTask.execute(url);

            }
        });

        Button dateHappenedSpinner = (Button) v.findViewById(R.id.date_button);
        dateHappenedSpinner.setOnClickListener(DatePickerFragment.createDatePickerOnClickListener(getFragmentManager()));

        Spinner crimeTypeSpinner = (Spinner) v.findViewById(R.id.crime_type_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, DatabaseHelper.retrieveAllCrimeTypes());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        crimeTypeSpinner.setAdapter(adapter);
    }

    private String getAutoCompleteUrl(String place){

        // Obtain browser key from https://code.google.com/apis/console
        String key = "key=AIzaSyAF2ML9qhl-K-VP2TMztzWGR0azEwgOZX0";

        // place to be be searched
        String input = "input="+place;

        // place type to be searched
        String types = "types=geocode";

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = input+"&"+types+"&"+sensor+"&"+key;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/place/autocomplete/"+output+"?"+parameters;

        return url;
    }

    private String getPlaceDetailsUrl(String ref){

        // Obtain browser key from https://code.google.com/apis/console
        String key = "key=AIzaSyAF2ML9qhl-K-VP2TMztzWGR0azEwgOZX0";

        // reference of place
        String reference = "reference="+ref;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = reference+"&"+sensor+"&"+key;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/place/details/"+output+"?"+parameters;

        return url;
    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        private int downloadType=0;

        // Constructor
        public DownloadTask(int type){
            this.downloadType = type;
        }

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            switch(downloadType){
                case PLACES:
                    // Creating ParserTask for parsing Google Places
                    placesParserTask = new ParserTask(PLACES);

                    // Start parsing google places json data
                    // This causes to execute doInBackground() of ParserTask class
                    placesParserTask.execute(result);

                    break;

                case PLACES_DETAILS :
                    // Creating ParserTask for parsing Google Places
                    placeDetailsParserTask = new ParserTask(PLACES_DETAILS);

                    // Starting Parsing the JSON string
                    // This causes to execute doInBackground() of ParserTask class
                    placeDetailsParserTask.execute(result);
            }
        }
    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String,String>>>{

        int parserType = 0;

        public ParserTask(int type){
            this.parserType = type;
        }

        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<HashMap<String, String>> list = null;

            try{
                jObject = new JSONObject(jsonData[0]);

                switch(parserType){
                    case PLACES :
                        PlaceJSONParser placeJsonParser = new PlaceJSONParser();
                        // Getting the parsed data as a List construct
                        list = placeJsonParser.parse(jObject);
                        break;
                    case PLACES_DETAILS :
                        PlaceDetailsJSONParser placeDetailsJsonParser = new PlaceDetailsJSONParser();
                        // Getting the parsed data as a List construct
                        list = placeDetailsJsonParser.parse(jObject);
                }

            }catch(Exception e){
                Log.d("Exception",e.toString());
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> result) {

            switch(parserType){
                case PLACES :
                    String[] from = new String[] { "description"};
                    int[] to = new int[] { android.R.id.text1 };

                    // Creating a SimpleAdapter for the AutoCompleteTextView
                    SimpleAdapter adapter = new SimpleAdapter(getActivity().getBaseContext(), result, android.R.layout.simple_list_item_1, from, to);

                    // Setting the adapter
                    atvPlaces.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    break;
                case PLACES_DETAILS :
                    HashMap<String, String> hm = result.get(0);

                    // Getting latitude from the parsed data
                    double latitude = Double.parseDouble(hm.get("lat"));

                    // Getting longitude from the parsed data
                    double longitude = Double.parseDouble(hm.get("lng"));

//                    // Getting reference to the SupportMapFragment of the activity_main.xml
//                    SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
//
//                    // Getting GoogleMap from SupportMapFragment
//                    googleMap = fm.getMap();
//
//                    LatLng point = new LatLng(latitude, longitude);
//
//                    CameraUpdate cameraPosition = CameraUpdateFactory.newLatLng(point);
//                    CameraUpdate cameraZoom = CameraUpdateFactory.zoomBy(5);
//
//                    // Showing the user input location in the Google Map
//                    googleMap.moveCamera(cameraPosition);
//                    googleMap.animateCamera(cameraZoom);
//
//                    MarkerOptions options = new MarkerOptions();
//                    options.position(point);
//                    options.title("Position");
//                    options.snippet("Latitude:"+latitude+",Longitude:"+longitude);
//
//                    // Adding the marker in the Google Map
//                    googleMap.addMarker(options);

                    break;
            }
        }
    }
}
