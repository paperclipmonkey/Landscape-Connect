package com.example.michaelwaterworth.testqdownloader;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.alexbbb.uploadservice.UploadServiceBroadcastReceiver;

public class QuestionnairesActivity extends AppCompatActivity{
    public static final int LISTFRAGMENT = 0;
    public static final int UPLOADFRAGMENT = 1;

    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_navigation_menu);
            //actionBar.setTitle(R.string.questionnaires);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        //Initializing NavigationView
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                //Closing drawer on item click
                mDrawerLayout.closeDrawers();
                Fragment fragment;

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    case R.id.drawer_questionnaires:
                        fragment = new QuestionnairesFragment();
                        setTitle(R.string.questionnaires);
                        break;
                    case R.id.drawer_about:
                        fragment = new AboutFragment();
                        setTitle(R.string.about);
                        break;
                    default:
                        fragment = new UploadListFragment();
                        setTitle("Upload Queue");
                        break;

                }
                android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.contentFragment, fragment);
                fragmentTransaction.commit();
                return true;
            }
        });

        switchFragment(LISTFRAGMENT);
    }

    public void switchFragment(int fragmentId){
        Fragment fragment;
        if(fragmentId == 0){
            setTitle(R.string.questionnaires);
            fragment =  new QuestionnairesFragment();
        } else {
            setTitle(R.string.upload_queue);
            fragment = new UploadListFragment();
        }

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.contentFragment, fragment);
        transaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static final String TAG = "AndroidUploadService";

    private final UploadServiceBroadcastReceiver uploadReceiver =
        new UploadServiceBroadcastReceiver() {

            // you can override this progress method if you want to get
            // the completion progress in percent (0 to 100)
            // or if you need to know exactly how many bytes have been transferred
            // override the method below this one
            @Override
            public void onProgress(String uploadId, int progress) {
                Log.i(TAG, "The progress of the upload with ID "
                        + uploadId + " is: " + progress);
            }

            @Override
            public void onProgress(final String uploadId,
                                   final long uploadedBytes,
                                   final long totalBytes) {
                Log.i(TAG, "Upload with ID " + uploadId +
                        " uploaded bytes: " + uploadedBytes
                        + ", total: " + totalBytes);
            }

            @Override
            public void onError(String uploadId, Exception exception) {
                Log.e(TAG, "Error in upload with ID: " + uploadId + ". "
                        + exception.getLocalizedMessage(), exception);
            }

            @Override
            public void onCompleted(String uploadId,
                                    int serverResponseCode,
                                    String serverResponseMessage) {
                Log.i(TAG, "Upload with ID " + uploadId
                        + " has been completed with HTTP " + serverResponseCode
                        + ". Response from server: " + serverResponseMessage);

                //If your server responds with a JSON, you can parse it
                //from serverResponseMessage string using a library
                //such as org.json (embedded in Android) or Google's gson
            }
        };

    @Override
    protected void onResume() {
        super.onResume();
        uploadReceiver.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        uploadReceiver.unregister(this);
    }
}
