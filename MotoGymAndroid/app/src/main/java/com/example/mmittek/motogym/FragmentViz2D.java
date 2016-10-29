package com.example.mmittek.motogym;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by mmittek on 10/28/16.
 */

public class FragmentViz2D extends Fragment {

    Vector2Plot mVectorPlotXY;
    Vector2Plot mVectorPlotYZ;
    Vector2Plot mVectorPlotZX;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_viz2d, container, false);
        return rootView;
    }


    public void onViewCreated(View view, Bundle savedInstanceState) {
        mVectorPlotXY = (Vector2Plot) view.findViewById(R.id.xy_vector_plot);
        mVectorPlotYZ = (Vector2Plot) view.findViewById(R.id.yz_vector_plot);
        mVectorPlotZX = (Vector2Plot) view.findViewById(R.id.zx_vector_plot);

    }
}