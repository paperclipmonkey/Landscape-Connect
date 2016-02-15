package uk.co.threeequals.landscapeconnect;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import java.text.DateFormat;

/**
 * Created by michaelwaterworth on 28/01/2016. Copyright Michael Waterworth
 */
public class QuestionnaireActivity extends AppCompatActivity {

    private Questionnaire questionnaire;
    private Button nextButton;
    private String TAG = "QuestionnaireActivity";

    public QuestionnaireActivity(){

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_new_response){
            Intent intent = new Intent(this, SectionsActivity.class);
            intent.putExtra("id", questionnaire.getId());
            startActivity(intent);
            return true;
        } else {
            super.onOptionsItemSelected(item);
            return false;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.questionnaire_details);

        long questionnaireNum =  getIntent().getLongExtra("id", -1);

        questionnaire = Questionnaire.load(Questionnaire.class, questionnaireNum);


        TextView descriptionView = (TextView) findViewById(R.id.questionnaire_details_description);
        TextView installedView = (TextView) findViewById(R.id.questionnaire_details_installed);
        TextView websiteView = (TextView) findViewById(R.id.questionnaire_details_website);
        TextView shortCodeView = (TextView) findViewById(R.id.questionnaire_details_shortcode);

        DateFormat df = DateFormat.getDateInstance();

        descriptionView.setText(questionnaire.getDescription());
        installedView.setText(df.format(questionnaire.getDateAdded().getTime()));
        websiteView.setText(getString(R.string.base_url) + "questionnaires/" + questionnaire.getServerId());
        shortCodeView.setText(questionnaire.getServerId());


        //nextButton = (Button) findViewById(R.id.section_button_next_done);
        //nextButton.setOnClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //actionBar.setTitle("New Landscape");
            actionBar.setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.clear();
        MenuInflater mInflater =  new MenuInflater(getApplicationContext());
        mInflater.inflate(R.menu.questionnaire_fragment, menu);
        return true;
    }
}
