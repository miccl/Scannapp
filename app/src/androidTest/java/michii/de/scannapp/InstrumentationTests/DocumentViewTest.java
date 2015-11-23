package michii.de.scannapp.InstrumentationTests;

import android.content.Context;
import android.support.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import michii.de.scannapp._view.activities.MainActivity;
import michii.de.scannapp.model.DatamodelApi;
import michii.de.scannapp.rest.tests.TestUtils;

/**
 * TODO: Add a class header comment!
 *
 * @author Michii
 * @since 23.07.2015
 */
@RunWith(JUnit4.class)
public class DocumentViewTest {

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


}
