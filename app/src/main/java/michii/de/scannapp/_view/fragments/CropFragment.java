package michii.de.scannapp._view.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.soundcloud.android.crop.Crop;

import java.io.File;

import michii.de.scannapp.model.DatamodelApi;


/**
 * Functional fragment to crop a image using <a href="https://github.com/jdamcd/android-crop">android-crop</a>.
 * @author Michii
 * @since 28.05.2015
 */
public class CropFragment extends Fragment {

    public static final int EVENT_OK = 0;
    public static final int EVENT_CANCEL = 1;
    public static final int EVENT_ERROR = 2;
    private static final String TAG = CropFragment.class.getSimpleName();
    private static final String ARG_IMAGE_URI = "arg_image_uri";
    private static final String ARG_CROP_URI = "arg_crop_uri";
    private static final String CROP_IMAGE_URI = "crop_image_uri";
    private static final String CROP_CROP_URI = "crop_crop_uri";
    private OnFragmentInteractionListener mListener;
    private Uri mImageUri;
    private Uri mCropUri;
    private DatamodelApi mDatamodelApi;

    public CropFragment() {

    }

    /**
     * Constructs a new instance with given image Uri and the crop Uri.
     * If the cropUri is {@code null}, a new file must be created.
     * @param imageUri the uri of the image to crop
     * @param cropUri the uri of the cropped image
     * @return new instance
     */
    public static CropFragment newInstance(Uri imageUri, Uri cropUri) {
        CropFragment fragment = new CropFragment();
        Bundle args = new Bundle();
        args.putString(ARG_IMAGE_URI, imageUri.toString());
        if (cropUri != null) {
            args.putString(ARG_CROP_URI, cropUri.toString());
        }
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        mDatamodelApi = new DatamodelApi(getActivity());

        Uri imageUri = null;
        Uri cropUri = null;
        if (getArguments() != null) {
            imageUri = Uri.parse(getArguments().getString(ARG_IMAGE_URI));
            String cropUriString = getArguments().getString(ARG_CROP_URI);
            if (cropUriString != null) {
                cropUri = Uri.parse(cropUriString);
            }
        }

        startCropIntent(imageUri, cropUri);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * Starts the crop intent using the given image and crop uri.
     * If the image uri is {@code null} or the related file is {@code null} it returns doing nothing.
     * If the crop uri is {@code null} a new image file need to created.
     * @param imageUri the image's file uri to crop
     * @param cropUri the uri of the file to crop
     */
    private void startCropIntent(Uri imageUri, Uri cropUri) {
        File file;

        if (imageUri == null || mDatamodelApi.getFileFromUri(imageUri) == null) {
            mListener.onCropFinished(EVENT_ERROR, imageUri);
            return;
        }
        mImageUri = imageUri;

        if (cropUri == null) {
            file = mDatamodelApi.createTempImageFile("crop");
        } else {
            file = mDatamodelApi.getFileFromUri(mCropUri);
        }

        if (file != null) {
            //TODO hier besser, es kommt der Fehler ragmentActivity(4418): Activity result no fragment exists for index: 0x22d73
//            Crop.of(source, Uri.fromFile(file)).start(getActivity(), this);
            mCropUri = Uri.fromFile(file);
            Crop.of(mImageUri, mCropUri).start(getActivity());
        } else {
            Log.d(TAG, "Error by Creating Crop File");
        }
        Log.d("TEST", Integer.toString(getActivity().getSupportFragmentManager().getBackStackEntryCount()));

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            mListener.onCropFinished(EVENT_OK, mCropUri);
            mCropUri = null;
        } else if (resultCode == Activity.RESULT_CANCELED) {
            mCropUri = null;
            mListener.onCropFinished(EVENT_CANCEL, null);
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

        if (mImageUri != null) {
            outState.putString(CROP_IMAGE_URI, mImageUri.toString());
        }
        if (mCropUri != null) {
            outState.putString(CROP_CROP_URI, mCropUri.toString());
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            String imageUriString = savedInstanceState.getString(ARG_CROP_URI, null);

            if (imageUriString != null) {
                mImageUri = Uri.parse(imageUriString);
            }
            String cropUriString = savedInstanceState.getString(ARG_CROP_URI, null);
            if (cropUriString != null) {
                mCropUri = Uri.parse(cropUriString);
            }
        }

    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onCropFinished(int event, Uri uri);
    }

    public class CropFragmentEvent {
        public Uri uri;

        public CropFragmentEvent(Uri uri) {
            this.uri = uri;
        }
    }
}
