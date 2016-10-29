package com.example.mmittek.motogym;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import java.util.Observable;

public class DataBuffer extends Observable implements SensorEventListener, LocationListener {


    protected long mRecordsCounter = 0;
    protected long mTimestamp;
    protected float mAccX;
    protected float mAccY;
    protected float mAccZ;
    protected float mLAccX;
    protected float mLAccY;
    protected float mLAccZ;
    protected float mGravX;
    protected float mGravY;
    protected float mGravZ;
    protected float mMagFieldX;
    protected float mMagFieldY;
    protected float mMagFieldZ;
    protected float mSpeedGPS;

    protected float mGyroX;
    protected float mGyroY;
    protected float mGyroZ;

    protected float[] mRotMat;
    protected float[] mOrientAngles;

    protected double mLatGPS;
    protected double mLonGPS;
    protected double mAltGPS;
    protected double mAccGPS;


    public DataBuffer() {
        reset();
    }

    public void reset() {


        mRecordsCounter = 0;
        mTimestamp = 0;
        mAccX = 0;
        mAccY = 0;
        mAccZ = 0;
        mLAccX = 0;
        mLAccY = 0;
        mLAccZ = 0;
        mGravX= 0;
        mGravY = 0;
        mGravZ = 0;
        mMagFieldX = 0;
        mMagFieldY = 0;
        mMagFieldZ = 0;
        mSpeedGPS = 0;
        mLatGPS = 0;
        mLonGPS = 0;
        mAltGPS = 0;
        mAccGPS = 0;
        mGyroX = 0;
        mGyroY = 0;
        mGyroZ = 0;

        mRotMat = new float[9];
        for(int i=0; i<9; i++) {
            mRotMat[i] = 0;
        }

        mOrientAngles = new float[3];
        for(int i=0; i<3; i++) {
            mOrientAngles[i] = 0;
        }
    }


    public static final long convertToAbsoluteTimestampMillis(long sensorTimestampNs) {
        long timeInMillis = (System.currentTimeMillis()
                + (sensorTimestampNs- System.nanoTime())/1000000L);
        return timeInMillis;
    }

    public final String getCSVHeader() {
        String header = "timestamp, accx, accy, accz, gyrox, gyroy, gyroz, laccx, laccy, laccz, gravx, gravy, gravz, magfieldx, magfieldy, magfieldz, anglex, angley, anglez, rot0, rot1, rot2, rot3, rot4, rot5, rot6, rot7, rot8, speedgps, latgps, longps, altgps, accgps\r\n";
        return header;
    }

    public final String getCSVRecord() {
        return "" + mTimestamp + "," +
                mAccX + "," + mAccY + "," + mAccZ + "," +
                mGyroX + "," + mGyroY + "," + mGyroZ + "," +
                mLAccX + "," + mLAccY + "," + mLAccZ + "," +
                mGravX + "," + mGravY + "," + mGravZ + "," +
                mMagFieldX + "," + mMagFieldY + "," + mMagFieldZ + "," +
                mOrientAngles[0] + "," + mOrientAngles[1] + "," + mOrientAngles[2] + "," +
                mRotMat[0] + "," + mRotMat[1] + "," + mRotMat[2] + "," + mRotMat[3] + "," + mRotMat[4] + "," + mRotMat[5] + "," + mRotMat[6] + "," + mRotMat[7] + "," + mRotMat[8] + "," +
                mSpeedGPS + "," + mLatGPS + "," + mLonGPS + "," + mAltGPS + "," + mAccGPS +
                "\r\n";
    }


    public void setChanged() {
        super.setChanged();
        mRecordsCounter++;
    }

    public final float[] getRotMat() {
        return mRotMat;
    }

    public final float[] getOrientAngles() {
        return mOrientAngles;
    }

    public final long getRecordsCounter() {
        return mRecordsCounter;
    }

    public void setGyroXYZ(long timestamp, float[] xyz) {
        boolean changed = false;
        if(xyz[0] != mGyroX) { mGyroX = xyz[0]; changed = true;}
        if(xyz[1] != mGyroY) { mGyroY = xyz[1]; changed = true;}
        if(xyz[2] != mGyroZ) { mGyroZ = xyz[2]; changed = true;}
        if(changed) {
            mTimestamp = timestamp;
            setChanged();
            notifyObservers();
        }
    }


    public void setAccXYZ(long timestamp, float[] xyz) {
        boolean changed = false;
        if(xyz[0] != mAccX) { mAccX = xyz[0]; changed = true;}
        if(xyz[1] != mAccY) { mAccY = xyz[1]; changed = true;}
        if(xyz[2] != mAccZ) { mAccZ = xyz[2]; changed = true;}
        if(changed) {
            mTimestamp = timestamp;
           // updateOrientationAngles();  // only for magnetic field and accelerometer
            setChanged();
            notifyObservers();
        }
    }

    public final float[] getAccXYZ() {
        return new float[]{ mAccX, mAccY, mAccZ };
    }

    public void setLinearAccXYZ(long timestamp, float[] xyz) {
        boolean changed = false;
        if(xyz[0] != mLAccX) { mLAccX = xyz[0]; changed = true;}
        if(xyz[1] != mLAccY) { mLAccY = xyz[1]; changed = true;}
        if(xyz[2] != mLAccZ) { mLAccZ = xyz[2]; changed = true;}
        if(changed) {
            mTimestamp = timestamp;
            setChanged();
            notifyObservers();
        }
    }

    public void setGravXYZ(long timestamp, float[] xyz) {
        boolean changed = false;
        if(xyz[0] != mGravX) {mGravX = xyz[0]; changed = true;}
        if(xyz[1] != mGravY) {mGravY = xyz[1]; changed = true;}
        if(xyz[2] != mGravZ) {mGravZ = xyz[2]; changed = true;}
        if(changed) {
            mTimestamp = timestamp;
            setChanged();
            notifyObservers();
        }
    }

    public void setGPS(long timestamp, float speed, double lat, double lon, double alt, double acc) {
        boolean changed = false;
        if(speed != mSpeedGPS) { mSpeedGPS = speed; changed = true;}
        if(lat != mLatGPS) {mLatGPS = lat; changed = true; }
        if(lon != mLonGPS) { mLonGPS = lon; changed = true; }
        if(alt != mAltGPS) { mAltGPS = alt; changed = true; }
        if(acc != mAccGPS) { mAccGPS = acc; changed = true; }


        if(changed) {
            mTimestamp = timestamp;
            setChanged();
            notifyObservers();
        }
    }

    public void setMagFieldXYZ(long timestamp, float[] xyz) {
        boolean changed = false;
        if(xyz[0] != mMagFieldX) {mMagFieldX = xyz[0]; changed = true;}
        if(xyz[1] != mMagFieldY) {mMagFieldY = xyz[1]; changed = true;}
        if(xyz[2] != mMagFieldZ) {mMagFieldZ = xyz[2]; changed = true;}
        if(changed) {
            mTimestamp = timestamp;
         //   updateOrientationAngles();  // only for magnetic field and accelerometer
            setChanged();
            notifyObservers();
        }
    }

    public final float[] getMagFieldXYZ() {
        return new float[]{ mMagFieldX, mMagFieldY, mMagFieldZ };
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        int sensorType = sensorEvent.sensor.getType();
        long timestamp = sensorEvent.timestamp/1000000L;

        if(sensorType == Sensor.TYPE_ACCELEROMETER) {
            setAccXYZ( timestamp, sensorEvent.values);
        } else if(sensorType == Sensor.TYPE_GYROSCOPE) {
            setGyroXYZ(timestamp, sensorEvent.values);
        } else if(sensorType == Sensor.TYPE_MAGNETIC_FIELD) {
            setMagFieldXYZ(timestamp, sensorEvent.values);
        } else if(sensorType == Sensor.TYPE_GRAVITY) {
            setGravXYZ(timestamp, sensorEvent.values);
        } else if(sensorType == Sensor.TYPE_LINEAR_ACCELERATION) {
            setLinearAccXYZ(timestamp, sensorEvent.values);
        } else if(sensorType == Sensor.TYPE_ROTATION_VECTOR) {
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        long timestamp = location.getElapsedRealtimeNanos()/1000000L;
        setGPS(timestamp,
            location.getSpeed(),
            location.getLatitude(),
            location.getLongitude(),
            location.getAltitude(),
            location.getAccuracy());
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}