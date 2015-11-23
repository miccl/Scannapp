package michii.de.scannapp.ModelTests;

import android.content.Context;
import android.support.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import co.uk.rushorm.core.RushSearch;
import michii.de.scannapp._view.activities.MainActivity;
import michii.de.scannapp.model.DatamodelApi;
import michii.de.scannapp.model.business_logic.document.Document;
import michii.de.scannapp.rest.tests.TestUtils;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

/**
 * TODO: Add a class header comment!
 *
 * @author Michii
 * @since 16.07.2015
 */
@RunWith(JUnit4.class)
public class DocumentTest {

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
    public void testAddDocument() throws Exception {
        Document doc = mDatamodelApi.addDoc();
        assertNotNull("Document should be added", doc);
        assertNotNull("Document should be added to database", new RushSearch().whereId(doc.getId()).findSingle(Document.class));
    }

    @Test
    public void testSetDocument() throws Exception {
        Document doc = mDatamodelApi.addDoc();
        doc = mDatamodelApi.setDocument(doc, "Documentoo");
        assertEquals("Documentoo", doc.getTitle());

        Document doc2 = mDatamodelApi.addDoc();
        assertNotNull(doc2);
        doc2 = mDatamodelApi.setDocument(doc, "Documentoo");
        assertNull(doc2);
    }

    @Test
    public void testDeleteDocument() throws Exception {
        Document doc = mDatamodelApi.addDoc();
        assertNotNull("Document should be added", doc);
        assertNotNull("Document should be added to database", new RushSearch().whereId(doc.getId()).findSingle(Document.class));

        mDatamodelApi.delete(doc);
        assertNull("Document should not be in to database", new RushSearch().whereId(doc.getId()).findSingle(Document.class));

    }
}
