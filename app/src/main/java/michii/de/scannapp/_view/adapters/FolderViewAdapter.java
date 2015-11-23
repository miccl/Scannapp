package michii.de.scannapp._view.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import michii.de.scannapp.R;
import michii.de.scannapp._view.fragments.FolderViewFragment;
import michii.de.scannapp.model.data.file.DateUtil;

/**
 * ViewAdapter for {@link michii.de.scannapp._view.fragments.FolderViewFragment}.
 * Provides itemViews for a list of {@link michii.de.scannapp._view.fragments.FolderViewFragment.Folder}.
 * @author Michii
 * @since 12.06.2015
 */
public class FolderViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_MONTH = 0;
    public static final int TYPE_YEAR = 1;

    private List<FolderViewFragment.Folder> mFolderList = new ArrayList<>();
    private LayoutInflater mLayoutInflater;

    public FolderViewAdapter(Context context, List<FolderViewFragment.Folder> folderList) {
        mLayoutInflater = LayoutInflater.from(context);
        mFolderList = folderList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_MONTH) {
            View view = mLayoutInflater.inflate(R.layout.row_month_view, parent, false);
            return new MonthViewHolder(view);
        } else {
            View view = mLayoutInflater.inflate(R.layout.row_year_view, parent, false);
            return new YearViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof MonthViewHolder) {
            MonthViewHolder monthHolder = (MonthViewHolder) holder;
            FolderViewFragment.Folder currentFolder = mFolderList.get(position);
            monthHolder.title.setText(DateUtil.getMonthString(currentFolder.month));
            monthHolder.number.setText(Long.toString(currentFolder.number));
        } else if (holder instanceof YearViewHolder) {
            YearViewHolder yearHolder = (YearViewHolder) holder;
            FolderViewFragment.Folder currentFolder = mFolderList.get(position);
            yearHolder.title.setText(Long.toString(currentFolder.year));
//            yearHolder.number.setText(Long.toString(currentFolder.number));
            yearHolder.number.setText("");
        }
    }

    @Override
    public int getItemViewType(int position) {
        FolderViewFragment.Folder folder = mFolderList.get(position);
        if (folder.type == TYPE_MONTH) {
            return TYPE_MONTH;
        } else {
            return TYPE_YEAR;
        }
    }

    @Override
    public int getItemCount() {
        return mFolderList.size();
    }

    public void delete(FolderViewFragment.Folder folder) {
        int position = mFolderList.indexOf(folder);
        mFolderList.remove(folder);
        if (position == 0) { // Bug: See https://github.com/lucasr/twoway-view/issues/134
            notifyDataSetChanged();
        } else {
            notifyItemRemoved(position);
        }


    }

    class MonthViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView number;

        public MonthViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tv_title);
            number = (TextView) itemView.findViewById(R.id.text_number);

        }

    }

    class YearViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView number;

        public YearViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tv_title);
            number = (TextView) itemView.findViewById(R.id.text_number);

        }

    }
}