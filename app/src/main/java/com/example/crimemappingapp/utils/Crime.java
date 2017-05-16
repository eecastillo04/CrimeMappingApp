package com.example.crimemappingapp.utils;

import android.graphics.Point;

import com.google.android.gms.maps.model.LatLng;

public class Crime {
    private String location;
    private LatLng latLng;
    private long dateMillis;
    private int crimeTypeId;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public long getDateMillis() {
        return dateMillis;
    }

    public void setDateMillis(long dateMillis) {
        this.dateMillis = dateMillis;
    }

    public int getCrimeTypeId() {
        return crimeTypeId;
    }

    public void setCrimeTypeId(int crimeTypeId) {
        this.crimeTypeId = crimeTypeId;
    }
}
