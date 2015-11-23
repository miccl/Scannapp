package michii.de.scannapp._view.fragments;


import android.app.SearchManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
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
import michii.de.scannapp._view.adapters.DocumentViewAdapter;
import michii.de.scannapp._view.dialogs.AddDialogFragment;
import michii.de.scannapp._view.provider.SearchRecentSuggestionProvider;
import michii.de.scannapp.model.DatamodelApi;
import michii.de.scannapp.model.business_logic.document.Document;

/**
 * Layout fragment to present given documents in a list.
 * The list is implemented with a RecyclerView in combination with {@link DocumentViewAdapter}
 *
 */
public class DocumentViewFragment extends BusFragment {


    private static final String ARG_DATA_VALUE = "arg_data_value";
    public static final String ARG_DATA_TYPE = "arg_data_type";
    public static final String DATA_TYPE_RECENT = "data_type_recent";
    public static final String DATA_TYPE_YEAR = "data_type_year";
    public static final String DATA_TYPE_MONTH = "data_type_month";
    public static final String DATA_TYPE_CATEGORY = "data_type_category";
    public static final String DATA_TYPE_SEARCH = "data_type_search";
    private static final String TAG = DocumentViewFragment.class.getSimpleName();
    private static final String ARG_DATA_VALUE2 = "arg_data_value2";

    private DocumentViewAdapter mRecyclerAdapter;
    private List<Document> mDocuments = new ArrayList<>();
    private ActionMode mActionMode;
    private int mCounter = 0;

    private ItemSelectionSupport mItemSelection;
    private DatamodelApi mDatamodelApi;
    private String mDataType;
    private String mDataValue;
    private Document mDocument;
    private int mDataValue2;

    /**
     * Constructs a new fragment using the data type and value.
     * The type stands for specific possible document as document of specific year, month or category.
     * The value stands for the specific value of that type.
     * @param data_type the type of the documents
     * @param data_value the value of the specific type
     * @return fragment with the specific list of documents
     */
    public static DocumentViewFragment newInstance(String data_type, String data_value) {
        DocumentViewFragment fragment = new DocumentViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DATA_TYPE, data_type);
        args.putString(ARG_DATA_VALUE, data_value);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Constructs a new fragment using specified data type, year and month.
     * It is used to get documents from a sepcific year and month.
     * @param data_type the type of documents
     * @param year the searched year
     * @param month the searched month
     * @return fragrment with the specific list of documents
     */
    public static DocumentViewFragment newInstance(String data_type, int year, int month) {
        DocumentViewFragment fragment = new DocumentViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DATA_TYPE, data_type);
        args.putString(ARG_DATA_VALUE, Integer.toString(year));
        args.putInt(ARG_DATA_VALUE2, month);
        fragment.setArguments(args);
        return fragment;

    }

    public DocumentViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mDataType = getArguments().getString(ARG_DATA_TYPE);
            mDataValue = getArguments().getString(ARG_DATA_VALUE);
            mDataValue2 = getArguments().getInt(ARG_DATA_VALUE2);
        }
        mDatamodelApi = new DatamodelApi(getActivity());
        setHasOptionsMenu(true);

        mDocuments = intializeData();

    }

    /**
     * Initialized the document list based on the data type and data value defined in the constructor
     * @return list of documents
     */
    private List<Document> intializeData() {
        switch (mDataType) {
            case DATA_TYPE_RECENT:
                return mDatamodelApi.getDocsByRecent();
            case DATA_TYPE_YEAR:
                return mDatamodelApi.getDocsByYear(Integer.parseInt(mDataValue));
            case DATA_TYPE_CATEGORY:
                return mDatamodelApi.getDocsByCategory(mDataValue);
            case DATA_TYPE_SEARCH:
                return mDatamodelApi.searchDocument(mDataValue);
            case DATA_TYPE_MONTH:
                return mDatamodelApi.getDocsByMonth(Integer.parseInt(mDataValue), mDataValue2);
        }
        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_document_view, container, false);
        setupToolbar(mDocuments.size());

        return rootView;

    }

    private void setupToolbar(int size) {
        String title = getString(R.string.title_document) + " (" + size + ") ";
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if(activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TwoWayView recyclerView = (TwoWayView) view.findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLongClickable(true);

        final ItemClickSupport itemClick = ItemClickSupport.addTo(recyclerView);
        mItemSelection = ItemSelectionSupport.addTo(recyclerView);

        itemClick.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View child, int position, long id) {
                if (mActionMode == null) {
                    String currentDocId = mDocuments.get(position).getId();
                    if (currentDocId != null) {
                        EventBus.getDefault().post(new DocumentViewFragmentEvent(currentDocId));
                    }
                } else {
                    if (mItemSelection.isItemChecked(position)) {
                        mCounter--;
                    } else {
                        mCounter++;
                    }
                    mActionMode.invalidate();
                }
            }

        });

        itemClick.setOnItemLongClickListener(new ItemClickSupport.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(RecyclerView parent, View child, int position, long id) {
                if (mActionMode != null) {
                    return true;
                }
                mItemSelection.setChoiceMode(ItemSelectionSupport.ChoiceMode.MULTIPLE);
                mItemSelection.setItemChecked(position, true);
                mCounter = 1;
                // Start the CAB using the ActionMode.Callback defined above
                AppCompatActivity activity = (AppCompatActivity) getActivity();
                activity.startSupportActionMode(mActionModeCallback);

                return true;
            }
        });


        final Drawable divider = ContextCompat.getDrawable(getActivity(), R.drawable.divider);
        recyclerView.addItemDecoration(new DividerItemDecoration(divider));

        mRecyclerAdapter = new DocumentViewAdapter(getActivity(), mDocuments);
        recyclerView.setAdapter(mRecyclerAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_document, menu);

        //         Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_item_search).getActionView();

        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
//            case R.id.add_document:
//                Document doc = mDatamodelApi.addDoc();
//                mDocuments.add(doc);
//                mRecyclerAdapter.add(doc);
//                return true;
            case R.id.menu_item_clear_history:
                SearchRecentSuggestions suggestions = new SearchRecentSuggestions(getActivity(),
                        SearchRecentSuggestionProvider.AUTHORITY, SearchRecentSuggestionProvider.MODE);
                suggestions.clearHistory();
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

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {


        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
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
                case 1: //one selected
                    Document doc = getCheckedItem();
                    if (doc != null) {
                        mode.setTitle(doc.getTitle());
                    }
                    mode.getMenuInflater().inflate(R.menu.action_mode_single, menu);
                    return true;
                default: //more than one selected
                    mode.setTitle(mCounter + " items selected");
                    mode.getMenuInflater().inflate(R.menu.action_mode_multiple, menu);
                    return true;

            }
        }


        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            //get positions of checked documents
            switch (item.getItemId()) {
                case R.id.action_mode_item_delete:
                    // delete the checked docs in database, adapter and in the fragment
                    for (Document current_checked_doc : getCheckedItems()) {
                        mDatamodelApi.delete(current_checked_doc);
                        mRecyclerAdapter.delete(current_checked_doc);
                        mDocuments.remove(current_checked_doc);
                    }
                    setupToolbar(mDocuments.size());
                    mode.finish(); // Action picked, so close the CAB
                    return true;
                case R.id.action_mode_item_edit:
                    mDocument = getCheckedItem();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    AddDialogFragment newFragment = AddDialogFragment.newInstance("Change document title");
                    newFragment.show(fragmentManager, "dialog");
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
        for(int i=0; i<mDocuments.size(); i++) {
            if(!mItemSelection.isItemChecked(i))
                mItemSelection.setItemChecked(i, true);
        }
        mCounter = mDocuments.size();
    }

    private void showDialog(String title) {
    }

    @NotNull
    private List<Document> getCheckedItems() {
        SparseBooleanArray checkArray = mItemSelection.getCheckedItemPositions();
        List<Document> checked_items = new ArrayList<>();
        for(int i=0; i< checkArray.size(); i++) {
            int position = checkArray.keyAt(i);
            if (checkArray.valueAt(i)) {
                checked_items.add(mDocuments.get(position));
            }
        }
        Log.d(TAG, "checked_items: " + checked_items.toString());
        return checked_items;
    }

    private Document getCheckedItem() {
        SparseBooleanArray checkArray = mItemSelection.getCheckedItemPositions();
        for(int i=0; i< checkArray.size(); i++) {
            int position = checkArray.keyAt(i);
            if (checkArray.valueAt(i)) {
                Log.d(TAG, "checked_item: " + mDocuments.get(position));
                return mDocuments.get(position);
            }
        }
        return null;
    }

    @SuppressWarnings("unused")
    public void onEvent(AddDialogFragment.AddDialogFragmentEvent event) {
        if (event.title != null) {
            int position = mDocuments.indexOf(mDocument);
            Document doc = mDatamodelApi.setDocument(mDocument, event.title);
            if(doc != null) {
                mDocument = doc;
                mRecyclerAdapter.update(position, mDocument);
            } else {
                Toast.makeText(getActivity(), "Name is already in use", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void updateData() {
        mDocuments = intializeData();
        mRecyclerAdapter.setData(mDocuments);
        setupToolbar(mDocuments.size());
        Log.d(TAG, "updateData()");
//        mRecyclerAdapter.notifyDataSetChanged();
    }

    public class DocumentViewFragmentEvent {
        public final String doc_id;

        public DocumentViewFragmentEvent(String doc_id) {
            this.doc_id = doc_id;
        }
    }

}
