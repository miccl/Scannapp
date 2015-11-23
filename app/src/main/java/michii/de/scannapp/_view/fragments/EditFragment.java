package michii.de.scannapp._view.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.aviary.android.feather.sdk.AviaryIntent;
import com.aviary.android.feather.sdk.internal.Constants;
import com.aviary.android.feather.sdk.internal.filters.ToolLoaderFactory;
import com.aviary.android.feather.sdk.internal.headless.utils.MegaPixels;
import com.aviary.android.feather.sdk.utils.AviaryIntentConfigurationValidator;

import org.jetbrains.annotations.NotNull;

import java.io.File;

import michii.de.scannapp.model.DatamodelApi;

/**
 * Functional fragment to do image editing using <a href="https://creativesdk.adobe.com/docs/android/#/articles/imageediting/index.html">Adobe Creative SDK</a>.
 * @author Michii
 * @since 28.05.2015
 */
public class EditFragment extends Fragment {

    private static final String TAG = EditFragment.class.getSimpleName();
    public static final int TOOLS_WITHOUT_CROP = 0;
    public static final int TOOLS_WITH_CROP = 1;
    public static final int EVENT_OK = 0;
    public static final int EVENT_CANCEL = 1;
    public static final int EVENT_ERROR = 2;
    private static final int ACTION_REQUEST_FEATHER = 100;
    private static final String ARG_CROP_URI = "crop_uri";
    private static final String ARG_MODE = "arg_mode";
    private static final String EDIT_CROP_URI = "edit_crop_uri";
    private static final String EDIT_EDIT_URI = "edit_edit_uri";
    private OnFragmentInteractionListener mListener;
    private Uri mCropUri;
    private Uri mEditUri;
    private DatamodelApi mDatamodelApi;
    private int mMode;

    public EditFragment() {

    }

    /**
     * Constructs a new instance using the specified image uri  and mode.
     * The given uri specifies the image that should be edited .
     * The mode decides the presented editing tools.
     * Possible modes are {@link EditFragment#TOOLS_WITH_CROP} and {@link EditFragment#TOOLS_WITHOUT_CROP}.
     * @param uri the image file's uri
     * @param mode the tool mode
     * @return a new image with given image and mode
     */
    public static EditFragment newInstance(Uri uri, int mode) {
        EditFragment fragment = new EditFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CROP_URI, uri.toString());
        args.putInt(ARG_MODE, mode);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            mCropUri = Uri.parse(getArguments().getString(ARG_CROP_URI));
            mMode = getArguments().getInt(ARG_MODE);
        }
        mDatamodelApi = new DatamodelApi(getActivity());
        setupAviary();

        startEditingIntent();


    }

    /**
     * Setups the {@link AviaryIntent}.
     */
    private void setupAviary() {
        // pre-load the cds service
        Intent cdsIntent = AviaryIntent.createCdsInitIntent(getActivity().getBaseContext());
        getActivity().startService(cdsIntent);
//        printConfiguration();

        // verify the CreativeSDKImage configuration
        try {
            AviaryIntentConfigurationValidator.validateConfiguration(getActivity());
        } catch (Throwable e) {
            new AlertDialog.Builder(getActivity()).setTitle("Error")
                    .setMessage(e.getMessage()).show();
        }

    }


    /**
     * Starts the editing intent.
     * To saved the edited image a file must be created using {@link DatamodelApi}.
     * The specific intent is created by using {@link EditFragment#getEditIntent()}.
     */
    private void startEditingIntent() {
        File file;
        if (mCropUri == null || mDatamodelApi.getFileFromUri(mCropUri) == null) {
            mListener.onEditFinished(EVENT_ERROR, mCropUri);
            return;
        }

        if (mEditUri == null) {
            file = mDatamodelApi.createTempImageFile("edit");
        } else {
            file = mDatamodelApi.getFileFromUri(mEditUri);
        }

        if (file != null) {
            mEditUri = Uri.fromFile(file);
        } else {
            new AlertDialog.Builder(getActivity())
                    .setTitle(android.R.string.dialog_alert_title)
                    .setMessage("Failed to create a new File").show();
            return;
        }
        //newIntent.putExtra(Constants.EXTRA_TOOLS_LIST, tools);

        // ..and start feather
        startActivityForResult(getEditIntent(), ACTION_REQUEST_FEATHER);

    }

    /**
     * Creates a new edit intent with the given image to edit and the created edit file.
     * The tools are determined by using {@link EditFragment#getTools()}.
     * @return the new intent
     */
    @NotNull
    private Intent getEditIntent() {
        Intent newIntent = new AviaryIntent.Builder(getActivity())
                .setData(mCropUri) // input image src
                .withOutput(mEditUri) // output file
                .withOutputFormat(Bitmap.CompressFormat.JPEG) // output format
                .withOutputSize(MegaPixels.Mp5) // output size
                .withOutputQuality(100) // output quality
                .withNoExitConfirmation(true)
                .saveWithNoChanges(true)
//                .withPreviewSize(1024)
                .withToolList(getTools())
                .withVibrationEnabled(true)
                        //.quickLaunchTool(ToolLoaderFactory.Tools.CROP.toString(), null)
                .build();
        newIntent.putExtra(Constants.EXTRA_WHITELABEL, true);
        return newIntent;
    }

    /**
     * Retrieves the tools that are presented based on the specified mode.
     * @return the choosen tools
     */
    private ToolLoaderFactory.Tools[] getTools() {

        ToolLoaderFactory.Tools[] toolsWithoutCrop = new ToolLoaderFactory.Tools[]{
                //                ToolLoaderFactory.Tools.CROP,
                ToolLoaderFactory.Tools.ENHANCE,
                ToolLoaderFactory.Tools.ORIENTATION,
                ToolLoaderFactory.Tools.SHARPNESS,
                ToolLoaderFactory.Tools.LIGHTING,
                ToolLoaderFactory.Tools.COLOR,
                ToolLoaderFactory.Tools.FOCUS,
                ToolLoaderFactory.Tools.TEXT,
                ToolLoaderFactory.Tools.WHITEN,
                ToolLoaderFactory.Tools.EFFECTS,
//                ToolLoaderFactory.Tools.REDEYE,
//                ToolLoaderFactory.Tools.CROP,
                ToolLoaderFactory.Tools.DRAW,
                ToolLoaderFactory.Tools.SPLASH,
//                ToolLoaderFactory.Tools.STICKERS,
//                ToolLoaderFactory.Tools.BLEMISH,
                ToolLoaderFactory.Tools.MEME,
//                ToolLoaderFactory.Tools.ORIENTATION,
//                ToolLoaderFactory.Tools.FRAMES,
                ToolLoaderFactory.Tools.BLUR
//                ToolLoaderFactory.Tools.VIGNETTE,
//                ToolLoaderFactory.Tools.OVERLAYS
        };

        switch (mMode) {
            case TOOLS_WITHOUT_CROP:
                return toolsWithoutCrop;
            case TOOLS_WITH_CROP:
                ToolLoaderFactory.Tools[] toolsWithCrop = new ToolLoaderFactory.Tools[toolsWithoutCrop.length + 1];
                toolsWithCrop[0] = ToolLoaderFactory.Tools.CROP;
                for (int i = 0; i < toolsWithoutCrop.length; i++) {
                    toolsWithCrop[i + 1] = toolsWithoutCrop[i];
                }
                return toolsWithCrop;
            default:
                return toolsWithoutCrop;


        }
    }
    /**
     * This method is called when feather has completed ( ie. user clicked on "done" or just exit the activity without saving ).
     * <br />
     * If user clicked the "done" button you'll receive RESULT_OK as resultCode, RESULT_CANCELED otherwise.
     *
     * @param requestCode
     * 	- it is the code passed with startActivityForResult
     * @param resultCode
     * 	- result code of the activity launched ( it can be RESULT_OK or RESULT_CANCELED )
     * @param data
     * 	- the result data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case ACTION_REQUEST_FEATHER:
                    mListener.onEditFinished(EVENT_OK, mEditUri);
                    mEditUri = null;
//                    EventBus.getDefault().post(new EditFragmentEvent(mEditUri));
                    break;
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            switch (requestCode) {
                case ACTION_REQUEST_FEATHER:
                    // delete the result file, if exists
                    mEditUri = null;
                    mListener.onEditFinished(EVENT_CANCEL, null);
                    break;
            }
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mEditUri != null) {
            outState.putString(EDIT_EDIT_URI, mEditUri.toString());
        }
        if (mCropUri != null) {
            outState.putString(ARG_CROP_URI, mCropUri.toString());
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            String editUriString = savedInstanceState.getString(EDIT_EDIT_URI, null);
            if (editUriString != null) {
                mEditUri = Uri.parse(editUriString);
            }
            String cropUriString = savedInstanceState.getString(EDIT_CROP_URI, null);
            if (cropUriString != null) {
                mCropUri = Uri.parse(cropUriString);
            }
        }

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onEditFinished(int event, Uri uri);
    }

    public class EditFragmentEvent {
        public Uri uri;

        public EditFragmentEvent(Uri uri) {
            this.uri = uri;
        }
    }
}
