package michii.de.scannapp._view.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import michii.de.scannapp.R;
import michii.de.scannapp._view.fragments.AboutViewFragment;

/**
 * ViewAdapter for {@link AboutViewFragment}.
 * Provides itemViews for a list of {@link michii.de.scannapp._view.fragments.AboutViewFragment.Information}.
 * @author Michii
 * @since 18.06.2015
 *
 */
public class AboutViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private final LayoutInflater mLayoutInflater;
    private final List<AboutViewFragment.Information> mInformations;

    /**
     * Constructs a new adapter using the specified context and {@link michii.de.scannapp._view.fragments.AboutViewFragment.Information} list
     * @param context the executing context
     * @param informations list of informations, which should be presented
     */
    public AboutViewAdapter(Context context, List<AboutViewFragment.Information> informations) {
        mLayoutInflater = LayoutInflater.from(context);
        mInformations = informations;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.row_about_view, parent, false);
        return new AboutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        AboutViewHolder licenseHolder = (AboutViewHolder) holder;
        AboutViewFragment.Information currentInformation = mInformations.get(position);
        licenseHolder.title.setText(currentInformation.title);
        licenseHolder.icon.setImageResource(currentInformation.iconId);
        licenseHolder.summary.setText(currentInformation.summary);


    }

    @Override
    public int getItemCount() {
        return mInformations.size();
    }

    class AboutViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public ImageView icon;
        public TextView summary;

        public AboutViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tv_title);
            icon = (ImageView) itemView.findViewById(R.id.iv_icon);
            summary = (TextView) itemView.findViewById(R.id.tv_summary);
        }

    }
}
