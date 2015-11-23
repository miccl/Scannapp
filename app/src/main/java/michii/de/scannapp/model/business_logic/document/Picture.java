package michii.de.scannapp.model.business_logic.document;

import co.uk.rushorm.core.annotations.RushTableAnnotation;
import michii.de.scannapp.model.data.database.DatabaseHandler;

/**
 * Business Class to present a image produced in the scan process.
 * The produced image file is specified by its uri.
 * @author Michii
 * @since 23.05.2015
 */
@RushTableAnnotation
public class Picture extends DatabaseHandler {

    /**
     * the title of the picture
     */
    private String title;
    /**
     * the creation date
     */
    private long date;
    /**
     * the uri of the image file.
     */
    private String uriString;
    /**
     * the size of the image file.
     */
    private long size;

    public Picture() {
        /* Empty constructor required */
    }

    /**
     * Constructs a new {@link Picture} using the specified title, date and uriString.
     * @param title picture's title
     * @param date picture's date
     * @param uriString picture's uriString
     */
    public Picture(String title, long date, String uriString) {
        this.title = title;
        this.date = date;
        this.uriString = uriString;
        this.size = 0;
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

    @Override
    public String toString() {
        return "Picture{" +
                "title='" + title + '\'' +
                ", uriString='" + uriString + '\'' +
                ", date=" + date +
                ", size=" + size +
                '}';
    }

    public boolean equals(Picture picture) {
        if (this == picture) return true;
        if (picture == null) return false;

//        if (getId() != null ? !getId().equals(attribute.getId()) : attribute.getId() != null) return false;
        return title.equals(picture.title);
    }



}