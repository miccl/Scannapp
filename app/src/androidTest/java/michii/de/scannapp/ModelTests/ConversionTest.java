package michii.de.scannapp.ModelTests;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileNotFoundException;

import michii.de.scannapp.R;
import michii.de.scannapp._view.activities.MainActivity;
import michii.de.scannapp.model.DatamodelApi;
import michii.de.scannapp.model.business_logic.conversion.ConcreteStrategy_Ocr_Text;
import michii.de.scannapp.model.business_logic.conversion.ConcreteStrategy_Pdf;
import michii.de.scannapp.model.business_logic.conversion.ConversionStrategy;
import michii.de.scannapp.model.business_logic.document.Document;
import michii.de.scannapp.model.data.file.FileUtil;
import michii.de.scannapp.rest.tests.TestUtils;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * TODO: Add a class header comment!
 *
 * @author Michii
 * @since 12.07.2015
 */
@RunWith(AndroidJUnit4.class)
public class ConversionTest {

    @Rule
    public ActivityTestRule activityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Rule
    public TemporaryFolder  folder = new TemporaryFolder();

    private DatamodelApi mDatamodelApi;
    private Context mContext;

    @Before
    public void setup() throws Exception {
        mContext = activityTestRule.getActivity();
        mDatamodelApi = new DatamodelApi(activityTestRule.getActivity());

        TestUtils.cleanDatabase(mContext);
    }

    @Test
         public void testConvertPdf() throws Exception {
        Document doc = getDocumentWithPicture();

        ConversionStrategy strategy = new ConcreteStrategy_Pdf(mContext);
        Uri uri = mDatamodelApi.doConvert(doc, strategy);
        assertNotNull(uri);
        assertEquals(1, doc.getAttachedConversions().size());

        // if you do a second convert, the old one should be overwritten and not a new added
        Uri uri2 = mDatamodelApi.doConvert(doc, strategy);
        assertNotNull(uri);
        assertEquals(1, doc.getAttachedConversions().size());
        assertTrue(uri.equals(uri2));

    }

    @Test
    public void testConvertOcr() throws Exception {
        Document doc = getDocumentWithPicture();

        ConversionStrategy strategy = new ConcreteStrategy_Ocr_Text(mContext);
        Uri uri = mDatamodelApi.doConvert(doc, strategy);
        assertNotNull(uri);
        assertEquals(1, doc.getAttachedConversions().size());

        // if you do a second convert, the old one should be overwritten and not a new added
        Uri uri2 = mDatamodelApi.doConvert(doc, strategy);
        assertNotNull(uri2);
        assertEquals(1, doc.getAttachedConversions().size());
        assertTrue(uri.equals(uri2));
    }

    @NotNull
    private Document getDocumentWithPicture() throws FileNotFoundException {
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_launcher);
//        File folder = new File(FileHandler.EXTERNAL_APP_DIRECTORY + File.separator + "temp");
        File createdFile = FileUtil.createFile(folder.getRoot(), "myfile.jpg");
        assertTrue("File should be created", createdFile.exists());
        assertNotNull(createdFile);

        File imageFile = FileUtil.createImageFile(createdFile, bitmap, 90);
        assertTrue("File should be created", imageFile.exists());
        Log.d("TOASDASD", imageFile.getAbsolutePath());

        Document doc = mDatamodelApi.addDoc();
        mDatamodelApi.addPic(doc, Uri.fromFile(imageFile), false);
        doc.save();

        return doc;
    }

}
