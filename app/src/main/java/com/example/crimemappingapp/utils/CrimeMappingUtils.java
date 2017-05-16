package com.example.crimemappingapp.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrimeMappingUtils {
    private static Map<Integer, String> crimeTypeMap = new HashMap<>();

    public static void setCrimeTypeMap(HashMap<Integer,String> aCrimeTypeMap) {
        crimeTypeMap = aCrimeTypeMap;
    }

    public static Map<Integer, String> getCrimeTypeMap() {
        return crimeTypeMap;
    }

    public static List<String> getSortedCrimeTypes() {
        List<String> crimeTypeNames = new ArrayList<>(crimeTypeMap.values());
        Collections.sort(crimeTypeNames);
        return crimeTypeNames;
    }
}
