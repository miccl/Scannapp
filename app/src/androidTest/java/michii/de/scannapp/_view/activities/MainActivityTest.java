package michii.de.scannapp._view.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import michii.de.scannapp.R;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * TODO: Add a class header comment!
 *
 * @author Michii
 * @since 29.06.2015
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    private static final String STRIN_TO_BE_TYPED = "Espresso";
    @Rule
    public ActivityTestRule activityTestRule = new ActivityTestRule<>(MainActivity.class);
    private Context mContext;

    @Before
    public void setup() throws Exception {
        mContext = activityTestRule.getActivity();
    }


    @Test
    public void testToolbarItem() {

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        // Click on the icon - we can find it by the r.Id.
        // Click the item.
        onView(withText("Settings"))
                .perform(click());
    }



    @Test
    public void testStartDetailActivity() {
//        Intent intent = new Intent(mContext, DetailActivity.class);
//        intent.putExtra("doc_id", "id");
//        mContext.startActivity(intent);
    }

    @Test
    public void testStartAboutActivitiy() {


    }

    @Test
    public void testStartSettingsActivitiy() {
    }

    private void goHome(Activity activity) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        activity.startActivity(intent);
    }

    @Test
    public void testRecyclerView() {
//        onData(allOf(withId(R.id.list), withParent(withId(R.id.fragment_view)))).atPosition(0).perform(click());
        onView(withId(R.id.list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

    }

    @Test
    public void testNavigationDrawer() {
//        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
//        // Click on the icon - we can find it by the r.Id.
//        // Click the item.
//        onView(withText("About"))
//                .perform(click());
//        onView(withText(R.string.drawer_item_about)).perform(click());
    }





}