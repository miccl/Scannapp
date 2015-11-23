package michii.de.scannapp.model.data.file;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import michii.de.scannapp.R;
import michii.de.scannapp.model.data.settings.SettingsHandler;

/**
 * Class to create, update or delete files and folders on the ExternalStorage.
 * It acts as Facade while using {@link DateUtil}, {@link FileUtil} and {@link BitmapUtil}.
 * @author Michii
 * @since 31.05.2015
 */
public class FileHandler {

    private static final String PUBLIC_SAVE_DIRECTORY = Environment.getExternalStorageDirectory() + File.separator;
    public static final String EXTERNAL_APP_DIRECTORY = PUBLIC_SAVE_DIRECTORY + "Scannapp" + File.separator;
    private static final  String IMAGE_DIRECTORY = "pictures" + File.separator;
    private static final  String PDF_DIRECTORY = "pdfs" + File.separator;
    private static final String OCR_DIRECTORY = "ocr" + File.separator ;
    private static final String WORD_DIRECTORY = "word" + File.separator;
    private static final String TEMP_DIR = "temp" + File.separator;
    private final SettingsHandler mSettingsHandler;

    private Context mContext;

    /**
     * Constructs a new filehandler using the specified context.
     * @param context the executing context
     */
    public FileHandler(Context context) {
        mContext = context;
        mSettingsHandler = new SettingsHandler(context);
    }


    /**
     * Creates a new image file named with the given using the given uri.
     * A file gets created with the
     * @param source the uri of the file to copy
     * @return created file
     */
    public File createImageFile(Uri source, String title) {
        File dir = createFolder(IMAGE_DIRECTORY);
        try {
            Bitmap bitmap = BitmapUtil.getBitmap(mContext, source);
            int quality = mSettingsHandler.readFromPreferences(mContext.getString(R.string.pref_key_image_quality), 90);
//            File file = FileUtil.createTempImageFile(dir, "pic");
            File imageFile = FileUtil.createFile(dir, title + ".jpg");
            return FileUtil.createImageFile(imageFile, bitmap, quality);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Creates an pdf-file (.pdf) and the needed folder structure using {@link FileUtil}
     * @param title the file's title
     * @return the created file
     */
    public static File createPdfFile(String title) {
        File dir_pdf = createFolder(PDF_DIRECTORY);
        return FileUtil.createFile(dir_pdf, title + ".pdf");
    }

    /**
     * Creates an ocr-file (.txt) and the needed folder structure using {@link FileUtil}
     * @param title the file's title
     * @return the created file
     */
    public static File createOcrFile(String title) {
        File dir_ocr = createFolder(OCR_DIRECTORY);
        return FileUtil.createFile(dir_ocr, title + ".txt");
    }

    /**
     * Creates the needed folder structure of year/month/format.
     * The folder gets created by using {@link FileUtil}
     * @param format the file's title
     * @return the created folder
     */
    public static File createFolder(String format) {
        // get current year, month
        int year = DateUtil.getCurrentYear();
        int month = DateUtil.getCurrentMonth();

        // create folder structur
        String path = EXTERNAL_APP_DIRECTORY;
        path += year + File.separator;
        path += month + File.separator;
        path += format;
        return FileUtil.createFolder(path);
    }


    public boolean renameFile(Context context, Uri uri, String title) {
        String path = FileUtil.getRealPathFromURI(context, uri);
        return FileUtil.renameFile(path, title);
    }

    /**
     * Deletes the file specified by the given uri using the {@link FileUtil}
     * @param uri the file's uri
     * @return {@code true} if file got deleted, otherwise {@code false}
     */
    public boolean deleteFile(Uri uri) {
        String path = FileUtil.getRealPathFromURI(mContext, uri);
        return FileUtil.deleteFileNoThrow(path);
    }

    /**
     * Contructs the next document name using the {@link SettingsHandler}.
     * The name follows a defined name schemata which can be configured in the settings.
     * @return the name of next document
     */
    public String getNextDocumentName() {
        String title = "Document";
        int count = mSettingsHandler.readFromPreferences(mContext.getString(R.string.pref_key_document_count), 0);
        String result = title + " " + count;
        mSettingsHandler.saveToPreferences(mContext.getString(R.string.pref_key_document_count), count+1);
        return result;
    }

    /**
     * Retrieve the file with given uri on the ExternalStorage using {@link FileUtil}.
     * @param uri file's uri
     * @return file if it exists, otherwise {@code null}
     */
    public File getFile(Uri uri) {
        String path = FileUtil.getRealPathFromURI(mContext, uri);
        return new File(path);
    }

    /**
     * Creates a text file with the given uri and the given text.
     * @param text text to saved
     * @param uri the file's uro
     * @return the text file, if it got successfully created, otherwise {@code null}
     */
    public File writeText(String text, Uri uri) {
        File text_file = getFile(uri);
        try {
            FileWriter writer = new FileWriter(text_file);
            writer.append(text);
            writer.flush();
            writer.close();
            return text_file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Creates a temporary file with the given prefix.
     * @param prefix the prefix of file's title
     * @return file if it got sucessfully created, otherwise {@code null}
     */
    public static File createTempImageFile(String prefix) {
        File temp_dir = createFolder(TEMP_DIR);
//        File temp_dir = mContext.getFilesDir();
        String title = prefix + "_";
        return FileUtil.createTempImageFile(temp_dir, title);
    }

    /**
     * Method to invoke the system's media scanner to add your photo to the Media Provider's database, making it available in the Android Gallery application and to other apps
     * @param imageUri the image to add
     */
    private void galleryAddPic(Uri imageUri) {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        mediaScanIntent.setData(imageUri);
        mContext.sendBroadcast(mediaScanIntent);
    }

}
