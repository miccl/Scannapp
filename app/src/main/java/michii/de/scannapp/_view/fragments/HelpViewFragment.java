package michii.de.scannapp._view.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.lucasr.twowayview.widget.TwoWayView;

import java.util.ArrayList;
import java.util.List;

import michii.de.scannapp.R;
import michii.de.scannapp._view.adapters.HelpViewAdapter;

/**
 * Layout fragment to present the different {@link michii.de.scannapp._view.fragments.HelpViewFragment.Help} in a list.
 * The list is implemented with a RecyclerView in combination with {@link HelpViewAdapter}
 */
public class HelpViewFragment extends Fragment {

    private List<Help> mHelps;
    private HelpViewAdapter mRecyclerAdapter;

    public static HelpViewFragment newInstance() {
        return new HelpViewFragment();
    }
    
    public HelpViewFragment() {
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
        View layout = inflater.inflate(R.layout.fragment_help_view, container, false);
        setupToolbar();
        return layout;
    }

    private void setupToolbar() {
        AppCompatActivity activity = (AppCompatActivity)  getActivity();
        if(activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle("Help");
        }

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TwoWayView recyclerView = (TwoWayView) view.findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLongClickable(true);

        mHelps = getData();
        mRecyclerAdapter = new HelpViewAdapter(getActivity(), mHelps);
        recyclerView.setAdapter(mRecyclerAdapter);
    }

    private List<Help> getData() {
        List<Help> helps = new ArrayList<>();
        helps.add(new Help("Help", "This a a help", 0));
        helps.add(new Help("Help1", "This a a help", 0));
        helps.add(new Help("Help1", "This a a help", 0));

        return helps;
    }


    public class Help {
        public String title;
        public String description;
        public int iconId;

        public Help(String title, String description, int iconId) {
            this.title = title;
            this.description = description;
            this.iconId = iconId;
        }
    }
}
