package michii.de.scannapp._view.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.lucasr.twowayview.ItemClickSupport;
import org.lucasr.twowayview.ItemSelectionSupport;
import org.lucasr.twowayview.widget.DividerItemDecoration;
import org.lucasr.twowayview.widget.TwoWayView;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import michii.de.scannapp.R;
import michii.de.scannapp._view.adapters.CategoryViewAdapter;
import michii.de.scannapp._view.dialogs.AddDialogFragment;
import michii.de.scannapp.model.DatamodelApi;
import michii.de.scannapp.model.business_logic.document.Category;

/**
 * Layout fragment to present the saved categories in a list.
 * The list is implemented with a RecyclerView in combination with {@link CategoryViewAdapter}
 * @author Michii
 * @since 12.06.2015
 */
public class CategoryViewFragment extends BusFragment {


    private static final int DIALOG_MODE_ADD = 0;
    private static final int DIALOG_MODE_UPDATE = 1;
    private CategoryViewAdapter mRecyclerAdapter;
    private ActionMode mActionMode;
    private int mCounter;

    private ItemSelectionSupport mItemSelection;
    private DatamodelApi mDatamodelApi;
    private List<Category> mCategories;
    private Category mCategory;
    private int mDialogMode;


    public static CategoryViewFragment newInstance() {
        return new CategoryViewFragment();
    }

    public CategoryViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatamodelApi = new DatamodelApi(getActivity());
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_category_view, container, false);
        updateToolbar();

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_category, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_category:
                showDialog(DIALOG_MODE_ADD);
                return true;
            case R.id.menu_item_select_all:
                mItemSelection.setChoiceMode(ItemSelectionSupport.ChoiceMode.MULTIPLE);
                selectAll();
                // Start the CAB using the ActionMode.Callback defined above
                AppCompatActivity activity = (AppCompatActivity) getActivity();
                activity.startSupportActionMode(mActionModeCallback);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }


    private void updateToolbar() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if(activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle(R.string.drawer_item_category);
        }
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
                if (mActionMode == null) {
                    Category category = mCategories.get(position);
                    String categoryTitle = category.getTitle();
                    DocumentViewFragment fragment = DocumentViewFragment.newInstance(DocumentViewFragment.DATA_TYPE_CATEGORY, categoryTitle);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_view, fragment);
                    fragmentTransaction.commit();
                } else {
                    if (mItemSelection.isItemChecked(position))
                        mCounter--;
                    else
                        mCounter++;
                    mActionMode.invalidate();
                }
            }

        });

        itemClick.setOnItemLongClickListener(
                new ItemClickSupport.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(RecyclerView parent, View child, int position, long id) {
                        if (mActionMode != null) {
                            return true;
                        }

                        mItemSelection.setChoiceMode(ItemSelectionSupport.ChoiceMode.MULTIPLE);
                        mItemSelection.setItemChecked(position, true);
                        mCounter = 1;

                        /// / Start the CAB using the ActionMode.Callback defined above
                        AppCompatActivity activity = (AppCompatActivity) getActivity();
                        mActionMode = activity.startSupportActionMode(mActionModeCallback);
                        return true;
                    }
                }

        );

        final Drawable divider = ContextCompat.getDrawable(getActivity(), R.drawable.divider);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(divider));
        mItemSelection = ItemSelectionSupport.addTo(mRecyclerView);

        mRecyclerAdapter = new CategoryViewAdapter(getActivity(), getData());
        mRecyclerView.setAdapter(mRecyclerAdapter);
    }

    private List<Category> getData() {
        mCategories = mDatamodelApi.getCategories();
        return mCategories;


    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {


        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            mActionMode = mode;
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            menu.clear();
            switch (mCounter) {
                case 0:
                    mode.finish();
                    return true;
                case 1:
                    Category category = getCheckedItem();
                    if (category != null) {
                        mode.setTitle(category.getTitle());
                    }
                    mode.getMenuInflater().inflate(R.menu.action_mode_single, menu);
                    return true;
                default:
                    mActionMode.setTitle(mCounter + " items selected");
                    mode.getMenuInflater().inflate(R.menu.action_mode_multiple, menu);
                    return true;
            }
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            switch (item.getItemId()) {
                case R.id.action_mode_item_edit:
                    mCategory = getCheckedItem();
                    showDialog(DIALOG_MODE_UPDATE);
                    mode.finish(); // Action picked, so close the CAB
                    return true;
                case R.id.action_mode_item_delete:
                    // delete the checked docs in database, adapter and in the fragment
                    for (Category current_checked_cat : getCheckedItems()) {
                        mDatamodelApi.delete(current_checked_cat);
                        mRecyclerAdapter.delete(current_checked_cat);
                        mCategories.remove(current_checked_cat);
                    }

                    EventBus.getDefault().post(new CategoryViewFragmentEvent());
                    mode.finish(); // Action picked, so close the CAB
                    return true;
                case R.id.action_mode_item_select_all:
                    selectAll();
                    mode.invalidate();
                    return true;
                default:
                    return false;
            }
        }


        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mItemSelection.clearChoices();
            mItemSelection.setChoiceMode(ItemSelectionSupport.ChoiceMode.NONE);
            mActionMode = null;
        }
    };

    private void selectAll() {
        for(int i=0; i<mCategories.size(); i++) {
            if(!mItemSelection.isItemChecked(i))
                mItemSelection.setItemChecked(i, true);
        }
        mCounter = mCategories.size();
    }

    private void showDialog(int mode) {
        mDialogMode = mode;
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        AddDialogFragment dialogFragment;
        switch (mDialogMode) {
            case DIALOG_MODE_ADD:
                dialogFragment = AddDialogFragment.newInstance("Add category");
                dialogFragment.show(fragmentManager, "dialog");
                break;
            case DIALOG_MODE_UPDATE:
                dialogFragment = AddDialogFragment.newInstance("Change category title");
                dialogFragment.show(fragmentManager, "dialog");
                break;
        }

    }


    @NotNull
    private List<Category> getCheckedItems() {
        SparseBooleanArray checkArray = mItemSelection.getCheckedItemPositions();
        List<Category> checked_items = new ArrayList<>();
        for(int i=0; i< checkArray.size(); i++) {
            int position = checkArray.keyAt(i);
            if (checkArray.valueAt(i)) {
                checked_items.add(mCategories.get(position));
            }
        }
        return checked_items;
    }

    private Category getCheckedItem() {
        SparseBooleanArray checkArray = mItemSelection.getCheckedItemPositions();
        for(int i=0; i< checkArray.size(); i++) {
            int position = checkArray.keyAt(i);
            if (checkArray.valueAt(i)) {
                return mCategories.get(position);
            }
        }
        return null;
    }

    @SuppressWarnings("unused")
    public void onEvent(AddDialogFragment.AddDialogFragmentEvent event) {
        String title = event.title;
        if (title != null) {
            int position = mCategories.indexOf(mCategory);

            switch (mDialogMode) {
                case DIALOG_MODE_UPDATE:
                    Category cat = mDatamodelApi.setCategory(mCategory, title);
                    if (cat != null) {
                        mCategory = cat;
                        mRecyclerAdapter.update(position, mCategory);
                    } else {
                        Toast.makeText(getActivity(), "Category does not exist", Toast.LENGTH_LONG).show();
                    }

                    break;
                case DIALOG_MODE_ADD:
                    if (mDatamodelApi.addCategory(null, title) != null) {
                        mCategories = mDatamodelApi.getCategories();
                        mRecyclerAdapter.setData(mCategories);
                        EventBus.getDefault().post(new CategoryViewFragmentEvent());
                        Toast.makeText(getActivity(), "Added category \"" + title + "\"", Toast.LENGTH_LONG).show();
                    } else {//exists already
                        Toast.makeText(getActivity(), "Category already exist", Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }
        Log.d("MAIN", "CATEGORY");

    }

    public class CategoryViewFragmentEvent {

    }

}

