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
import java.util.UUID;

/**
 * Responses Model as used to save Questionnaire Responses
 */
@Table(name = "Responses", id = BaseColumns._ID)
public class Response extends Model {

    @Expose
    @Column(name = "Timestamp")
    public Long timestamp;//Date & Time to be completed
    // - - - - - - Location fields - - - - - - - -
    @Expose
    @Column(name = "Lng")
    public Double lng;//Date & Time to be completed
    @Expose
    @Column(name = "Lat")
    public Double lat;//Date & Time to be completed
    @Expose
    @Column(name = "LocAcc")
    public Float locAcc;//Location accuracy
    // - - - - - - End Location fields - - - - - - - -
    @Expose
    @Column(name = "Questionnaire", onDelete = Column.ForeignKeyAction.CASCADE)
    public Questionnaire questionnaire;

    @Expose
    @Column(name = "Photo")
    public String photo;//File address to photo

    @Expose
    @Column(name = "Thumb")
    public String thumb;//File address to thumbnail photo

    @Expose
    @Column(name = "Finished")
    public Boolean finished;//Finished is when all required sections are complete and the user says it's finished, ready for upload

    @Column(name = "id")
    private int id;

    @Expose
    @Column(name = "Uuid")
    public String uuid;

    @Expose
    @Column(name = "PercentUploaded")
    public int percentUploaded;//Save the % the response has been uploaded. Used to display in the uploading responses UI

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
    public boolean getFinished() { return this.finished; }

    /**
     * UUIDs are used to provide a unique ID to the server. This stops duplicate entries being uploaded.
     */
    public void generateUUID(){
        if(this.uuid == null){
            this.uuid = UUID.randomUUID().toString();
        }
    }
}
