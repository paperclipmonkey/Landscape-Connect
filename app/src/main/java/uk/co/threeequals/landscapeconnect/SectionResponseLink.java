package uk.co.threeequals.landscapeconnect;

/**
 * Holds a link between sections and section responses
 * Not used in JSON or Db - purely for UI and listItems
 * Created by michaelwaterworth on 16/11/2015. Copyright Michael Waterworth
 */
public class SectionResponseLink {

    final SectionResponse sectionResponse;
    final Section section;

    public SectionResponseLink(SectionResponse sectionResponse, Section section) {
        this.sectionResponse = sectionResponse;
        this.section = section;
    }
}
