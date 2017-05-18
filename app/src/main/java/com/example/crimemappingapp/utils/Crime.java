package com.example.crimemappingapp.utils;

import android.graphics.Point;

import com.google.android.gms.maps.model.LatLng;

public class Crime {
    private int id;
    private String location;
    private LatLng latLng;
    private long dateMillis;
    private CrimeTypes crimeType;

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

    public CrimeTypes getCrimeType() {
        return crimeType;
    }

    public void setCrimeType(CrimeTypes crimeType) {
        this.crimeType = crimeType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
