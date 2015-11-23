package com.example.michaelwaterworth.testqdownloader;

import android.database.Cursor;
import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by michaelwaterworth on 29/10/2015. Copyright Michael Waterworth
 */

@Table(name = "Responses", id = BaseColumns._ID)
public class QuestionResponse extends Model {

    @Column(name = "id")
    private int id;

    @Expose
    @Column(name = "Question")
    public Question question;

    @Expose
    @Column(name = "RData")
    public String rData;

    // This method is optional, does not affect the foreign key creation.
    public List<SectionResponse> items() {
        return getMany(SectionResponse.class, "Response");
    }

    public static QuestionResponse newInstance(Cursor c){
        int _id = c.getInt(c.getColumnIndex("_id"));
        return new Select()
                .from(QuestionResponse.class)
                .where("_id = ?", _id)
                .executeSingle();
    }

    //Complete meaning question has been answered
    public boolean isComplete(){
        return ! this.rData.isEmpty();
    }
}
