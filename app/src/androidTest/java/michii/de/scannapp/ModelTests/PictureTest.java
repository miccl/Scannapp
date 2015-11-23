package michii.de.scannapp.ModelTests;

import android.content.Context;
import android.net.Uri;
import android.support.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;

import co.uk.rushorm.core.RushSearch;
import michii.de.scannapp._view.activities.MainActivity;
import michii.de.scannapp.model.DatamodelApi;
import michii.de.scannapp.model.business_logic.document.Document;
import michii.de.scannapp.model.business_logic.document.Picture;
import michii.de.scannapp.rest.tests.TestUtils;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

/**
 * TODO: Add a class header comment!
 *
 * @author Michii
 * @since 16.07.2015
 */
@RunWith(JUnit4.class)
public class PictureTest {

    @Rule
    public ActivityTestRule activityTestRule = new ActivityTestRule<>(MainActivity.class);

    private DatamodelApi mDatamodelApi;
    private Context mContext;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();


    @Before
    public void setup() throws Exception {
        mContext = activityTestRule.getActivity();
        mDatamodelApi = new DatamodelApi(activityTestRule.getActivity());

        TestUtils.cleanDatabase(mContext);
    }

    @After
    public void tearDown() throws Exception {
        TestUtils.cleanDatabase(mContext);
    }

    @Test
    public void addPictureTest() throws Exception {
        Document doc = mDatamodelApi.addDocWithoutSave();

        File src_file = TestUtils.createImageFile(mContext, folder.getRoot(), "test");
        Picture pic = mDatamodelApi.addPic(doc, Uri.fromFile(src_file), false);
//        assertNotNull(doc);
//        Picture pic = doc.getAttachedPictures().get(0);
        assertNotNull(pic);
        Uri src_uri = Uri.parse(pic.getUriString());
        assertFalse("Picture should not be in database", new RushSearch().whereId(pic.getId()).findSingle(Picture.class) != null);

        mDatamodelApi.saveWithPictures(doc);
        assertFalse(src_uri.toString().equals(pic.getUriString()));
        assertTrue("Picture should be in database", new RushSearch().whereId(pic.getId()).findSingle(Picture.class) != null);
        assertEquals(doc.getAttachedPictures().size(), 1);
        File file = mDatamodelApi.getFileFromUri(Uri.parse(pic.getUriString()));
        assertTrue("File should be created", file.exists());

        File src_file2 = TestUtils.createImageFile(mContext, folder.getRoot(), "test");

        // Add and save picture immediately
        Picture pic2 = mDatamodelApi.addPic(doc, Uri.fromFile(src_file2), true);
        assertEquals(doc.getAttachedPictures().size(), 2);

//        Picture pic2 = doc.getAttachedPictures().get(1);
        File file2 = mDatamodelApi.getFileFromUri(Uri.parse(pic2.getUriString()));
        assertTrue("Picture should be in database", pic2.isInDatabase());
        assertTrue("File2 should be created", file2.exists());

        mDatamodelApi.delete(doc);
    }

    @Test
    public void deletePictureTest() throws Exception {
        Document doc = mDatamodelApi.addDoc();

        File src_file = TestUtils.createImageFile(mContext, folder.getRoot(), "pic");
        Picture pic = mDatamodelApi.addPic(doc, Uri.fromFile(src_file), false);
//        Picture pic = doc.getAttachedPictures().get(0);
        mDatamodelApi.saveWithPictures(doc);
        File file = mDatamodelApi.getFileFromUri(Uri.parse(pic.getUriString()));

        mDatamodelApi.delete(pic);
        assertFalse("Picture should not be in database", new RushSearch().whereId(pic.getId()).findSingle(Picture.class) != null);
        assertFalse("File should be deleted", file.exists());


        // Add and save picture immediately
        File src_file2 = TestUtils.createImageFile(mContext, folder.getRoot(), "pic");
        Picture pic2 = mDatamodelApi.addPic(doc, Uri.fromFile(src_file2), true);
//        Picture pic2 = doc.getAttachedPictures().get(1);
        File file2 = mDatamodelApi.getFileFromUri(Uri.parse(pic2.getUriString()));

        mDatamodelApi.delete(pic2);
        assertFalse("Picture2 should not be in database", new RushSearch().whereId(pic2.getId()).findSingle(Picture.class) != null);
        assertFalse("File should be deleted", file2.exists());
    }

    @Test
    public void setPictureTest() throws Exception {
        Document doc = mDatamodelApi.addDoc();

        File src_file = TestUtils.createImageFile(mContext, folder.getRoot(), "pic");
        Picture pic = mDatamodelApi.addPic(doc, Uri.fromFile(src_file), true);
        doc.save();
//        Picture pic = doc.getAttachedPictures().get(0);
        // Change the name and the uri of the picture
        assertTrue(pic.isInDatabase());
        File src_file2 = TestUtils.createImageFile(mContext, folder.getRoot(), "pic2");
        pic = mDatamodelApi.setPicture(pic, "banane", Uri.parse(pic.getUriString()));
        assertNotNull("Picture should exist", pic);
        assertEquals("Picture should have changed title", pic.getTitle(), "banane");
        assertTrue("Picture should be in database", pic.isInDatabase());
        doc.getAttachedPictures().set(0, pic);
        // Change only the name
        File src_file3 = TestUtils.createImageFile(mContext, folder.getRoot(), "pic3");
//        doc = new RushSearch().whereId(doc.getId()).findSingle(Document.class);
        Picture pic2 = mDatamodelApi.addPic(doc, Uri.fromFile(src_file3), true);
//        Picture pic2 = doc.getAttachedPictures().get(1);
        assertEquals(2, doc.getAttachedPictures().size());
        assertNotNull("Picture123123d exist", pic2);
        pic2 = mDatamodelApi.setPicture(pic2, "title2", Uri.parse(pic2.getUriString()));
        assertNotNull("Picture2 should exist", pic2);
        assertEquals("title2", pic2.getTitle());
        assertTrue("Picture should be in database", pic2.isInDatabase());
        doc.getAttachedPictures().set(1, pic2);


        //Change to a already existing name
        Picture pic3 = mDatamodelApi.setPicture(pic2, pic.getTitle(),  Uri.parse(pic2.getUriString()));
        assertNull(pic3);


    }

}

