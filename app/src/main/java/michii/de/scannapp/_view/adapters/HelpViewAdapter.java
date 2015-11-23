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
import michii.de.scannapp._view.fragments.HelpViewFragment;

/**
 * ViewAdapter for {@link michii.de.scannapp._view.fragments.HelpViewFragment}.
 * Provides itemViews for a list of {@link michii.de.scannapp._view.fragments.HelpViewFragment.Help}.
 *
 * @author Michii
 * @since 03.07.2015
 */
public class HelpViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final LayoutInflater mLayoutInflater;
    private final List<HelpViewFragment.Help> mHelps;

    public HelpViewAdapter(Context context, List<HelpViewFragment.Help> helps) {
        mLayoutInflater = LayoutInflater.from(context);
        mHelps = helps;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.row_help_view, parent, false);
        return new HelpViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        HelpViewHolder helpHolder = (HelpViewHolder) holder;
        HelpViewFragment.Help currentHelp = mHelps.get(position);
        helpHolder.title.setText(currentHelp.title);
        helpHolder.description.setText(currentHelp.description);
        if(currentHelp.iconId != 0) {
            helpHolder.icon.setImageResource(currentHelp.iconId);
        }
    }

    @Override
    public int getItemCount() {
        return mHelps.size();
    }

    class HelpViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView description;
        public ImageView icon;

        public HelpViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tv_title);
            description = (TextView) itemView.findViewById(R.id.tv_description);
            icon = (ImageView) itemView.findViewById(R.id.iv_icon);
        }

    }
}
