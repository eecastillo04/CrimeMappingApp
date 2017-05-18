package com.example.crimemappingapp.utils;

import java.util.ArrayList;
import java.util.List;

public enum CrimeTypes {
    ARSON(0, "Arson", "#836953"),
    ASSAULT(1, "Assault", "#ffd1dc"),
    DRUG_RELATED(2, "Drug Related", "#ffb347"),
    MURDER(3, "Murder", "#fdfd96"),
    RAPE(4, "Rape", "#77dd77"),
    ROBBERY(5, "Robbery", "#aec6cf"),
    THEFT(6, "Theft", "#99a8d1"),
    OTHERS(7, "Others", "#cb99c9");

    private final int id;
    private final String displayName;
    private final String hexColor;

    CrimeTypes(int id, String displayName, String hexColor) {
        this.id = id;
        this.displayName = displayName;
        this.hexColor = hexColor;
    }

    public int getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getHexColor() {
        return hexColor;
    }

    public static CrimeTypes getCrimeType(int id) {
        if(ARSON.id == id) return ARSON;
        if(ASSAULT.id == id) return ASSAULT;
        if(DRUG_RELATED.id == id) return DRUG_RELATED;
        if(MURDER.id == id) return MURDER;
        if(RAPE.id == id) return RAPE;
        if(ROBBERY.id == id) return ROBBERY;
        if(THEFT.id == id) return THEFT;
        return OTHERS;
    }

    public static CrimeTypes getCrimeType(String crimeTypeName) {
        String aCrimeTypeName = crimeTypeName.toLowerCase();
        if(ARSON.displayName.toLowerCase().equals(aCrimeTypeName)) return ARSON;
        if(ASSAULT.displayName.equals(aCrimeTypeName)) return ASSAULT;
        if(DRUG_RELATED.displayName.equals(aCrimeTypeName)) return DRUG_RELATED;
        if(MURDER.displayName.equals(aCrimeTypeName)) return MURDER;
        if(RAPE.displayName.equals(aCrimeTypeName)) return RAPE;
        if(ROBBERY.displayName.equals(aCrimeTypeName)) return ROBBERY;
        if(THEFT.displayName.equals(aCrimeTypeName)) return THEFT;
        return OTHERS;
    }

    public static String getCrimeTypeName(int id) {
        if(ARSON.id == id) return ARSON.displayName;
        if(ASSAULT.id == id) return ASSAULT.displayName;
        if(DRUG_RELATED.id == id) return DRUG_RELATED.displayName;
        if(MURDER.id == id) return MURDER.displayName;
        if(RAPE.id == id) return RAPE.displayName;
        if(ROBBERY.id == id) return ROBBERY.displayName;
        if(THEFT.id == id) return THEFT.displayName;
        return OTHERS.displayName;
    }

    public static int getCrimeTypeId(String crimeTypeName) {
        String aCrimeTypeName = crimeTypeName.toLowerCase();
        if(ARSON.displayName.toLowerCase().equals(aCrimeTypeName)) return ARSON.id;
        if(ASSAULT.displayName.equals(aCrimeTypeName)) return ASSAULT.id;
        if(DRUG_RELATED.displayName.equals(aCrimeTypeName)) return DRUG_RELATED.id;
        if(MURDER.displayName.equals(aCrimeTypeName)) return MURDER.id;
        if(RAPE.displayName.equals(aCrimeTypeName)) return RAPE.id;
        if(ROBBERY.displayName.equals(aCrimeTypeName)) return ROBBERY.id;
        if(THEFT.displayName.equals(aCrimeTypeName)) return THEFT.id;
        return OTHERS.id;
    }

    public static List<String> getAllDisplayNames() {
        List<String> displayNames = new ArrayList<>();
        for(CrimeTypes crimeType: values()) {
            displayNames.add(crimeType.getDisplayName());
        }
        return displayNames;
    }
}
