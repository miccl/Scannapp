package michii.de.scannapp._view.activities;

import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import michii.de.scannapp.BuildConfig;
import michii.de.scannapp.R;

import static junit.framework.Assert.assertNotNull;

/**
 * Created by Michii on 27.06.2015.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class MainActivityTest {

    private MainActivity mActivity;
    private Toolbar toolbar;

    @Before
    public void setup() {
        mActivity = Robolectric.buildActivity(MainActivity.class).create().get();
//        toolbar = (Toolbar) mActivity.findViewById(R.id.toolbar);

    }

    @Test
    public void shouldBeNotNull() throws Exception {
        assertNotNull("MainActivity is not instantiated", mActivity);
        toolbar = (Toolbar) mActivity.findViewById(R.id.toolbar);
        assertNotNull(toolbar);
        ListView listView = (ListView) mActivity.findViewById(R.id.list);
    }
//
//    @Test
// public void navigationDrawerShouldBeCreated() throws Exception {
//    assertNotNull("Drawer is not instantiated", mActivity.mDrawer);
//}
//
    @Test
    public void documentViewShouldBeCreated() throws Exception {
//        DocumentViewFragment mDocumentViewFragment = DocumentViewFragment.newInstance(DocumentViewFragment.DATA_TYPE_RECENT, null);
////        startFragment(mDocumentViewFragment);
//        assertNotNull("DocumentViewFragment not instantiated", mDocumentViewFragment);
//
////        TwoWayView mRecyclerView = (TwoWayView) mDocumentViewFragment.getView().findViewById(R.id.list);
////        TwoWayView mRecyclerView = mDocumentViewFragment.mRecyclerView;
////        assertNotNull("RecyclerView not instantiated", mRecyclerView);
////        mRecyclerView.measure(0, 0);
////        mRecyclerView.layout(0, 0, 100, 10000);
////        RelativeLayout firstItemLayout = (RelativeLayout) mRecyclerView.getChildAt(0);
//////        shadowOf(firstItemLayout).performHapticFeedback(true);

    }
//
////    private void startFragment(DocumentViewFragment fragment) {
////        FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
////        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
////        fragmentTransaction.replace(R.id.container, fragment);
////        fragmentTransaction.addToBackStack(null);
////        fragmentTransaction.commit();
////    }
//
//    @Test
//    public void pressingButtonForAboutActivity() throws Exception {
////        String resultValue = "Testing Text";
////        // Construct Shadow versions of Activity and Intent
////        Intent startedIntent = shadowOf(mActivity).getNextStartedActivity();
////        // Verify the intent was started with correct result extra
////        assertThat(startedIntent.getComponent().getClassName(), equalTo(AboutActivity.class.getName()));
//    }
//
//
//    @Test
//    public void pressingListItemForDetailActivity() throws Exception {
////        RecyclerView recyclerView = (RecyclerView) mActivity.findViewById(R.id.fragment_view);
//    }
}
