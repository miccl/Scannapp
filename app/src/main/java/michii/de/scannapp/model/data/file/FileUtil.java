package michii.de.scannapp.model.data.file;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;

/**
 * Class to create, update or delete files and folders on the ExternalStorage Using {@link File}.
 *
 * @author Michii
 * @since 07.06.2015
 */
public class FileUtil {
    private static final String TAG = FileUtil.class.getSimpleName();

    /**
     * Creates a folder on ExternalStorage under the given path.
     * If the specified folder already exists, the existing folder is returned.
     * @param path the path of the folder
     * @return the folder if it already exist or got sucessfully created, otherwise {@code null}
     */
    public static File createFolder(String path) {
        File subdirect = new File(path);

        if (subdirect.exists() && subdirect.isDirectory()) {
            return subdirect;
        }

        if (subdirect.mkdirs()) {
            Log.d(TAG, "Directory '" + subdirect.getAbsolutePath() + "' got created");
            return subdirect;
        } else {
            Log.d(TAG, "Error by creating" + subdirect.getAbsolutePath() + "/");
            return null;
        }

    }

    /**
     * Remove the extension from the given string
     * @param s the string
     * @return the string with removed extension
     */
    public static String removeExtension(String s) {

        String separator = System.getProperty("file.separator");
        String filename;

        // Remove the path upto the filename.
        int lastSeparatorIndex = s.lastIndexOf(separator);
        if (lastSeparatorIndex == -1) {
            filename = s;
        } else {
            filename = s.substring(lastSeparatorIndex + 1);
        }

        // Remove the extension.
        int extensionIndex = filename.lastIndexOf(".");
        if (extensionIndex == -1)
            return filename;

        return filename.substring(0, extensionIndex);
    }

    /**
     * Retrieves the extension of the given file name
     * @param fileName the file's name
     * @return the extension
     */
    public static String getExtension(String fileName) {

//        String extension = "";
//
//        int i = fileName.lastIndexOf('.');
//        if (i > 0) {
//            extension = fileName.substring(i+1);
//        }
        return FilenameUtils.getExtension(fileName);
    }

    /**
     * Retrieves the real path from the given Uri.
     * See http://afterthoughtsoftware.com/posts/Storing-image-URIs-using-Android-SQLite
     * and http://stackoverflow.com/questions/2789276/android-get-real-path-by-uri-getpath
     *
     * @param contentUri the uri
     * @return the path
     */
    public static String getRealPathFromURI(Context context, Uri contentUri) {
        if (contentUri != null) {
            String result;
            Cursor cursor = context.getContentResolver().query(contentUri, null, null, null, null);
            if (cursor == null) {
                return contentUri.getPath();
            } else {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                result = cursor.getString(idx);
                cursor.close();
            }
            return result;
        }
        return null;
    }

    /**
     * Creating a new image file using the specified file, bitmap and quality.
     * The Bitmap is compressed the quality.
     * @param file the file where to store the image
     * @param bitmap the bitmap of the image
     * @param quality the save quality of the image
     * @return the image file, if it got sucessfully created, otherwise {@code null}
     * @throws FileNotFoundException
     */
    public static File createImageFile(File file, Bitmap bitmap, int quality) {

        OutputStream outFile = null;
        try {
            outFile = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outFile);
            outFile.flush();
            outFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

    /**
     * Create temporary image file using specified storage directory and image name
     * @param storageDir the file's directory
     * @param imageFileName the file's name
     * @return file if it got successfully created, otherwise {@code null}
     */
    public static File createTempImageFile(File storageDir, String imageFileName) {
        File image;
        try {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
            return image;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Creates a new file under the given directory and with the given title.
     * @param dir the directory to save
     * @param title the file's title
     * @return file
     */
    public static File createFile(File dir, String title) {
        File file = new File(dir, title);
        if (!file.exists()) {
            try {
                file.createNewFile() ;
                Log.d("TEST", "create new File wurde gemacht");
            } catch (IOException e) {
                Log.d("TEST", "create new File errooor");
            }
        }
        return file;
    }

    /**
     * Delete a file without throwing any exception
     *
     * @param path the file's path
     * @return {@code true} if file got sucessfully delete, otherwise {@code false}
     */
    public static boolean deleteFileNoThrow(String path) {
        File file;
        try {
            file = new File(path);
        } catch (NullPointerException e) {
            return false;
        }

        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * Formats the given size in byte to x.xx format in KB, MB or GB.
     * @param byte_size the size in byte
     * @return formatted size
     */
    public static String formatBytes(long byte_size) {
        String hrSize;

        double k = byte_size / 1024.0;
        double m = byte_size / 1048576.0;
        double g = byte_size / 1073741824.0;

        DecimalFormat dec = new DecimalFormat("0.00");

        if (g > 1) {
            hrSize = dec.format(g).concat(" GB");
        } else if (m > 1) {
            hrSize = dec.format(m).concat(" MB");
        } else if (k > 1) {
            hrSize = dec.format(k).concat(" KB");
        } else {
            hrSize = dec.format(byte_size).concat(" Byte");
        }
        return hrSize;
    }

    /**
     * Renames title of the file with the given path to the given title.
     * @param path the path of the old file
     * @param title the title to change to
     * @return {@code true} if file got successfully renamed, otherwise {@code false}.
     */
    public static boolean renameFile(String path, String title) {
        File oldFile;
        try {
            oldFile = new File(path);
        } catch (NullPointerException e) {
            return false;
        }

        if (oldFile.exists()) {
            String parent_path = oldFile.getParent();
            String prefix = getExtension(path);
            String newTitle = title + "." + prefix;
            File newFile;
            try {
                newFile = new File(parent_path, newTitle);
            } catch (NullPointerException e) {
                return false;
            }
            if (newFile.exists()) {
                return oldFile.renameTo(newFile);

            }

        }
        return false;
    }
}
