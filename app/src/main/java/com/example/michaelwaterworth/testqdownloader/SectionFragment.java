package com.example.michaelwaterworth.testqdownloader;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;

/**
 * Created by michaelwaterworth on 27/10/2015. Copyright Michael Waterworth
 */
public class SectionFragment extends Fragment implements View.OnClickListener {
//    protected ViewFlipper flipper;
    protected ViewGroup base;
    protected Questionnaire questionnaire;
    protected Response response;
    protected int sectionNum;
    protected ArrayList<Question> questionsArr;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        base = (ViewGroup) inflater.inflate(R.layout.fragment_section, container, false);
        getActivity().setTitle(getActivity().getString(R.string.new_landscape));
        setHasOptionsMenu(true);

        //View Flipper for switching between pages
//        flipper = (ViewFlipper) base.findViewById(R.id.switcher);

        response = ((SectionsActivity) getActivity()).getResponse();

        questionnaire = response.questionnaire;

        String questions = questionnaire.getQuestions();

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        Section[] objs2 = gson.fromJson(questions, Section[].class);

        sectionNum = getArguments().getInt("section_num");

        questionsArr = new ArrayList<>(Arrays.asList(objs2[sectionNum].getQuestions()));

        for(Question sec : questionsArr){
            if(sec == null){
                questionsArr.remove(sec);
            }
        }

        //Build the UI
        buildQuestionsView(questionsArr, (ViewGroup) base.findViewById(R.id.questions));

        Button donebutton = (Button) base.findViewById(R.id.section_button_done);
        donebutton.setOnClickListener(this);

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
        SectionResponse sectionResponse = response.items().get(sectionNum);

        String res = "";

        for(Question q: questionsArr){
            res += q.getSerialisedAnswer();
        }

        sectionResponse.data = res;

        //TODO - Check if section is complete.
        sectionResponse.complete = true;

        sectionResponse.save();

        getFragmentManager().popBackStackImmediate();
    }

    public void buildQuestionsView(List<Question> questions, ViewGroup base){
        for(Question question : questions){
            base.addView(question.createBaseView(getContext()));
        }
    }

}
