package michii.de.scannapp.ModelTests;

import android.content.Context;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import co.uk.rushorm.core.RushSearch;
import michii.de.scannapp._view.activities.MainActivity;
import michii.de.scannapp.model.DatamodelApi;
import michii.de.scannapp.model.business_logic.document.Attribute;
import michii.de.scannapp.model.business_logic.document.Document;
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
 * @since 09.07.2015
 */
@RunWith(AndroidJUnit4.class)
public class T015_AttributeTest {

    @Rule
    public ActivityTestRule activityTestRule = new ActivityTestRule<>(MainActivity.class);

    private DatamodelApi mDatamodelApi;
    private Context mContext;


    @Before
    public void setup() throws Exception {
        mContext = activityTestRule.getActivity();
        mDatamodelApi = new DatamodelApi(activityTestRule.getActivity());

        TestUtils.cleanDatabase(mContext);
    }

    @Test
    public void testAddAttribute() throws Exception {
        Document doc = mDatamodelApi.addDoc();

        String title = "note";
        String value = "I am a note";

        // Add a new attribute
        Attribute attr  = mDatamodelApi.addAttribute(doc, title, value);
//        Attribute attr = doc.getAttribute(title);
        assertNotNull("Attribute should be added", attr);
        assertNotNull("Attribute should be added to database", new RushSearch().whereId(attr.getId()).findSingle(Attribute.class));
        assertEquals("Attribute should have given title", title, attr.getTitle());
        assertEquals("Document should have given value", value, attr.getValue());
        assertTrue("Document should contain attribute", doc.containsAttribute(attr.getTitle()));

        // Add a new attribute to no document
        Document document = null;
        Attribute attr2 = mDatamodelApi.addAttribute(document, title, value);
        assertNull("Attribute should not be added", attr2);

    }

    @Test
    public void testSetAttribute() throws Exception {
        Document doc = mDatamodelApi.addDoc();

        String title = "note";
        String value = "I am a note";

        // Add a new attribute
        Attribute attr = mDatamodelApi.addAttribute(doc, title, value);
//        Attribute attr = doc.getAttribute(title);
        // Update the value
        String value2 = "I am the second note";
        Attribute attr2 = mDatamodelApi.setAttribute(doc, title, value2);
        assertNotNull(attr2);
        assertTrue("Document should contain attribute", doc.containsAttribute(attr2.getTitle()));
        assertTrue("Category should still contain 1 partOf", doc.getAttachedAttributes().size() == 1);
        assertEquals("Document should have new given value", value2, attr2.getValue());

        //Update

    }

    @Test
    public void testDeleteAttribute() throws Exception {
        Document doc = mDatamodelApi.addDoc();

        String title = "note";
        String value = "I am a note";

        // Add a new attribute
        Attribute attr = mDatamodelApi.addAttribute(doc, title, value);
//        Attribute attr = doc.getAttribute(title);
        // Delete the attribute
        mDatamodelApi.delete(attr);
        Document new_doc = new RushSearch().whereId(doc.getId()).findSingle(Document.class);
        assertNull("Should be deleted in database", new RushSearch().whereId(attr.getId()).findSingle(Attribute.class));
        assertFalse("Document should not contain the attribute", new_doc.containsAttribute(attr.getTitle()));

    }

}