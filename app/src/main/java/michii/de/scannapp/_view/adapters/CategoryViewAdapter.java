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
import michii.de.scannapp.model.business_logic.document.Category;

/**
 * ViewAdapter for {@link michii.de.scannapp._view.fragments.CategoryViewFragment}.
 * Provides itemViews for a list of {@link Category}.
 * @author Michii
 * @since 12.06.2015
 */
public class CategoryViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<Category> mCategories = new ArrayList<>();
    private LayoutInflater mLayoutInflater;

    public CategoryViewAdapter(Context context, List<Category> categories) {
        mLayoutInflater = LayoutInflater.from(context);
        mCategories = categories;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.row_category_view, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Category currentCategory = mCategories.get(position);
        CategoryViewHolder categoryViewHolder = (CategoryViewHolder) holder;
        categoryViewHolder.title.setText(currentCategory.getTitle());
        categoryViewHolder.number.setText(Integer.toString(currentCategory.getAttachedDocs().size()));
    }

    @Override
    public int getItemCount() {
        return mCategories.size();
    }

    public void delete(Category category) {
        int position = mCategories.indexOf(category);
        mCategories.remove(category);
        if (position == 0) { // Bug: See https://github.com/lucasr/twoway-view/issues/134
            notifyDataSetChanged();
        } else {
            notifyItemRemoved(position);
        }

    }

    public void update(int position, Category changed_category) {
        Category cat = mCategories.get(position);
        cat.setTitle(changed_category.getTitle());
        notifyItemChanged(position);

    }

    public void setData(List<Category> categories) {
        mCategories = categories;
        notifyDataSetChanged();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        private TextView number;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tv_title);
            number = (TextView) itemView.findViewById(R.id.text_number);
        }

    }
}