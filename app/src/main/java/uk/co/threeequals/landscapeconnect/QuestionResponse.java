package uk.co.threeequals.landscapeconnect;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

/**
 * Question Response saves a users response to a question
 * Data is serialised as rData
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
