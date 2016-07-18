package uk.co.threeequals.landscapeconnect;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.text.DateFormat;

/**
 * Show one questionnaire's details
 */
public class QuestionnaireActivity extends AppCompatActivity {

    private Questionnaire questionnaire;

    public QuestionnaireActivity(){

    }

    public void startNewResponse(){
        Intent intent = new Intent(this, SectionsActivity.class);
        intent.putExtra("id", questionnaire.getId());
        startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.questionnaire_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewResponse();
            }
        });

        long questionnaireNum =  getIntent().getLongExtra("id", -1);

        questionnaire = Questionnaire.load(Questionnaire.class, questionnaireNum);

        TextView descriptionView = (TextView) findViewById(R.id.questionnaire_details_description);
        TextView installedView = (TextView) findViewById(R.id.questionnaire_details_installed);
        TextView websiteView = (TextView) findViewById(R.id.questionnaire_details_website);
        TextView creatorView = (TextView) findViewById(R.id.questionnaire_details_creator);

        // Set Collapsing Toolbar layout to the screen
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        //TextView shortCodeView = (TextView) findViewById(R.id.questionnaire_details_shortcode);

        DateFormat df = DateFormat.getDateInstance();

        collapsingToolbar.setTitle(questionnaire.getTitle());
        descriptionView.setText(questionnaire.getDescription());
        installedView.setText(df.format(questionnaire.getDateAdded().getTime()));
        websiteView.setText(getString(R.string.base_url) + "questionnaires/" + questionnaire.getServerId());
        creatorView.setText(questionnaire.getOwnerName());
        //shortCodeView.setText(questionnaire.getServerId());

        //Set visibility of check/cross bools
        if(questionnaire.getGetInitialPhoto()){
            findViewById(R.id.usesPhotoCheck).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.usesPhotoCross).setVisibility(View.VISIBLE);
        }

        if(questionnaire.getGetLocation()){
            findViewById(R.id.usesLocationCheck).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.usesLocationCross).setVisibility(View.VISIBLE);
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(questionnaire.getTitle());
            actionBar.setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        super.onCreateOptionsMenu(menu);
//        menu.clear();
//        MenuInflater mInflater =  getMenuInflater();
//        mInflater.inflate(R.menu.questionnaire_menubar, menu);
//        return true;
//    }
}
