package uk.co.threeequals.landscapeconnect;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Fragment that is displayed when starting a new response
 * Takes image, grabs location, lists sections, saves response
 * Created by michaelwaterworth on 27/10/2015. Copyright Michael Waterworth
 */
public class SectionsFragment extends Fragment {
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int MY_PERMISSIONS_REQUEST_STORAGE = 3;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 4;

    @Bind(R.id.switcher) ViewFlipper flipper;
    @Bind(R.id.sections_list) ListView listView;

    private ViewGroup base;

    private Questionnaire questionnaire;
    private Response response;
    private ArrayList<SectionResponseLink> sectionResponseLinks;
    private LocationGetter locationGetter;

    private void pageNext() {
        flipper.showNext();  // Switches to the next view
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (base == null) {
            base = (ViewGroup) inflater.inflate(R.layout.fragment_sections, container, false);
        }

        //Dependancy injection for views
        ButterKnife.bind(this, base);


        getActivity().setTitle(getActivity().getString(R.string.new_landscape));
        setHasOptionsMenu(true);

        response = ((SectionsActivity) getActivity()).getResponse();

        questionnaire = response.questionnaire;

        // Get the data source
        List<Section> arrayOfSections = ((SectionsActivity) getActivity()).getQuestionnaire().getSections();

        //Section Response Link links Section and SectionResponse. Holding the View and the Db result.
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

        getActivity().invalidateOptionsMenu();

        if(questionnaire.getGetLocation()) {
            //Check permissions and turn on Location Listener Service
            checkLocationPermissions();
        } else {
            //TODO Hide the checker on both pages

        }

        buildIntroPage();

        return base;
    }

    /**
     * Build the UI for the intro page
     * Customised with title, description and image
     */
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
        } else {
            Log.d("Qs", "No intro image");
        }

        if (!questionnaire.getGetLocation()) {
            //Hide location spinner when we don't need to use it
            ViewGroup locationView = (ViewGroup) base.findViewById(R.id.page1_intro_location);
            locationView.setVisibility(View.GONE);
        }
    }

    private void openSection(int section_id) {
        ((SectionsActivity) getActivity()).switchToSection(section_id);
    }

    @OnClick({R.id.button_take_photo})
    public void buttonTakePhoto(View view) {
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
        locationGetter = new LocationGetter(getContext(), 10);//Context, accuracy target
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
                photoFile = MyApp.createImageFile(response);
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.sections_menubar, menu);
    }

//    @Override
//    public void onPrepareOptionsMenu(Menu menu) {
//        if (checkComplete()) {
//            MenuItem item = menu.getItem(0);
//            SpannableString spanString = new SpannableString(menu.getItem(0).getTitle().toString() + "Rdy");
//            spanString.setSpan(new ForegroundColorSpan(Color.WHITE), 0, spanString.length(), 0); //fix the color to white
//            item.setTitle(spanString);
//        } else {
//            MenuItem item = menu.getItem(0);
//            SpannableString spanString = new SpannableString(menu.getItem(0).getTitle().toString() + "Not");
//            spanString.setSpan(new ForegroundColorSpan(Color.BLACK), 0, spanString.length(), 0); //fix the color to white
//            item.setTitle(spanString);
//        }
//    }

    /**
     * Ensure all required sections of response exist
     * @return Bool - is response completed
     */
    private boolean checkComplete() {
        if(questionnaire.getGetLocation()){//If questionnaire asks for location
            if(response.lat == null){
                return false;
            }
        }
        if(questionnaire.getGetInitialPhoto()){//We wanted to take a photo
            if(response.photo == null || response.photo.length() < 1) return false;//Questionnaire not complete until it has a photo
        }

        for (SectionResponseLink srl : sectionResponseLinks) {
            if (srl.section.hasRequiredQuestions() && !srl.sectionResponse.isCompleted()) {
                //Required and not completed - Escape.
                return false;
            }
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /**
             * Clicked upload button in top menu
             */
            case R.id.action_upload:
                if(checkComplete()) {
                    response.setFinished();
                    response.percentUploaded = 0;
                    response.generateUUID();
                    response.save();

                    //Start the upload service...
                    Intent serviceIntent = new Intent(getContext(), LSUploadService.class);
                    getContext().startService(serviceIntent);

                    getActivity().finish();
                    return true;
                } else {
                    Toast.makeText(getContext(), "Please complete all required sections", Toast.LENGTH_LONG).show();
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Ensure we have all the permissions we asked for
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("TAG", "Permission Granted");
                    checkPermissions();
                } else {
                    Log.d("TAG", "Permission denied camera");
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

    /**
     * Check if we can get location
     */
    private void getLocation() {
        LatLng latLng = locationGetter.getLocation();

        if(latLng == null) {
            Log.d("SectionFragment", "LatLng empty. Setting 1s timeout");
            final Handler h = new Handler();
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getLocation();
                }
            }, 1000); // 1 second delay (takes millis)
        } else {
            Float accuracy = locationGetter.getAccuracy();
            Log.d("SectionFragment", "Location grabbed from Fragment" + latLng.toString());
            if (accuracy < questionnaire.getGetLocationAccuracy()) {
                locationGetter.cancel(true);
                response.lat = latLng.latitude;
                response.lng = latLng.longitude;
                response.locAcc = accuracy;
                response.save();

                //Hide notification / Show success
                View getLocationView = base.findViewById(R.id.sections_get_location);
                getLocationView.setVisibility(View.GONE);
                View gotLocationView = base.findViewById(R.id.sections_got_location);
                gotLocationView.setVisibility(View.VISIBLE);

                //Enable the upload button
                //By rebuilding the menu
                getActivity().invalidateOptionsMenu();

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
    }

    /**
     * Respond to taking a photo returning to activity
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            if(questionnaire.getGetLocation()) {
                getLocation();
            }
            response.timestamp = Calendar.getInstance().getTimeInMillis();
            MyApp.resizeToThumb(response);
            response.save();
            pageNext();
        }
    }



}
