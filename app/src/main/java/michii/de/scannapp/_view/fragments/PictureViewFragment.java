package michii.de.scannapp._view.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.github.alexkolpa.fabtoolbar.FabToolbar;

import org.jetbrains.annotations.NotNull;
import org.lucasr.twowayview.ItemClickSupport;
import org.lucasr.twowayview.ItemSelectionSupport;
import org.lucasr.twowayview.widget.TwoWayView;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import michii.de.scannapp.R;
import michii.de.scannapp._view.adapters.PictureViewAdapter;
import michii.de.scannapp.model.DatamodelApi;
import michii.de.scannapp.model.business_logic.document.Document;
import michii.de.scannapp.model.business_logic.document.Picture;
import michii.de.scannapp.rest.utils.IntentUtils;

/**
 * Layout fragment to present the saved {@link Picture}s in a list.
 * The list is implemented with a RecyclerView in combination with {@link PictureViewFragment}.
 */
public class PictureViewFragment extends Fragment {


    private static final String ARG_DOCUMENT = "doc";

    public static final int EVENT_EDIT = 0;
    public static final int EVENT_PRINT = 1;
    public static final int EVENT_DELETE = 2;
    public static final int EVENT_RENAME = 3;

    private int mCounter;
    private ItemSelectionSupport mItemSelection;
    private ActionMode mActionMode;
    private PictureViewAdapter mRecyclerAdapter;
    private FabToolbar mFabToolbar;
    private List<Picture> mPictures;
    private DatamodelApi mDatamodelApi;
    private String mDocId;


    /**
     * Constructs a new instance using the specified document id.
     * The document id stands for a specific document in database.
     * @param doc_id the document's id
     * @return a new instance with the given document
     */
    public static PictureViewFragment newInstance(String doc_id) {
        PictureViewFragment fragment = new PictureViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DOCUMENT, doc_id);
        fragment.setArguments(args);
        return fragment;
    }

    public PictureViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatamodelApi = new DatamodelApi(getActivity());
        String doc_id = null;
        if (getArguments() != null) {
             doc_id = getArguments().getString(ARG_DOCUMENT);
        }

        if(doc_id == null) {
            return;
        }
        mDocId = doc_id;
        mPictures = initializeData();
        mFabToolbar = (FabToolbar) getActivity().findViewById(R.id.fab_toolbar);

        setHasOptionsMenu(true);
    }

    private List<Picture> initializeData() {
        if(mDocId != null) {
            Document doc = mDatamodelApi.getDocumentById(mDocId);
            if(doc != null) {
                return doc.getAttachedPictures();
            }
        }
        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_picture_view, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TwoWayView recyclerView = (TwoWayView) view.findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLongClickable(true);

        final ItemClickSupport itemClick = ItemClickSupport.addTo(recyclerView);

        itemClick.setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClick(RecyclerView parent, View child, int position, long id) {
                        if (mActionMode == null) {
                            Picture pic = mPictures.get(position);
                            Uri uri = Uri.parse(pic.getUriString());
                            IntentUtils.viewImage(getActivity(), uri);

                        } else {
                            if (mItemSelection.isItemChecked(position)) {
                                mCounter--;

                            } else {
                                mCounter++;
                            }
                            mActionMode.invalidate();
                        }
                    }

                }

        );

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
//            setClicked(child, true);

                        // Start the CAB using the ActionMode.Callback defined above
                        AppCompatActivity activity = (AppCompatActivity) getActivity();
                        mActionMode = activity.startSupportActionMode(mActionModeCallback);

//            mCounter = 1;
                        return true;
                    }
                }

        );

        recyclerView.addOnScrollListener(
                new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        mFabToolbar.hide();
                    }
                }

        );

        mItemSelection = ItemSelectionSupport.addTo(recyclerView);

        mRecyclerAdapter = new PictureViewAdapter(getActivity(), mPictures);
        recyclerView.setAdapter(mRecyclerAdapter);

    }


    private android.support.v7.view.ActionMode.Callback mActionModeCallback = new android.support.v7.view.ActionMode.Callback() {
        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(android.support.v7.view.ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            mActionMode = mode;
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(android.support.v7.view.ActionMode mode, Menu menu) {
//           Log.d("TAG", mCounter + "");
            menu.clear();
            switch (mCounter) {
                case 0:
                    mActionMode.finish();
                    return true;
                case 1:
                    Picture pic = getCheckedItem();
                    if (pic != null) {
                        mActionMode.setTitle(pic.getTitle());
                    }
                    mode.getMenuInflater().inflate(R.menu.action_mode_single_picture, menu);
                    return true;
                default:
                    mActionMode.setTitle(mCounter + " items selected");
                    mode.getMenuInflater().inflate(R.menu.action_mode_multiple, menu);
                    return true;

            }
        }


        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(android.support.v7.view.ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_mode_item_delete:
                    for (Picture current_checked_pic : getCheckedItems()) {
                        mRecyclerAdapter.remove(current_checked_pic);
                        mPictures.remove(current_checked_pic);
                        EventBus.getDefault().post(new PictureViewEditEvent(EVENT_DELETE, current_checked_pic));
                    }
                    mode.finish(); // Action picked, so close the CAB
                    return true;
                case R.id.menu_picture_rename:
                    Picture checked_pic = getCheckedItem();
                    EventBus.getDefault().post(new PictureViewEditEvent(EVENT_RENAME, checked_pic));
                    mode.finish(); // Action picked, so close the CAB
                    return true;

                case R.id.menu_picture_copy:
                    //TODO
                    mode.finish(); // Action picked, so close the CAB
                    return true;
                case R.id.action_mode_item_edit:
                    checked_pic = getCheckedItem();
                    EventBus.getDefault().post(new PictureViewEditEvent(EVENT_EDIT, checked_pic));
                    mode.finish(); // Action picked, so close the CAB
                    return true;
                case R.id.action_mode_item_print:
                    checked_pic = getCheckedItem();
                    EventBus.getDefault().post(new PictureViewEditEvent(EVENT_PRINT, checked_pic));
                    mode.finish(); // Action picked, so close the CAB
                    return true;
                case R.id.action_mode_item_select_all:
                    for(int i=0; i<mPictures.size(); i++) {
                        if(!mItemSelection.isItemChecked(i))
                            mItemSelection.setItemChecked(i, true);
                    }
                    mCounter = mPictures.size();
                    mode.invalidate();
                    return true;
//                case R.id.menu_picture_share:
////                    Picture checked_pic = mPictures.get(getCheckedItems()[0]);
//                    //TODO
//                    mode.finish(); // Action picked, so close the CAB
//                    return true;
                default:
                    return false;
            }

        }


        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(android.support.v7.view.ActionMode mode) {
            mItemSelection.clearChoices();
            mItemSelection.setChoiceMode(ItemSelectionSupport.ChoiceMode.NONE);
//        clearChoices();
            mActionMode = null;
        }


    };

    public void updateData() {
        mPictures = initializeData();
        mRecyclerAdapter.setData(mPictures);
    }

    public void update(Picture old_picture, Picture new_picture) {
        int position = mPictures.indexOf(old_picture);
        mRecyclerAdapter.update(position, new_picture);
    }

    @NotNull
    private List<Picture> getCheckedItems() {
        SparseBooleanArray checkArray = mItemSelection.getCheckedItemPositions();
        List<Picture> checked_items = new ArrayList<>();
        for(int i=0; i< checkArray.size(); i++) {
            int position = checkArray.keyAt(i);
            if (checkArray.valueAt(i)) {
                checked_items.add(mPictures.get(position));
            }
        }
        return checked_items;
    }

    private Picture getCheckedItem() {
        SparseBooleanArray checkArray = mItemSelection.getCheckedItemPositions();
        for(int i=0; i< checkArray.size(); i++) {
            int position = checkArray.keyAt(i);
            if (checkArray.valueAt(i)) {
                return mPictures.get(position);
            }
        }
        return null;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mActionMode != null) {
            mActionMode.finish();
        }
    }

    public class PictureViewEditEvent {
        public final int event;
        public final Picture pic;

        public PictureViewEditEvent(int event, Picture pic) {
            this.event = event;
            this.pic = pic;
        }
    }
}
