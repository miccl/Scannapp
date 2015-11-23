package michii.de.scannapp._view.fragments;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import michii.de.scannapp.R;
import michii.de.scannapp.rest.utils.SeekBarPreference;

/**
 * Layout fragment to present the set of settings for the application.
 * Uses {@link PreferenceFragment} for the load of the settings.
 * @author Michii
 * @since 27.05.2015
 */
public class SettingsFragment extends PreferenceFragment  {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadGeneralSettings();
        loadScanSettings();
        loadPdfSettings();
        loadOcrSettings();
    }

    private void loadGeneralSettings() {
        addPreferencesFromResource(R.xml.pref_general);
    }


    private void loadScanSettings() {
        addPreferencesFromResource(R.xml.pref_scan);
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_key_image_quality)));
    }

    private void loadPdfSettings() {
        addPreferencesFromResource(R.xml.pref_pdf);
    }


    private void loadOcrSettings() {
        addPreferencesFromResource(R.xml.pref_ocr);
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_key_ocr_language)));
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);
            } else if (preference instanceof SeekBarPreference) {
                int intValue = Integer.valueOf(stringValue);
                preference.setSummary(intValue + "%");
            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        if(preference instanceof SeekBarPreference) {
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getInt(preference.getKey(), 90));

        } else {
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getString(preference.getKey(), ""));
        }
    }
}
