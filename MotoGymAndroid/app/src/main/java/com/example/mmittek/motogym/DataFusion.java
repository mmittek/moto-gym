package com.example.mmittek.motogym;
import java.util.Observable;


/**
 * Created by mmittek on 10/28/16.
 */

public class DataFusion extends Observable {

    double mAlpha = 0.8;
    double[] mGravity;
    double[] mLinearAcceleration;
    double[] mMagneticField;
    double[] mAbsoluteOrientation;
    double[] mAbsoluteAcceleration;

    public DataFusion() {

        mGravity = new double[]{0,0,0};
        mLinearAcceleration = new double[]{0,0,0};
        mMagneticField = new double[]{0,0,0};
        mAbsoluteOrientation = new double[]{0,0,0};
        mAbsoluteAcceleration = new double[]{0,0,0};
    }

    public void feedAccelerationXYZ(double[] accelerationXYZ) {
        for(int i=0; i<3; i++) {
            mGravity[i] = mAlpha*accelerationXYZ[i] + (1-mAlpha)*accelerationXYZ[i];
        }

        double gravityMagnitude = Math.sqrt(mGravity[0]*mGravity[0] + mGravity[1]*mGravity[1] + mGravity[2]*mGravity[2]);


        for(int i=0; i<3; i++) {
            mLinearAcceleration[i] = accelerationXYZ[i] - 9.80665*mGravity[i]/gravityMagnitude;
        }

        setChanged();
        notifyObservers();
    }

    public final double getMagnitude(final double[] vec) {
        return Math.sqrt(vec[0]*vec[0] + vec[1]*vec[1] + vec[2]*vec[2]);

    }

    public final double[] getAbsoluteAcceleration() {
        return mAbsoluteAcceleration;
    }

    public final double[] getAbsoluteOrientation() {
        return mAbsoluteOrientation;
    }

    public void feedMagneticField(final double[] magneticFieldXYZ) {
        mMagneticField = magneticFieldXYZ;
        double magneticFieldMagnitude = getMagnitude(magneticFieldXYZ);
        double accelerationMagnitude = getMagnitude(mLinearAcceleration);
        for (int i=0; i<3; i++) {
            mAbsoluteOrientation[i] = mGravity[i] - 9.80665*magneticFieldXYZ[i] / magneticFieldMagnitude;
 //           mAbsoluteAcceleration[i] = mLinearAcceleration[i] - accelerationMagnitude*magneticFieldXYZ[i]/magneticFieldMagnitude;
        }
        setChanged();
        notifyObservers();
    }

    public final double[] getMagneticField() {
        return mMagneticField;
    }

    public final double[] getLinearAcceleration() {
        return mLinearAcceleration;
    }

    public final double[] getGravity() {
        return mGravity;
    }
}
