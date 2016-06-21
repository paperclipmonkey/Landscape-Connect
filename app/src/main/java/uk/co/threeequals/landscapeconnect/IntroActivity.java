package uk.co.threeequals.landscapeconnect;

import android.content.SharedPreferences;
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
 */
public class IntroActivity extends AppCompatActivity {
    @Bind(R.id.intro_flipper)
    public ViewFlipper flipper;
    @Bind(R.id.intro_button_next)
    public Button nextButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        ButterKnife.bind(this);
        setSeenIntro();
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

    /**
     * Once the user has signed the intro signature panel
     */
    private void setSeenIntro() {
        SharedPreferences settings = getSharedPreferences(MyApp.SHAREDPREFERENCES, 1);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(MyApp.SEENINTRO, true);
        // Commit the edit
        editor.apply();
    }
}
