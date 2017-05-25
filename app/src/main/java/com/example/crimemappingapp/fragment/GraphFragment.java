package com.example.crimemappingapp.fragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
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
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class GraphFragment extends DialogFragment {

    private Map<Integer, Integer> crimeYearFrequencyMap = new HashMap<>();
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
        for(Crime crime: visibleCrimes) {
            int year = DateUtils.getYear(crime.getDateMillis());
            if(!crimeYearFrequencyMap.containsKey(year)) {
                crimeYearFrequencyMap.put(year, 0);
                minYear = Math.min(minYear, year);
            }
            crimeYearFrequencyMap.put(year, crimeYearFrequencyMap.get(year)+1);
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
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for(int i=minYear; i<=currentYear; i++) {
            int freq = crimeYearFrequencyMap.containsKey(i) ? crimeYearFrequencyMap.get(i): 0;
            dataPointList.add(new DataPoint(i, freq));
            Log.e(String.valueOf(i), String.valueOf(freq));
        }

        GraphView graph = (GraphView) v.findViewById(R.id.graph);

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPointList.toArray(new DataPoint[dataPointList.size()]));
        graph.addSeries(series);

        graph.getViewport().setScalable(true);
        graph.getViewport().setScrollable(true);
    }

}
