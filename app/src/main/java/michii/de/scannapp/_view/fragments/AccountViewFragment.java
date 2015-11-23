package michii.de.scannapp._view.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
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
import michii.de.scannapp._view.adapters.AccountViewAdapter;
import michii.de.scannapp._view.dialogs.AddAccountDialogFragment;
import michii.de.scannapp.model.DatamodelApi;
import michii.de.scannapp.model.business_logic.document.Account;

/**
 * Layout fragment to present the saved accounts in a list.
 * The list is implemented with a RecyclerView in combination with {@link AccountViewAdapter}
 * @author Michii
 * @since 02.07.2015
 */
public class AccountViewFragment extends BusFragment {

    public static final int EVENT_UPDATE = 0;
    public static final int EVENT_ADD = 1;
    public static final int EVENT_DELETE = 2;
    public static final int EVENT_PICK = 3;

    private ItemSelectionSupport mItemSelection;
    private List<Account> mAccounts;
    private AccountViewAdapter mRecyclerAdapter;
    private DatamodelApi mDatamodelApi;
    private int mCounter;
    private ActionMode mActionMode;
    private Account mAccount;
    private int mDialogMode;

    public static AccountViewFragment newInstance() {
        return new AccountViewFragment();
    }

    public AccountViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mDatamodelApi = new DatamodelApi(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_account_view, container, false);
        updateToolbar();

        return rootView;
    }

    private void updateToolbar() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if(activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle("Account");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_account, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_account:
                showDialog(EVENT_ADD, null);
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
                if (mItemSelection.getChoiceMode() == ItemSelectionSupport.ChoiceMode.NONE) {
                    EventBus.getDefault().post(new AccountViewFragmentEvent(EVENT_PICK, position));
                } else {
                    if (mItemSelection.isItemChecked(position))
                        mCounter--;
                    else
                        mCounter++;
                    mActionMode.invalidate();
                }
            }

        });
//
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
        recyclerView.addItemDecoration(new DividerItemDecoration(divider));
        mItemSelection = ItemSelectionSupport.addTo(recyclerView);

        mRecyclerAdapter = new AccountViewAdapter(getActivity(), getData());
        recyclerView.setAdapter(mRecyclerAdapter);
    }

    private List<Account> getData() {
        mAccounts = mDatamodelApi.getAccounts();
        return mAccounts;


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
                    Account account = getCheckedItem();
                    if (account != null) {
                        mode.setTitle(account.getName());
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
                    mAccount = getCheckedItem();
                    showDialog(EVENT_UPDATE, mAccount);
                    mode.finish(); // Action picked, so close the CAB
                    return true;
                case R.id.action_mode_item_delete:
                    // delete the checked docs in database, adapter and in the fragment
                    for (Account current_checked_acc : getCheckedItems()) {
                        mDatamodelApi.delete(current_checked_acc);
                        mRecyclerAdapter.delete(current_checked_acc);
                        mAccounts.remove(current_checked_acc);
                    }
                    EventBus.getDefault().post(new AccountViewFragmentEvent(EVENT_DELETE));
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
        for(int i=0; i<mAccounts.size(); i++) {
            if(!mItemSelection.isItemChecked(i))
                mItemSelection.setItemChecked(i, true);
        }
        mCounter = mAccounts.size();
    }

    @NotNull
    private List<Account> getCheckedItems() {
        SparseBooleanArray checkArray = mItemSelection.getCheckedItemPositions();
        List<Account> checked_items = new ArrayList<>();
        for(int i=0; i< checkArray.size(); i++) {
            int position = checkArray.keyAt(i);
            if (checkArray.valueAt(i)) {
                checked_items.add(mAccounts.get(position));
            }
        }
        return checked_items;
    }

    private Account getCheckedItem() {
        SparseBooleanArray checkArray = mItemSelection.getCheckedItemPositions();
        for(int i=0; i< checkArray.size(); i++) {
            int position = checkArray.keyAt(i);
            if (checkArray.valueAt(i)) {
                return mAccounts.get(position);
            }
        }
        return null;
    }

    private void showDialog(int mode, Account mAccount) {
        mDialogMode = mode;
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        AddAccountDialogFragment dialogFragment;
        switch (mDialogMode) {
            case EVENT_ADD:
                dialogFragment = AddAccountDialogFragment.newInstance("Add account");
                dialogFragment.show(fragmentManager, "dialog");
                break;
            case EVENT_UPDATE:
                dialogFragment = AddAccountDialogFragment.newInstance("Change account", mAccount.getName(), mAccount.getEmail());
                dialogFragment.show(fragmentManager, "dialog");
                break;
        }

    }


    @SuppressWarnings("unused")
    public void onEvent(AddAccountDialogFragment.AddAccountDialogEvent event) {
        String name = event.name;
        String email = event.email;
        if (name != null) {
            switch (mDialogMode) {
                case EVENT_UPDATE:
                    int position = mAccounts.indexOf(mAccount);
                    Account acc = mDatamodelApi.setAccount(mAccount, name, email, null, null);
                    if (acc != null) {
                        mAccount = acc;
                        mRecyclerAdapter.update(position, mAccount);
                        EventBus.getDefault().post(new AccountViewFragmentEvent(EVENT_UPDATE));

                    } else {
                        Toast.makeText(getActivity(), "Account with name " + event.name + " already exist", Toast.LENGTH_LONG).show();
                    }
                    break;
                case EVENT_ADD:
                    if (mDatamodelApi.addAccount(name, email) != null) {
                        mAccounts = mDatamodelApi.getAccounts();
                        mRecyclerAdapter.setData(mAccounts);
                        EventBus.getDefault().post(new AccountViewFragmentEvent(EVENT_ADD));
                        Toast.makeText(getActivity(), "Added account \'" + event.name + "\'", Toast.LENGTH_LONG).show();
                    } else {//exists already
                        Toast.makeText(getActivity(), "Account with name " + event.name + " already exist", Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }
    }

    public class AccountViewFragmentEvent {
        public final int event;
        public int pos;

        public AccountViewFragmentEvent(int event) {
            this.event = event;
        }

        public AccountViewFragmentEvent(int event, int pos) {
            this.event = event;
            this.pos = pos;
        }
    }


}
