package com.example.michaelwaterworth.testqdownloader;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.gson.Gson;

/**
 * Created by michaelwaterworth on 27/10/2015. Copyright Michael Waterworth
 */
public class PagesFragment extends Fragment implements View.OnClickListener {
    protected ViewFlipper flipper;
    protected ViewGroup base;

    protected void setTaskProgress(int percentage){
        ProgressBar progressBar = (ProgressBar) base.findViewById(R.id.task_progressbar);
        if(progressBar != null) {
            progressBar.setProgress(percentage);
        }
    }

    public void pageNext(){
        if(flipper != null) {
            flipper.showNext();  // Switches to the next view
        }
    }

    public PagesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        base = (ViewGroup) inflater.inflate(R.layout.fragment_sections, container, false);
        getActivity().setTitle(getActivity().getString(R.string.new_landscape));
        setHasOptionsMenu(true);

        //View Flipper for switching between pages
        flipper = (ViewFlipper) base.findViewById(R.id.switcher);

        Button b = (Button) base.findViewById(R.id.button_take_photo);
        b.setOnClickListener(this);

        long qsId = getActivity().getIntent().getLongExtra("id", -1);

        Log.d("ID", "" + qsId);
        Qs qs = Qs.load(Qs.class, qsId);

        String questions = qs.getQuestions();
        questions = "[{\"title\":\"Section 1\",\"required\":true,\"pages\":[{\"questions\":[{\"title\":\"What is your name\",\"type\":\"string\"},{\"title\":\"Tell me a little about yourself\",\"type\":\"textarea\"}]}]}]";
        Log.d("Questions", "" + questions);

        Gson gson = new Gson();

        Section[] objs2 = gson.fromJson(questions, Section[].class);

        //Build the UI
        buildSectionsView(objs2, (ViewGroup) base.findViewById(R.id.diary_page));

        return base;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //case R.id.button_take_photo:
            //    break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.sections_fragment, menu);
    }

    public void buildSectionsView(Section[] sections, ViewGroup base){
        for(Section section : sections){
            base.addView(buildSectionView(section));
        }
    }

    public View buildSectionView(Section section){
        LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);// Identify and inflate the new view you seek to project on the current view.
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.sections_row, null);// You would want to add your new inflated view to your layout

        TextView title = (TextView) viewGroup.findViewById(R.id.section_row_title);
        title.setText(section.getTitle());

        TextView required = (TextView) viewGroup.findViewById(R.id.section_row_description);
        required.setText("" + section.isRequired());

        return viewGroup;
    }

    public View buildQuestionView(Question question){
        LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);// Identify and inflate the new view you seek to project on the current view.
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.sections_row, null);// You would want to add your new inflated view to your layout

        RelativeLayout relativeLayout = new RelativeLayout(getContext());
        TextView title = new TextView(getContext());
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

        relativeLayout.addView(title);
        relativeLayout.addView(view);

        return relativeLayout;
    }

}
