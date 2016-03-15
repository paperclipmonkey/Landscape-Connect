package uk.co.threeequals.landscapeconnect;

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
 * Responses Model as used to save Questionnaire Responses
 * Created by michaelwaterworth on 29/10/2015. Copyright Michael Waterworth
 */

@Table(name = "Responses", id = BaseColumns._ID)
public class Response extends Model {

    @Expose
    @Column(name = "Timestamp")
    public Long timestamp;//Date & Time to be completed
    @Expose
    @Column(name = "Lat")
    public Double lat;//Date & Time to be completed

    // - - - - - - Location fields - - - - - - - -
    @Expose
    @Column(name = "Lng")
    public Double lng;//Date & Time to be completed
    @Expose
    @Column(name = "LocAcc")
    public Float locAcc;//Location accuracy
    @Expose
    @Column(name = "Questionnaire", onDelete = Column.ForeignKeyAction.CASCADE)
    public Questionnaire questionnaire;

    // - - - - - - End Location fields - - - - - - - -
    @Expose
    @Column(name = "Photo")
    public String photo;//File address to photo
    //Finished is when all required sections are complete and the user says it's finished, ready for upload
    @Expose
    @Column(name = "Finished")
    public Boolean finished;
    @Column(name = "id")
    private int id;

    public static Response newInstance(Cursor c) {
        int _id = c.getInt(c.getColumnIndex("_id"));
        return new Select()
                .from(Response.class)
                .where("_id = ?", _id)
                .executeSingle();
    }

    public static List<Response> getFinishedResponses() {
        return new Select()
                .from(Response.class)
                .where("Finished = ?", true)
                .execute();
    }

    public List<SectionResponse> getSectionResponses() {
        return getMany(SectionResponse.class, "Response");
    }

    public Calendar getDateCompleted() {
        Calendar rDate = Calendar.getInstance();
        rDate.setTimeInMillis(timestamp);
        return rDate;
    }

    public void deleteFull() {
        List<SectionResponse> sectionResponses = getSectionResponses();
        for (SectionResponse sectionResponse : sectionResponses) {
            List<QuestionResponse> questionResponses = sectionResponse.getQuestionResponses();
            for (QuestionResponse questionResponse : questionResponses) {
                questionResponse.delete();
            }
            sectionResponse.delete();
        }
        this.delete();
    }

    public void setFinished() {
        this.finished = true;
    }
}
