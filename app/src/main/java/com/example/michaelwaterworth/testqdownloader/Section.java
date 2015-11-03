package com.example.michaelwaterworth.testqdownloader;

/**
 * Created by michaelwaterworth on 28/10/2015. Copyright Michael Waterworth
 */
public class Section {
    public String title;
    public boolean required;

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean completed;
    public Question[] questions;

    public Question[] getQuestions() {
        return questions;
    }

    public void setQuestions(Question[] questions) {
        this.questions = questions;
    }

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
