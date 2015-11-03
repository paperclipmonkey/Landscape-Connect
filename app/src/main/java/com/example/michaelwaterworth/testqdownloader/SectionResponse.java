package com.example.michaelwaterworth.testqdownloader;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

/**
 * Created by michaelwaterworth on 29/10/2015. Copyright Michael Waterworth
 */
@Table(name = "SectionResponse", id = BaseColumns._ID)
public class SectionResponse extends Model{

    @Expose
    @Column(name = "Title")
    public String title;//Date & Time to be completed

    @Column(name = "Response")
    public Response response;

    @Expose
    @Column(name = "Data")
    public String data;//Date & Time to be completed
}
