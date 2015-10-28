package com.example.michaelwaterworth.testqdownloader;

/**
 * Created by michaelwaterworth on 28/10/2015. Copyright Michael Waterworth
 */
public class Question {
    public String title;
    public String type;
    public boolean required;
    public String selectOptions;

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

    public String getSelectOptions() {
        return selectOptions;
    }

    public void setSelectOptions(String selectOptions) {
        this.selectOptions = selectOptions;
    }
}