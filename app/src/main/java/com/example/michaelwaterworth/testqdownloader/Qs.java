package com.example.michaelwaterworth.testqdownloader;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.From;
import com.activeandroid.query.Select;
import com.google.gson.annotations.Expose;

import java.util.Calendar;
import java.util.List;

/**
 * Base class for a task object in the database
 * Created by michaelwaterworth on 30/07/15. Copyright Michael Waterworth
 */

@Table(name = "Qs", id = BaseColumns._ID)
public class Qs extends Model implements Parcelable {
    public static final Creator<Qs> CREATOR = new Creator<Qs>() {
        @Override
        public Qs createFromParcel(Parcel in) {
            return new Qs(in);
        }

        @Override
        public Qs[] newArray(int size) {
            return new Qs[size];
        }
    };

    public static Qs newInstance(Cursor c){
//        Qs ld = new Qs()
          int _id = c.getInt(c.getColumnIndex("_id"));
//        ld.setId(_id);
//        return ld;
        return new Select()
                .from(Qs.class)
                .where("_id = ?", _id)
                .executeSingle();
    }

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
    @Column(name = "ServerId")
    private String serverId;//Any serverId to be sent to the Task class

    @Expose
    @Column(name = "Questions")
    private String questions;//Bool has this Task already been used as a notification

    public Qs() {
        dateAdded = Calendar.getInstance().getTimeInMillis() / 1000;
    }

    public static List<Qs> getAll() {
        return new Select()
                .from(Qs.class)
                //.where("Category = ?", category.getId())
                .orderBy("name ASC")
                .execute();
    }

    public static List<Qs> getFromIds(long[] ids) {
        From query = new Select()
                .from(Qs.class)
                .where("_id = -1");
        for (long id: ids) {
            query.or("_id = ?", id);
            System.out.println(id);
        }
        return query.execute();
    }

    public static void deleteFromIds(long[] ids) {
        From query = new Delete()
                .from(Qs.class)
                .where("_id = -1");
        for (long id: ids) {
            query.or("_id = ?", id);
            System.out.println(id);
        }
        query.execute();
    }

    public Qs(Parcel in) {
        dateAdded = in.readLong();
        name = in.readString();
        serverId = in.readString();
        questions = in.readString();
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

    public String getQuestions() {
        return questions;
    }

    public void setQuestions(String questions) {
        this.questions = questions;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public Calendar getDateAdded() {
        Calendar rDate = Calendar.getInstance();
        rDate.setTimeInMillis(dateAdded * 1000);

        return rDate;
    }

    public void setDateAdded(Calendar rDate) {
        this.dateAdded = rDate.getTimeInMillis() / 1000;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeLong(dateAdded);
        out.writeString(name);
        out.writeString(serverId);
        out.writeString(questions);
    }
}
