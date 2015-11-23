package michii.de.scannapp._view.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import michii.de.scannapp.R;

/**
 * Layout fragment to present information's of the creators of the application.
 */
public class ContactViewFragment extends Fragment {
    private Toolbar mToolbar;

    public static ContactViewFragment newInstance() {
        return new ContactViewFragment();
    }
    
    public ContactViewFragment() {
        // Required empty public constructor
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_contact_view, container, false);
        setupToolbar();
        return layout;
    }

    private void setupToolbar() {
        AppCompatActivity activity = (AppCompatActivity)  getActivity();
        if(activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle("Contact");
        }

    }

}
