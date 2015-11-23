package michii.de.scannapp.model.business_logic.document;


import co.uk.rushorm.core.annotations.RushTableAnnotation;
import michii.de.scannapp.model.data.database.DatabaseHandler;

/**
 * Class to present specific attributes of objects.
 * It can be attached to a {@link Document} or {@link Conversion}
 * @author Michii
 * @since 02.06.2015
 */
@RushTableAnnotation
public class Attribute extends DatabaseHandler {
    /**
     * The title of the attribute.
     * Must be unique in a document.
     */
    private String title;
    /**
     * The value of the attribute
     */
    private String value;

    public Attribute(){
        /* Empty constructor required */
    }

    /**
     * Constructs a new {@link Attribute} using the specified title and value.
     * @param title the attribute's title
     * @param value the attribute's value
     */
    public Attribute(String title, String value) {
        this.title = title;
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Attribute{" +
                "title='" + title + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    public boolean equals(Attribute attribute) {
        if (this == attribute) return true;
        if (attribute == null) return false;

//        if (getId() != null ? !getId().equals(attribute.getId()) : attribute.getId() != null) return false;
        return title.equals(attribute.title);
    }

}
