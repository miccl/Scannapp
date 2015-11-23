package michii.de.scannapp.model.business_logic.document;

import java.util.ArrayList;
import java.util.List;

import co.uk.rushorm.core.annotations.RushList;
import co.uk.rushorm.core.annotations.RushTableAnnotation;
import michii.de.scannapp.model.data.database.DatabaseHandler;

/**
 * Class to present the produced document after a scan process.
 * Therefore it can have multiple attached {@link Picture}'s.
 * Moreover it can have attached {@link Attribute}'s and {@link Conversion}'s.
 * @author Michii
 * @created 22.5.2015
 */

@RushTableAnnotation
public class Document extends DatabaseHandler {

    private String title;
    private long date;
    @RushList(classType = Picture.class)
    private List<Picture> attachedPictures = new ArrayList<>();
    @RushList(classType = Attribute.class)
    private List<Attribute> attachedAttributes = new ArrayList<>();
    @RushList(classType = Conversion.class)
    private List<Conversion> attachedConversions = new ArrayList<>();

    public Document() {
        /* Empty constructor required */
    }

    /**
     * Constructs a new {@link Document} using specified title and date.
     * @param title documents' title
     * @param date document's date
     */
    public Document(String title, long date) {
        this.title = title;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public List<Picture> getAttachedPictures() {
        return attachedPictures;
    }

    public void setAttachedPictures(List<Picture> attachedPictures) {
        this.attachedPictures = attachedPictures;
    }

    public List<Attribute> getAttachedAttributes() {
        return attachedAttributes;
    }

    public void setAttachedAttributes(List<Attribute> attachedAttributes) {
        this.attachedAttributes = attachedAttributes;
    }
    public List<Conversion> getAttachedConversions() {
        return attachedConversions;
    }

    public void setAttachedConversions(List<Conversion> attachedConversions) {
        this.attachedConversions = attachedConversions;
    }


    public void addPicture(Picture picture) {
        attachedPictures.add(picture);
    }


    public void rearrangePictures(Picture picture, int position) {
        //TODO http://stackoverflow.com/questions/4938626/moving-items-around-in-an-arraylist
        //Collections.rotate(this.attachedScans.subList(scan.getPosition(), ))
    }

    public void addAttribute(Attribute attribute) {
        attachedAttributes.add(attribute);
    }


    public void addConversion(Conversion conversion) {
        attachedConversions.add(conversion);
    }

    public void removeAttribute(Attribute attribute) {
        this.attachedAttributes.remove(attribute);
    }

    public void removeConversion(Conversion conversion) {
        this.attachedConversions.remove(conversion);
    }

    public void removePicture(Picture picture) {
        this.attachedPictures.remove(picture);
    }




    @Override
    public String toString() {
        return "Document{" +
                "title='" + title + '\'' +
                ", date=" + date +
                '}';
    }

    /**
     * Returns whether or not the document contains the attribute with given title.
     * @param attr_title attribute's title
     * @return {@code true} if document contains attribute, otherwise {@code false}
     */
    public boolean containsAttribute(String attr_title) {
        for (Attribute attr : attachedAttributes) {
            if (attr.getTitle().equals(attr_title)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return whether or not the document contains the conversion with given type.
     * @param type conversion's type
     * @return {@code true} if document contains conversion, otherweise {@code false}
     */
    public boolean containsConversion(String type) {
        for (Conversion conv : attachedConversions) {
            if (conv.getType().equals(type)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns attribute with given title.
     * {@code null} is returned, if document does not contain attribute with given title.
     * @param title attribute's title
     * @return attribute if document contains it, otherwise {@code null}.
     */
    public Attribute getAttribute(String title) {
        for (Attribute attr : attachedAttributes) {
            if (attr.getTitle().equals(title)) {
                return attr;
            }
        }
        return null;
    }

    /**
     * Returns conversion with given type.
     * {@code null} is returned, if document does not contain conversion with given type.
     * @param type conversion's type
     * @return , otherweise {@code null}
     */
    public Conversion getConversion(String type) {
        for (Conversion conv : attachedConversions) {
            if(conv.getType().equals(type)) {
                return conv;
            }
        }
        return null;
    }

    public boolean equals(Document document) {
        if (this == document) return true;
        if (document == null) return false;

//        if (getId() != null ? !getId().equals(attribute.getId()) : attribute.getId() != null) return false;
        return title.equals(document.title);
    }

}