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
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.Observable;
import java.util.Observer;

import static com.example.mmittek.motogym.FragmentRecords.getCurrentTimestampString;




// Following : https://developer.android.com/guide/topics/sensors/sensors_overview.html

public class MainActivity extends AppCompatActivity implements Observer, View.OnClickListener {
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


        FragmentRawData rawDataFragment = (FragmentRawData)getSupportFragmentManager().findFragmentById(R.id.raw_data_fragment);
        registerListeners( rawDataFragment, SensorManager.SENSOR_DELAY_UI);


        FragmentRecords recordsFragment = (FragmentRecords)getSupportFragmentManager().findFragmentById(R.id.records_fragment);
        registerListeners( recordsFragment.getDataBuffer(), SensorManager.SENSOR_DELAY_FASTEST );


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


        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,recordsFragment.getDataBuffer());
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, recordsFragment.getDataBuffer());

    }

    /*
    public final DataBuffer getDataBuffer() {
        return mDataBuffer;
    }
*/

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

    public void resetDataFusion(View view) {
        mDataFusion.reset();
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
        }
    }
}
