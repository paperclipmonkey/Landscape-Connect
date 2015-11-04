package com.example.michaelwaterworth.testqdownloader;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
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

        Gson gson = new Gson();

        Section[] objs2 = gson.fromJson(questions, Section[].class);

        sectionNum = getArguments().getInt("section_num");

        ArrayList<Question> questionsArr = new ArrayList<>(Arrays.asList(objs2[sectionNum].getQuestions()));

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
        //TODO - Save section data
        sectionResponse.data = "Section completed data as JSON";
        sectionResponse.save();

        getFragmentManager().popBackStackImmediate();
    }

    public void buildQuestionsView(List<Question> questions, ViewGroup base){
        for(Question question : questions){
            base.addView(buildQuestionView(question));
        }
    }

    public View buildQuestionView(Question question){
        LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);// Identify and inflate the new view you seek to project on the current view.
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.question, null);// You would want to add your new inflated view to your layout

        TextView title = (TextView) viewGroup.findViewById(R.id.question_question);
        title.setText(question.getTitle());

        View view;
        switch (question.getType()){
            case "textarea":
                EditText textareaObj = new EditText(getContext());
                view = textareaObj;
                break;
            case "string":
                EditText stringObj = new EditText(getContext());
                stringObj.setLines(1);
                view = stringObj;
                break;
            case "radio":
                RadioGroup radioGroup = new RadioGroup(getContext());
                for(String option: question.getSelect()){
                    RadioButton radioButton = new RadioButton(getContext());
                    radioButton.setText(option);
                    radioGroup.addView(radioButton);
                }
                view = radioGroup;
                break;
            case "multi":
                LinearLayout layout = new LinearLayout(getContext());
                layout.setOrientation(LinearLayout.VERTICAL);
                for(String option: question.getSelect()) {
                    CheckBox checkBox = new CheckBox(getContext());
                    checkBox.setText(option);
                    layout.addView(checkBox);
                }
                view = layout;
                break;
            default:
                view = new View(getContext());
        }

        viewGroup.addView(view);

        return viewGroup;
    }

}
