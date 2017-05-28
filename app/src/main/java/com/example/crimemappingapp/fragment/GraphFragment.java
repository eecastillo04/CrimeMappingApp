package com.example.crimemappingapp.fragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.crimemappingapp.R;
import com.example.crimemappingapp.activity.CrimeMapActivity;
import com.example.crimemappingapp.utils.Crime;
import com.example.crimemappingapp.utils.CrimeTypes;
import com.example.crimemappingapp.utils.DateUtils;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.Series;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class GraphFragment extends DialogFragment {

    private Map<String, Integer> crimeFrequencyMap = new HashMap<>();
    private List<String> crimeFrequencyMapKeyList = new ArrayList<>();
    private int minYear;

    public static GraphFragment newInstance(int title, Collection<Crime> visibleCrimes) {
        GraphFragment frag = new GraphFragment();
        Bundle args = new Bundle();
        args.putInt("title", title);
        frag.setArguments(args);
        frag.createGraphData(visibleCrimes);
        return frag;
    }

    private void createGraphData(Collection<Crime> visibleCrimes) {
        minYear = 10000;
        int maxYear = 0;
        List<Crime> visibleCrimeList = new ArrayList<>(visibleCrimes);

        Collections.sort(visibleCrimeList, new Comparator<Crime>() {

            @Override
            public int compare(Crime c1, Crime c2) {
                return c1.getDateMillis() > c2.getDateMillis() ? 0: 1;
            }
        });

        Map<Crime, String> crimeYearMap = new HashMap<>();
        for(Crime crime: visibleCrimeList) {
            int year = DateUtils.getYear(crime.getDateMillis());
            minYear = Math.min(minYear, year);
            maxYear = Math.max(maxYear, year);
            crimeYearMap.put(crime, String.valueOf(year));
        }

        int diff = maxYear - minYear;
        for(int i=minYear; i<=maxYear; i++) {
            if(diff <= 2) {
                for(int j=0; j<12; j++) {
                    String key = DateUtils.getMonthString(j) + " " + i;
                    crimeFrequencyMap.put(key, 0);
                    crimeFrequencyMapKeyList.add(key);
                    Log.e(key, key);
                }
            } else {
                String key = String.valueOf(i);
                crimeFrequencyMap.put(key, 0);
                crimeFrequencyMapKeyList.add(key);
                Log.e(key, key);
            }
        }

        for(Crime crime: visibleCrimeList) {
            String key = "";
            if(diff <= 2) {
                key = DateUtils.getMonthString(crime.getDateMillis()) + " " + crimeYearMap.get(crime);
            } else {
                key = crimeYearMap.get(crime);
            }

            crimeFrequencyMap.put(key, crimeFrequencyMap.get(key) + 1);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = getArguments().getInt("title");

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_graph, null);

        initView(v);

        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setView(v)
                .setNeutralButton(R.string.alert_dialog_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // do nothing
                            }
                        }
                )
                .create();
    }

    private void initView(View v) {
        List<DataPoint> dataPointList = new ArrayList<>();

        int crimeFrequencyMapSize = crimeFrequencyMapKeyList.size();
        for(int i=0; i<crimeFrequencyMapSize; i++) {
            dataPointList.add(new DataPoint(i, crimeFrequencyMap.get(crimeFrequencyMapKeyList.get(i))));
        }

        GraphView graph = (GraphView) v.findViewById(R.id.graph);

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPointList.toArray(new DataPoint[dataPointList.size()]));
        graph.addSeries(series);
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(10);

        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if(isValueX) {
                    return crimeFrequencyMapKeyList.get((int) value);
                } else {
                    if(value != 0 && value % (int) value != 0) return "";
                    return super.formatLabel(value, isValueX);
                }
            }
        });

        graph.getViewport().setScrollable(true);
    }

}