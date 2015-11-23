package michii.de.scannapp._view.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.lucasr.twowayview.ItemClickSupport;
import org.lucasr.twowayview.widget.TwoWayView;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import michii.de.scannapp.R;
import michii.de.scannapp._view.adapters.AboutViewAdapter;

/**
 * Layout fragment to present the choice between {@link LicenseViewFragment}, {@link ContactViewFragment}, {@link ContactViewFragment} and {@link HelpViewFragment} in a information list.
 * The list is implemented with a RecyclerView in combination with {@link AboutViewAdapter}
 */
public class AboutViewFragment extends Fragment  {

    public static final int TAG_CONTACT = 0;
    public static final int TAG_CONTRIBUTE = 1;
    public static final int TAG_HELP = 2;
    public static final int TAG_LICENSE = 3;

    public static AboutViewFragment newInstance() {
        return new AboutViewFragment();
    }
    
    public AboutViewFragment() {
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
        View layout = inflater.inflate(R.layout.fragment_about_view, container, false);
        AppCompatActivity activity = (AppCompatActivity)  getActivity();
        if(activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle("About");
        }

        return layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TwoWayView mRecyclerView = (TwoWayView) view.findViewById(R.id.list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLongClickable(true);

        final ItemClickSupport itemClick = ItemClickSupport.addTo(mRecyclerView);

        itemClick.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View child, int position, long id) {
                EventBus.getDefault().post(new AboutViewFragmentEvent(position));
            }

        });

        AboutViewAdapter mRecyclerAdapter = new AboutViewAdapter(getActivity(), getData());
        mRecyclerView.setAdapter(mRecyclerAdapter);
    }

    private List<Information> getData() {
        List<Information> data = new ArrayList<>();

        String[] titles = getResources().getStringArray(R.array.about_title);
        int[] icons = {R.drawable.ic_action_account_circle_trim_36dp,
                R.drawable.ic_communication_chat_trim_36dp,
                R.drawable.ic_action_help_trim_36dp,
                R.drawable.ic_action_extension_trim_36dp};
        String[] summaries = getResources().getStringArray(R.array.about_summary);

        for (int i = 0; i < titles.length; i++) {
            Information info = new Information();
            info.title = titles[i];
            info.iconId = icons[i];
            info.summary = summaries[i];
            data.add(info);
        }
        return data;
    }

    /**
     * Business class to present the information of the different choice by its name, a icon and a short summary.
     */
    public class Information {
        public String title;
        public int iconId;
        public String summary;
    }

    public class AboutViewFragmentEvent {
        public final int id;

        public AboutViewFragmentEvent(int id) {
            this.id = id;
        }
    }

}
