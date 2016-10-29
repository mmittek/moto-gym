package com.example.mmittek.motogym;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.Observable;
import java.util.Observer;

import static com.example.mmittek.motogym.FragmentRecords.getCurrentTimestampString;


class DataBuffer extends Observable {


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

    protected SensorManager mSensorManager;

    public DataBuffer(SensorManager sensorMananger) {
        mSensorManager = sensorMananger;
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
            updateOrientationAngles();  // only for magnetic field and accelerometer
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
            updateOrientationAngles();  // only for magnetic field and accelerometer
            setChanged();
            notifyObservers();
        }
    }

    public final float[] getMagFieldXYZ() {
        return new float[]{ mMagFieldX, mMagFieldY, mMagFieldZ };
    }

    // Compute the three orientation angles based on the most recent readings from
    // the device's accelerometer and magnetometer.
    protected void updateOrientationAngles() {
        // Update rotation matrix, which is needed to update orientation angles.
        mSensorManager.getRotationMatrix(mRotMat, null,
                getAccXYZ(), getMagFieldXYZ());
        // "mRotationMatrix" now has up-to-date information.
        mSensorManager.getOrientation(mRotMat, mOrientAngles);
        // "mOrientationAngles" now has up-to-date information.
    }
}


// Following : https://developer.android.com/guide/topics/sensors/sensors_overview.html

public class MainActivity extends AppCompatActivity implements Observer, View.OnClickListener {
    DataBuffer mDataBuffer;
    ToggleButton mRecordToggleButton;
    TextView mRecordFileNameTextView;
    EditText mRecordLabelEditText;
    DataFusion mDataFusion;


    SensorManager mSensorManager;
    Sensor mAccelerometer;
    Sensor mLinearAcceleration;
    Sensor mRotation;
    Sensor mMagneticField;
    Sensor mGravity;
    Sensor mGyroscope;

    AnglePlot mAnglePlotX;
    AnglePlot mAnglePlotY;
    AnglePlot mAnglePlotZ;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mLinearAcceleration = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mRotation = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mMagneticField = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mGravity =  mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);


        Fragment rawDataFragment = getSupportFragmentManager().findFragmentById(R.id.raw_data_fragment);
        registerListeners( (SensorEventListener)rawDataFragment, SensorManager.SENSOR_DELAY_UI);



        mRecordToggleButton = (ToggleButton)findViewById(R.id.recording_toggle_button);
        mRecordFileNameTextView = (TextView)findViewById(R.id.recorded_file_name_text_view);

        mDataFusion = new DataFusion();
        registerListeners( mDataFusion, SensorManager.SENSOR_DELAY_FASTEST );
        mDataFusion.addObserver(this);


        mAnglePlotX = (AnglePlot) findViewById(R.id.angle_plot_x);
        mAnglePlotY = (AnglePlot) findViewById(R.id.angle_plot_y);
        mAnglePlotZ = (AnglePlot) findViewById(R.id.angle_plot_z);

        // Get angle plot

        mRecordLabelEditText = (EditText)findViewById(R.id.record_label_edit_text);
//        FragmentRawData rawDataFragment = (FragmentRawData)getSupportFragmentManager().findFragmentById(R.id.raw_data_fragment); //(FragmentRawData)findViewById(R.id.raw_data_fragment);
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  },1 );
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) rawDataFragment);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) rawDataFragment);
    }

    public final DataBuffer getDataBuffer() {
        return mDataBuffer;
    }

    protected String getDefaultFilename(String postfix) {
        String defaultFilename;
        if((postfix == null) || (postfix.length() == 0)) {
            defaultFilename= getCurrentTimestampString() + ".csv";

        } else {
             defaultFilename = getCurrentTimestampString() +"_" + postfix +".csv";
        }
        return defaultFilename;
    }




    @Override
    public void onClick(View view) {

    }

    protected final void registerListeners(SensorEventListener sensorEventListener) {
        registerListeners(sensorEventListener, SensorManager.SENSOR_DELAY_UI);
    }

    protected void setSensorDelay(SensorEventListener sensorEventListener, int sensorDelay ) {
        mSensorManager.unregisterListener(sensorEventListener);
        registerListeners(sensorEventListener, sensorDelay);
    }


    protected final void registerListeners(SensorEventListener sensorEventListener, int sensorDelay) {
//        int sensorDelay = SensorManager.SENSOR_DELAY_NORMAL;
        mSensorManager.registerListener(sensorEventListener, mAccelerometer, sensorDelay);
        mSensorManager.registerListener(sensorEventListener, mLinearAcceleration, sensorDelay);
        mSensorManager.registerListener(sensorEventListener, mRotation, sensorDelay);
        mSensorManager.registerListener(sensorEventListener, mMagneticField, sensorDelay);
        mSensorManager.registerListener(sensorEventListener, mGravity, sensorDelay);
        mSensorManager.registerListener(sensorEventListener, mGyroscope, sensorDelay);
    }

    @Override
    public void update(Observable observable, Object o) {

    if(observable == mDataFusion) {

            double[] gravity = mDataFusion.getGravity();
            double[] linearAcceleration = mDataFusion.getLinearAcceleration();
            double[] magneticField = mDataFusion.getMagneticField();
            double[] absoluteOrientation = mDataFusion.getAbsoluteOrientation();
            double[] vec = absoluteOrientation;
            double[] angle = mDataFusion.getAngle();

        mAnglePlotX.setAngle( (float)angle[0] );
        mAnglePlotY.setAngle( (float)angle[1] );
        mAnglePlotZ.setAngle( (float)angle[2] );

//            mVectorPlotXY.setVector( new double[]{vec[0], vec[1]} );
//            mVectorPlotYZ.setVector( new double[]{vec[2], vec[1]} );
//            mVectorPlotZX.setVector( new double[]{vec[0], vec[2]} );


        }

    }



}
