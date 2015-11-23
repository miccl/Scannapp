package michii.de.scannapp._view.fragments;

import android.support.v4.app.Fragment;

import de.greenrobot.event.EventBus;

/**
 * Fragment which implements the registration to the EventBus.
 * @author Michii
 * @since 10.07.2015
 */
public class BusFragment extends Fragment {


    /**
     * Registers the Fragment to the EventBus.
     */
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    /**
     * Unegisters the Fragment to the EventBus.
     */
    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

}
