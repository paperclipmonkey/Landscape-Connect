package com.example.michaelwaterworth.landscapeconnect;

/**
 * Created by michaelwaterworth on 16/11/2015. Copyright Michael Waterworth
 */
public class SectionResponseLink {
    //Holds a link between sections and section responses
    //Not used in JSON or Db - purely for UI and listItems
    final SectionResponse sectionResponse;
    final Section section;

    public SectionResponseLink(SectionResponse sectionResponse, Section section){
        this.sectionResponse = sectionResponse;
        this.section = section;
    }
}
