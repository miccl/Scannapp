package michii.de.scannapp;

import android.app.Application;
import android.content.Context;

import com.adobe.creativesdk.foundation.AdobeCSDKFoundation;
import com.aviary.android.feather.sdk.IAviaryClientCredentials;

import co.uk.rushorm.android.RushAndroid;

/**
 * Application class of the application.
 * Initiliazes the app context, the Rush database and the Adobe aviary client.
 * @author Michii
 * @since 22.05.2015
 */
public class MyApplication extends Application implements IAviaryClientCredentials {

    private static final String CREATIVE_SDK_SAMPLE_CLIENT_ID = "a7b4420e19ec44af96748620a7dc976a";
    private static final String CREATIVE_SDK_SAMPLE_CLIENT_SECRET = "a4a88f7c-69cf-4668-98e9-c14acb8ebabb";


    private static MyApplication mInstance;

    public static MyApplication getInstance() {
        return mInstance;
    }

    public static Context getAppContext() {
        return mInstance.getApplicationContext();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        RushAndroid.initialize(getAppContext());
        AdobeCSDKFoundation.initializeCSDKFoundation(getApplicationContext());
    }


    @Override
    public String getClientID() {
        return CREATIVE_SDK_SAMPLE_CLIENT_ID;
    }

    @Override
    public String getClientSecret() {
        return CREATIVE_SDK_SAMPLE_CLIENT_SECRET;
    }


    @Override
    public String getBillingKey() {
        return "";
    }
}
