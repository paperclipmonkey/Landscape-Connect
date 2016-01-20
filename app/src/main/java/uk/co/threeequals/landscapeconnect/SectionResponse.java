package uk.co.threeequals.landscapeconnect;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by michaelwaterworth on 29/10/2015. Copyright Michael Waterworth
 */
@Table(name = "SectionResponse", id = BaseColumns._ID)
public class SectionResponse extends Model {

    @Expose
    @Column(name = "Title")
    public String title;//Title of completed section

    @Expose
    @Column(name = "Response", onDelete = Column.ForeignKeyAction.CASCADE)
    public Response response;

    @Expose
    @Column(name = "Complete")
    public boolean complete;

    public List<QuestionResponse> getQuestionResponses() {
        return getMany(QuestionResponse.class, "SectionResponse");
    }

    public boolean isCompleted() {
        return complete;
    }

    public void setCompleted(boolean complete) {
        this.complete = complete;
    }
}
