package com.example.mmittek.motogym;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.Observable;


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

public class FragmentRecords extends Fragment {

    ArrayAdapter<FileViewModel> mRecordsArrayAdapter;
    ListView mRecordsListView;


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
        mRecordsListView.addHeaderView( getLayoutInflater(null).inflate(R.layout.records_list_header, mRecordsListView, false) );
        mRecordsListView.setAdapter(mRecordsArrayAdapter);
        refreshListOfRecords();
    }
}
