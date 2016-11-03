package com.example.mmittek.motogym;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by mmittek on 10/28/16.
 */

public class FragmentViz2D extends Fragment implements Observer {

    Vector2Plot mVectorPlotXY;
    Vector2Plot mVectorPlotZY;
    Vector2Plot mVectorPlotZX;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_viz2d, container, false);
        return rootView;
    }


    public void onViewCreated(View view, Bundle savedInstanceState) {
        mVectorPlotXY = (Vector2Plot) view.findViewById(R.id.xy_vector_plot);
        mVectorPlotZY = (Vector2Plot) view.findViewById(R.id.zy_vector_plot);
        mVectorPlotZX = (Vector2Plot) view.findViewById(R.id.zx_vector_plot);

    }

    public void update(Observable observable, Object o) {
        if(observable instanceof SensorFusion) {
            SensorFusion sensorFusion = (SensorFusion) observable;
            float[] fusedOrientation = sensorFusion.getFusedOrientation();

            mVectorPlotXY.setVector(new double[]{ fusedOrientation[0], fusedOrientation[1] });
            mVectorPlotZY.setVector(new double[]{ fusedOrientation[2], fusedOrientation[1] });
            mVectorPlotZX.setVector(new double[]{ fusedOrientation[2], fusedOrientation[0] });

        }

    }
}