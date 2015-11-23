package michii.de.scannapp.model.business_logic.document;

import java.util.ArrayList;
import java.util.List;

import co.uk.rushorm.core.annotations.RushList;
import co.uk.rushorm.core.annotations.RushTableAnnotation;
import michii.de.scannapp.model.data.database.DatabaseHandler;

/**
 * Class to present different conversions of a {@link Document}.
 * The conversions getting created with {@link michii.de.scannapp.model.business_logic.conversion.ConversionStrategy}.
 * @author Michii
 * @since 03.06.2015
 */
@RushTableAnnotation
public class Conversion extends DatabaseHandler {
    /**
     * The title of the conversion.
     */
    private String title;
    /**
     * The date the conversion got created.
     */
    private long date;
    /**
     * The uriString of the file where the conversion is saved.
     */
    private String uriString;
    /**
     * Type of the conversion.
     */
    private String type;
    /**
     * Size of the conversion file specified by the uriString.
     */
    private long size;

    @RushList(classType = Attribute.class)
    private List<Attribute> attachedAttributes = new ArrayList<>();

    public Conversion() {
        /* Empty constructor required */
    }

    /**
     * Constructs a new {@link Conversion} using specified title, type, date, uriString and size.
     * @param title conversion's title
     * @param type conversion's type
     * @param date conversion's date
     * @param uriString conversion's uriString
     * @param size conversion's size
     */
    public Conversion(String title, String type, long date, String uriString, long size) {
        this.title = title;
        this.type = type;
        this.date = date;
        this.uriString = uriString;
        this.size = size;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getUriString() {
        return uriString;
    }

    public void setUriString(String uriString) {
        this.uriString = uriString;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public List<Attribute> getAttachedAttributes() {
        return attachedAttributes;
    }

    public void setAttachedAttributes(List<Attribute> attachedAttributes) {
        this.attachedAttributes = attachedAttributes;
    }

    public void addAttribute(Attribute attribute) {
        this.attachedAttributes.add(attribute);
    }

    public void removeAttribute(Attribute attribute) {
        this.attachedAttributes.remove(attribute);
    }

    @Override
    public String toString() {
        return "Conversion{" +
                "title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", date='" + date + '\'' +
                ", uriString='" + uriString + '\'' +
                '}';
    }

    /**
     * Returns whether or not the document contains the attribute with given title.
     * @param attr_title attribute's title
     * @return {@code true} if conversion contains attribute, otherwise {@code false}
     */
    public boolean containsAttribute(String attr_title) {
        for (Attribute attr : attachedAttributes) {
            if (attr.getTitle().equals(attr_title)) {
                return true;
            }
        }
        return false;
    }

    public boolean equals(Conversion conversion) {
        if (this == conversion) return true;
        if (conversion == null) return false;

//        if (getId() != null ? !getId().equals(attribute.getId()) : attribute.getId() != null) return false;
        return title.equals(conversion.type);
    }

}
