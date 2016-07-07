package uk.co.threeequals.landscapeconnect;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Section Fragment shows a list of questions that can be answered
 * Works with a viewFlipper to allow paging
 */
public class SectionFragment extends Fragment implements View.OnClickListener {
    private static final int QUESTIONS_PER_PAGE = 3;
    private static final String R_SECTION_KEY = "sKey";
    private static final String R_PAGE_KEY = "pKey";
    private ViewGroup base;
    private SectionResponse sectionResponse;
    private int sectionNum;
    private int startPageNum = 0;//Allows coming back to the right page after orientation change
    private List<Question> questionsArr;
    private ViewFlipper flipper;
    private Button doneButton;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //TODO Hijacking all choice menu items from this Fragment
        super.onOptionsItemSelected(item);
        getFragmentManager().popBackStackImmediate();
        return true;
    }

    @Override
    public void onDestroyView() {
        saveResult();//Save the data out when destroying
        super.onDestroyView();
        base = null;
        flipper = null;
        doneButton = null;
        questionsArr = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sectionResponse = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        base = (ViewGroup) inflater.inflate(R.layout.fragment_section, container, false);
        getActivity().setTitle(getActivity().getString(R.string.new_landscape));
        setHasOptionsMenu(true);

        //View Flipper for switching between pages
        flipper = (ViewFlipper) base.findViewById(R.id.section_page_switcher);

        sectionNum = getArguments().getInt("section_num");
        Section section = ((SectionsActivity) getActivity()).getSection(sectionNum);

        if(savedInstanceState != null){
            startPageNum = savedInstanceState.getInt(R_PAGE_KEY);
        }

        questionsArr = section.getQuestions();
        Response response = ((SectionsActivity) getActivity()).getResponse();
        sectionResponse = response.getSectionResponses().get(sectionNum);

        doneButton = (Button) base.findViewById(R.id.section_button_next_done);
        doneButton.setOnClickListener(this);

        //Build the UI
        buildQuestionsView(questionsArr, flipper, sectionResponse);

        ImageView imageViewTouch = (ImageView) base.findViewById(R.id.section_image);

        Picasso.with(getContext())
                .load(response.photo)
                .error(R.drawable.hi_res_icon)
                .into(imageViewTouch);

        //Go to the correct page in the section
        flipper.setDisplayedChild(startPageNum);

        return base;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.section_button_next_done:
                //check if all the questions on the page have been completed
                serialiseData();
                for (QuestionResponse questionResponse : sectionResponse.getQuestionResponses()) {
                    //Check all questions on pages up to the current page
                    if (questionResponse.question.isRequired()) {
                        if (sectionResponse.getQuestionResponses().indexOf(questionResponse) + 1 <= ((flipper.getDisplayedChild() + 1) * QUESTIONS_PER_PAGE)) {
                            if (!questionResponse.isComplete()) {
                                Toast.makeText(getContext(), R.string.please_complete, Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                    }
                }


                if (flipper.getDisplayedChild() + 1 == flipper.getChildCount()) {
                    saveResult();
                    getFragmentManager().popBackStackImmediate();
                } else {
                    flipper.setDisplayedChild(flipper.getDisplayedChild() + 1);
                    final ScrollView scrollView = (ScrollView) base;

                    scrollView.post(new Runnable() {
                        public void run() {
                            scrollView.scrollTo(0, scrollView.getTop());
                            scrollView.fullScroll(View.FOCUS_UP);
                        }
                    });
                    setButtonText();
                }
                break;
        }
    }

    public int getDisplayedPage(){
        return flipper.getDisplayedChild();
    }

    public int getDisplayedSection(){
        return sectionNum;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        int currentPage = getDisplayedPage();
        outState.putInt(R_PAGE_KEY, currentPage);

        int currentSection = getDisplayedSection();
        outState.putInt(R_SECTION_KEY, currentSection);

        Log.i("SectionsActivity", "Current Section: " + currentSection);
        Log.i("SectionsActivity", "Current Page: " + currentPage);
    }

    private void setButtonText() {
        //Change the button text if this is the last page
        if (flipper.getDisplayedChild() + 1 == flipper.getChildCount()) {
            doneButton.setText(R.string.done);
        }
    }

    private void serialiseData() {
        int i = 0;
        for (Question q : questionsArr) {
            // 1 - 1 mapping between questions and question responses. Messy but it works.
            QuestionResponse qr = sectionResponse.getQuestionResponses().get(i);
            String qSerialisedResponse = q.getSerialisedAnswer();
            //if(qSerialisedResponse != null) {//Stop String "null"
                qr.rData = qSerialisedResponse;
                qr.save();
            //}
            i++;
        }
    }

    private void saveResult() {
        serialiseData();
        sectionResponse.setCompleted(true);
        for (QuestionResponse questionResponse : sectionResponse.getQuestionResponses()) {
            if (questionResponse.question.isRequired()) {
                if (!questionResponse.isComplete()) {
                    sectionResponse.setCompleted(false);
                }
            }
        }

        sectionResponse.save();
    }

    private void buildQuestionsView(List<Question> questions, ViewGroup flipper, SectionResponse sectionResponse) {
        int i = 0;
        int qppi = 1;//Questions per page iterator

        ViewGroup currentBase = (ViewGroup) getActivity().getLayoutInflater().inflate(R.layout.fragment_section_page, null);
        flipper.addView(currentBase);

        while (i < questions.size()) {
            if (qppi % (QUESTIONS_PER_PAGE + 1) == 0) {
                currentBase = (ViewGroup) getActivity().getLayoutInflater().inflate(R.layout.fragment_section_page, null);
                flipper.addView(currentBase);
                qppi = 1;
            }
            Question question = questions.get(i);
            currentBase.addView(question.createBaseView(getContext(), sectionResponse.getQuestionResponses().get(i)));//TODO - Ordering
            i++;
            qppi++;
        }

        //To be done after building the UI
        setButtonText();
    }
}
