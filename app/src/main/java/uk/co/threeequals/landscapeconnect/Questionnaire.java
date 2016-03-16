package uk.co.threeequals.landscapeconnect;

import android.database.Cursor;
import android.provider.BaseColumns;
import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.From;
import com.activeandroid.query.Select;
import com.google.gson.annotations.Expose;

import java.util.Calendar;
import java.util.List;

/**
 * Base class for a Questionnaire object
 * Relationships in the database are:
 * Section
 *      Question
 *          Choice
 * Response
 *      SectionResponse
 *          QuestionResponse
 * Created by michaelwaterworth on 30/07/15. Copyright Michael Waterworth
 */

@Table(name = "Questionnaire", id = BaseColumns._ID)
public class Questionnaire extends Model {

    @Column(name = "id")
    private int id;
    @Expose
    @Column(name = "DateAdded")
    private Long dateAdded;//Date & Time to be completed
    @Expose
    @Column(name = "Name")
    private String name;//Name of class to be run
    @Expose
    @Column(name = "Description")
    private String description;//Title to be used in notification
    @Expose
    @Column(name = "IntroTitle")
    private String introTitle;//Title to be used when opening questionnaire
    @Expose
    @Column(name = "IntroDescription")
    private String introDescription;//Description to be used when opening questionnaire
    @Expose
    @Column(name = "IntroImage")
    private String introImage;//Image to be used when opening questionnaire
    @Expose
    @Column(name = "ServerId")
    private String serverId;//Any serverId to be sent to the Task class
    @Expose
    @Column(name = "UploadUrl")
    private String uploadUrl;//Any serverId to be sent to the Task class
    @Expose
    @Column(name = "GetLocation")
    private Boolean getLocation;//Any serverId to be sent to the Task class
    @Expose
    @Column(name = "GetLocationAccuracy")
    private int getLocationAccuracy;//Any serverId to be sent to the Task class
    @Expose
    @Column(name = "GetInitialPhoto")
    private Boolean getInitialPhoto;//Any serverId to be sent to the Task class

    @Expose
    private Section[] sections;

    public Boolean getGetLocation() {
        return getLocation;
    }

    public void setGetLocation(Boolean getLocation) {
        this.getLocation = getLocation;
    }

    public int getGetLocationAccuracy() {
        return getLocationAccuracy;
    }

    public void setGetLocationAccuracy(int getLocationAccuracy) {
        this.getLocationAccuracy = getLocationAccuracy;
    }

    public Boolean getGetInitialPhoto() {
        return getInitialPhoto;
    }

    public void setGetInitialPhoto(Boolean getInitialPhoto) {
        this.getInitialPhoto = getInitialPhoto;
    }


    public Questionnaire() {
        dateAdded = Calendar.getInstance().getTimeInMillis() / 1000;
    }

    public static Questionnaire newInstance(Cursor c) {
        int _id = c.getInt(c.getColumnIndex("_id"));
        return new Select()
                .from(Questionnaire.class)
                .where("_id = ?", _id)
                .executeSingle();
    }

    public static List<Questionnaire> getAll() {
        return new Select()
                .from(Questionnaire.class)
                //.where("Category = ?", category.getId())
                .orderBy("name ASC")
                .execute();
    }

    public static List<Questionnaire> getFromIds(long[] ids) {
        From query = new Select()
                .from(Questionnaire.class)
                .where("_id = -1");
        for (long id : ids) {
            query.or("_id = ?", id);
            System.out.println(id);
        }
        return query.execute();
    }

    public static void deleteFromIds(long[] ids) {
        From query = new Select()
                .from(Questionnaire.class)
                .where("_id = -1");
        for (long id : ids) {
            query.or("_id = ?", id);
            System.out.println(id);
        }
        List<Questionnaire> qs = query.execute();

        for (Questionnaire questionnaire : qs) {
            //Remove responses
            List<Response> responses = questionnaire.getResponses();
            for (Response response : responses) {
                /* Code moved to response object */
//                List<SectionResponse> sectionResponses = response.getSectionResponses();
//                for(SectionResponse sectionResponse: sectionResponses){
//                    List<QuestionResponse> questionResponses = sectionResponse.getQuestionResponses();
//                    for(QuestionResponse questionResponse: questionResponses){
//                        questionResponse.delete();
//                    }
//                    sectionResponse.delete();
//                }
                response.deleteFull();
            }

            List<Section> secList = questionnaire.getSections();
            for (Section s : secList) {
                List<Question> queList = s.getQuestions();
                for (Question question : queList) {
                    question.delete();
                }
                s.delete();
            }

            questionnaire.delete();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIntroTitle() {
        return introTitle;
    }

    public void setIntroTitle(String introTitle) {
        this.introTitle = introTitle;
    }

    public String getIntroDescription() {
        return introDescription;
    }

    public void setIntroDescription(String introDescription) {
        this.introDescription = introDescription;
    }

    public String getIntroImage() {
        return introImage;
    }

    public void setIntroImage(String introImage) {
        this.introImage = introImage;
    }

    public List<Section> getSections() {
        return getMany(Section.class, "Questionnaire");
    }

    public void setSections(Section[] sections) {
        this.sections = sections;
    }

    public List<Response> getResponses() {
        return getMany(Response.class, "Questionnaire");
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getUploadUrl() {
        return uploadUrl;
    }

    public void setUploadUrl(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }

    public Calendar getDateAdded() {
        Calendar rDate = Calendar.getInstance();
        rDate.setTimeInMillis(dateAdded * 1000);

        return rDate;
    }

    public void setDateAdded(Calendar rDate) {
        this.dateAdded = rDate.getTimeInMillis() / 1000;
    }

    public void saveQuestionnaire() {
        Log.d("Saving", "Questionnaire");
        this.save();
        for (Section section : sections) {
            section.questionnaire = this;
            section.saveSection();
        }
    }
}
