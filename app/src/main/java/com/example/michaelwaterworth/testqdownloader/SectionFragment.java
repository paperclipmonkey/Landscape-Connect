package com.example.michaelwaterworth.testqdownloader;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
//        flipper = (ViewFlipper) base.findViewById(R.id.switcher);

        sectionNum = getArguments().getInt("section_num");
        section = ((SectionsActivity) getActivity()).getSection(sectionNum);
        questionsArr = section.getQuestions();
        response = ((SectionsActivity) getActivity()).getResponse();
        sectionResponse = response.getSectionResponses().get(sectionNum);


        //Build the UI
        buildQuestionsView(questionsArr, (ViewGroup) base.findViewById(R.id.questions), sectionResponse);

        Button doneButton = (Button) base.findViewById(R.id.section_button_done);
        doneButton.setOnClickListener(this);

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
            case R.id.section_button_done:
                sendResult();
                break;
        }
    }

    public void sendResult(){
        int i = 0;
        for(Question q: questionsArr){
            //res += q.getSerialisedAnswer();
            QuestionResponse qr = sectionResponse.getQuestionResponses().get(i);
            qr.rData = q.getSerialisedAnswer();
            i++;
        }

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

    public void buildQuestionsView(List<Question> questions, ViewGroup base, SectionResponse sectionResponse){
        int i = 0;
        while(i < questions.size()){
            Question question = questions.get(i);
            base.addView(question.createBaseView(getContext(), sectionResponse.getQuestionResponses().get(i)));//TODO - Ordering
            i++;
        }
    }
}
