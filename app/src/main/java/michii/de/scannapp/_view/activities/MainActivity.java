package michii.de.scannapp._view.activities;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.soundcloud.android.crop.Crop;

import java.util.ArrayList;
import java.util.List;

import michii.de.scannapp.R;
import michii.de.scannapp._view.dialogs.AddPageDialogFragment;
import michii.de.scannapp._view.fragments.AccountViewFragment;
import michii.de.scannapp._view.fragments.CameraFragment;
import michii.de.scannapp._view.fragments.CategoryViewFragment;
import michii.de.scannapp._view.fragments.CropFragment;
import michii.de.scannapp._view.fragments.DocumentViewFragment;
import michii.de.scannapp._view.fragments.EditFragment;
import michii.de.scannapp._view.fragments.FolderViewFragment;
import michii.de.scannapp._view.fragments.ImageFragment;
import michii.de.scannapp._view.provider.SearchRecentSuggestionProvider;
import michii.de.scannapp.model.DatamodelApi;
import michii.de.scannapp.model.business_logic.document.Account;
import michii.de.scannapp.model.business_logic.document.Document;
import michii.de.scannapp.rest.logging.L;

/**
 * Activity to start the scan process and navigate to the other activities.
 *
 */
public class MainActivity extends BusActivity implements View.OnClickListener,
        CameraFragment.OnFragmentInteractionListener, ImageFragment.OnFragmentInteractionListener,
        CropFragment.OnFragmentInteractionListener, EditFragment.OnFragmentInteractionListener {


    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int PROFILE_ITEM_MANAGE = 2;
    private static final int DRAWER_ITEM_RECENT = 0;
    private static final int DRAWER_ITEM_MONTHLY = 1;
    private static final int DRAWER_ITEM_CATEGORY = 2;
    private static final int DRAWER_ITEM_SETTINGS = 3;
    private static final int DRAWER_ITEM_ABOUT = 4;

    private Toolbar mToolbar;
    private FloatingActionsMenu fab_menu;
    private AccountHeader mAccountHeader;
    private IProfile mActiveProfil;

    private DatamodelApi mDatamodelApi;
    private Document mDocument;

    private String mCameraIntentTag;
    private Uri mCameraUri;
    private Uri mCropUri;
    private Uri mEditUri;

    private Fragment mViewFragment;
    private Fragment mScanFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatamodelApi = new DatamodelApi(this);

        if (getIntent() != null) {
            handleIntent(getIntent());
        }

        setupSettings();
        setupToolbar();
        setupFAB();
        setupDrawer();
        startLayoutFragment(DocumentViewFragment.newInstance(DocumentViewFragment.DATA_TYPE_RECENT, null));
//        setupDrawer();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mViewFragment instanceof DocumentViewFragment) {
            ((DocumentViewFragment) mViewFragment).updateData();
        }

    }


    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    SearchRecentSuggestionProvider.AUTHORITY, SearchRecentSuggestionProvider.MODE);
            suggestions.saveRecentQuery(query, null);
            doSearch(query);
        }
    }

    /**
     * Constructs a new {@link DocumentViewFragment} using {@link DocumentViewFragment#DATA_TYPE_SEARCH} with the given query.
     *
     * @param query the query to search
     */
    private void doSearch(String query) {
        startLayoutFragment(DocumentViewFragment.newInstance(DocumentViewFragment.DATA_TYPE_SEARCH, query));
        Log.d(TAG, "Search " + query);
    }

    /**
     * Setups the Toolbar.
     */
    private void setupToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    /**
     * Setups the NavigationDrawer.
     */
    private void setupDrawer() {
        buildHeader();

        new DrawerBuilder()
                .withActivity(this)
                .withToolbar(mToolbar)
                .withAccountHeader(mAccountHeader)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_item_recent).withIcon(R.drawable.ic_action_action_restore).withIdentifier(DRAWER_ITEM_RECENT),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_monthly).withIcon(R.drawable.ic_action_action_today).withIdentifier(DRAWER_ITEM_MONTHLY),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_category).withIcon(R.drawable.ic_action_maps_local_offer).withIdentifier(DRAWER_ITEM_CATEGORY),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_settings).withIcon(R.drawable.ic_action_action_settings).withIdentifier(DRAWER_ITEM_SETTINGS),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_about).withIcon(R.drawable.ic_action_action_info).withIdentifier(DRAWER_ITEM_ABOUT)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(AdapterView<?> adapterView, View view, int i, long l, IDrawerItem iDrawerItem) {
                        if (iDrawerItem != null && iDrawerItem instanceof Nameable) {
                            switch (iDrawerItem.getIdentifier()) {
                                case DRAWER_ITEM_MONTHLY:
                                    startLayoutFragment(FolderViewFragment.newInstance());
                                    break;
                                case DRAWER_ITEM_RECENT:
                                    startLayoutFragment(DocumentViewFragment.newInstance(DocumentViewFragment.DATA_TYPE_RECENT, null));
                                    break;
                                case DRAWER_ITEM_CATEGORY:
                                    startLayoutFragment(CategoryViewFragment.newInstance());
                                    break;
                                case DRAWER_ITEM_SETTINGS:
                                    startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                                    break;
                                case DRAWER_ITEM_ABOUT:
                                    startActivity(new Intent(MainActivity.this, AboutActivity.class));
                                    break;
                            }
                        }
                        return false;
                    }
                })
                .withAnimateDrawerItems(true)
                .build();
    }


    /**
     * Builds the header for the NavigationDrawer.
     */
    private void buildHeader() {

        List<IProfile> profiles = getAccounts();

        mAccountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(profiles.toArray(new IProfile[profiles.size()]))
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                        //sample usage of the onProfileChanged listener
                        //if the clicked item has the identifier 1 add a new profile ;)
                        if (profile instanceof IDrawerItem && profile.getIdentifier() == PROFILE_ITEM_MANAGE) {
                            AccountViewFragment newFragment = AccountViewFragment.newInstance();
                            startLayoutFragment(newFragment);
                        } else if (profile instanceof IDrawerItem) {
                            mActiveProfil = profile;
                            L.T(MainActivity.this, "Account \'" + mActiveProfil.getName() + "\' selected");
                        }

                        //false if you have not consumed the event and it should close the drawer
                        return false;
                    }
                })
                .build();
    }

    /**
     * Retrieves currently saved accounts using the {@link DatamodelApi}.
     *
     * @return list of saved accounts
     */
    private ArrayList<IProfile> getAccounts() {
        List<Account> accounts = mDatamodelApi.getAccounts();
        IProfile manage = new ProfileSettingDrawerItem().withName(getString(R.string.manage_accounts)).withIcon(GoogleMaterial.Icon.gmd_settings).withIdentifier(PROFILE_ITEM_MANAGE);

        ArrayList<IProfile> profiles = new ArrayList<>();
        if (accounts.size() > 0) { //Create empty account
            for (int i = 0; i < accounts.size(); i++) {
                Account currentAccount = accounts.get(i);
                String name = currentAccount.getName();
                String email = currentAccount.getEmail();
                profiles.add(new ProfileDrawerItem().withName(name).withEmail(email));
            }
        }
        profiles.add(manage);

        return profiles;
    }


    /**
     * Setups the Floating Action Button
     */
    private void setupFAB() {
        fab_menu = (FloatingActionsMenu) findViewById(R.id.fab_menu);
        FloatingActionButton fab_action_photo = (FloatingActionButton) findViewById(R.id.fab_action_photo);
        FloatingActionButton fab_action_gallery = (FloatingActionButton) findViewById(R.id.fab_action_gallery);
        fab_action_photo.setOnClickListener(this);
        fab_action_gallery.setOnClickListener(this);
        fab_menu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
//                mFragmentView.setAlpha(0.2f);
            }

            @Override
            public void onMenuCollapsed() {
//                mFragmentView.setAlpha(1f);

            }
        });

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            handleIntent(intent);
        }
    }

    private void setupSettings() {
        mDatamodelApi.setDefaultValues();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_action_photo:
                mDocument = mDatamodelApi.addDocWithoutSave();
                mCameraIntentTag = CameraFragment.CAMERA_INTENT_TAG;
                break;
            case R.id.fab_action_gallery:
                mDocument = mDatamodelApi.addDocWithoutSave();
                mCameraIntentTag = CameraFragment.GALLERY_INTENT_TAG;
                break;
        }
        CameraFragment cameraFragment = CameraFragment.newInstance(mCameraIntentTag);
        startScanFragment(cameraFragment);
        fab_menu.toggle();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Crop.REQUEST_CROP) {
            if (mScanFragment != null && mScanFragment instanceof CropFragment) {
                mScanFragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    /**
     * Starts the LayoutFragment using {@link FragmentTransaction} and {@link FragmentManager}.
     *
     * @param fragment the fragment to start
     */
    private void startLayoutFragment(Fragment fragment) {
        mViewFragment = fragment;
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_view, fragment);
        fragmentTransaction.commit();
    }


    private void startScanFragment(Fragment fragment) {
        mScanFragment = fragment;
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, mScanFragment);
        fragmentTransaction.commit();
    }


    @Override
    public void onCameraFinished(int event, Uri cameraUri) {
        switch (event) {
            case CameraFragment.EVENT_OK:
                if (cameraUri != null) {
                    mCameraUri = cameraUri;
                    startScanFragment(CropFragment.newInstance(mCameraUri, null));
                } else {
                    showScanError();
                }
                break;
            case CameraFragment.EVENT_CANCEL:
                deleteCameraUri();
                mDatamodelApi.delete(mDocument);
                break;
            case CameraFragment.EVENT_ERROR:
                showScanError();
                break;
        }
    }

    @Override
    public void onImageFinished(Uri uri) {

    }

    @Override
    public void onCropFinished(int event, Uri cropUri) {
        Log.d(TAG, "Crop-Event: " + event);
        switch (event) {
            case CropFragment.EVENT_OK:
                if (cropUri != null) {
                    mCropUri = cropUri;
                    startScanFragment(EditFragment.newInstance(cropUri, EditFragment.TOOLS_WITHOUT_CROP));
                } else {
                    showScanError();
                }
                break;
            case CropFragment.EVENT_CANCEL:
                deleteCameraUri();
                deleteCropUri();
                startScanFragment(CameraFragment.newInstance(mCameraIntentTag));
                break;
            case CropFragment.EVENT_ERROR:
                showScanError();
                break;
        }
    }


    @Override
    public void onEditFinished(int event, Uri editUri) {
        switch (event) {
            case EditFragment.EVENT_OK:
                if (editUri != null && mDocument != null) {
                    mEditUri = editUri;
                    mDatamodelApi.addPic(mDocument, mEditUri, false);
                    deleteCameraUri();
                    deleteCropUri();
                    if (mScanFragment != null) {
                        getSupportFragmentManager().beginTransaction().remove(mScanFragment).commit();
                    }
                    showAddPageDialog(mDocument.getAttachedPictures().size());
                } else {
                    showScanError();
                }
                break;
            case EditFragment.EVENT_CANCEL:
                if (mCameraUri != null && mCropUri != null) {
                    deleteEditUri(editUri);
                    startScanFragment(CropFragment.newInstance(mCameraUri, mCropUri));
                } else {
                    showScanError();
                }
                break;
            case EditFragment.EVENT_ERROR:
                showScanError();
                break;
        }
    }

    private void showScanError() {
        deleteCameraUri();
        deleteCropUri();

        if (mEditUri != null) {
            mDatamodelApi.deleteFile(mEditUri);
        }

        deleteDocument();

        if (mScanFragment != null) {
            getSupportFragmentManager().beginTransaction().remove(mScanFragment).commit();
        }
        L.t(this, getString(R.string.error));

    }

    private void deleteDocument() {
        if (mDocument != null) {
            mDatamodelApi.delete(mDocument);
            mDocument = null;
        }
    }

    private void deleteEditUri(Uri editUri) {
        if (mEditUri != null) {
            mDatamodelApi.deleteFile(editUri);
            mEditUri = null;
        }
    }

    private void deleteCropUri() {
        if (mCropUri != null) {
            mDatamodelApi.deleteFile(mCropUri);
            mCropUri = null;
        }
    }

    private void deleteCameraUri() {
        if (mCameraUri != null && mCameraIntentTag.equals(CameraFragment.CAMERA_INTENT_TAG)) {
            mDatamodelApi.deleteFile(mCameraUri);
        }
        mCameraUri = null;
    }


    @SuppressWarnings("unused")
    public void onEvent(CameraFragment.CameraFragmentEvent event) {
    }

    @SuppressWarnings("unused")
    public void onEvent(ImageFragment.ImageFragmentEvent event) {
    }

    @SuppressWarnings("unused")
    public void onEvent(CropFragment.CropFragmentEvent event) {
    }

    @SuppressWarnings("unused")
    public void onEvent(EditFragment.EditFragmentEvent event) {
    }


    private void showAddPageDialog(int page_number) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        AddPageDialogFragment addPageDialogFragment = AddPageDialogFragment.newInstance(page_number);
        addPageDialogFragment.show(fragmentManager, "missiles");
    }


    @SuppressWarnings("unused")
    public void onEvent(AddPageDialogFragment.AddPageDialogEvent event) {
        if (event.addPage) { //User clicked Add Page, start the scanprocess again
            mCameraIntentTag = CameraFragment.CAMERA_INTENT_TAG;
            CameraFragment cameraFragment = CameraFragment.newInstance(mCameraIntentTag);
            startScanFragment(cameraFragment);
        } else { //User clicked finished, saveWithPictures
            final ProgressDialog pdfProgressDialog = ProgressDialog.show(MainActivity.this, "Please wait ...", "Saving document ...", true);
            pdfProgressDialog.setCancelable(false);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final Document doc = mDatamodelApi.saveWithPictures(mDocument);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pdfProgressDialog.dismiss();
                            if (doc != null) {
                                Toast.makeText(MainActivity.this, R.string.saved_successfully, Toast.LENGTH_LONG).show();
                                startDetailActivity(mDocument.getId());
                            } else {
                                Toast.makeText(MainActivity.this, "An error occured while saving the document.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }).start();
        }
    }


    private void startDetailActivity(String doc_id) {
        if (doc_id != null) {
            Intent i = new Intent(this, DetailActivity.class);
            i.putExtra("doc_id", doc_id);
            if (mActiveProfil != null && !mActiveProfil.getName().equals(getString(R.string.manage_accounts))) {
                i.putExtra("acc_name", mActiveProfil.getName());
            }
            startActivity(i);
        } else {
            L.t(this, getString(R.string.error));
        }
    }

    @SuppressWarnings("unused")
    public void onEvent(DocumentViewFragment.DocumentViewFragmentEvent event) {
        startDetailActivity(event.doc_id);
    }

    @SuppressWarnings("unused")
    public void onEvent(AccountViewFragment.AccountViewFragmentEvent event) {
        switch (event.event) {
            case AccountViewFragment.EVENT_ADD:
            case AccountViewFragment.EVENT_UPDATE:
            case AccountViewFragment.EVENT_DELETE:
                mAccountHeader.setProfiles(getAccounts());
                //Reactivate the account that was activated before
                mAccountHeader.setActiveProfile(mActiveProfil);
                break;
            case AccountViewFragment.EVENT_PICK:
                mActiveProfil = mAccountHeader.getProfiles().get(event.pos);
                mAccountHeader.setActiveProfile(mActiveProfil);
                L.t(this, "Account \'" + mActiveProfil.getName() + "\' selected");
//                startLayoutFragment(DocumentViewFragment.newInstance(DocumentViewFragment.DATA_TYPE_RECENT, null));
                break;
        }
    }

}
