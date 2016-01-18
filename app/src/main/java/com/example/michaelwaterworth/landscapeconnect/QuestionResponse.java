package com.example.michaelwaterworth.landscapeconnect;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

/**
 * Created by michaelwaterworth on 29/10/2015. Copyright Michael Waterworth
 */

@Table(name = "QuestionResponse", id = BaseColumns._ID)
public class QuestionResponse extends Model {

    @Expose
    @Column(name = "Question")
    public Question question;
    @Expose
    @Column(name = "RData")
    public String rData;
    @Column(name = "SectionResponse")
    public SectionResponse sectionResponse;
    @Column(name = "id")
    private int id;

    //Complete meaning question has been answered
    public boolean isComplete() {
        if (this.rData != null) {
            if (!this.rData.isEmpty()) {
                return true;
            }
        }
        return false;
    }
}
