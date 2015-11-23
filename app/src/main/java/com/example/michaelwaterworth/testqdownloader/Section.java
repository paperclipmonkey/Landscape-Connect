package com.example.michaelwaterworth.testqdownloader;

import android.provider.BaseColumns;
import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by michaelwaterworth on 28/10/2015. Copyright Michael Waterworth
 */
@Table(name = "Section", id = BaseColumns._ID)
public class Section extends Model{
    @Column(name = "id")
    private int id;

    @Expose
    @Column(name = "Title")
    public String title;

    @Expose
    @Column(name = "Required")
    public boolean required;

    @Expose
    public Question[] questions;


    @Column(name = "Questionnaire", onDelete = Column.ForeignKeyAction.CASCADE)
    public Questionnaire questionnaire;

    public static List<Section> getAll() {
        // This is how you execute a query
        return new Select()
                .from(Section.class)
                //.where("Category = ?", category.getId())
                //.orderBy("Name ASC")
                .execute();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Question> getQuestions() {
        return getMany(Question.class, "Section");
    }

    public void saveSection(){
        Log.d("Saving", "Section");
        this.save();
        for(Question question: questions){
            question.section = this;
            question.saveQuestion();
        }
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }
}
