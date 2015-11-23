package michii.de.scannapp.ModelTests;

import android.content.Context;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import michii.de.scannapp._view.activities.MainActivity;
import michii.de.scannapp.model.DatamodelApi;
import michii.de.scannapp.model.business_logic.document.Document;
import michii.de.scannapp.rest.tests.TestUtils;

import static junit.framework.Assert.assertEquals;

/**
 * TODO: Add a class header comment!
 *
 * @author Michii
 * @since 22.07.2015
 */
@RunWith(AndroidJUnit4.class)
public class SearchTest {

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
    public void testSearch() {

        mDatamodelApi.setDocument(mDatamodelApi.addDoc(), "Test");
        mDatamodelApi.setDocument(mDatamodelApi.addDoc(), "Fest");

        List<Document> docs = mDatamodelApi.searchDocument("Test");
        assertEquals(docs.size(), 1);

        List<Document> docs2 =mDatamodelApi.searchDocument("est");
        assertEquals(docs2.size(), 2);
    }
}
