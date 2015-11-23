package michii.de.scannapp.model;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import co.uk.rushorm.core.RushSearch;
import michii.de.scannapp.model.business_logic.conversion.ConversionStrategy;
import michii.de.scannapp.model.business_logic.document.Account;
import michii.de.scannapp.model.business_logic.document.Attribute;
import michii.de.scannapp.model.business_logic.document.Category;
import michii.de.scannapp.model.business_logic.document.Conversion;
import michii.de.scannapp.model.business_logic.document.Document;
import michii.de.scannapp.model.business_logic.document.Picture;
import michii.de.scannapp.model.data.database.DatabaseHandler;
import michii.de.scannapp.model.data.file.DateUtil;
import michii.de.scannapp.model.data.file.FileHandler;
import michii.de.scannapp.model.data.settings.SettingsHandler;

/**
 * Facade class to use components of the model.
 * Provides methods to create, save and manipulate the business logic classes.
 * Provides methods to interact with database, settings and externalstorage using {@link DatabaseHandler}, {@link SettingsHandler} and {@link FileHandler}
 *
 * @author Michii
 * @since 22.05.2015
 */
public class DatamodelApi {

    private static final String TAG = DatamodelApi.class.getSimpleName();

    private FileHandler mFileHandler;
    private SettingsHandler mSettingsHandler;

    /**
     * Constructs a new object using the specified context.
     * Initializes instance of {@link SettingsHandler} and {@link FileHandler}.
     *
     * @param context the executing context
     */
    public DatamodelApi(Context context) {
        mSettingsHandler = new SettingsHandler(context);
        mFileHandler = new FileHandler(context);
    }

    /**
     * Creates and saves a {@link Document}.
     * The document title is generated following the specified name scheme.
     *
     * @return created document
     */
    public Document addDoc() {
        String title = mFileHandler.getNextDocumentName();
        long date = DateUtil.getCurrentDate();
        Document doc = new Document(title, date);
        doc.save();
        return doc;
    }

    /**
     * Creates a {@link Document} without saving.
     * The document title is generated following the specified name scheme.
     *
     * @return created document
     */
    public Document addDocWithoutSave() {
        String title = mFileHandler.getNextDocumentName();
        long date = DateUtil.getCurrentDate();
        return new Document(title, date);
    }

    /**
     * Creates a {@link Picture} with given uri and adds it to the given {@link Document}.
     * If {@code save} is {@code true} the picture also gets saved.
     * Therefore the given document must be in database.
     * If save is {@code false} the picture only gets added to the given document if it is not {@code null}.
     *
     * @param document document to which the picture should be added
     * @param uri      uri of the picture
     * @param save     whether the picture gets immediately saved in the database or not
     * @return resulting document, if the picture got successfully added, otherwise {@code null}
     */
    public Picture addPic(Document document, Uri uri, Boolean save) {
        if (document == null) {
            return null;
        }

        long date = DateUtil.getCurrentDate();
        String title = "pic_" + date;
        Picture pic = new Picture(title, date, uri.toString());

        if (!save) {
            document.addPicture(pic);
            Log.i(TAG, "Added " + pic.toString());
            return pic;
        }

        //picture should be saved too
        if (!document.isInDatabase()) {
            return null;
        }
        File file = createImageFile(pic);
        if (file == null) {
            return null;
        }

        pic.setUriString(Uri.fromFile(file).toString());
        pic.setSize(file.length());
        deleteFile(uri);
        document.addPicture(pic);
        document.save();
        Log.i(TAG, "Saved" + pic.toString());
        return pic;

    }

    /**
     * Creates and saves a {@link Attribute} with given title and value and adds it to the given document.
     * {@code null} is returned if given document is not in database or a attribute with the given
     * title is already attached to the document.
     *
     * @param document the document to which the attribute should be added
     * @param title    the attribut's title
     * @param value    the attribute's value
     * @return resulting document, if the attribute got successfully added, otherwise {@code null}
     */
    public Attribute addAttribute(Document document, String title, String value) {
        if (document == null || !document.isInDatabase()) {
            return null;
        }

        if (document.containsAttribute(title)) {
            return null;
        }

        Attribute new_attribute = new Attribute(title, value);
        document.addAttribute(new_attribute);
        document.save();
        Log.i(TAG, "Added " + new_attribute.toString());
        return new_attribute;

    }

    /**
     * Creates and saves a {@link Attribute} with given title and value and adds it to the given conversion.
     * {@code null} is returned if given document is not in database or a attribute with the given
     * title is already attached to the document.
     *
     * @param conversion the conversion to which the attribute should be added
     * @param title      the attribut's title
     * @param value      the attribute's value
     * @return resulting document, if the attribute got successfully added, otherwise {@code null}
     */
    public Attribute addAttribute(Conversion conversion, String title, String value) {
        if (conversion == null || !conversion.isInDatabase()) {
            return null;
        }

        if (conversion.containsAttribute(title)) {
            return null;
        }

        Attribute new_attribute = new Attribute(title, value);
        conversion.addAttribute(new_attribute);
        conversion.save();
        Log.i(TAG, "Added " + new_attribute.toString());
        return new_attribute;
    }


    /**
     * Adds the given {@link Document} to the {@link Category} with the given title.
     * The given document must be in database.
     * If the {@link Category} with the title doesn't exists in database, it gets created.
     *
     * @param document the document which should be added
     * @param title    the title of the category which the document should be added to
     * @return category if the document got successfully added, otherwise {@code null}
     */
    public Category addCategory(Document document, String title) {
        Category category = new RushSearch().whereEqual("title", title).findSingle(Category.class);
        if (category == null) {
            category = new Category(title);
            if (document != null && document.isInDatabase()) {
                category.addDocument(document);
                Log.i(TAG, "Added " + document.toString() + " to " + category.toString());
            }
            category.save();
            Log.i(TAG, "Added " + category.toString());
            return category;
        } else if (document != null && document.isInDatabase()) {
            if (!category.containsDocument(document)) {
                category.addDocument(document);
                category.save();
                Log.i(TAG, "Added " + document.toString() + " to " + category.toString());
                return category;
            }
        }
        return null;
    }

    /**
     * Updates a {@link Account} with given name and email.
     * The name must be unique in the database and the email must not be null.
     *
     * @param name  the account's name
     * @param email the account's email
     * @return the created account if the name was not already in database and the email is not null, otherwise {@code null}
     */
    public Account addAccount(String name, String email) {
        if (new RushSearch().whereEqual("name", name).findSingle(Account.class) != null) {
            return null;
        }
        if (email == null) {
            return null;
        }

        Account account = new Account(name, email);
        account.save();
        Log.i(TAG, "Added " + account.toString());
        return account;
    }

    /**
     * Disassociate given {@link Document} from given {@link Category}
     * Returns {@code null} if the given document and category are not in database or the category does not contain the document.
     *
     * @param document the document to remove
     * @param category the category from which the document should be removed
     * @return changed category or {@code null}
     */
    public Category removeCategory(Document document, Category category) {
        if (document == null || !document.isInDatabase()) {
            return null;
        }

        if (category == null || !category.isInDatabase()) {
            return null;
        }

        if (!category.containsDocument(document)) {
            return null;
        }

        category.removeDocument(document);
        category.save();
        Log.i(TAG, "Removed " + document.toString() + " from " + category.toString());
        return category;
    }


    /**
     * Updates the title of the given {@link Document} to the given value.
     * {@code null} is returned if given document is not in database.
     *
     * @param document document to change
     * @param title    wanted title
     * @return changed document
     */
    public Document setDocument(Document document, String title) {
        if (document == null || !document.isInDatabase()) {
            return null;
        }

        Document doc = new RushSearch().whereId(document.getId()).findSingle(Document.class);
        if (new RushSearch().whereEqual("title", title).findSingle(Document.class) != null) { //unique title
            return null;
        }

        doc.setTitle(title);
        doc.save();
        Log.i(TAG, "Set " + doc.toString());
        return doc;
    }

    /**
     * Updates the title and uri the given {@link Picture} to the given values.
     * The given picture must be in database and the given title shouldn't be used before, otherwise {@code null} is returned.
     *
     * @param picture picture to change
     * @param title   the picture's title
     * @param uri     the picture's uri
     */
    public Picture setPicture(Picture picture, String title, Uri uri) {
        if (picture == null || !picture.isInDatabase()) {
            return null;
        }

        Picture pic = new RushSearch().whereId(picture.getId()).findSingle(Picture.class);

        if (title != null && !pic.getTitle().equals(title)) {
            if (new RushSearch().whereEqual("title", title).findSingle(Picture.class) != null) {
                return null;
            }
            pic.setTitle(title);
        }

        if (uri != null && !pic.getUriString().equals(uri.toString())) {
            pic.setUriString(uri.toString());
            File file = createImageFile(pic);
            if (file != null) {
                pic.setUriString(Uri.fromFile(file).toString());
                pic.setSize(file.length());
            } else {
                return null;
            }
        }

        Log.i(TAG, "Set " + pic.toString());
        pic.save();

        return pic;
    }


    /**
     * Updates the title of the given {@link Category} to given value.
     * {@code null} is returned if given category is not in database or the name is already taken.
     *
     * @param category category to change
     * @param title    wanted title
     * @return category if given category is not in database and the name is already taken, otherwise {@code null}
     */
    public Category setCategory(Category category, String title) {
        if (category == null || !category.isInDatabase()) {
            return null;
        }

        if (new RushSearch().whereEqual("title", title).findSingle(Category.class) != null) { //not unique title
            return null;
        }

        Category cat = new RushSearch().whereId(category.getId()).findSingle(Category.class);
        cat.setTitle(title);

        Log.i(TAG, "Set " + cat.toString());
        cat.save();
        return cat;

    }


    /**
     * Updates the value of the given {@link Attribute} from the given document to values.
     * The document must be in database and contain the attribute with the given title.
     * The attribute must be in database and the given value is not null.
     *
     * @param document the document to which the attribute should be attached
     * @param title    the attribute's title
     * @param value    the wanted value
     * @return changed attribute if given document and given attribute is in database and the document does contain the attribute, otherwise {@code null}
     */
    public Attribute setAttribute(Document document, String title, String value) {
        if (document == null || !document.isInDatabase()) {
            return null;
        }
        Attribute attr = document.getAttribute(title);
        if (attr == null || !attr.isInDatabase()) {
            return null;
        }

        if (value == null) {
            return null;
        }

        attr.setValue(value);

        attr.save();
        Log.i(TAG, "Set " + attr.toString());
        return attr;

    }

    /**
     * Updates attributes of {@link Account} with given values.
     * {@code null} is returned if given account is not in database.
     *
     * @param account   account to change
     * @param name      wanted name
     * @param email     wanted email
     * @param cloud     wanted cloud
     * @param signature wanted signature
     * @return changed account if given account is in database, otherwise {@code null}
     */
    public Account setAccount(Account account, String name, String email, String cloud, String signature) {

        if (account == null || !account.isInDatabase()) {
            return null;
        }

        Account acc = new RushSearch().whereId(account.getId()).findSingle(Account.class);

        if (name != null && !acc.getName().equals(name)) {
            if (new RushSearch().whereEqual("name", name).findSingle(Account.class) != null) {
                return null;
            }
            acc.setName(name);
        }


        if (email == null) {
            return null;
        }

        acc.setEmail(email);
        acc.setCloud(cloud);
        acc.setSignatur(signature);

        acc.save();
        Log.i(TAG, "Set " + acc.toString());
        return acc;

    }

    /**
     * Converts {@link Document} with the given {@link ConversionStrategy}.
     * {@code null} is returned if given document is not in database or the convertion doesn't work.
     *
     * @param document           document to convert
     * @param conversionStrategy wanted concrete ConversionStrategy
     * @return uri of the conversion
     */
    public Uri doConvert(Document document, ConversionStrategy conversionStrategy) {
        if (document == null || !document.isInDatabase()) {
            return null;
        }
        Conversion conv = conversionStrategy.convert(document);

        if (conv == null) {
            return null;
        }

        if (!conv.isInDatabase()) { //conversion wasnt in database before
            document.addConversion(conv);
            document.save();
            Log.i(TAG, "Added " + conv.toString());
        } else {
            conv.save();
            Log.i(TAG, "Updated " + conv.toString());
        }

        return Uri.parse(conv.getUriString());

    }

    /**
     * Retrieve all saved {@link Document}
     *
     * @return all saved documents
     */
    public List<Document> getDocs() {

        return new RushSearch().orderAsc("date").find(Document.class);
    }

    /**
     * Counts all saved {@link Document}
     *
     * @return count of all saved documents
     */
    public long countDocs() {
        return new RushSearch().count(Document.class);
    }

    /**
     * Retrieve all saved {@link Document}s from {@link Category} with given title
     *
     * @param title title of category
     * @return all saved documents from given category
     */
    public List<Document> getDocsByCategory(String title) {
        Category cat = new RushSearch().whereEqual("title", title).findSingle(Category.class);
        return cat.getAttachedDocs();
    }

    /**
     * Retrieve all saved {@link Document}s which got created in given year.
     *
     * @param year the year of creation
     * @return all documents which got created in given year
     */
    public List<Document> getDocsByYear(int year) {
        Date start_date = DateUtil.getStartDate(year);
        Date end_date = DateUtil.getEndDate(year);
        return new RushSearch().whereAfter("date", start_date).and().whereBefore("date", end_date).orderAsc("date").find(Document.class);

    }

    /**
     * Retrieve the number of all saved {@link Document}s which got created in given year.
     *
     * @param year the year of creation
     * @return number of all saved {@link Document}s which got created in given year
     */
    public long countDocsByYear(int year) {
        Date start_date = DateUtil.getStartDate(year);
        Date end_date = DateUtil.getEndDate(year);
        return new RushSearch().whereAfter("date", start_date).and().whereBefore("date", end_date).orderAsc("date").count(Document.class);
    }


    public List<Document> getDocsByMonth(int year, int month) {
        Date start_date = DateUtil.getStartDate(year, month);
        Date end_date = DateUtil.getEndDate(year, month);
        return new RushSearch().whereAfter("date", start_date).and().whereBefore("date", end_date).orderAsc("date").find(Document.class);
    }

    public long countDocsByMonth(int year, int month) {
        Date start_date = DateUtil.getStartDate(year, month);
        Date end_date = DateUtil.getEndDate(year, month);
        return new RushSearch().whereAfter("date", start_date).and().whereBefore("date", end_date).orderAsc("date").count(Document.class);

    }

    /**
     * Retrieve all saved documents which got created in the last 30 days.
     *
     * @return documents of last 30 days
     */
    public List<Document> getDocsByRecent() {
        Date start_date = DateUtil.daysAgo(30);
        Date end_date = new Date();
        return new RushSearch().whereAfter("date", start_date).and().whereBefore("date", end_date).orderAsc("date").find(Document.class);
    }

    public long countDocsByRecent() {
        Date start_date = DateUtil.daysAgo(30);
        Date end_date = new Date();
        return new RushSearch().whereAfter("date", start_date).and().whereBefore("date", end_date).orderAsc("date").count(Document.class);
    }

    public List<Category> getCategories() {
        return new RushSearch().orderAsc("title").find(Category.class);
    }

    public long countCategories() {
        return new RushSearch().orderAsc("title").count(Category.class);
    }

    /**
     * Retrieve all assigned categories of the given document.
     *
     * @param document the document
     * @return all assigned categories of
     */
    public List<Category> getCategoriesByDocument(Document document) {
        if (document == null || !document.isInDatabase()) {
            return null;
        }

        List<Category> categories = new RushSearch().orderAsc("title").find(Category.class);
        List<Category> result = new ArrayList<>();
        for (Category cat : categories) {
            if (cat.containsDocument(document))
                result.add(cat);
        }
        return result;
    }

    public List<Account> getAccounts() {
        return new RushSearch().find(Account.class);
    }


    public boolean delete(Document document) {
        if (document == null || !document.isInDatabase()) {
            return false;
        }

        for (Picture pic : document.getAttachedPictures()) {
            deleteFile(Uri.parse(pic.getUriString()));
        }
        for (Conversion conv : document.getAttachedConversions()) {
            deleteFile(Uri.parse(conv.getUriString()));
        }

        document.delete();
        Log.i(TAG, "delete " + document.toString());
        return true;
    }

    public boolean delete(Picture picture) {
        if (picture == null || !picture.isInDatabase()) {
            return false;
        }

        deleteFile(Uri.parse(picture.getUriString()));
        picture.delete();
        Log.i(TAG, "delete " + picture.toString());
        return true;
    }

    public boolean delete(Conversion conversion) {
        if (conversion == null || !conversion.isInDatabase()) {
            return false;
        }

        conversion.delete();
        Log.i(TAG, "delete " + conversion.toString());
        return true;
    }


    public boolean delete(Attribute attribute) {
        if (attribute == null || !attribute.isInDatabase()) {
            return false;
        }

        attribute.delete();
        Log.i(TAG, "delete " + attribute.toString());
        return true;
    }


    public boolean delete(Category category) {
        if (category == null || !category.isInDatabase()) {
            return false;
        }

        category.delete();
        Log.i(TAG, "delete " + category.toString());
        return true;
    }

    public boolean delete(Account account) {
        if (account == null || !account.isInDatabase()) {
            return false;
        }

        account.delete();
        Log.i(TAG, "delete " + account.toString());
        return true;

    }


    /**
     * Save {@link Document} with its assigned {@link Picture}s.
     * For every picture, that is not already in database, a new image file gets created.
     * Returns {@code null}, if a imagefile couldnt be created.
     *
     * @param document the document to save
     * @return document, if no file creation error occurs, otherwise {@code null}
     */
    public Document saveWithPictures(Document document) {
        for (Picture pic : document.getAttachedPictures()) {
            if (pic.getId() == null) { //not in database and therefore no file created
                final Uri src_uri = Uri.parse(pic.getUriString());
                File file = createImageFile(pic);
                if (file != null) {
                    pic.setUriString(Uri.fromFile(file).toString());
                    pic.setSize(file.length());
                    deleteFile(src_uri);
                } else {
                    return null;
                }
            }
        }
        document.save();
        return document;
//        Log.i(TAG, document.toString() + " saved");
    }

    /**
     * Creates a new image file based on the uri of the given picture using the {@link FileHandler}.
     *
     * @param picture the picture
     * @return the created image file
     */
    private File createImageFile(Picture picture) {
        Uri uri = Uri.parse(picture.getUriString());
        File file = mFileHandler.createImageFile(uri, picture.getTitle());
        if (file != null) {
            Log.i(TAG, "Create image file" + file.getAbsolutePath());
            return file;
        }
        return null;
    }

    /**
     * Creates a empty tempImageFile with the given prefix using the {@link FileHandler}.
     *
     * @param prefix the prefix of the created file
     * @return created temp file
     */
    public File createTempImageFile(String prefix) {
        File file = FileHandler.createTempImageFile(prefix);
        Log.i(TAG, "Created Temp File " + file.getAbsolutePath());
        return file;
    }

    /**
     * Method to save the given string value with the given name to settings using {@link SettingsHandler}.
     *
     * @param preferenceName  the name to save the value in settings
     * @param preferenceValue the value to save
     */
    public void saveToPreferences(String preferenceName, String preferenceValue) {
        mSettingsHandler.saveToPreferences(preferenceName, preferenceValue);
    }

    /**
     * Method to save the given integer value with the given name to settings using {@link SettingsHandler}.
     *
     * @param preferenceName  the name to save the value in settings
     * @param preferenceValue the value to save
     */
    public void saveToPreferences(String preferenceName, int preferenceValue) {
        mSettingsHandler.saveToPreferences(preferenceName, preferenceValue);
    }


    /**
     * Method to read the string value from the given name from settings using the {@link SettingsHandler}.
     * Returns the given default value, if nothing is stored under the given name.
     *
     * @param preferenceName the setting's name
     * @param defaultValue the setting's default value
     * @return setting's value, if stored, otherwise default value
     */
    public String readFromPreferences(String preferenceName, String defaultValue) {
        return mSettingsHandler.readFromPreferences(preferenceName, defaultValue);
    }

    /**
     * Method to read integer value from the given name from settings using the {@link SettingsHandler}.
     * Returns the given default value, if nothing is stored under the given name.
     *
     * @param preferenceName the setting's name
     * @param defaultValue the setting's default value
     * @return setting's value, if stored, otherwise default value
     */
    public int readFromPreferences(String preferenceName, int defaultValue) {
        return mSettingsHandler.readFromPreferences(preferenceName, defaultValue);
    }


    /**
     * Sets the default values of the settings
     */
    public void setDefaultValues() {
        mSettingsHandler.setDefaultValues();
    }


    public Document getDocumentById(String doc_id) {
        return new RushSearch().whereId(doc_id).findSingle(Document.class);
    }

    /**
     * Retrieve all saved document's title which are like the given title.
     * If a document's title starts, ends or contains the given title, it gets added to the list.
     *
     * @param title the title to search
     * @return the list of found documents
     */
    public List<Document> searchDocument(String title) {
        Log.i(TAG, "Searched " + title);
        return new RushSearch()
                .whereLike("title", title).or()
                .whereEndsWith("title", title).or()
                .whereStartsWith("title", title).or()
                .whereContains("title", title)
                .find(Document.class);
    }

    /**
     * Deletes the file with the given uri using the {@link FileHandler}.
     *
     * @param uri the file's uri, which should be deleted
     * @return {@code true} if the file with given uri got deleted, otherwise {@code false}
     */
    public boolean deleteFile(Uri uri) {
        boolean result = mFileHandler.deleteFile(uri);
        if (result) {
            Log.i(TAG, "Deleted file " + uri.toString());
        } else {
            Log.i(TAG, "Error by deleting " + uri.toString());
        }
        return result;
    }

    /**
     * Retrieve the file with the given uri using the {@link FileHandler}.
     *
     * @param uri the file's uri, which is searched
     * @return the file
     */
    public File getFileFromUri(Uri uri) {
        return mFileHandler.getFile(uri);
    }

    /**
     * Retrieve a saved account with the given name.
     *
     * @param name the account's name
     * @return account if it was saved, otherwise {@code null}
     */
    public Account getAccountByName(String name) {
        return new RushSearch().whereEqual("name", name).findSingle(Account.class);
    }

}
