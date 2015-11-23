package michii.de.scannapp._view.activities;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import de.greenrobot.event.EventBus;

/**
 * Activity which implements the registration to the EventBus.
 * @author Michii
 * @since 10.07.2015
 */
public class BusActivity extends AppCompatActivity {

    /**
     * Registers the Activity to the EventBus.
     */
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        Log.d(this.getClass().getSimpleName(), "onStart");
    }

    /**
     * Unregisters the Activity from the EventBus.
     */
    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
        Log.d(this.getClass().getSimpleName(), "onStop");
    }
}
