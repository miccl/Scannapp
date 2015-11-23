package michii.de.scannapp._view.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.github.alexkolpa.fabtoolbar.FabToolbar;
import com.soundcloud.android.crop.Crop;

import java.util.ArrayList;
import java.util.List;

import michii.de.scannapp.R;
import michii.de.scannapp._view.dialogs.AddDialogFragment;
import michii.de.scannapp._view.dialogs.CategoryPickerDialogFragment;
import michii.de.scannapp._view.dialogs.DeleteDialogFragment;
import michii.de.scannapp._view.dialogs.SharePickerDialogFragment;
import michii.de.scannapp._view.fragments.CameraFragment;
import michii.de.scannapp._view.fragments.CropFragment;
import michii.de.scannapp._view.fragments.EditFragment;
import michii.de.scannapp._view.fragments.ImageFragment;
import michii.de.scannapp._view.fragments.PictureViewFragment;
import michii.de.scannapp._view.fragments.PrintFragment;
import michii.de.scannapp.model.DatamodelApi;
import michii.de.scannapp.model.business_logic.conversion.ConcreteStrategy_Ocr_Text;
import michii.de.scannapp.model.business_logic.conversion.ConcreteStrategy_Pdf;
import michii.de.scannapp.model.business_logic.conversion.ConversionStrategy;
import michii.de.scannapp.model.business_logic.document.Account;
import michii.de.scannapp.model.business_logic.document.Attribute;
import michii.de.scannapp.model.business_logic.document.Category;
import michii.de.scannapp.model.business_logic.document.Conversion;
import michii.de.scannapp.model.business_logic.document.Document;
import michii.de.scannapp.model.business_logic.document.Picture;
import michii.de.scannapp.rest.logging.L;
import michii.de.scannapp.rest.utils.IntentUtils;
import michii.de.scannapp.rest.utils.MaterialUtil;

/**
 * Activity to edit and use the chosen document.
 */
public class DetailActivity extends BusActivity implements View.OnClickListener,
        EditFragment.OnFragmentInteractionListener, ImageFragment.OnFragmentInteractionListener,
        CameraFragment.OnFragmentInteractionListener, CropFragment.OnFragmentInteractionListener {

    private static final int PICTURE_NEW = 0;
    private static final int PICTURE_UPDATE = 1;
    private static final String TAG = DetailActivity.class.getSimpleName();
    private static final int MODE_ADD_NOTE = 0;
    private static final int MODE_ADD_CATEGORY = 1;
    private static final int MODE_RENAME_DOC = 2;
    private static final int MODE_RENAME_PIC = 3;
    private static final int MODE_EDIT_NOTE = 4;
    private static final String DOCUMENT_ID = "document_id";

    private int mAddMode;
    private Toolbar mToolbar;
    private FabToolbar mFabToolbar;
    private Document mDocument;
    private DatamodelApi mDatamodelApi;
    private int mEditMode;
    private Picture mPicture;
    private Uri mCameraUri;
    private Uri mCropUri;
    private Account mAccount;
    private String mCameraIntentTag;
    private Fragment mScanFragment;
    private Uri mEditUri;
    private PictureViewFragment mPictureViewFragment;
    private String mDocId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mDatamodelApi = new DatamodelApi(this);
        String doc_id = null;
        String acc_name = null;
        if (getIntent() != null) {
            Intent i = getIntent();
            doc_id = i.getExtras().getString("doc_id");
            Log.d(TAG, "doc_id: " + doc_id);
            acc_name = i.getExtras().getString("acc_name");
            Log.d(TAG, "acc_name: " + acc_name);

        }
        if (doc_id == null) {
            return;
        }
        mDocId = doc_id;
        initializeDocument();

        if (acc_name != null) {
            mAccount = mDatamodelApi.getAccountByName(acc_name);
        }
        mToolbar = MaterialUtil.setupToolbar(this, mDocument.getTitle());
        setupFabToolbar();
        setupPictureView();
        mDatamodelApi.setDefaultValues();

    }


    private void setupFabToolbar() {
        mFabToolbar = (FabToolbar) findViewById(R.id.fab_toolbar);
        mFabToolbar.setColor(getResources().getColor(R.color.colorAccent));
        mFabToolbar.setButtonIcon(R.drawable.ic_action_edit);
        mFabToolbar.setAnimationDuration(500);

        findViewById(R.id.pdf).setOnClickListener(this);
        findViewById(R.id.print).setOnClickListener(this);
        findViewById(R.id.note).setOnClickListener(this);
        findViewById(R.id.share).setOnClickListener(this);
        findViewById(R.id.add_photo).setOnClickListener(this);
    }

    private void setupPictureView() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        mPictureViewFragment = PictureViewFragment.newInstance(mDocument.getId());
        fragmentTransaction.replace(R.id.fragment_picture_view, mPictureViewFragment);
        fragmentTransaction.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.menu_item_rename:
                mAddMode = MODE_RENAME_DOC;
                FragmentManager fragmentManager = getSupportFragmentManager();
                AddDialogFragment newFragment = AddDialogFragment.newInstance("Rename Document", mDocument.getTitle());
                newFragment.show(fragmentManager, "dialog");
                return true;
            case R.id.menu_item_email:
                if (hasAttachedPictures(mDocument)) {
                    Uri pdf_uri = createPdf();
                    if (pdf_uri != null) {
                        if (mAccount != null && mAccount.getEmail() != null) {
                            IntentUtils.sendEmail(this, mDocument.getTitle(), pdf_uri, mAccount.getEmail());
                        } else {
                            Toast.makeText(this, "You need first configure a mail", Toast.LENGTH_LONG).show();
                        }
                    }
                }
                return true;
            case R.id.menu_item_assign_category:
                showCategoryPickDialog();
                return true;

            case R.id.menu_item_delete_document:
                mDatamodelApi.delete(mDocument);
                startActivity(new Intent(DetailActivity.this, MainActivity.class));
                return true;

            case R.id.menu_item_add_gallery:
                mEditMode = PICTURE_NEW;
                mCameraIntentTag = CameraFragment.GALLERY_INTENT_TAG;
                CameraFragment cameraFragment = CameraFragment.newInstance(CameraFragment.GALLERY_INTENT_TAG);
                startScanFragment(cameraFragment);
                return true;
//            case R.id.menu_item_note:
//                showAttributeAddDialog("Note");
//                return true;
            case R.id.menu_item_ocr_document:
                if (hasAttachedPictures(mDocument)) {
                    startOcr();
                }
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPictureViewFragment.updateData();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_photo:
                mEditMode = PICTURE_NEW;
                mCameraIntentTag = CameraFragment.CAMERA_INTENT_TAG;
                CameraFragment cameraFragment = CameraFragment.newInstance(CameraFragment.CAMERA_INTENT_TAG);
                startScanFragment(cameraFragment);
                break;
            case R.id.share:
                if (hasAttachedPictures(mDocument)) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    SharePickerDialogFragment sharePickerDialogFragment = SharePickerDialogFragment.newInstance();
                    sharePickerDialogFragment.show(fragmentManager, "dialog");
                }
                break;
            case R.id.fab_toolbar:

                mFabToolbar.show();
                break;
            case R.id.pdf:
                if (hasAttachedPictures(mDocument)) {
                    createAndViewPdf();
                }
                break;
            case R.id.print:
                if (hasAttachedPictures(mDocument)) {
                    startPrint();
                }
                break;
            case R.id.note:
                showAttributeAddDialog("Note");
                break;
        }

    }

    private boolean hasAttachedPictures(Document doc) {
        if (doc.getAttachedPictures().size() > 0) {
            return true;
        } else {
            L.To(this, getString(R.string.doc_no_pic));
            return false;
        }
    }

    private void startOcr() {
        String pref_key = getResources().getString(R.string.pref_key_ocr_language);
        String defaultValue = getResources().getString(R.string.pref_default_ocr_language);
        String lang = mDatamodelApi.readFromPreferences(pref_key, defaultValue);

        final ProgressDialog ocrProgessDialog = ProgressDialog.show(DetailActivity.this, "Please wait ...", "Converting(" + lang + ") ...", true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                ConversionStrategy strategy = new ConcreteStrategy_Ocr_Text(DetailActivity.this);
                final Uri ocr_uri = mDatamodelApi.doConvert(mDocument, strategy);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ocrProgessDialog.dismiss();
                        if (ocr_uri != null) {
                            L.t(DetailActivity.this, "The document was successfully converted.");
                            IntentUtils.viewText(DetailActivity.this, ocr_uri);
                        } else {
                            L.t(DetailActivity.this, "An error occurred while converting the document.");
                        }
                    }
                });
            }
        }).start();

    }

    private void createAndViewPdf() {
        final ProgressDialog pdfProgressDialog = ProgressDialog.show(DetailActivity.this, "Please wait ...", "Converting ...", true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Uri uri = createPdf();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pdfProgressDialog.dismiss();
                        if (uri != null) {
                            L.t(DetailActivity.this, getString(R.string.convert_pdf_positiv));
                            IntentUtils.viewPdf(DetailActivity.this, uri);
                        } else {
                            L.t(DetailActivity.this, getString(R.string.convert_pdf_negativ));
                        }
                    }
                });
            }
        }).start();
    }


    private void startPrint() {
        if (mDocument.getAttachedPictures().size() > 0) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment printFragment = PrintFragment.newInstance(mDocument.getId(), PrintFragment.PRINT_MODE_PDF);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(printFragment, "print");
            //fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else {
            Toast.makeText(this, R.string.doc_no_picture, Toast.LENGTH_LONG).show();
        }
    }

    private Uri createPdf() {
        ConversionStrategy strategy = new ConcreteStrategy_Pdf(this);
        return mDatamodelApi.doConvert(mDocument, strategy);
    }

    private void showAttributeAddDialog(String title) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        AddDialogFragment dialogFragment;
        Attribute attr = mDocument.getAttribute(title);
        if (attr == null) {
            mAddMode = MODE_ADD_NOTE;
            dialogFragment = AddDialogFragment.newInstance(title);
        } else {
            mAddMode = MODE_EDIT_NOTE;
            dialogFragment = AddDialogFragment.newInstance(title, attr.getValue());
        }
        dialogFragment.show(fragmentManager, "dialog");

    }

    private void showCategoryPickDialog() {
        mAddMode = MODE_ADD_CATEGORY;
        FragmentManager fragmentManager = getSupportFragmentManager();
        CategoryPickerDialogFragment categoryPickerDialogFragment = CategoryPickerDialogFragment.newInstance(mDocument.getId());
        categoryPickerDialogFragment.show(fragmentManager, "dialog");
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
                if (editUri != null) {
                    switch (mEditMode) {
                        case PICTURE_NEW:
                            mEditUri = editUri;
                            mDatamodelApi.addPic(mDocument, mEditUri, true);

                            break;
                        case PICTURE_UPDATE:
                            Picture pic = mDatamodelApi.setPicture(mPicture, mPicture.getTitle(), editUri);
                            if (pic != null) {
                                mPicture = pic;
                            }
                            break;
                    }
                    deleteCameraUri();
                    deleteCropUri();
                    if (mScanFragment != null) {
                        getSupportFragmentManager().beginTransaction().remove(mScanFragment).commit();
                    }
                    mPictureViewFragment.updateData();
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


        if (mScanFragment != null) {
            getSupportFragmentManager().beginTransaction().remove(mScanFragment).commit();
        }
        Toast.makeText(this, R.string.error_scan, Toast.LENGTH_LONG).show();

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


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mDocId != null) {
            outState.putString(DOCUMENT_ID, mDocId);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        if(savedInstanceState.getString(DOCUMENT_ID) != null) {
            mDocId = savedInstanceState.getString(DOCUMENT_ID);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @SuppressWarnings("unused")
    public void onEvent(AddDialogFragment.AddDialogFragmentEvent event) {
        String title = event.title;
        if (title != null) {
            switch (mAddMode) {
                case MODE_ADD_NOTE:
                    mDatamodelApi.addAttribute(mDocument, "Note", title);
                    break;
                case MODE_EDIT_NOTE:
                    Attribute attr = mDatamodelApi.setAttribute(mDocument, "Note", title);
                    if (attr != null) {
                        initializeDocument();
                    }
                    break;
                case MODE_ADD_CATEGORY:
                    mDatamodelApi.addCategory(mDocument, title);
                    showCategoryPickDialog();
                    break;
                case MODE_RENAME_DOC:
                    Document doc = mDatamodelApi.setDocument(mDocument, title);
                    if (doc != null) {
                        mDocument.setTitle(doc.getTitle());
                        mToolbar.setTitle(doc.getTitle());
                    } else {
                        Toast.makeText(this, R.string.name_in_use, Toast.LENGTH_LONG).show();
                    }
                    break;
                case MODE_RENAME_PIC:
                    Picture pic = mDatamodelApi.setPicture(mPicture, event.title, Uri.parse(mPicture.getUriString()));
                    if (pic != null) {
                        mPicture.setTitle(pic.getTitle());
                        initializeDocument();
                        mPictureViewFragment.updateData();
                    } else {
                        Toast.makeText(this, R.string.name_in_use, Toast.LENGTH_LONG).show();
                    }
            }
        }

    }

    private void initializeDocument() {
        mDocument =  mDatamodelApi.getDocumentById(mDocId);
    }


    @SuppressWarnings("unused")
    public void onEvent(PictureViewFragment.PictureViewEditEvent event) {
        if (event.pic != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction;
            switch (event.event) {
                case PictureViewFragment.EVENT_EDIT:
                    mEditMode = PICTURE_UPDATE;
                    mPicture = event.pic;
                    EditFragment editFragment = EditFragment.newInstance(Uri.parse(mPicture.getUriString()), EditFragment.TOOLS_WITH_CROP);
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container, editFragment);
                    fragmentTransaction.commit();
                    break;
                case PictureViewFragment.EVENT_PRINT:
                    Fragment printFragment = PrintFragment.newInstance(event.pic.getId(), PrintFragment.PRINT_MODE_PICTURE);
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.add(printFragment, "print");
                    fragmentTransaction.commit();
                    break;
                case PictureViewFragment.EVENT_DELETE:
                    mDatamodelApi.delete(event.pic);
                    initializeDocument();
                    break;
                case PictureViewFragment.EVENT_RENAME:
                    mAddMode = MODE_RENAME_PIC;
                    mPicture = event.pic;
                    AddDialogFragment newFragment = AddDialogFragment.newInstance(getString(R.string.rename_pic), mPicture.getTitle());
                    newFragment.show(fragmentManager, "dialog");
                    break;
            }
        }

    }

    private void startScanFragment(Fragment fragment) {
        mScanFragment = fragment;
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();
    }

    @SuppressWarnings("unused")
    public void onEvent(SharePickerDialogFragment.SharePickerDialogEvent event) {
        switch (event.item) {
            case SharePickerDialogFragment.PDF:
                if (hasAttachedPictures(mDocument)) {
                    Uri pdf_uri = createPdf();
                    if (pdf_uri != null) {
                        IntentUtils.sharePdf(this, mDocument.getTitle(), pdf_uri);
                    }
                }
                break;
            case SharePickerDialogFragment.JPG:
                ArrayList<Uri> uriList = new ArrayList<>();
                for (Picture pic : mDocument.getAttachedPictures()) {
                    Uri uri = Uri.parse(pic.getUriString());
                    uriList.add(uri);
                }
                IntentUtils.shareImageList(this, uriList);
                break;
            case SharePickerDialogFragment.OCR:
                if (hasAttachedPictures(mDocument)) {
                    Conversion ocr = mDocument.getConversion(ConcreteStrategy_Ocr_Text.TYPE_OCR);
                    if (ocr != null) {
                        Uri ocr_uri = Uri.parse(ocr.getUriString());
                        IntentUtils.shareText(this, mDocument.getTitle(), ocr_uri);
                    } else {
                        Toast.makeText(this, "You need first execute ocr", Toast.LENGTH_LONG).show();
                    }
                }
        }
    }

    @SuppressWarnings("unused")
    public void onEvent(CategoryPickerDialogFragment.CategoryPickerEvent event) {
        List<Integer> selected_indexes = event.selectedIndex;
        List<Category> categories = mDatamodelApi.getCategories();
        List<Category> old_selectedCategories = mDatamodelApi.getCategoriesByDocument(mDocument);

        List<Category> new_selected_categories = new ArrayList<>();
        for (Integer curr_pos : selected_indexes) {
            Category curr_cat = categories.get(curr_pos);
            new_selected_categories.add(curr_cat);
        }

        // look for all categories
        for (Category current_cat : categories) {
            // look if the current_cat was selected
            boolean old_selected = current_cat.partOf(old_selectedCategories);
            //look if the current_cat got selected
            boolean new_selected = current_cat.partOf(new_selected_categories);

            if (!old_selected && new_selected) { // if it wasnt selected before but is now, add the category to the document
                mDatamodelApi.addCategory(mDocument, current_cat.getTitle());
            } else if (old_selected && !new_selected) { //if it got deselected, remove the category from the document
                mDatamodelApi.removeCategory(mDocument, current_cat);
            }
        }
    }

    @SuppressWarnings("unused")
    public void onEvent(DeleteDialogFragment.DeleteDialogFragmentEvent event) {
    }




}
