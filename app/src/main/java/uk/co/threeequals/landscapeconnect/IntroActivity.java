package uk.co.threeequals.landscapeconnect;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ViewFlipper;

/**
 * Created by michaelwaterworth on 08/03/2016. Copyright Michael Waterworth
 */
public class IntroActivity extends AppCompatActivity implements View.OnClickListener {
    ViewFlipper flipper;
    Button nextButton;
    Button prevButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_activity);

        flipper = (ViewFlipper) findViewById(R.id.intro_flipper);


        nextButton = (Button) findViewById(R.id.intro_button_next);
        nextButton.setOnClickListener(this);

        prevButton = (Button) findViewById(R.id.intro_button_prev);
        prevButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.intro_button_next){
            if(flipper.getDisplayedChild() == flipper.getChildCount() - 1){
                finish();
                return;
            }
            if(flipper.getDisplayedChild() == flipper.getChildCount() - 2){
                nextButton.setText(R.string.letsgo);
            }
            //Show the next page
            flipper.setDisplayedChild(flipper.getDisplayedChild() + 1);
        }
        if(v.getId() == R.id.intro_button_prev){
            if(flipper.getDisplayedChild() == 0){
                finish();
                return;
            }
            nextButton.setText(R.string.next);
            //Show the previous page
            flipper.setDisplayedChild(flipper.getDisplayedChild() - 1);
        }
    }
}
