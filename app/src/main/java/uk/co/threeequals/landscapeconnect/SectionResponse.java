package uk.co.threeequals.landscapeconnect;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Section Response model
 * Models section and QuestionResponses to sections
 * Internally holds Title and doesn't link to the original Section
 */
@Table(name = "SectionResponse", id = BaseColumns._ID)
public class SectionResponse extends Model {

    @Expose
    @Column(name = "SectionId")
    public String sectionId;//Title of completed section

    @Expose
    @Column(name = "Response", onDelete = Column.ForeignKeyAction.CASCADE)
    public Response response;

    @Expose
    @Column(name = "Complete")
    public boolean complete;

    public List<QuestionResponse> getQuestionResponses() {
        return getMany(QuestionResponse.class, "SectionResponse");
    }

    public String getSectionId() {
        return sectionId;
    }


    public boolean isCompleted() {
        return complete;
    }

    public void setCompleted(boolean complete) {
        this.complete = complete;
    }
}
