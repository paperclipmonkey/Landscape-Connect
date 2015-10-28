package com.example.michaelwaterworth.testqdownloader;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
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
public class SectionsFragment extends Fragment implements View.OnClickListener {
    static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    static final int REQUEST_IMAGE_CAPTURE = 2;
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

    public SectionsFragment() {
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

//        //Build a sample piece of JSON
//        Question q1 = new Question();
//        q1.setTitle("How old are you");
//        q1.setType("string");
//        q1.setRequired(true);
//
//        Page p1 = new Page();
//
//        ArrayList<Question> questionArr = new ArrayList<Question>();
//        questionArr.add(q1);
//
//        p1.setQuestions(questionArr);
//
//        Section s1 = new Section();
//        s1.setRequired(true);
//        s1.setTitle("Section 1");
//
//        ArrayList<Page> pagesArray = new ArrayList<Page>();
//        pagesArray.add(p1);
//        s1.setPages(pagesArray);
//
//        ArrayList<Section> sectionsArray = new ArrayList<Section>();
//        sectionsArray.add(s1);

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
            case R.id.button_take_photo:
                buttonTakePhoto(v);
                break;
        }
    }

    public void buttonTakePhoto(View view) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {

        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.sections_fragment, menu);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("TAG", "Permission Granted");
                    //TODO
                } else {
                    Log.d("TAG", "Permission denied");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK) {
            Log.d("Photo", "Got photo");
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            //mImageView.setImageBitmap(imageBitmap);
            pageNext();
        }
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