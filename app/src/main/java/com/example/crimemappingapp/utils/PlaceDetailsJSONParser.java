package com.example.crimemappingapp.utils;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * http://wptrafficanalyzer.in/blog/selecting-google-place-from-autocompletetextview-and-marking-in-google-map-android-api-v2/
 */

public class PlaceDetailsJSONParser {

    /** Receives a JSONObject and returns a list */
    public List<HashMap<String,String>> parse(JSONObject jObject){

        Double lat = Double.valueOf(0);
        Double lng = Double.valueOf(0);

        HashMap<String, String> hm = new HashMap<>();
        List<HashMap<String, String>> list = new ArrayList<>();

        try {

            lat = (Double)jObject.getJSONObject("result").getJSONObject("geometry").getJSONObject("location").get("lat");
            lng = (Double)jObject.getJSONObject("result").getJSONObject("geometry").getJSONObject("location").get("lng");

        } catch (Exception e) {
            Log.e("Unable to parse JSON object", e.getMessage());
        }

        hm.put("lat", Double.toString(lat));
        hm.put("lng", Double.toString(lng));

        list.add(hm);
        return list;
    }
}
