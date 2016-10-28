package com.example.mmittek.motogym;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Observable;
import java.util.Observer;


class FileViewModel extends Observable implements View.OnClickListener {
    File mFile;
    boolean mSelected;

    public FileViewModel(File file) {
        mFile = file;
        mSelected = false;
    }

    public final File getFile() {
        return mFile;
    }

    public final void setSelected(boolean selected) {
        if(selected != mSelected) {
            mSelected = selected;
            setChanged();
            notifyObservers();
        }
    }

    public final boolean isSelected() {
        return mSelected;
    }

    @Override
    public void onClick(View view) {
        if(view instanceof CheckBox) {
            CheckBox checkbox = (CheckBox)view;
            setSelected( checkbox.isChecked() );
        }
    }
}

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

public class MainActivity extends AppCompatActivity implements Observer, SensorEventListener, View.OnClickListener {


    SensorManager mSensorManager;
    Sensor mAccelerometer;
    Sensor mLinearAcceleration;
    Sensor mRotation;
    Sensor mMagneticField;
    Sensor mGravity;
    Sensor mGyroscope;

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

    int mUpdateGUISampleInterval = 2;
    long sampleCounter = 0;

    TextView mCameraInfoTextView;

    Vector2Plot mVectorPlotXY;
    Vector2Plot mVectorPlotYZ;
    Vector2Plot mVectorPlotZX;

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

        mVectorPlotXY = (Vector2Plot) findViewById(R.id.xy_vector_plot);
        mVectorPlotYZ = (Vector2Plot) findViewById(R.id.yz_vector_plot);
        mVectorPlotZX = (Vector2Plot) findViewById(R.id.zx_vector_plot);

        mHandler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Log.d("main activity", "got message " + msg);

            }
        };

        CameraDevice.StateCallback myCameraDeviceStateCallback = new CameraDevice.StateCallback() {
            @Override
            public void onOpened(CameraDevice cameraDevice) {
                Log.d("main", "camera opened! " + cameraDevice);
                try {


                    cameraDevice.createCaptureRequest( cameraDevice.TEMPLATE_STILL_CAPTURE );



                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onDisconnected(CameraDevice cameraDevice) {

            }

            @Override
            public void onError(CameraDevice cameraDevice, int i) {

            }
        };

        mCameraInfoTextView = (TextView) findViewById(R.id.camera_info_text_view);
        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            String[] cameraIds = mCameraManager.getCameraIdList();
            String cameraInfo = "";
            for (String cameraId : cameraIds) {
                    cameraInfo = cameraInfo + cameraId;

            }
            mCameraInfoTextView.setText( cameraInfo );

            mCameraManager.openCamera( "0", myCameraDeviceStateCallback, mHandler );


        }catch(CameraAccessException e){

        }






        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);



        mDataBuffer = new DataBuffer(mSensorManager);
        mRecordsCounterTextView = (TextView) findViewById(R.id.records_counter_text_view);

        mRecordsListView = (ListView)findViewById(R.id.recorded_files_list_view);
        mRecordsArrayAdapter = new ArrayAdapter<FileViewModel>(this, R.layout.records_list_item){

            @Override
            public void add(FileViewModel fileViewModel) {
                super.add(fileViewModel);
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View listItem = convertView;

               LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
                listItem = inflater.inflate(R.layout.records_list_item, parent, false);

                FileViewModel fileViewModel = getItem(position);

                CheckBox checkbox = (CheckBox)listItem.findViewById(R.id.records_list_item_checkbox);
                checkbox.setChecked( fileViewModel.isSelected() );
                checkbox.setTag(position);
                checkbox.setOnClickListener( fileViewModel );

                TextView fileNameTextView = (TextView)listItem.findViewById(R.id.records_list_item_file_name_text_view);
                fileNameTextView.setText( fileViewModel.getFile().getName() );

                TextView fileSizeTextView = (TextView)listItem.findViewById(R.id.records_list_item_file_size_text_view);
                fileSizeTextView.setText( "" + Math.ceil(fileViewModel.getFile().length()/1024.0f) + "kb");


                return listItem;
            }
        };
        mRecordsListView.addHeaderView( getLayoutInflater().inflate(R.layout.records_list_header, mRecordsListView, false) );
        mRecordsListView.setAdapter(mRecordsArrayAdapter);

        refreshListOfRecords();


        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // Define a listener that responds to location updates


        final MainActivity mainActivity = this;
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                //makeUseOfNewLocation(location);
                mainActivity.onLocation(location);

//                long timetampMillis = DataBuffer.convertToAbsoluteTimestampMillis(location.getElapsedRealtimeNanos());
                long timetampMillis = location.getElapsedRealtimeNanos()/1000000L;

                mainActivity.getDataBuffer().setGPS(
                        timetampMillis,
                        location.getSpeed(),
                        location.getLatitude(),
                        location.getLongitude(),
                        location.getAltitude(),
                        location.getAccuracy());

            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };
//checkPermission()

        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  },
                    1 );
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);


        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mLinearAcceleration = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mRotation = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

//        mMagneticField = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mMagneticField = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mGravity =  mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        /*
        List<Sensor> deviceSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        for(Sensor sensor : deviceSensors) {
            arrayAdapter.add( sensor.toString() );
        }

        arrayAdapter.add("asd");
        */


        registerListeners();

    }

    public final DataBuffer getDataBuffer() {
        return mDataBuffer;
    }

    public static File writeToExternal(Context context, String filename){
        try {
            File file = new File(context.getExternalFilesDir(null), filename); //Get file location from external source
            InputStream is = new FileInputStream(context.getFilesDir() + File.separator + filename); //get file location from internal
            OutputStream os = new FileOutputStream(file); //Open your OutputStream and pass in the file you want to write to
            byte[] toWrite = new byte[is.available()]; //Init a byte array for handing data transfer
            Log.i("Available ", is.available() + "");
            int result = is.read(toWrite); //Read the data from the byte array
            Log.i("Result", result + "");
            os.write(toWrite); //Write it to the output stream
            is.close(); //Close it
            os.close(); //Close it
            Log.i("Copying to", "" + context.getExternalFilesDir(null) + File.separator + filename);
            Log.i("Copying from", context.getFilesDir() + File.separator + filename + "");
            return file;
        } catch (Exception e) {
            Toast.makeText(context, "File write failed: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show(); //if there's an error, make a piece of toast and serve it up
        }
        return null;
    }

    public final void shareSelectedFiles(View view) {
        // Create list of Uris for the selected files
        ArrayList<Uri> filesToSend = new ArrayList<Uri>();
        for(int i=0; i<mRecordsArrayAdapter.getCount(); i++) {
            FileViewModel fileViewModel = mRecordsArrayAdapter.getItem(i);
            if(!fileViewModel.isSelected()) continue;
            File file = fileViewModel.getFile();
            if(file.exists() && file.canRead()) {
                File exFile = writeToExternal(this, file.getName() );
                exFile.deleteOnExit();
                filesToSend.add( Uri.parse("file://" + exFile.getAbsolutePath()) );
            }
        }

        if(filesToSend.size() > 0) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
            sendIntent.putExtra(Intent.EXTRA_STREAM, filesToSend);
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, "MotoGym Records");
            sendIntent.setType("text/csv");
            startActivity(Intent.createChooser(sendIntent, "SEND TO" ));
        }
    }

    public final void deleteSelectedFiles(View view) {
        for(int i=0; i<mRecordsArrayAdapter.getCount(); i++) {
            FileViewModel fileViewModel = mRecordsArrayAdapter.getItem(i);
            if( fileViewModel.isSelected() ) {
                // delete file
                fileViewModel.getFile().delete();
            }
        }
        refreshListOfRecords();
    }

    public final void onLocation(Location location) {
        TextView locationTextView = (TextView)findViewById(R.id.location_text_view);
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
        TextView rotationMatrixTextView = (TextView)findViewById(R.id.rotation_matrix_text_view);
        TextView orientAnglesTextView = (TextView)findViewById(R.id.orient_angles_text_view);
        TextView orientAnglesDegTextView = (TextView) findViewById(R.id.orient_angles_degs_text_view);
        float[] orientAngles = mDataBuffer.getOrientAngles();
        float[] rotationMatrix = mDataBuffer.getRotMat();
        orientAnglesTextView.setText(String.format("angle: %.2f, %.2f, %.2f", orientAngles[0], orientAngles[1], orientAngles[2] ));
        orientAnglesDegTextView.setText(String.format("angle: %.1f, %.1f, %.1f", orientAngles[0]*180/3.14f, orientAngles[1]*180/3.14f, orientAngles[2]*180/3.14f ));
        rotationMatrixTextView.setText(String.format("rotmat: %.2f, %.2f, %.2f \n %.2f, %.2f, %.2f \n %.2f, %.2f, %.2f", rotationMatrix[0], rotationMatrix[1], rotationMatrix[2], rotationMatrix[3], rotationMatrix[4], rotationMatrix[5], rotationMatrix[6], rotationMatrix[7], rotationMatrix[8] ));
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        // The light sensor returns a single value.
        // Many sensors return 3 values, one for each axis.
//        float lux = event.values[0];
        // Do something with this sensor value.

        sampleCounter++;
        boolean updateGUI = ( sampleCounter%mUpdateGUISampleInterval == 0 );
        //long timestampMillis = convertToAbsoluteTimestampMillis(event.timestamp);
        long timestampMillis = event.timestamp/1000000L;

        if(event.sensor == mAccelerometer) {
            mDataBuffer.setAccXYZ(timestampMillis, event.values );
            if(updateGUI) {
                TextView accDataTextView = (TextView) findViewById(R.id.sensors_accelerometer_text_view);
                accDataTextView.setText(String.format("acc: %.2f, %.2f, %.2f", event.values[0], event.values[1], event.values[2]));
                updateAnglesAndRotMatTextViews();

                mDataFusion.feedAccelerationXYZ(new double[]{ event.values[0], event.values[1], event.values[2] });

            }
        } else if(event.sensor == mLinearAcceleration) {
            mDataBuffer.setLinearAccXYZ(timestampMillis, event.values );
            if(updateGUI) {
                TextView accDataTextView = (TextView) findViewById(R.id.sensors_linear_accelerometer_text_view);
                accDataTextView.setText(String.format("linacc: %.2f, %.2f, %.2f", event.values[0], event.values[1], event.values[2]));
            }
        } else if(event.sensor == mRotation) {
            if(updateGUI) {
                TextView accDataTextView = (TextView) findViewById(R.id.sensors_rotation_text_view);
                accDataTextView.setText(String.format("rot: %.2f, %.2f, %.2f, %.2f, %.2f", event.values[0], event.values[1], event.values[2], event.values[3], event.values[4]));
            }

        }else if(event.sensor == mMagneticField) {
            mDataBuffer.setMagFieldXYZ( timestampMillis, event.values );
            mDataFusion.feedMagneticField( new double[]{ event.values[0], event.values[1], event.values[2] } );

            if(updateGUI) {
                TextView accDataTextView = (TextView) findViewById(R.id.sensors_magnetic_field_text_view);
                accDataTextView.setText(String.format("magfield: %.2f, %.2f, %.2f", event.values[0], event.values[1], event.values[2]));
                updateAnglesAndRotMatTextViews();
            }
        } else if(event.sensor == mGravity) {
            mDataBuffer.setGravXYZ( timestampMillis, event.values );
            if(updateGUI) {
                TextView accDataTextView = (TextView) findViewById(R.id.sensors_gravity_text_view);
                accDataTextView.setText(String.format("grav: %.2f, %.2f, %.2f", event.values[0], event.values[1], event.values[2]));
            }
        } else if(event.sensor == mGyroscope) {
            mDataBuffer.setGyroXYZ( timestampMillis, event.values );
            if(updateGUI) {
                TextView tv = (TextView) findViewById(R.id.sensors_gyroscope_text_view);
                tv.setText(String.format("gyro: %.2f, %.2f, %.2f", event.values[0], event.values[1], event.values[2]));
            }
        }

    }



    protected final void registerListeners() {
        int sensorDelay = SensorManager.SENSOR_DELAY_NORMAL;


        mSensorManager.registerListener(this, mAccelerometer, sensorDelay);
        mSensorManager.registerListener(this, mLinearAcceleration, sensorDelay);
        mSensorManager.registerListener(this, mRotation, sensorDelay);
        mSensorManager.registerListener(this, mMagneticField, sensorDelay);
        mSensorManager.registerListener(this, mGravity, sensorDelay);
        mSensorManager.registerListener(this, mGyroscope, sensorDelay);

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
        String[] fileNames = getFilesDir().list();
        mRecordsArrayAdapter.clear();
        for(String fileName: fileNames) {
            File f = new File( getFilesDir(), fileName );
            mRecordsArrayAdapter.add( new FileViewModel(f) );
        }
        mRecordsArrayAdapter.notifyDataSetChanged();
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
    }
}
