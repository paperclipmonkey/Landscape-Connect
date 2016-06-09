package uk.co.threeequals.landscapeconnect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import net.gotev.uploadservice.UploadServiceBroadcastReceiver;

/**
 * First Activity shown on load
 * Displays a list of questionnaires
 * Also shows a left hand burger menu
 */
public class QuestionnairesActivity extends AppCompatActivity {
    public static final int QUESTIONNAIRES_FRAGMENT = 0;
    public static final int UPLOAD_FRAGMENT = 1;
    private static final String TAG = "QuestionnairesActivity";

    private final UploadServiceBroadcastReceiver uploadReceiver =
            new UploadServiceBroadcastReceiver() {

                // you can override this progress method if you want to get
                // the completion progress in percent (0 to 100)
                // or if you need to know exactly how many bytes have been transferred
                // override the method below this one
                @Override
                public void onProgress(String uploadId, int progress) {
                    Log.i(TAG, "Updating response % with  ID "
                            + uploadId + " is: " + progress);


                    Response response = Response.load(Response.class, Integer.parseInt(uploadId));
                    if(response != null) {
                        response.percentUploaded = progress;
                        response.save();
                    }
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

                    Response response = Response.load(Response.class, Integer.parseInt(uploadId));
                    response.percentUploaded = 0;
                }

                @Override
                public void onCompleted(String uploadId,
                                        int serverResponseCode,
                                        byte[] serverResponse) {
                    String serverResponseMessage = new String(serverResponse);
                    Log.i(TAG, "Upload with ID " + uploadId
                            + " has been completed with HTTP " + serverResponseCode
                            + ". Response from server: " + serverResponseMessage);

                    //If your server responds with a JSON, you can parse it
                    //from serverResponseMessage string using a library
                    //such as org.json (embedded in Android) or Google's gson
                }
            };
    private DrawerLayout mDrawerLayout;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    /**
     * Check whether the user has signed the intro panel before
     * @return Boolean whether they've signed
     */
    private boolean hasSeenIntro() {
        // Restore preferences
        SharedPreferences settings = getSharedPreferences(MyApp.SHAREDPREFERENCES, 1);
        return settings.getBoolean(MyApp.SEENINTRO, false);
    }

    /**
     * Run on first opening of the app
     */
    public void startIntro() {
        Intent intent = new Intent(this, IntroActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDrawerLayout = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseCrash.report(new Exception("Questionnaire Activity Error"));

        FirebaseCrash.log("Questionnaire Activity created");


        if (!hasSeenIntro()) {
            //Show intro screens
            startIntro();
        }

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
        final NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);


        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                //Checking if the item is in checked state or not, if not make it in checked state
               // menuItem.setChecked(true);

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
                    case R.id.drawer_intro:
                        Intent intent = new Intent(getBaseContext(), IntroActivity.class);
                        startActivity(intent);
                        return true;
                    default:
                        fragment = new ResponsesFragment();
                        setTitle(getString(R.string.upload_queue));
                        break;
                }
                
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.contentFragment, fragment);
                fragmentTransaction.commit();
                return true;
            }
        });

        switchFragment(QUESTIONNAIRES_FRAGMENT);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void switchFragment(int fragmentId) {
        Fragment fragment;
        if (fragmentId == QUESTIONNAIRES_FRAGMENT) {
            setTitle(R.string.questionnaires);
            fragment = new QuestionnairesFragment();
        } else if (fragmentId == UPLOAD_FRAGMENT) {
            setTitle(R.string.upload_queue);
            fragment = new ResponsesFragment();
        } else {
            setTitle(R.string.about);
            fragment = new AboutFragment();
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

        super.onOptionsItemSelected(item);

        switch (id) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

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

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Questionnaires Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://uk.co.threeequals.landscapeconnect/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Questionnaires Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://uk.co.threeequals.landscapeconnect/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
