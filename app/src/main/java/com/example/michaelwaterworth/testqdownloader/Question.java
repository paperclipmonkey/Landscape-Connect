package com.example.michaelwaterworth.testqdownloader;

import android.content.Context;
import android.provider.BaseColumns;
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

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by michaelwaterworth on 28/10/2015. Copyright Michael Waterworth
 */
@Table(name = "Question", id = BaseColumns._ID)
public class Question extends Model{
    public static final String QTYPE_STRING = "string";
    public static final String QTYPE_MULTI = "multi";
    public static final String QTYPE_RADIO = "radio";
    public static final String QTYPE_TEXTAREA = "textarea";

    @Column(name = "id")
    private int id;

    @Column(name = "Title")
    @Expose
    public String title;

    @Column(name = "Type")
    @Expose
    public String type;

    @Column(name = "Required")
    @Expose
    public boolean required;

    @Expose
    public Choice[] choices;

    @Column(name = "Section" , onDelete = Column.ForeignKeyAction.CASCADE)
    @Expose
    public Section section;

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

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public List<Choice> getChoices() {
        return getMany(Choice.class, "Question");
    }

    public ViewGroup createBaseView(Context cx, QuestionResponse questionResponse){
        LayoutInflater inflater = (LayoutInflater)cx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);// Identify and inflate the new view you seek to project on the current view.
        baseView = (ViewGroup) inflater.inflate(R.layout.question, null);// You would want to add your new inflated view to your layout

        TextView title = (TextView) baseView.findViewById(R.id.question_question);
        title.setText(getTitle());

        View view;
        switch (getType()){
            case QTYPE_TEXTAREA:
                EditText textAreaObj = new EditText(cx);
                if(questionResponse != null && questionResponse.rData!= null){
                    textAreaObj.setText(questionResponse.rData);
                }
                view = textAreaObj;
                break;
            case QTYPE_STRING:
                EditText stringObj = new EditText(cx);
                stringObj.setLines(1);
                view = stringObj;
                break;
            case QTYPE_RADIO:
                RadioGroup radioGroup = new RadioGroup(cx);
                for(Choice option: getChoices()){
                    int index = getChoices().indexOf(option);
                    RadioButton radioButton = new RadioButton(cx);
                    radioButton.setText(option.choice);
                    radioButton.setId(index);
                    //TODO - Add a delimiter so we can check against full responses
                    radioGroup.addView(radioButton);
                    if(questionResponse.rData != null && questionResponse.rData.contains(option.choice)){
                        radioGroup.check(index);
                    }
                }
                view = radioGroup;
                break;
            case QTYPE_MULTI:
                LinearLayout layout = new LinearLayout(cx);
                layout.setOrientation(LinearLayout.VERTICAL);
                for(Choice option: getChoices()){
                    CheckBox checkBox = new CheckBox(cx);
                    checkBox.setText(option.choice);
                    if(questionResponse.rData != null && questionResponse.rData.contains(option.choice)){
                        checkBox.setChecked(true);
                    }
                    layout.addView(checkBox);
                }
                view = layout;
                break;
            default:
                view = new View(cx);
        }

        baseView.addView(view);
        return baseView;
    }


    public String getSerialisedAnswer(){
        int count;
        switch (type){
            case QTYPE_STRING:
            case QTYPE_TEXTAREA:
                count = baseView.getChildCount();
                for (int i = 0; i <= count; i++) {
                    View v = baseView.getChildAt(i);
                    if (v instanceof EditText) {
                        return ((EditText) v).getText().toString();
                    }
                }
                break;
            case QTYPE_MULTI:
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
            case QTYPE_RADIO:
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
        }
        Log.e("question", "Error parsing results");
        return null;
    }

    public void saveQuestion(){
        this.save();
        if(choices != null){
        for(Choice choice: choices){
            choice.question = this;
            choice.save();
        }
        }
    }
}
