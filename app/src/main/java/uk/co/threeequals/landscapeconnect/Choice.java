package uk.co.threeequals.landscapeconnect;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

/**
 * Choices are subjects of questions - Used to hold an individual choice for a question. Normalised dataSet
 */
@Table(name = "Choice", id = BaseColumns._ID)
public class Choice extends Model {

    @Column(name = "Choice")
    @Expose
    public String choice;

    //Maintain a link back to the question
    @Column(name = "Question", onDelete = Column.ForeignKeyAction.CASCADE)
    public Question question;

    @Column(name = "id")
    private int id;
}