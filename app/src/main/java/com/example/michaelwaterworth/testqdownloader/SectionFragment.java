package com.example.michaelwaterworth.testqdownloader;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;

/**
 * Created by michaelwaterworth on 27/10/2015. Copyright Michael Waterworth
 */
public class SectionFragment extends Fragment implements View.OnClickListener {
//    protected ViewFlipper flipper;
    protected ViewGroup base;
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

        sectionNum = getArguments().getInt("section_num");
        questionsArr = ((SectionsActivity) getActivity()).getSection(sectionNum);

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
        Log.d("sectionFragment", "Saving result for section: " + sectionNum);
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
