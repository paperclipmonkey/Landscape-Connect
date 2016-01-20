package uk.co.threeequals.landscapeconnect;

import android.provider.BaseColumns;
import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by michaelwaterworth on 28/10/2015. Copyright Michael Waterworth
 */
@Table(name = "Section", id = BaseColumns._ID)
public class Section extends Model {
    @Expose
    @Column(name = "Title")
    public String title;
    @Expose
    public Question[] questions;
    @Column(name = "Questionnaire", onDelete = Column.ForeignKeyAction.CASCADE)
    public Questionnaire questionnaire;
    @Column(name = "id")
    private int id;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Question> getQuestions() {
        return getMany(Question.class, "Section");
    }

    public void saveSection() {
        Log.d("Saving", "Section");
        this.save();
        for (Question question : questions) {
            question.section = this;
            question.saveQuestion();
        }
    }

    public boolean hasRequiredQuestions() {
        List<Question> questions = getMany(Question.class, "Section");
        for (Question q : questions) {
            if (q.isRequired()) {
                return true;
            }
        }
        return false;
    }
}
