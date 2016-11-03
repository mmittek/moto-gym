package com.example.mmittek.motogym;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by mmittek on 10/28/16.
 */

public class FragmentRawData extends Fragment  implements Observer, SensorEventListener, Spinner.OnItemSelectedListener, LocationListener {


    long sampleCounter = 0;
    Spinner mSensorDelaySpinner;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_raw_data, container, false);
        return rootView;
    }


    public void onViewCreated(View view, Bundle savedInstanceState) {


        mSensorDelaySpinner = (Spinner)view.findViewById(R.id.sensor_delay_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.sensor_delay_strings, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSensorDelaySpinner.setAdapter(adapter);
        mSensorDelaySpinner.setOnItemSelectedListener(this);


        final FragmentRawData thisFragment = this;
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                //makeUseOfNewLocation(location);
//                mainActivity.onLocation(location);

//                long timetampMillis = DataBuffer.convertToAbsoluteTimestampMillis(location.getElapsedRealtimeNanos());
                long timetampMillis = location.getElapsedRealtimeNanos()/1000000L;
/*
                thisFragment.getDataBuffer().setGPS(
                        timetampMillis,
                        location.getSpeed(),
                        location.getLatitude(),
                        location.getLongitude(),
                        location.getAltitude(),
                        location.getAccuracy());
                        */

            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };





    }

    public final void onLocation(Location location) {
        TextView locationTextView = (TextView) getView().findViewById(R.id.location_text_view);
        locationTextView.setText("speed: " + location.getSpeed() + ", " + location.getLatitude() + ", " + location.getLongitude() + ", " + location.getAltitude() + " (" + location.getAccuracy() + ")");
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }


    /**
     * Only after accelerometer and magnetic field
     */
    protected void updateAnglesAndRotMatTextViews() {
        TextView rotationMatrixTextView = (TextView)getView().findViewById(R.id.rotation_matrix_text_view);
        TextView orientAnglesTextView = (TextView)getView().findViewById(R.id.orient_angles_text_view);
        TextView orientAnglesDegTextView = (TextView) getView().findViewById(R.id.orient_angles_degs_text_view);
//        float[] orientAngles = mDataBuffer.getOrientAngles();
//        float[] rotationMatrix = mDataBuffer.getRotMat();
  //      orientAnglesTextView.setText(String.format("angle: %.2f, %.2f, %.2f", orientAngles[0], orientAngles[1], orientAngles[2] ));
    //    orientAnglesDegTextView.setText(String.format("angle: %.1f, %.1f, %.1f", orientAngles[0]*180/3.14f, orientAngles[1]*180/3.14f, orientAngles[2]*180/3.14f ));
      //  rotationMatrixTextView.setText(String.format("rotmat: %.2f, %.2f, %.2f \n %.2f, %.2f, %.2f \n %.2f, %.2f, %.2f", rotationMatrix[0], rotationMatrix[1], rotationMatrix[2], rotationMatrix[3], rotationMatrix[4], rotationMatrix[5], rotationMatrix[6], rotationMatrix[7], rotationMatrix[8] ));
    }





    @Override
    public final void onSensorChanged(SensorEvent event) {
        // The light sensor returns a single value.
        // Many sensors return 3 values, one for each axis.
//        float lux = event.values[0];
        // Do something with this sensor value.

        sampleCounter++;
        long timestampMillis = event.timestamp/1000000L;

        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            //mDataBuffer.setAccXYZ(timestampMillis, event.values );
            //if(updateGUI) {
                TextView accDataTextView = (TextView) getView().findViewById(R.id.sensors_accelerometer_text_view);
            //    accDataTextView.setText(String.format("acc (%.1f): %.2f, %.2f, %.2f", mDataFusion.getAccSamplingRate(), event.values[0], event.values[1], event.values[2]));
                updateAnglesAndRotMatTextViews();

              //  mDataFusion.feedAccelerationXYZ(new double[]{ event.values[0], event.values[1], event.values[2] });

            //}
        } else if(event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            //mDataBuffer.setLinearAccXYZ(timestampMillis, event.values );
            //if(updateGUI) {
                TextView accDataTextView = (TextView) getView().findViewById(R.id.sensors_linear_accelerometer_text_view);
                accDataTextView.setText(String.format("linacc: %.2f, %.2f, %.2f", event.values[0], event.values[1], event.values[2]));
            //}
        } else if(event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            //if(updateGUI) {
                TextView accDataTextView = (TextView) getView().findViewById(R.id.sensors_rotation_text_view);
                accDataTextView.setText(String.format("rot: %.2f, %.2f, %.2f, %.2f, %.2f", event.values[0], event.values[1], event.values[2], event.values[3], event.values[4]));
            //}

        }else if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            //mDataBuffer.setMagFieldXYZ( timestampMillis, event.values );
            //mDataFusion.feedMagneticField( new double[]{ event.values[0], event.values[1], event.values[2] } );

            //if(updateGUI) {
                TextView accDataTextView = (TextView) getView().findViewById(R.id.sensors_magnetic_field_text_view);
                accDataTextView.setText(String.format("magfield: %.2f, %.2f, %.2f", event.values[0], event.values[1], event.values[2]));
                updateAnglesAndRotMatTextViews();
            //}
        } else if(event.sensor.getType() == Sensor.TYPE_GRAVITY) {
            //mDataBuffer.setGravXYZ( timestampMillis, event.values );
            //if(updateGUI) {
                TextView accDataTextView = (TextView) getView().findViewById(R.id.sensors_gravity_text_view);
                accDataTextView.setText(String.format("grav: %.2f, %.2f, %.2f", event.values[0], event.values[1], event.values[2]));
            //}
        } else if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            //mDataBuffer.setGyroXYZ( timestampMillis, event.values );
            //if(updateGUI) {
                TextView tv = (TextView) getView().findViewById(R.id.sensors_gyroscope_text_view);
                tv.setText(String.format("gyro: %.2f, %.2f, %.2f", event.values[0], event.values[1], event.values[2]));
            //}
        }

    }


    // -------------------- SPINNER START
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        int sensorDelay = SensorManager.SENSOR_DELAY_NORMAL;

        switch(i) {
            case 0:
                // normal
                sensorDelay = SensorManager.SENSOR_DELAY_NORMAL;
                Log.d("main", "normal sensors");
                break;

            case 1:
                // game
                sensorDelay = SensorManager.SENSOR_DELAY_GAME;
                Log.d("main", "game sensors");
                break;

            case 2:
                // fastest
                sensorDelay = SensorManager.SENSOR_DELAY_FASTEST;
                Log.d("main", "fastest sensors");
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    // -------------------- SPINNER STOP



    @Override
    public void onLocationChanged(Location location) {

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

    @Override
    public void update(Observable observable, Object o) {
        if(observable instanceof SensorFusion) {
            SensorFusion sensorFusion = (SensorFusion)observable;




        }
    }
}
