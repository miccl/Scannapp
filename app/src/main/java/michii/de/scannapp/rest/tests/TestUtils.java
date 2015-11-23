package michii.de.scannapp.rest.tests;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import co.uk.rushorm.android.RushAndroid;
import co.uk.rushorm.core.RushCore;
import michii.de.scannapp.R;
import michii.de.scannapp.model.DatamodelApi;
import michii.de.scannapp.model.business_logic.document.Document;
import michii.de.scannapp.model.business_logic.document.Picture;
import michii.de.scannapp.model.data.file.FileUtil;
import michii.de.scannapp.rest.tests.TestObjects.SetupObject;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * TODO: Add a class header comment!
 *
 * @author Michii
 * @since 16.07.2015
 */
public class TestUtils {

    public static void cleanDatabase(Context context) {
        context.deleteDatabase("Scannapp.db");
        RushAndroid.initialize(context.getApplicationContext());
        // Saving this object makes setUp wait until initialize finishes
        // otherwise it seems that the thread initialize is done on gets killed
        new SetupObject().save();
    }

    public static File createImageFile(Context context, File folder, String title) throws FileNotFoundException {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
        File createdFile =  FileUtil.createFile(folder, title + ".jpg");
        assertTrue("File should be created", createdFile.exists());
        assertNotNull(createdFile);

        File imageFile = FileUtil.createImageFile(createdFile, bitmap, 90);
        assertTrue("File should be created", imageFile.exists());
        return imageFile;
    }


    public static void deletePictures(Context context, Document doc) {
        List<Picture> pics = doc.getAttachedPictures();
        for(Picture pic : pics) {
            DatamodelApi datamodelApi = new DatamodelApi(context);
            datamodelApi.deleteFile(Uri.parse(pic.getUriString()));
        }
    }

    public static void deleteDocs() {
        RushCore.getInstance().deleteAll(Document.class);
    }

    public static Bitmap getBitmapFromAsset(AssetManager mgr, String path) {
        InputStream is = null;
        Bitmap bitmap = null;
        try {
            is = mgr.open(path);
            bitmap = BitmapFactory.decodeStream(is);
        } catch (final IOException e) {
            bitmap = null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignored) {
                }
            }
        }
        return bitmap;
    }
}
