package michii.de.scannapp.model.data.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import michii.de.scannapp.R;

/**
 * Class to provide several methods to work with the settings.
 * The information is saved in {@link SharedPreferences}.
 */
public class SettingsHandler {

    private final SharedPreferences mSharedPreference;
    private Context mContext;

    /**
     * Constructs a new settings handler using the given context.
     * {@link SharedPreferences} is initialized with {@link PreferenceManager#getDefaultSharedPreferences(Context)}.
     * @param context the execution mContext
     */
    public SettingsHandler(Context context) {
        mContext = context;
        mSharedPreference = PreferenceManager.getDefaultSharedPreferences(context);
    }

//    public String PREF_KEY_EMAIL = mContext.getResources().getString(R.string.pref_key_email);
//    public String PREF_KEY_NAME = mContext.getResources().getString(R.string.pref_key_name);


    /**
     * Sets the default values from the settings.
     */
    public void setDefaultValues() {
        PreferenceManager.setDefaultValues(mContext, R.xml.pref_general, false);
        PreferenceManager.setDefaultValues(mContext, R.xml.pref_scan, false);
        PreferenceManager.setDefaultValues(mContext, R.xml.pref_ocr, false);
        PreferenceManager.setDefaultValues(mContext, R.xml.pref_pdf, false);

    }

    /**
     * Saves the given string value under the given name in settings.
     * @param preferenceName the setting's name
     * @param preferenceValue the setting's value
     */
    public void saveToPreferences(String preferenceName, String preferenceValue) {
        SharedPreferences.Editor editor = mSharedPreference.edit();
        editor.putString(preferenceName, preferenceValue);
        editor.apply();
    }

    /**
     * Reads the string value of the setting with the given name.
     * Returns the given default value, if there is no setting with the given name
     * @param preferenceName the setting's name
     * @param defaultValue the setting's default value
     * @return setting's value, if stored, otherwise default value
     */
    public String readFromPreferences(String preferenceName, String defaultValue) {
        return mSharedPreference.getString(preferenceName, defaultValue);
    }

    /**
     * Saves the given integer value under the given name in settings.
     * @param preferenceName the setting's name
     * @param preferenceValue the setting's value
     */
    public void saveToPreferences(String preferenceName, int preferenceValue) {
        SharedPreferences.Editor editor = mSharedPreference.edit();
        editor.putInt(preferenceName, preferenceValue);
        editor.apply();
    }

    /**
     * Reads the integer value of the setting with the given name.
     * Returns the given default value, if there is no setting with the given name
     * @param preferenceName the setting's name
     * @param defaultValue the setting's default value
     * @return setting's value, if stored, otherwise default value
     */
    public int readFromPreferences(String preferenceName, int defaultValue) {
        return mSharedPreference.getInt(preferenceName, defaultValue);
    }


}
