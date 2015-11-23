package michii.de.scannapp._view.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;
import org.lucasr.twowayview.ItemClickSupport;
import org.lucasr.twowayview.ItemSelectionSupport;
import org.lucasr.twowayview.widget.TwoWayView;

import java.util.ArrayList;
import java.util.List;

import michii.de.scannapp.R;
import michii.de.scannapp._view.adapters.FolderViewAdapter;
import michii.de.scannapp.model.DatamodelApi;
import michii.de.scannapp.model.business_logic.document.Document;
import michii.de.scannapp.model.data.file.DateUtil;

/**
 * Layout fragment to present month folder for the document.
 * The list is implemented with a RecyclerView in combination with {@link FolderViewAdapter}
 * @author Michii
 * @since 12.06.2015
 */
public class FolderViewFragment extends Fragment {


    private FolderViewAdapter mRecyclerAdapter;

    private ItemSelectionSupport mItemSelection;
    private List<Folder> mFolderList;
    private DatamodelApi mDataModelApi;
    private int mCounter;
    private ActionMode mActionMode;



    public FolderViewFragment() {
        // Required empty public constructor
    }

    public static FolderViewFragment newInstance() {
        return new FolderViewFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataModelApi = new DatamodelApi(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_folder_view, container, false);
        updateToolbar();
        return rootView;

    }

    private void updateToolbar() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if(activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle("Month");
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
                if (mActionMode == null) {

                    Folder folder = mFolderList.get(position);
                    int year = folder.year;
                    int month = folder.month;
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    DocumentViewFragment fragment = DocumentViewFragment.newInstance(DocumentViewFragment.DATA_TYPE_MONTH, year, month);
//                fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.replace(R.id.fragment_view, fragment);
                    fragmentTransaction.commit();
                }else {
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


        mItemSelection = ItemSelectionSupport.addTo(recyclerView);

        mFolderList = getData();
        mRecyclerAdapter = new FolderViewAdapter(getActivity(), mFolderList);
        recyclerView.setAdapter(mRecyclerAdapter);
    }

    /**
     * Retrieves the folders based on the saved documents and especially their creation times.
     * @return list of folders
     */
    private List<Folder> getData() {
        List<Folder> folders = new ArrayList<>();

        int start_year = 2014;
        int end_year = DateUtil.getCurrentYear();

        for (int curr_year = start_year; curr_year <= end_year; curr_year++) {
            long year_count = mDataModelApi.countDocsByYear(curr_year);
            if (year_count > 0) {
                folders.add(new Folder(FolderViewAdapter.TYPE_YEAR, curr_year, -1, year_count));
                for(int curr_month=0; curr_month<11; curr_month++) {
                    long month_count = mDataModelApi.countDocsByMonth(curr_year, curr_month);
                    if(month_count > 0) {
                        folders.add(new Folder(FolderViewAdapter.TYPE_MONTH, curr_year, curr_month, month_count));
                    }
                }
            }
        }

        return folders;
    }

    /**
     * Class to present a specific folder of a year or month.
     */
    public class Folder {
        public int type;
        public int year;
        public int month;
        public long number;

        public Folder(int type, int year, int month, long number) {
            this.type = type;
            this.year = year;
            this.month = month;
            this.number = number;
        }
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
                    Folder folder = mFolderList.get(getCheckedItems()[0]);
                    mode.setTitle(DateUtil.getMonthString(folder.month));
                    mode.getMenuInflater().inflate(R.menu.action_mode_multiple, menu);
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
            int[] checked_positions = getCheckedItems();

            switch (item.getItemId()) {
                case R.id.action_mode_item_delete:
                    // get checked documents
                    List<Folder> checked_folders = new ArrayList<>();
                    for (int checked_position : checked_positions) {
                        checked_folders.add(mFolderList.get(checked_position));
                    }
                    // delete the checked docs in database, adapter and in the fragment
                    for (Folder current_checked_folder : checked_folders) {
                        List<Document> docs = mDataModelApi.getDocsByMonth(current_checked_folder.year, current_checked_folder.month);
                        for(Document doc : docs) {
                            mDataModelApi.delete(doc);
                        }
                        mRecyclerAdapter.delete(current_checked_folder);
                        mFolderList.remove(current_checked_folder);
                    }

                    mode.finish(); // Action picked, so close the CAB
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

    @NotNull
    private int[] getCheckedItems() {
        SparseBooleanArray checkArray = mItemSelection.getCheckedItemPositions();
        int[] checked_positions = new int[checkArray.size()];
        int j = 0;
        for (int i = 0; i < checkArray.size(); i++) {
            int position = checkArray.keyAt(i);
            if (checkArray.valueAt(i)) {
                checked_positions[j] = position;
                j++;
            }
        }
        return checked_positions;
    }

}

