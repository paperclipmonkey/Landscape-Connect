package com.example.michaelwaterworth.testqdownloader;

import com.google.gson.annotations.Expose;

/**
 * Created by michaelwaterworth on 28/10/2015. Copyright Michael Waterworth
 */
public class Section {
    @Expose
    public String title;
    @Expose
    public boolean required;
    @Expose
    public boolean completed;
    @Expose
    public Question[] questions;

    public Question[] getQuestions() { return questions; }

    public void setQuestions(Question[] questions) { this.questions = questions; }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

}
