package michii.de.scannapp._view.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import michii.de.scannapp.R;
import michii.de.scannapp._view.fragments.LicenseViewFragment;

/**
 * ViewAdapter for {@link LicenseViewFragment}.
 * Provides itemViews for a list of {@link michii.de.scannapp._view.fragments.LicenseViewFragment.License}.
 * @author Michii
 * @since 18.06.2015
 */
public class LicenseViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{


    private final LayoutInflater mLayoutInflater;
    private final List<LicenseViewFragment.License> mLicenses;

    public LicenseViewAdapter(Context context, List<LicenseViewFragment.License> licenses) {
        mLayoutInflater = LayoutInflater.from(context);
        mLicenses = licenses;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.row_license_view, parent, false);
        return new LicenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        LicenseViewHolder licenseHolder = (LicenseViewHolder) holder;
        LicenseViewFragment.License currentLicense = mLicenses.get(position);
        licenseHolder.title.setText(currentLicense.title);
//        licenseHolder.license.setText(currentLicense.license);
        licenseHolder.summary.setText(currentLicense.summary);

    }

    @Override
    public int getItemCount() {
        return mLicenses.size();
    }

    class LicenseViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        private TextView license;
        private TextView summary;

        public LicenseViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tv_title);
//            license = (TextView) itemView.findViewById(R.id.license);
            summary = (TextView) itemView.findViewById(R.id.tv_summary);
        }

    }
}
