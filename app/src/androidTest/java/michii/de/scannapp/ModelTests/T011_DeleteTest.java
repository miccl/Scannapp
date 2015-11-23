package michii.de.scannapp.ModelTests;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

import michii.de.scannapp.R;
import michii.de.scannapp._view.activities.MainActivity;
import michii.de.scannapp.model.DatamodelApi;
import michii.de.scannapp.model.data.file.FileUtil;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * TODO: Add a class header comment!
 *
 * @author Michii
 * @since 08.07.2015
 */
@RunWith(AndroidJUnit4.class)
public class T011_DeleteTest {

    @Rule
    public ActivityTestRule activityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    private DatamodelApi mDatamodelApi;
    private Context mContext;


    @Before
    public void setup() throws Exception {
        mContext = activityTestRule.getActivity();
        mDatamodelApi = new DatamodelApi(activityTestRule.getActivity());
    }

    /**
     * T00
     * @throws IOException
     */
    @Test
    public void testDeleteFile() throws IOException {
        long size = FileUtils.sizeOfDirectory(folder.getRoot());
        File createdFile = folder.newFile("myfile.txt");
        assertTrue(createdFile.exists());

        Uri uri = Uri.fromFile(createdFile);
        mDatamodelApi.deleteFile(uri);

        assertTrue("File should be deleted", !createdFile.exists());
        long after_size = FileUtils.sizeOfDirectory(folder.getRoot());
        assertEquals(size, after_size);
    }

    @Test
    public void testDeleteImageFile() throws IOException {
        long size = FileUtils.sizeOfDirectory(folder.getRoot());

        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_launcher);
//        File folder = new File(FileHandler.EXTERNAL_APP_DIRECTORY + File.separator + "temp");
        File createdFile =  FileUtil.createFile(folder.getRoot(), "myfile.jpg");
        assertTrue("File should be created", createdFile.exists());
        assertNotNull(createdFile);

        File imageFile = FileUtil.createImageFile(createdFile, bitmap, 90);
        assertTrue("File should be created", imageFile.exists());

        Uri uri = Uri.fromFile(createdFile);
        mDatamodelApi.deleteFile(uri);

        assertTrue("File should be deleted", !createdFile.exists());
        long after_size = FileUtils.sizeOfDirectory(folder.getRoot());
        assertEquals(size, after_size);
    }

}
