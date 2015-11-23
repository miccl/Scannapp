package michii.de.scannapp.ModelTests;

import android.content.Context;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import co.uk.rushorm.android.RushAndroid;
import co.uk.rushorm.core.RushSearch;
import michii.de.scannapp._view.activities.MainActivity;
import michii.de.scannapp.model.DatamodelApi;
import michii.de.scannapp.model.business_logic.document.Category;
import michii.de.scannapp.model.business_logic.document.Document;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

/**
 * TODO: Add a class header comment!
 *
 * @author Michii
 * @since 08.07.2015
 */
@RunWith(AndroidJUnit4.class)
public class T014_CategoryTest {

    @Rule
    public ActivityTestRule activityTestRule = new ActivityTestRule<>(MainActivity.class);

    private DatamodelApi mDatamodelApi;
    private Context mContext;


    @Before
    public void setup() throws Exception {
        mContext = activityTestRule.getActivity();
        mDatamodelApi = new DatamodelApi(activityTestRule.getActivity());

        mContext.deleteDatabase("Scannapp.db");
        RushAndroid.initialize(mContext.getApplicationContext());
    }

    @Test
    public void testAssignCategory() throws Exception {
        Document doc = mDatamodelApi.addDoc();

        Category cat = mDatamodelApi.addCategory(doc, "Receipt");
        assertNotNull("Category should be added", cat);
        assertTrue("Category should contain document", cat.containsDocument(doc));
        assertTrue("Category should contain 1 element", cat.getAttachedDocs().size() == 1);

        Category cat2 = mDatamodelApi.addCategory(null, "Receipt2");
        assertNotNull("Category should be added", cat2);
        assertFalse("Category should contain document", cat2.containsDocument(doc));
        assertTrue("Category should contain 1 element", cat2.getAttachedDocs().size() == 0);

        // Add a already added category
        Category cat3 = mDatamodelApi.addCategory(null, "Receipt");
        assertNull("Category should not be added", cat3);

        //Add a already assigned document should
        Category cat4 = mDatamodelApi.addCategory(doc, "Receipt");
        assertNull("Document should not be added", cat4);

    }

    @Test
    public void testDissociateCategory() throws Exception {
        Document doc = mDatamodelApi.addDoc();

        // Add a category to document
        Category cat = mDatamodelApi.addCategory(doc, "Receipt");
        assertTrue("Category should contain document", cat.containsDocument(doc));
        assertTrue("Category should contain 1 element", cat.getAttachedDocs().size() == 1);

        // Remove it from the document
        cat = mDatamodelApi.removeCategory(doc, cat);
        assertNotNull(cat);
        assertFalse("Category should not contain document", cat.containsDocument(doc));
        assertTrue("Category should contain 0 elements", cat.getAttachedDocs().size() == 0);

        // Remove a not assigned category
        Category cat2 = mDatamodelApi.addCategory(null, "Receipt2");
        cat = mDatamodelApi.removeCategory(doc, cat2);
        assertNull(cat);

    }

    @Test
    public void testAssignTwiceCategory() throws Exception {
        Document doc = mDatamodelApi.addDoc();

        Category cat = mDatamodelApi.addCategory(doc, "Receipt");
        assertTrue("Category should contain document", cat.containsDocument(doc));
        assertTrue("Category should contain 1 element", cat.getAttachedDocs().size() == 1);

        //assign
        mDatamodelApi.addCategory(doc, "Receipt");
        assertTrue("Category should contain document", cat.containsDocument(doc));
        assertTrue("Category should contain 1 element", cat.getAttachedDocs().size() == 1);
    }

    @Test
    public void testDeleteCategory() throws Exception {
        Document doc = mDatamodelApi.addDoc();

        Category cat = mDatamodelApi.addCategory(doc, "Receipt");
        assertTrue("Category should contain document", cat.containsDocument(doc));
        assertTrue("Category should contain 1 element", cat.getAttachedDocs().size() == 1);

        mDatamodelApi.delete(cat);
        assertNull("Category should be deleted from database", new RushSearch().whereId(cat.getId()).findSingle(Category.class));
        assertNotNull("Document should not be deleted", new RushSearch().whereId(doc.getId()).findSingle(Document.class));
    }

    @Test
    public void testSetCategory() throws Exception {
        Category cat = mDatamodelApi.addCategory(null, "Receipt");
        assertEquals("Receipt", cat.getTitle());

        cat = mDatamodelApi.setCategory(cat, "Receipt2");
        assertEquals("Receipt2", cat.getTitle());

        Category cat2 = mDatamodelApi.addCategory(null, "Kassenbon");

        Category cat3 = mDatamodelApi.setCategory(cat, "Kassenbon");
        assertNull(cat3);
    }

    @Test
    public void testRemoveCategory() throws Exception {
        Document doc = mDatamodelApi.addDoc();

        Category cat = mDatamodelApi.addCategory(doc, "Receipt");
        assertEquals(1, cat.getAttachedDocs().size());

        //Remove the category from the document
        cat = mDatamodelApi.removeCategory(doc, cat);
        assertNotNull(cat);
        assertEquals(0, cat.getAttachedDocs().size());

        //Remove the category from a not assigned document
        cat = mDatamodelApi.removeCategory(doc, cat);
        assertNull(cat);
    }

}
