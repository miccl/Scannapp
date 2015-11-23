package michii.de.scannapp._view.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import michii.de.scannapp.R;
import michii.de.scannapp._view.fragments.AboutViewFragment;
import michii.de.scannapp._view.fragments.ContactViewFragment;
import michii.de.scannapp._view.fragments.ContributeViewFragment;
import michii.de.scannapp._view.fragments.HelpViewFragment;
import michii.de.scannapp._view.fragments.LicenseViewFragment;
import michii.de.scannapp.rest.utils.MaterialUtil;

/**
 * Activity to show more information about the application.
 */
public class AboutActivity extends BusActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        MaterialUtil.setupToolbar(this, "");
        setupAboutView();
    }

    /**
     * Setups the view by initiating a new {@link AboutViewFragment} instance.
     */
    private void setupAboutView() {
        AboutViewFragment aboutViewFragment = AboutViewFragment.newInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, aboutViewFragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("unused")
    public void onEvent(AboutViewFragment.AboutViewFragmentEvent event) {
        Fragment fragment = null;
        switch (event.id) {
            case AboutViewFragment.TAG_CONTACT:
                fragment = ContactViewFragment.newInstance();
                break;
            case AboutViewFragment.TAG_CONTRIBUTE:
                fragment = ContributeViewFragment.newInstance();
                break;
            case AboutViewFragment.TAG_HELP:
                fragment = HelpViewFragment.newInstance();
                break;
            case AboutViewFragment.TAG_LICENSE:
                fragment = LicenseViewFragment.newInstance();
                break;
        }
        startFragment(fragment);
    }

    /**
     * Starts the layout fragments.
     * Possible fragments are {@link AboutViewFragment}, {@link ContactViewFragment}, {@link ContributeViewFragment}, {@link HelpViewFragment} and {@link LicenseViewFragment}.
     * @param fragment instance of the fragment
     */
    private void startFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
