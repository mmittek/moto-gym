package com.example.mmittek.motogym;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

/**
 * Created by mmittek on 10/28/16.
 */

public class FragmentRecords extends Fragment implements View.OnClickListener, Observer {

    ArrayAdapter<FileViewModel> mRecordsArrayAdapter;
    ListView mRecordsListView;
    Button mDeleteButton;
    Button mShareButton;
    ToggleButton mRecordToggleButton;
    DataBuffer mDataBuffer;
    File mFile;
    BufferedWriter mBufferedWriter;
    long mRecordsCounter;
    TextView mRecordFileNameTextView;
    EditText mRecordLabelEditText;
    TextView mRecordsCounterTextView;
    boolean mRecording = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_records, container, false);
        return rootView;
    }

    protected void refreshListOfRecords() {

        String[] fileNames = getContext().getFilesDir().list();
        mRecordsArrayAdapter.clear();
        for(String fileName: fileNames) {
            File f = new File( getContext().getFilesDir(), fileName );
            mRecordsArrayAdapter.add( new FileViewModel(f) );
        }
        mRecordsArrayAdapter.notifyDataSetChanged();
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {


        mRecordFileNameTextView = (TextView)view.findViewById(R.id.recorded_file_name_text_view);
        mRecordLabelEditText = (EditText)view.findViewById(R.id.record_label_edit_text);

        mDataBuffer = new DataBuffer();
        mDataBuffer.addObserver(this);

        mRecordsCounterTextView = (TextView)view.findViewById(R.id.records_counter_text_view);

        mRecordsListView = (ListView)view.findViewById(R.id.recorded_files_list_view);
        mRecordsArrayAdapter = new ArrayAdapter<FileViewModel>(getContext(), R.layout.records_list_item){

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

        mRecordToggleButton = (ToggleButton) view.findViewById(R.id.recording_toggle_button);
        mRecordToggleButton.setOnClickListener(this);

        View headerView = getLayoutInflater(null).inflate(R.layout.records_list_header, mRecordsListView, false);
        mDeleteButton = (Button)headerView.findViewById(R.id.records_list_header_delete_button);
        mShareButton = (Button)headerView.findViewById(R.id.records_list_header_share_button);

        mDeleteButton.setOnClickListener(this);
        mShareButton.setOnClickListener(this);

        mRecordsListView.addHeaderView( headerView );
        mRecordsListView.setAdapter(mRecordsArrayAdapter);
        refreshListOfRecords();
    }

    public DataBuffer getDataBuffer() {
        if(mDataBuffer == null) {
            mDataBuffer = new DataBuffer();
        }
        return mDataBuffer;
    }

    public final void deleteSelectedFiles() {
        for(int i=0; i<mRecordsArrayAdapter.getCount(); i++) {
            FileViewModel fileViewModel = mRecordsArrayAdapter.getItem(i);
            if( fileViewModel.isSelected() ) {
                // delete file
                fileViewModel.getFile().delete();
            }
        }
        refreshListOfRecords();
    }

    public final void shareSelectedFiles() {
        // Create list of Uris for the selected files
        ArrayList<Uri> filesToSend = new ArrayList<Uri>();
        for(int i=0; i<mRecordsArrayAdapter.getCount(); i++) {
            FileViewModel fileViewModel = mRecordsArrayAdapter.getItem(i);
            if(!fileViewModel.isSelected()) continue;
            File file = fileViewModel.getFile();
            if(file.exists() && file.canRead()) {
                File exFile = writeToExternal( getContext() , file.getName() );
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

    public void toggleRecording(View view) {
        boolean state = mRecordToggleButton.isChecked();

        if(state) {
            startRecording();
        } else {
            stopRecording();
        }
    }


    protected boolean startRecording() {
        String timestampString = getCurrentTimestampString();
        String recordLabel = mRecordLabelEditText.getText().toString();

        String filename = timestampString + ".csv";
        if(recordLabel != null && recordLabel.length() > 0) {
            filename = timestampString + "_" + recordLabel + ".csv";
        }

        String csvHeader = mDataBuffer.getCSVHeader();


        try {
            File file = new File(getContext().getFilesDir(), filename);
            if(!file.createNewFile()) return false;
            if(!file.canWrite()) return false;

            BufferedWriter bufferedWriter = new BufferedWriter( new FileWriter(file) );
            bufferedWriter.write(csvHeader);

            mFile = file;
            mBufferedWriter = bufferedWriter;
            mRecordsCounter = 0;
            mDataBuffer.addObserver(this);
            refreshListOfRecords();
            mRecording = true;
            return true;
        }catch(IOException e) {
            return false;
        }
    }

    protected void stopRecording() {
        mDataBuffer.deleteObserver(this);

        try {
            mBufferedWriter.flush();
            mBufferedWriter.close();
            mBufferedWriter = null;
            mFile = null;
        }catch(IOException e) {
        }
        refreshListOfRecords();
        mRecording = false;
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

    @Override
    public void onClick(View view) {
        if(view == mDeleteButton) {
            deleteSelectedFiles();
        } else if(view == mShareButton) {
            shareSelectedFiles();
        } else if(view == mRecordToggleButton) {
            toggleRecording(mRecordToggleButton);


        }
    }

    @Override
    public void update(Observable observable, Object o) {

        if(!mRecording) return;

        if(observable == mDataBuffer) {

            mRecordsCounterTextView.setText( String.format("%d", mDataBuffer.getRecordsCounter()) );

            String record = mDataBuffer.getCSVRecord();
            try {
                mBufferedWriter.write(record);
            } catch(IOException e) {

            }
        }
    }

}
