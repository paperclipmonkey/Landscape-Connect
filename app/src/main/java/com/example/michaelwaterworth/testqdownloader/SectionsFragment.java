package com.example.michaelwaterworth.testqdownloader;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ViewFlipper;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by michaelwaterworth on 27/10/2015. Copyright Michael Waterworth
 */
public class SectionsFragment extends Fragment implements View.OnClickListener {
    static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    static final int MY_PERMISSIONS_REQUEST_STORAGE = 4;
    static final int REQUEST_IMAGE_CAPTURE = 2;
    static final int REQUEST_SECTION_DATA = 3;
    protected ViewFlipper flipper;
    protected ViewGroup base;
    protected Questionnaire questionnaire;
    protected Response response;

    public SectionsFragment() {
    }

    public void pageNext(){
        if(flipper != null) {
            flipper.showNext();  // Switches to the next view
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        base = (ViewGroup) inflater.inflate(R.layout.fragment_sections, container, false);
        getActivity().setTitle(getActivity().getString(R.string.new_landscape));
        setHasOptionsMenu(true);

        //View Flipper for switching between pages
        flipper = (ViewFlipper) base.findViewById(R.id.switcher);

        Button b = (Button) base.findViewById(R.id.button_take_photo);
        b.setOnClickListener(this);

        response = ((SectionsActivity) getActivity()).getResponse();

        questionnaire = response.questionnaire;

        String questions = questionnaire.getQuestions();

        Log.d("Questions", questions);

        Gson gson = new Gson();

        // Construct the data source
        ArrayList<Section> arrayOfSections = new ArrayList<>(Arrays.asList(gson.fromJson(questions, Section[].class)));

        //TODO Hack. Why is this -1?
        arrayOfSections.remove(arrayOfSections.size() - 1);

        Log.d("Size", "" + arrayOfSections.size());

        int i = 0;
        while(i < arrayOfSections.size()){
            Log.d("i", "" + i);
            Log.d("titles", arrayOfSections.get(i).getTitle());
            //Set whether the section is complete
            if(response.items().get(i) != null && response.items().get(i).data != null) {
                Log.d("IsCompleted", "Completed");
                arrayOfSections.get(i).setCompleted(true);
            } else {
                Log.d("IsCompleted", "Not completed");
            }
            i++;
        }

        // Create the adapter to convert the array to views
        SectionAdapter adapter = new SectionAdapter(getContext(), arrayOfSections);

        // Attach the adapter to a ListView
        ListView listView = (ListView) base.findViewById(R.id.sections_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                openSection(position);
            }
        });

        //Ensure we're on the right page based on the current Response status
        if(response.photo != null && !response.photo.isEmpty()){
            pageNext();
        }

        return base;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_take_photo:
                buttonTakePhoto(v);
                break;
            case R.id.section_row:
                openSection(0);
                break;
        }
    }

    public void openSection(int section_id){
        ((SectionsActivity)getActivity()).switchToSection(section_id);
    }

    public void buttonTakePhoto(View view) {
        checkPermissions();
    }

    public void checkPermissions(){
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.CAMERA)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                //TODO Add explanation
                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);

            } else {
                // No explanation needed, we can request the permission.
                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);
            }
        } else {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    //TODO Add explanation
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_STORAGE);

                } else {
                    // No explanation needed, we can request the permission.
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_STORAGE);
                }
            } else {
                takePhoto();
            }
        }
    }

    private void takePhoto(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e("Log", ex.getLocalizedMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        response.photo = "file:" + image.getAbsolutePath();
        response.save();
        return image;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.sections_fragment, menu);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("TAG", "Permission Granted");
                    checkPermissions();
                } else {
                    Log.d("TAG", "Permission denied");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("TAG", "Permission Granted");
                    checkPermissions();
                } else {
                    Log.d("TAG", "Permission denied");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK) {
            Log.d("Photo", "Got photo");
            pageNext();
        }
        if(requestCode == REQUEST_SECTION_DATA && resultCode == getActivity().RESULT_OK){
            Log.d("JSON", "Got JSON from Section");
            Bundle extras = data.getExtras();
            String json = (String) extras.get("data");
            pageNext();
        }
    }

}
