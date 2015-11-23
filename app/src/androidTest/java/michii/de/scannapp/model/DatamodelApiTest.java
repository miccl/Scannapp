package michii.de.scannapp.model;

import android.content.Context;
import android.support.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import michii.de.scannapp._view.activities.MainActivity;

/**
 * TODO: Add a class header comment!
 *  Testcases for {@link DatamodelApi}
 * @author Michii
 * @since 08.07.2015
 */
@RunWith(JUnit4.class)
public class DatamodelApiTest {

    @Rule
    public ActivityTestRule activityTestRule = new ActivityTestRule<>(MainActivity.class);
    private Context mContext;
    private DatamodelApi mDatamodelApi;

    @Before
    public void setup() throws Exception {
        mContext = activityTestRule.getActivity();
        mDatamodelApi = new DatamodelApi(mContext);
    }



}