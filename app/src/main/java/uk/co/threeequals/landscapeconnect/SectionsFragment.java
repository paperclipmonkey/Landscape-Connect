package uk.co.threeequals.landscapeconnect;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Fragment that is displayed when starting a new response
 * Takes image, grabs location, lists sections, saves response
 * Created by michaelwaterworth on 27/10/2015. Copyright Michael Waterworth
 */
public class SectionsFragment extends Fragment implements View.OnClickListener {
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int MY_PERMISSIONS_REQUEST_STORAGE = 3;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 4;
    private ViewFlipper flipper;
    private ViewGroup base;
    private Questionnaire questionnaire;
    private Response response;
    private ArrayList<SectionResponseLink> sectionResponseLinks;
    //private ProgressBar progressBar;
    private LocationGetter locationGetter;

    public SectionsFragment() {
    }

    private void setTaskProgress(int percentage) {
        Log.d("Progress", "" + percentage);

//        //Remove old progress bar from the UI
//        ViewGroup progressHolder = (ViewGroup) base.findViewById(R.id.task_progressbar_holder);
//        progressHolder.removeAllViews();
//
//        ViewGroup child = (ViewGroup) getActivity().getLayoutInflater().inflate(R.layout.progress_bar, null);
//
//        progressBar = (ProgressBar) child.findViewById(R.id.task_progressbar);
//
//        progressBar.setIndeterminate(false);
//        progressBar.setProgress(percentage);
//
//        progressHolder.addView(child);
    }


    private void pageNext() {
        flipper.showNext();  // Switches to the next view
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (base == null) {
            //Log.d("Base", "Base null");
            base = (ViewGroup) inflater.inflate(R.layout.fragment_sections, container, false);

            //View Flipper for switching between pages
            flipper = (ViewFlipper) base.findViewById(R.id.switcher);
            Button b = (Button) base.findViewById(R.id.button_take_photo);
            b.setOnClickListener(this);
        }

        //progressBar = (ProgressBar) base.findViewById(R.id.task_progressbar);

        getActivity().setTitle(getActivity().getString(R.string.new_landscape));
        setHasOptionsMenu(true);

        response = ((SectionsActivity) getActivity()).getResponse();

        questionnaire = response.questionnaire;

        // Get the data source
        List<Section> arrayOfSections = ((SectionsActivity) getActivity()).getQuestionnaire().getSections();

        //Section Response Link links section and sectionresponse. Holding the View and the Db result.
        sectionResponseLinks = new ArrayList<>();
        //Link sections and section responses.
        int i = 0;
        while (i < arrayOfSections.size()) {
            SectionResponseLink srl = new SectionResponseLink(response.getSectionResponses().get(i), arrayOfSections.get(i));
            sectionResponseLinks.add(srl);
            i++;
        }

        // Create the adapter to convert the array to views
        SectionAdapter adapter = new SectionAdapter(getContext(), sectionResponseLinks);

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
        if (response.photo != null && !response.photo.isEmpty() && flipper.getDisplayedChild() == 0) {
            pageNext();
        }

        calculateTaskProgress();

        getActivity().invalidateOptionsMenu();

        checkLocationPermissions();

        buildIntroPage();

        return base;
    }

    private void buildIntroPage() {
        TextView title = (TextView) base.findViewById(R.id.page1_intro_title);
        TextView subTitle = (TextView) base.findViewById(R.id.page1_intro_desciprion);
        ImageView image = (ImageView) base.findViewById(R.id.page1_intro_image);
        title.setText(questionnaire.getIntroTitle());
        subTitle.setText(questionnaire.getIntroDescription());
        if (questionnaire.getIntroImage() != null) {
            Log.d("Qs", "intro image");
            int introEnd = questionnaire.getIntroImage().indexOf(",");
            byte[] decodedString = Base64.decode(questionnaire.getIntroImage().substring(introEnd), Base64.DEFAULT);//Remove metadata from the start of the string
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            image.setImageBitmap(decodedByte);
            //image.setImageResource(R.drawable.hi_res_icon);
        } else {
            Log.d("Qs", "No intro image");
        }

        //page1_title, page1_subtitle
    }

    private void calculateTaskProgress() {
        int completedCount = 1;
        for (SectionResponseLink srl : sectionResponseLinks) {
            //Set whether the section is complete
            if (srl.sectionResponse != null && srl.sectionResponse.isCompleted()) {
                completedCount++;
            }
        }

        int percentage = Math.round(((float) completedCount / (float) (1 + sectionResponseLinks.size())) * 100);//Photo included as task
        setTaskProgress(percentage);
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

    private void openSection(int section_id) {
        ((SectionsActivity) getActivity()).switchToSection(section_id);
    }

    private void buttonTakePhoto(View view) {
        checkPermissions();
    }

    private void checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
            return;
        }
        /*
        Start the location watcher
        We can't do this until the permission requests have gone through
         */
        locationGetter = new LocationGetter(getContext());
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // No explanation needed, we can request the permission.
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
            return;
        }
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // No explanation needed, we can request the permission.
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_STORAGE);
            return;
        }

        //We've cleared all of the Android permission system - Take the photo
        takePhoto();
    }

    private void takePhoto() {

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
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.UK).format(new Date());
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
    public void onPrepareOptionsMenu(Menu menu) {
        if (checkComplete())
            menu.getItem(0).setEnabled(true);
        else
            menu.getItem(0).setEnabled(false);
    }

    private boolean checkComplete() {
        for (SectionResponseLink srl : sectionResponseLinks) {
            //Required and not complete Escape.
            if (srl.section.hasRequiredQuestions() && !srl.sectionResponse.isCompleted()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_upload:
                response.setFinished();
                response.save();

                //Start the upload service...
                Intent serviceIntent = new Intent(getContext(), LSUploadService.class);
                getContext().startService(serviceIntent);

                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
                    Log.d("TAG", "Permission denied camera");
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
                    Log.d("TAG", "Permission denied storage");
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("TAG", "Permission Granted location");
                    checkLocationPermissions();
                } else {
                    Log.d("TAG", "Permission denied location");
                }
            }
        }
    }

    private void getLocation() {
        //Check if we can get location
        LatLng latLng = locationGetter.getLocation();
        if(latLng == null){
            Toast.makeText(getContext(), "Can't grab location", Toast.LENGTH_LONG).show();
            return;
        }
        Float accuracy = locationGetter.getAccuracy();
        Log.d("SectionFragment", "Location grabbed from Fragment" + latLng.toString());
        if (locationGetter.getAccuracy() < 50) {
            locationGetter.cancel(true);
            response.lat = latLng.latitude;
            response.lng = latLng.longitude;
            response.locAcc = accuracy;
        } else {
            Log.d("SectionFragment", "Accuracy over 50m. Setting 1s timeout");
            final Handler h = new Handler();
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getLocation();
                }
            }, 1000); // 1 second delay (takes millis)
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            getLocation();
            response.timestamp = Calendar.getInstance().getTimeInMillis();
            pageNext();
        }
    }

}
