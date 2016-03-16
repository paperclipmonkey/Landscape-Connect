package uk.co.threeequals.landscapeconnect;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ViewFlipper;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Introduces the user to the application
 * Created by michaelwaterworth on 08/03/2016. Copyright Michael Waterworth
 */
public class IntroActivity extends AppCompatActivity {
    @Bind(R.id.intro_flipper) ViewFlipper flipper;
    @Bind(R.id.intro_button_next) Button nextButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        ButterKnife.bind(this);
    }

    @OnClick({ R.id.intro_button_next, R.id.intro_button_prev })
    public void nextPrevClick(View v) {
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
