package com.example.michaelwaterworth.landscapeconnect;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.List;

/**
 * Created by michaelwaterworth on 27/10/2015. Copyright Michael Waterworth
 */
public class SectionsActivity extends AppCompatActivity {
    private static final String R_ID_KEY = "rId";
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
        if(savedInstanceState != null && savedInstanceState.getLong(R_ID_KEY, -1) != -1){
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
    }

    private void loadQuestionnaire(Long questionnaireId){
        questionnaire = Questionnaire.load(Questionnaire.class, questionnaireId);
    }

    public Section getSection(int sectionNum){
        return questionnaire.getSections().get(sectionNum);
    }

    private void loadResponse(Long rId){
        response = Response.load(Response.class, rId);
    }

    private void createResponse(){
        // Construct the array of sections
        List<Section> arrayOfSections = questionnaire.getSections();

        response = new Response();
        response.questionnaire = questionnaire;
        //Get Id for Object
        response.save();

        for(Section section: arrayOfSections){
            SectionResponse sectionResponse = new SectionResponse();
            sectionResponse.title = section.title;//Copy title from section to section response
            sectionResponse.response = response;
            sectionResponse.save();

            for(Question question: section.getQuestions()){
                QuestionResponse questionResponse = new QuestionResponse();
                questionResponse.question = question;
                questionResponse.sectionResponse = sectionResponse;
                questionResponse.save();
            }
        }
    }

    public Questionnaire getQuestionnaire(){ return this.questionnaire; }

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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putLong(R_ID_KEY, response.getId());
        super.onSaveInstanceState(outState);
    }
}
