package uk.co.threeequals.landscapeconnect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.common.api.Status;

import java.util.List;

/**
 * Used to collect a response from the user
 * Holds the fragments that do the heavy lifting
 * Provides helper functions for loading the Questionnaire
 * Created by michaelwaterworth on 27/10/2015. Copyright Michael Waterworth
 */
public class SectionsActivity extends AppCompatActivity {
    private static final String R_ID_KEY = "rId";
    private static final int MY_REQUEST_CHECK_SETTINGS = 5;
    private static final String TAG = "SectionsActivity";
    private Response response;
    private Questionnaire questionnaire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sections);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("New Landscape");
            actionBar.setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        //Create Response object to base this on
        loadQuestionnaire(getIntent().getLongExtra("id", -1));
        if (savedInstanceState != null && savedInstanceState.getLong(R_ID_KEY, -1) != -1) {
            loadResponse(savedInstanceState.getLong(R_ID_KEY));
        } else {
            createResponse();
        }

        Fragment fragment = new SectionsFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.contentFragment, fragment);
        fragmentTransaction.commit();

        getSupportFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    public void onBackStackChanged() {
                        // Update your UI here.
                    }
                });

        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(mStatusReceiver, new IntentFilter(LocationGetter.INTENT_STATUS));
    }

    private void loadQuestionnaire(Long questionnaireId) {
        questionnaire = Questionnaire.load(Questionnaire.class, questionnaireId);
    }

    public Section getSection(int sectionNum) {
        return questionnaire.getSections().get(sectionNum);
    }

    private void loadResponse(Long rId) {
        response = Response.load(Response.class, rId);
    }

    private void createResponse() {
        // Construct the array of sections
        List<Section> arrayOfSections = questionnaire.getSections();

        response = new Response();
        response.questionnaire = questionnaire;
        //Get Id for Object
        response.save();

        for (Section section : arrayOfSections) {
            SectionResponse sectionResponse = new SectionResponse();
            sectionResponse.title = section.title;//Copy title from section to section response
            sectionResponse.response = response;
            sectionResponse.save();

            for (Question question : section.getQuestions()) {
                QuestionResponse questionResponse = new QuestionResponse();
                questionResponse.question = question;
                questionResponse.sectionResponse = sectionResponse;
                questionResponse.save();
            }
        }
    }

    public Questionnaire getQuestionnaire() {
        return this.questionnaire;
    }

    public Response getResponse() {
        return this.response;
    }

    public void switchToSection(int section) {
        Fragment fragment = new SectionFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("section_num", section);
        fragment.setArguments(bundle);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        // Add this transaction to the back stack
        transaction.addToBackStack("Section");
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
                return false;
        }

        return super.onOptionsItemSelected(item);
    }

    private final BroadcastReceiver mStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "got intent from Broadcast receiver");
            // Get extra data included in the Intent
            if (intent.hasExtra(LocationGetter.INTENT_STATUS_STATUS)) {

                try{
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    Status status = intent.getParcelableExtra(LocationGetter.INTENT_STATUS_STATUS);
                    status.startResolutionForResult(
                            SectionsActivity.this,
                            MY_REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    // Ignore the error.
                }
            }
        }
    };

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putLong(R_ID_KEY, response.getId());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.unregisterReceiver(mStatusReceiver);
        questionnaire = null;
        response = null;
    }
}
