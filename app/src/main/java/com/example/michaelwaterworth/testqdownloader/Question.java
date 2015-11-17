package com.example.michaelwaterworth.testqdownloader;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.annotations.Expose;

/**
 * Created by michaelwaterworth on 28/10/2015. Copyright Michael Waterworth
 */
public class Question {
    @Expose
    public String title;
    @Expose
    public String type;
    @Expose
    public boolean required;
    @Expose
    public String[] options;

    public ViewGroup baseView;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String[] getSelect() {
        return options;
    }

    public void setSelect(String[] selectOptions) {
        this.options = selectOptions;
    }

    public ViewGroup getBaseView() { return baseView; }

    public void setBaseView(ViewGroup baseView) { this.baseView = baseView; }

    public ViewGroup createBaseView(Context cx){
        LayoutInflater inflater = (LayoutInflater)cx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);// Identify and inflate the new view you seek to project on the current view.
        baseView = (ViewGroup) inflater.inflate(R.layout.question, null);// You would want to add your new inflated view to your layout

        TextView title = (TextView) baseView.findViewById(R.id.question_question);
        title.setText(getTitle());

        View view;
        switch (getType()){
            case "textarea":
                EditText textareaObj = new EditText(cx);
                view = textareaObj;
                break;
            case "string":
                EditText stringObj = new EditText(cx);
                stringObj.setLines(1);
                view = stringObj;
                break;
            case "radio":
                RadioGroup radioGroup = new RadioGroup(cx);
                for(String option: getSelect()){
                    RadioButton radioButton = new RadioButton(cx);
                    radioButton.setText(option);
                    radioGroup.addView(radioButton);
                }
                view = radioGroup;
                break;
            case "multi":
                LinearLayout layout = new LinearLayout(cx);
                layout.setOrientation(LinearLayout.VERTICAL);
                for(String option: getSelect()) {
                    CheckBox checkBox = new CheckBox(cx);
                    checkBox.setText(option);
                    layout.addView(checkBox);
                }
                view = layout;
                break;
            default:
                view = new View(cx);
        }

        //question.setBaseView(viewGroup);

        baseView.addView(view);

        return baseView;
    }


    public String getSerialisedAnswer(){
        int count;
        switch (type){
            case "string":
            case "textarea":
                count = baseView.getChildCount();
                for (int i = 0; i <= count; i++) {
                    View v = baseView.getChildAt(i);
                    if (v instanceof EditText) {
                        return ((EditText) v).getText().toString();
                    }
                }
                break;
            case "multi":
                String str = "";
                //TODO clean up hack. Reliant on subview. Instead check if derived from ViewGroup and sub-drill?
                ViewGroup answerBase = (ViewGroup) baseView.getChildAt(1);
                for (int i = 0; i <= answerBase.getChildCount(); i++) {
                    View v = answerBase.getChildAt(i);
                    if (v instanceof CheckBox) {
                        CheckBox checkBox = (CheckBox) v;
                        if(checkBox.isChecked()){
                            str += checkBox.getText().toString();
                        }
                    }
                }
                return str;
            case "radio":
                count = baseView.getChildCount();
                for (int i = 0; i <= count; i++) {
                    View v = baseView.getChildAt(i);
                    if (v instanceof RadioGroup) {
                        RadioGroup radioGroup = (RadioGroup) v;
                        int selId = radioGroup.getCheckedRadioButtonId();
                        if(selId != -1) {
                            return ((RadioButton) v.findViewById(selId)).getText().toString();
                        } return null;
                    }
                }
                break;
            //TODO Test support for single line?
        }
        Log.e("question", "Error parsing results");
        return null;
    }
}
