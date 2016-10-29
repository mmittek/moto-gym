package com.example.mmittek.motogym;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import java.util.Observable;


/**
 * Created by mmittek on 10/28/16.
 */

public class DataFusion extends Observable implements SensorEventListener {

    double mAlpha = 0.8;
    double[] mGravity;
    double[] mLinearAcceleration;
    double[] mMagneticField;
    double[] mAbsoluteOrientation;
    double[] mAbsoluteAcceleration;

    double[] mAngle;
    long mLastAngleTimestamp;

    long mPrevAccSampleTimestamp;
    double mAccSamplingRateSPS;

    public DataFusion() {
        reset();
    }

    public void reset() {
        mGravity = new double[]{0,0,0};
        mLinearAcceleration = new double[]{0,0,0};
        mMagneticField = new double[]{0,0,0};
        mAbsoluteOrientation = new double[]{0,0,0};
        mAbsoluteAcceleration = new double[]{0,0,0};
        mAngle = new double[]{0,0,0};
        mPrevAccSampleTimestamp = 0;
        mAccSamplingRateSPS = 0;
        mLastAngleTimestamp = 0;
        setChanged();
        notifyObservers();
    }

    public final double getAccSamplingRate() {
        return mAccSamplingRateSPS;
    }

    public void feedAccelerationXYZ(double[] accelerationXYZ) {

        // SAMPLING RATE ESTIMATION
        long now = System.currentTimeMillis();
        double dt = now-mPrevAccSampleTimestamp;
        mPrevAccSampleTimestamp = now;
        double a = 0.9;
        mAccSamplingRateSPS = a*mAccSamplingRateSPS + (1-a)*1000/dt;


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

    public final double getMagnitude(final float[] vec) {
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


    public final double[] getAngle() {
        return mAngle;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        int sensorType = sensorEvent.sensor.getType();
        if(sensorType == Sensor.TYPE_ACCELEROMETER) {

        } else if(sensorType == Sensor.TYPE_GYROSCOPE) {

            if(mLastAngleTimestamp == 0) {
                for(int i=0; i<3; i++) {
                    mAngle[i] = sensorEvent.values[i];
                }
            } else {
                double magnitude = getMagnitude( sensorEvent.values );
                double dt =   (sensorEvent.timestamp/1000000L - mLastAngleTimestamp)/18.0;
                for(int i=0; i<3; i++) {
                    mAngle[i] += sensorEvent.values[i]*dt;   // rectangular term
                }

            }
            mLastAngleTimestamp = sensorEvent.timestamp/1000000L;
            setChanged();
            notifyObservers();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
