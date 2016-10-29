package com.example.mmittek.motogym;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Observable;
import java.util.Observer;


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




    File mFile;

    TextView mRecordsCounterTextView;
    DataBuffer mDataBuffer;

    CameraManager mCameraManager;

    ToggleButton mRecordToggleButton;
    TextView mRecordFileNameTextView;
    BufferedWriter mBufferedWriter;
    ArrayAdapter<FileViewModel> mRecordsArrayAdapter;
    ListView mRecordsListView;

    Handler mHandler;

    int mUpdateGUISampleInterval = 10;

    TextView mCameraInfoTextView;


    EditText mRecordLabelEditText;

    DataFusion mDataFusion;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        mRecordToggleButton = (ToggleButton)findViewById(R.id.recording_toggle_button);
        mRecordFileNameTextView = (TextView)findViewById(R.id.recorded_file_name_text_view);


        mDataFusion = new DataFusion();
        mDataFusion.addObserver(this);


        // Get angle plot

        mRecordLabelEditText = (EditText)findViewById(R.id.record_label_edit_text);



        FragmentRawData rawDataFragment = (FragmentRawData)getSupportFragmentManager().findFragmentById(R.id.raw_data_fragment); //(FragmentRawData)findViewById(R.id.raw_data_fragment);
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














    /*
    @Override
    protected void onResume() {
        super.onResume();
    }
    */

    /*
    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
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

    protected void refreshListOfRecords() {
        /*
        String[] fileNames = getFilesDir().list();
        mRecordsArrayAdapter.clear();
        for(String fileName: fileNames) {
            File f = new File( getFilesDir(), fileName );
            mRecordsArrayAdapter.add( new FileViewModel(f) );
        }
        mRecordsArrayAdapter.notifyDataSetChanged();
        */
    }


    protected boolean startRecording() {
        String postfix = mRecordLabelEditText.getText().toString();

        String fileName = getDefaultFilename(postfix);


        mFile = new File(getFilesDir(), fileName);
        try {
            if (!mFile.createNewFile() || !mFile.canWrite()) {
                return false;
            }

            mBufferedWriter = new BufferedWriter(new FileWriter( mFile ));
            String csvHeader = mDataBuffer.getCSVHeader();
            mBufferedWriter.write( csvHeader );

            mDataBuffer.reset();
            mDataBuffer.addObserver(this);

            refreshListOfRecords();
            return true;
        }catch(IOException e) {
        }
        return false;
    }

    protected void stopRecording() {
        mDataBuffer.deleteObservers();

        try {
            mBufferedWriter.flush();
            mBufferedWriter.close();
            mBufferedWriter = null;
            mFile = null;
        }catch(IOException e) {
        }

        refreshListOfRecords();

    }

    public static String getCurrentTimestampString() {
        Calendar calendar = Calendar.getInstance();

        String timestampString = "" + calendar.get(Calendar.YEAR) +
                String.format("%02d", calendar.get(Calendar.MONTH)) +
                String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH)) + "_" +
                String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY)) +
                String.format("%02d", calendar.get(Calendar.MINUTE)) +
                String.format("%02d", calendar.get(Calendar.SECOND)) + "_"  +
                String.format("%03d", calendar.get(Calendar.MILLISECOND));
        return timestampString;
    }




    public void toggleRecording(View view) {
        if(view != mRecordToggleButton) return;
        boolean state = mRecordToggleButton.isChecked();

        if(state) {
            startRecording();
        } else {
            stopRecording();
        }

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void update(Observable observable, Object o) {
        /*
        if( observable == mDataBuffer ) {
            String csvRecord = mDataBuffer.getCSVRecord();
            if(mBufferedWriter == null)  return;
            try {
                mBufferedWriter.write(csvRecord);
                mRecordsCounterTextView.setText( String.format("%d", mDataBuffer.getRecordsCounter() ) );
            }catch (IOException e){
            }
        } else if(observable == mDataFusion) {

            double[] gravity = mDataFusion.getGravity();
            double[] linearAcceleration = mDataFusion.getLinearAcceleration();
            double[] magneticField = mDataFusion.getMagneticField();
            double[] absoluteOrientation = mDataFusion.getAbsoluteOrientation();
            double[] vec = absoluteOrientation;

            mVectorPlotXY.setVector( new double[]{vec[0], vec[1]} );
            mVectorPlotYZ.setVector( new double[]{vec[2], vec[1]} );
            mVectorPlotZX.setVector( new double[]{vec[0], vec[2]} );


        }
        */
    }



}
