package com.example.crimemappingapp.utils;

public class CrimeSearch {
    private final int crimeTypeId;
    private final long from;
    private final long to;

    public CrimeSearch(int crimeTypeId, long from, long to) {
        this.crimeTypeId = crimeTypeId;
        this.from = from;
        this.to = to;
    }

    public int getCrimeTypeId() {
        return crimeTypeId;
    }

    public long getFrom() {
        return from;
    }

    public long getTo() {
        return to;
    }
}