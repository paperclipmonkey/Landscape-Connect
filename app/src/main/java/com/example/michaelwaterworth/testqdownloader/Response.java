package com.example.michaelwaterworth.testqdownloader;

import android.database.Cursor;
import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.annotations.Expose;

import java.util.Calendar;
import java.util.List;

/**
 * Created by michaelwaterworth on 29/10/2015. Copyright Michael Waterworth
 */

@Table(name = "Responses", id = BaseColumns._ID)
public class Response extends Model {

    @Column(name = "id")
    private int id;

    @Expose
    @Column(name = "DateAdded")
    public Long dateAdded;//Date & Time to be completed


    @Expose
    @Column(name = "Questionnaire")
    public Questionnaire questionnaire;

    @Expose
    @Column(name = "Photo")
    public String photo;//Date & Time to be completed

    // This method is optional, does not affect the foreign key creation.
    public List<SectionResponse> items() {
        return getMany(SectionResponse.class, "Response");
    }

    @Expose
    @Column(name = "Complete")
    public Boolean complete;

    public static Response newInstance(Cursor c){
        int _id = c.getInt(c.getColumnIndex("_id"));
        return new Select()
                .from(Response.class)
                .where("_id = ?", _id)
                .executeSingle();
    }

    public Calendar getDateAdded() {
        Calendar rDate = Calendar.getInstance();
        rDate.setTimeInMillis(dateAdded * 1000);
        return rDate;
    }
}
