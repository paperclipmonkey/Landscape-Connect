package com.example.michaelwaterworth.testqdownloader;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.squareup.picasso.Picasso;

import java.util.List;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;

/**
 * Created by michaelwaterworth on 27/10/2015. Copyright Michael Waterworth
 */
public class SectionFragment extends Fragment implements View.OnClickListener {
//    protected ViewFlipper flipper;
    protected ViewGroup base;
    protected Response response;
    protected Section section;
    protected SectionResponse sectionResponse;
    protected int sectionNum;
    protected List<Question> questionsArr;
    protected ViewFlipper flipper;
    protected static int QUESTIONSPERPAGE = 3;
    protected Button doneButton;

//    protected void setTaskProgress(int percentage){
//        ProgressBar progressBar = (ProgressBar) base.findViewById(R.id.task_progressbar);
//        if(progressBar != null) {
//            progressBar.setProgress(percentage);
//        }
//    }
//
//    public void pageNext(){
//        if(flipper != null) {
//            flipper.showNext();  // Switches to the next view
//        }
//    }

    public SectionFragment() {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //TODO Hijacking all choice menu items from this Fragment
        super.onOptionsItemSelected(item);
        getFragmentManager().popBackStackImmediate();
        return true;
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
        section = ((SectionsActivity) getActivity()).getSection(sectionNum);
        questionsArr = section.getQuestions();
        response = ((SectionsActivity) getActivity()).getResponse();
        sectionResponse = response.getSectionResponses().get(sectionNum);

        doneButton = (Button) base.findViewById(R.id.section_button_next_done);
        doneButton.setOnClickListener(this);

        //Build the UI
        buildQuestionsView(questionsArr, flipper, sectionResponse);

        ImageViewTouch imageViewTouch = (ImageViewTouch) base.findViewById(R.id.section_image);

        Picasso.with(getContext())
                .load(response.photo)
                .fit()
                .centerInside()
                //.placeholder(R.drawable.loading)
                .into(imageViewTouch);

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
                    Log.d("SectionFragment", "Flipper Index: " + flipper.getDisplayedChild());
                    if(questionResponse.question.isRequired()) {
                        if (sectionResponse.getQuestionResponses().indexOf(questionResponse) + 1 <= (flipper.getDisplayedChild() + 1 * QUESTIONSPERPAGE)) {
                            Log.d("SectionFragment", "" + sectionResponse.getQuestionResponses().indexOf(questionResponse));
                            if (!questionResponse.isComplete()) {
                                Toast.makeText(getContext(), R.string.please_complete, Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                    }
                }


                if(flipper.getDisplayedChild() + 1 == flipper.getChildCount()){
                    sendResult();
                } else {
                    flipper.setDisplayedChild(flipper.getDisplayedChild() + 1);
                    ScrollView scrollView = (ScrollView) base;
                    scrollView.fullScroll(View.FOCUS_UP);
                    setButtonText();
                }
                break;
        }
    }

    public void setButtonText(){
        //Change the button text if this is the last page
        if(flipper.getDisplayedChild() + 1 == flipper.getChildCount()){
            doneButton.setText(R.string.done);
        }
    }

    public void serialiseData(){
        int i = 0;
        for(Question q: questionsArr){
            //res += q.getSerialisedAnswer();
            QuestionResponse qr = sectionResponse.getQuestionResponses().get(i);
            qr.rData = q.getSerialisedAnswer();
            i++;
        }
    }

    public void sendResult(){
        serialiseData();

        //TODO - Check if section is complete.
        //Check if section required.
        //If is then ensure all questions completed.
        sectionResponse.setCompleted(true);
        if(section.isRequired()) {
            for (QuestionResponse questionResponse : sectionResponse.getQuestionResponses()) {
                if(!questionResponse.isComplete()){
                    sectionResponse.setCompleted(false);
                }
            }
        }

        sectionResponse.save();

        getFragmentManager().popBackStackImmediate();
    }

    public void buildQuestionsView(List<Question> questions, ViewGroup flipper, SectionResponse sectionResponse){
        int i = 0;
        int qppi = 1;

        ViewGroup currrentBase = (ViewGroup) getActivity().getLayoutInflater().inflate(R.layout.fragment_section_page, null);
        flipper.addView(currrentBase);

        while(i < questions.size()){
            if(qppi % (QUESTIONSPERPAGE + 1) == 0){
                currrentBase = (ViewGroup) getActivity().getLayoutInflater().inflate(R.layout.fragment_section_page, null);
                flipper.addView(currrentBase);
                qppi = 1;
            }
            Question question = questions.get(i);
            currrentBase.addView(question.createBaseView(getContext(), sectionResponse.getQuestionResponses().get(i)));//TODO - Ordering
            i++;
            qppi++;
        }

        //To be done after building the UI
        setButtonText();
    }
}
