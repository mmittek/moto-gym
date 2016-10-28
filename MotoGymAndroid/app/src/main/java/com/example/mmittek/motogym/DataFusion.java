package com.example.mmittek.motogym;
import java.util.Observable;


/**
 * Created by mmittek on 10/28/16.
 */

public class DataFusion extends Observable {

    double mAlpha = 0.8;
    double[] mGravity;

    public DataFusion() {
        mGravity = new double[]{0,0,0};
    }

    public void feedAccelerationXYZ(double[] accelerationXYZ) {
        for(int i=0; i<3; i++) {
            mGravity[i] = mAlpha*accelerationXYZ[i] + (1-mAlpha)*accelerationXYZ[i];
        }
        setChanged();
        notifyObservers();
    }

    public final double[] getGravity() {
        return mGravity;
    }
}
