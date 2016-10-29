package com.example.mmittek.motogym;

import java.util.ArrayList;
import java.util.Observable;


class Sample {

    String mName;
    long mTimestamp;
    double[] mValues;

    public Sample(final String name, final long timestamp, final double[] values) {
        mName = name;
        mTimestamp = timestamp;
        mValues = values;
    }
}



public class DataQueue extends Observable {
    ArrayList<Sample> mSamplesCollection;
    public DataQueue() {
        mSamplesCollection = new ArrayList<Sample>();
    }


    public void addSample() {

    }
}
