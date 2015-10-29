package com.example.michaelwaterworth.testqdownloader;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.google.gson.annotations.Expose;

/**
 * Created by michaelwaterworth on 29/10/2015. Copyright Michael Waterworth
 */
public class SectionResponse extends Model{

    @Expose
    @Column(name = "Title")
    private String title;//Date & Time to be completed

    @Column(name = "Response")
    public Response response;

    @Expose
    @Column(name = "Data")
    private String data;//Date & Time to be completed
}
