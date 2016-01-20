package uk.co.threeequals.landscapeconnect;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

/**
 * Created by michaelwaterworth on 22/11/2015. Copyright Michael Waterworth
 */
@Table(name = "Choice", id = BaseColumns._ID)
public class Choice extends Model {
    @Column(name = "Choice")
    @Expose
    public String choice;
    @Column(name = "Question", onDelete = Column.ForeignKeyAction.CASCADE)
    public Question question;
    @Column(name = "id")
    private int id;
}