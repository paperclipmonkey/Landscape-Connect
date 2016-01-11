package com.example.michaelwaterworth.testqdownloader;

/**
 * Created by michaelwaterworth on 16/11/2015. Copyright Michael Waterworth
 */
public class SectionResponseLink {
    //Holds a link between sections and section responses
    SectionResponse sectionResponse;
    Section section;

    public SectionResponseLink(SectionResponse sectionResponse, Section section){
        this.sectionResponse = sectionResponse;
        this.section = section;
    }
}
