package com.example.mmittek.motogym;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
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
    protected float mSpeedGPS;
    protected double mLatGPS;
    protected double mLonGPS;


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
        mSpeedGPS = 0;
        mLatGPS = 0;
        mLonGPS = 0;
    }

    public final String getCSVHeader() {
        String header = "timestamp, accx, accy, accz, laccx, laccy, laccz, gravx, gravy, gravz, speedgps, latgps, longps\r\n";
        return header;
    }

    public final String getCSVRecord() {
        return "" + mTimestamp + "," +
                mAccX + "," + mAccY + "," + mAccZ + "," +
                mLAccX + "," + mLAccY + "," + mLAccZ + "," +
                mGravX + "," + mGravY + "," + mGravZ + "," +
                mSpeedGPS + "," + mLatGPS + "," + mLonGPS +
        "\r\n";
    }


    public void setChanged() {
        super.setChanged();
        mRecordsCounter++;
    }

    public final long getRecordsCounter() {
        return mRecordsCounter;
    }

    public void setAccXYZ(long timestamp, float[] xyz) {
        boolean changed = false;
        if(xyz[0] != mAccX) { mAccX = xyz[0]; changed = true;}
        if(xyz[1] != mAccY) { mAccY = xyz[1]; changed = true;}
        if(xyz[2] != mAccZ) { mAccZ = xyz[2]; changed = true;}
        if(changed) {
            mTimestamp = timestamp;
            setChanged();
            notifyObservers();
        }
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

    public void setSpeedLatLonGPS(long timestamp, float speed, double lat, double lon) {
        boolean changed = false;
        if(speed != mSpeedGPS) { mSpeedGPS = speed; changed = true;}
        if(lat != mLatGPS) {mLatGPS = lat; changed = true; }
        if(lon != mLonGPS) { mLonGPS = lon; changed = true; }
        if(changed) {
            mTimestamp = timestamp;
            setChanged();
            notifyObservers();
        }
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

    File mFile;

    TextView mRecordsCounterTextView;
    DataBuffer mDataBuffer;

    ToggleButton mRecordToggleButton;
    TextView mRecordFileNameTextView;
    BufferedWriter mBufferedWriter;
    ArrayAdapter<FileViewModel> mRecordsArrayAdapter;
    ListView mRecordsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecordToggleButton = (ToggleButton)findViewById(R.id.recording_toggle_button);
        mRecordFileNameTextView = (TextView)findViewById(R.id.recorded_file_name_text_view);

        mDataBuffer = new DataBuffer();
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

                TextView fileNameTextView = (TextView)listItem.findViewById(R.id.records_list_item_file_name);
                fileNameTextView.setText( fileViewModel.getFile().getName() );


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

                mainActivity.getDataBuffer().setSpeedLatLonGPS( location.getTime(), location.getSpeed(), location.getLatitude(), location.getLongitude() );

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

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mLinearAcceleration = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mRotation = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        mMagneticField = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        mGravity =  mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

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
        locationTextView.setText("speed: " + location.getSpeed() + ", " + location.getLatitude() + ", " + location.getLongitude());
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        // The light sensor returns a single value.
        // Many sensors return 3 values, one for each axis.
//        float lux = event.values[0];
        // Do something with this sensor value.
        if(event.sensor == mAccelerometer) {
            TextView accDataTextView = (TextView) findViewById(R.id.sensors_accelerometer_text_view);
            accDataTextView.setText(String.format("acc: %.2f, %.2f, %.2f", event.values[0], event.values[1], event.values[2]));
            mDataBuffer.setAccXYZ(event.timestamp, event.values );
        }

        if(event.sensor == mLinearAcceleration) {
            TextView accDataTextView = (TextView) findViewById(R.id.sensors_linear_accelerometer_text_view);
            accDataTextView.setText(String.format("linacc: %.2f, %.2f, %.2f", event.values[0], event.values[1], event.values[2]));
            mDataBuffer.setLinearAccXYZ( event.timestamp, event.values );
        }

        if(event.sensor == mRotation) {
            TextView accDataTextView = (TextView) findViewById(R.id.sensors_rotation_text_view);
            accDataTextView.setText(String.format("rot: %.2f, %.2f, %.2f", event.values[0], event.values[1], event.values[2]));

        }

        if(event.sensor == mMagneticField) {
            TextView accDataTextView = (TextView) findViewById(R.id.sensors_magnetic_field_text_view);
            accDataTextView.setText(String.format("magfield: %.2f, %.2f, %.2f", event.values[0], event.values[1], event.values[2]));
        }

        if(event.sensor == mGravity) {
            TextView accDataTextView = (TextView) findViewById(R.id.sensors_gravity_text_view);
            accDataTextView.setText(String.format("grav: %.2f, %.2f, %.2f", event.values[0], event.values[1], event.values[2]));
            mDataBuffer.setGravXYZ( event.timestamp, event.values );
        }

    }

    protected final void registerListeners() {
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mLinearAcceleration, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mRotation, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mMagneticField, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mGravity, SensorManager.SENSOR_DELAY_NORMAL);
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

    protected String getDefaultFilename() {
        String defaultFilename = getCurrentTimestampString() + ".csv";
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
        String fileName = getDefaultFilename();
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
    }

    public static String getCurrentTimestampString() {
        Calendar calendar = Calendar.getInstance();

        String timestampString = "" + calendar.get(Calendar.YEAR) + "-" +
                String.format("%02d", calendar.get(Calendar.MONTH)) + "-" +
                String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH)) + " " +
                String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY)) + ":" +
                String.format("%02d", calendar.get(Calendar.MINUTE)) + ":" +
                String.format("%02d", calendar.get(Calendar.SECOND)) + ":"  +
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
                mRecordsCounterTextView.setText( String.format("Records: %d", mDataBuffer.getRecordsCounter() ) );
            }catch (IOException e){

            }

        }
    }
}
