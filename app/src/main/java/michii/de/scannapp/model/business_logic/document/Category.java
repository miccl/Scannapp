package michii.de.scannapp.model.business_logic.document;

import java.util.ArrayList;
import java.util.List;

import co.uk.rushorm.core.annotations.RushDisableAutodelete;
import co.uk.rushorm.core.annotations.RushList;
import co.uk.rushorm.core.annotations.RushTableAnnotation;
import michii.de.scannapp.model.data.database.DatabaseHandler;

/**
 *
 * @author Michii
 * @since 22.05.2015
 */
@RushTableAnnotation
public class Category extends DatabaseHandler {

    private String title;

    @RushList(classType = Document.class)
    @RushDisableAutodelete
    private List<Document> attachedDocs = new ArrayList<>();

    public Category() {
        /* Empty constructor required */
    }

    /** Constructs a new {@link Category} using the specified title
     * @param title categories title
     */
    public Category(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Document> getAttachedDocs() {
        return attachedDocs;
    }

    public void setAttachedDocs(List<Document> attachedDocs) {
        this.attachedDocs = attachedDocs;
    }

    /**
     * Adds given document to category.
     * @param document document to add
     */
    public void addDocument(Document document) {
        attachedDocs.add(document);
    }

    /**
     * Removes given document from category
     * @param document document to remove
     */
    public void removeDocument(Document document) {
//        this.attachedDocs.remove(document);
//        Ziemlich daemlicher hack um die einfach Methode category.removeDocument auszufuehren
        //Diese funktioniert aus irgendwelchen Gruenden nicht
        // Korrekter Weg mit der Definition von "equals" und "hashCode" hat nicht zum Ergebnis gefuehrt
        List<Document> new_docs = new ArrayList<>();
        for (Document curr_doc : getAttachedDocs()) {
            if (!(curr_doc.equals(document))) {
                new_docs.add(curr_doc);
            }
        }

        setAttachedDocs(new_docs);
    }

    @Override
    public String toString() {
        return "Category{" +
                "title='" + title + '\'' +
                '}';
    }

    /**
     * Return whether or not the category contains the given document
     * @param document document to test
     * @return {@code true} if category contains document, otherwise {@code false}
     */
    public boolean containsDocument(Document document) {
        List<Document> docs = getAttachedDocs();
        for (Document doc : docs) {
            if (doc.getId().equals(document.getId())) {
                return true;
            }
        }
        return false;
    }

    public boolean equals(Category category) {
        if (this == category) return true;
        if (category == null) return false;

//        if (getId() != null ? !getId().equals(category.getId()) : category.getId() != null) return false;
        return title.equals(category.title);
    }



    /**
     * Return whether or not the given category list contains the {@link Category}.
     * @param categories the category list to check
     * @return {@code true} if category list contains category, otherwise {@code false}
     */
    public boolean partOf(List<Category> categories) {
        for (Category current_cat : categories) {
            if (this.equals(current_cat)) {
                return true;
            }
        }
        return false;
    }

}
