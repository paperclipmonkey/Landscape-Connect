package com.example.michaelwaterworth.testqdownloader;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by michaelwaterworth on 27/10/2015. Copyright Michael Waterworth
 */
public class SectionsActivity extends AppCompatActivity {
    private Response response;

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
        }

        //Create Response object to base this on
        response = createResponse(getIntent().getLongExtra("id", -1));

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
    }

    private Response createResponse(Long questionnaireId){
        Questionnaire questionnaire = Questionnaire.load(Questionnaire.class, questionnaireId);

        Gson gson = new Gson();

        // Construct the array of sections
        ArrayList<Section> arrayOfSections = new ArrayList<>(Arrays.asList(gson.fromJson(questionnaire.getQuestions(), Section[].class)));

        Response response = new Response();
        response.questionnaire = questionnaire;
        //Get Id for Object
        response.save();

        int i = 0;
        while(i < arrayOfSections.size()){
            SectionResponse sectionResponse = new SectionResponse();
            sectionResponse.response = response;
            sectionResponse.save();
            i++;
        }

        return response;
    }

    public Response getResponse(){
        return this.response;
    }

    public void switchToSection(int section){
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
}
