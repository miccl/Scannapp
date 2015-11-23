package michii.de.scannapp._view.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import michii.de.scannapp.R;

/**
 * Layout fragment to present contribution possibilities of the application.
 */
public class ContributeViewFragment extends Fragment {


    public static ContributeViewFragment newInstance() {
        return new ContributeViewFragment();
    }
    
    public ContributeViewFragment() {
        // Required empty public constructor
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_contribute_view, container, false);
        setupToolbar();
        return layout;
    }

    private void setupToolbar() {
        AppCompatActivity activity = (AppCompatActivity)  getActivity();
        if(activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle("Contribute");
        }
    }

}
