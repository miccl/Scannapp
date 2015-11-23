package michii.de.scannapp._view.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import michii.de.scannapp.R;
import michii.de.scannapp._view.fragments.SettingsFragment;
import michii.de.scannapp.rest.utils.MaterialUtil;

/**
 * A Activity that presents a set of application settings.
 * Using {@link SettingsFragment} to provide the single setting layouts.
 */
public class SettingsActivity extends AppCompatActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        MaterialUtil.setupToolbar(this, "Settings");
        startSettingFragment();
    }

    /**
     * Initiating the layout with a new {@link SettingsFragment} instance.
     */
    private void startSettingFragment() {
        SettingsFragment settingsFragment = new SettingsFragment();
        getFragmentManager().beginTransaction()
                .replace(R.id.container, settingsFragment)
                .commit();
    }


}
