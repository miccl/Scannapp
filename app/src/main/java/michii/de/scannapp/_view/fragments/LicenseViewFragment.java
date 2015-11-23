package michii.de.scannapp._view.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.lucasr.twowayview.ItemClickSupport;
import org.lucasr.twowayview.widget.TwoWayView;

import java.util.ArrayList;
import java.util.List;

import michii.de.scannapp.R;
import michii.de.scannapp._view.adapters.LicenseViewAdapter;

/**
 * Layout fragment to present the saved {@link michii.de.scannapp._view.fragments.LicenseViewFragment.License}s in a list.
 * The list is implemented with a RecyclerView in combination with {@link LicenseViewFragment}
 */
public class LicenseViewFragment extends Fragment {

    private static final String TAG = LicenseViewFragment.class.getSimpleName();
    private LicenseViewAdapter mRecyclerAdapter;
    private List<License> mLicenses;

    public static LicenseViewFragment newInstance() {
        return new LicenseViewFragment();
    }
    
    public LicenseViewFragment() {
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
        View layout = inflater.inflate(R.layout.fragment_license_view, container, false);
        setupToolbar();
        return layout;
    }

    private void setupToolbar() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if(activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle("License");
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TwoWayView recyclerView = (TwoWayView) view.findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLongClickable(true);

        final ItemClickSupport itemClick = ItemClickSupport.addTo(recyclerView);

        itemClick.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View child, int position, long id) {
                License license = mLicenses.get(position);
                String url = license.url;
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }

        });
        mLicenses = getData();
        mRecyclerAdapter = new LicenseViewAdapter(getActivity(), mLicenses);
        recyclerView.setAdapter(mRecyclerAdapter);
    }

    private List<License> getData() {
        List<License> data = new ArrayList<>();

        String[] titles = getResources().getStringArray(R.array.license_title);
        String[] urls = getResources().getStringArray(R.array.license_url);
        String[] licenses = getResources().getStringArray(R.array.license_license);
        String[] summaries = getResources().getStringArray(R.array.license_summary);
        if(titles.length == urls.length && urls.length == licenses.length && licenses.length == summaries.length) {
            for (int i = 0; i < titles.length; i++) {
                License current = new License();
                current.title = titles[i];
                current.url = urls[i];
                current.license = licenses[i];
                current.summary = summaries[i];
                data.add(current);
            }
        } else {
            Log.e(TAG, "stringarrays dont have the same length");
        }
        return data;
    }

    public class License {
        public String title;
        public String url;
        public String license;
        public String summary;

        public License() {}

        public License(String title, String url, String license, String summary) {
            this.title = title;
            this.url = url;
            this.license = license;
            this.summary = summary;
        }
    }
}
