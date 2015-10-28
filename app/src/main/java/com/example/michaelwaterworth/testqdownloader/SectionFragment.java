package com.example.michaelwaterworth.testqdownloader;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

/**
 * Created by michaelwaterworth on 27/10/2015. Copyright Michael Waterworth
 */
public class SectionFragment extends Fragment implements View.OnClickListener {
//    protected ViewFlipper flipper;
    protected ViewGroup base;

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

        long qsId = getActivity().getIntent().getLongExtra("id", -1);

        Log.d("ID", "" + qsId);
        Qs qs = Qs.load(Qs.class, qsId);

        String questions; //= qs.getQuestions();
        questions = "[{\"title\":\"Section 1\",\"required\":true,\"questions\":[{\"title\":\"What is your name\",\"type\":\"string\"},{\"title\":\"Tell me a little about yourself\",\"type\":\"textarea\"}]}]";
        Log.d("Questions", "" + questions);

        Gson gson = new Gson();

        Section[] objs2 = gson.fromJson(questions, Section[].class);

        Question[] questionsArr = objs2[0].getQuestions();
        Log.d("Questions: ", questionsArr.toString());

        //Build the UI
        buildQuestionsView(questionsArr, (ViewGroup) base.findViewById(R.id.thanks));


        Button donebutton = (Button) base.findViewById(R.id.section_button_done);
        donebutton.setOnClickListener(this);

        return base;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.section_button_done:
                returnToSections();
                break;
        }
    }

    public void returnToSections(){

    }

    public void buildQuestionsView(Question[] questions, ViewGroup base){
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
                view = stringObj;
                break;
            default:
                view = new View(getContext());
        }

        viewGroup.addView(view);

        return viewGroup;
    }

}
