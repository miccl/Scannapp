package michii.de.scannapp._view.fragments;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import java.io.File;

import michii.de.scannapp.model.DatamodelApi;

/**
 * Functional fragment to select a image.
 * It supports to capture a image using the camera or to select a image from gallery.
 */
public class CameraFragment extends Fragment {

    public static final String GALLERY_INTENT_TAG = "gallery_intent";
    public static final String CAMERA_INTENT_TAG = "camera_intent";
    public static final int EVENT_OK = 0;
    public static final int EVENT_CANCEL = 1;
    public static final int EVENT_ERROR = 2;
    private static final String TAG = CameraFragment.class.getSimpleName();
    private static final String INTENT = "intent";
    private static final int REQUEST_CAMERA_INTENT = 0;
    private static final int PHOTO_GALLERY_FRAGMENT = 1;
    private static final String CAPTURED_PHOTO_URI_KEY = "capture_photo_uri_key";
    private OnFragmentInteractionListener mListener;
    private Uri mCameraUri;
    private String mIntent;
    private DatamodelApi mDatamodelApi;


    public CameraFragment() {
        // Required empty public constructor
    }

    /**
     * Constructs a new instance using the specified mode to pick the image.
     * Possible modes are {@link CameraFragment#CAMERA_INTENT_TAG} and {@link CameraFragment#GALLERY_INTENT_TAG}.
     * @param mode the choosen mode to pick the image
     * @return new instance
     */
    public static CameraFragment newInstance(String mode) {
        CameraFragment fragment = new CameraFragment();
        Bundle args = new Bundle();
        args.putString(INTENT, mode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            mIntent = getArguments().getString(INTENT);
        }
        mDatamodelApi = new DatamodelApi(getActivity());
        if (mIntent != null) {
            switch (mIntent) {
                case CAMERA_INTENT_TAG:
                    startCameraIntent();
                    break;
                case GALLERY_INTENT_TAG:
                    startGalleryIntent();
                    break;
            }
        } else {
            mListener.onCameraFinished(EVENT_ERROR, null);
        }

    }

    /**
     * Starts a new camera intent using {@link Intent} with {@link MediaStore#ACTION_IMAGE_CAPTURE}.
     * Returns doing nothing if the device has no integrated camera.
     * To store the image a new image file is created using {@link DatamodelApi}.
     */
    private void startCameraIntent() {
        //Check if there is a camera.
        Context context = getActivity(); //Wenn im Fragment dann get Activity()
        PackageManager packageManager = context.getPackageManager();
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Toast.makeText(context, "This device does not have a camera.", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        try {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = mDatamodelApi.createTempImageFile("cam");
                if (photoFile != null) {
                    mCameraUri = Uri.fromFile(photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCameraUri);
                    startActivityForResult(takePictureIntent, REQUEST_CAMERA_INTENT);
                } else {
                    Toast toast = Toast.makeText(context, "There was a problem saving the photo...", Toast.LENGTH_SHORT);
                    toast.show();
                    mCameraUri = null;
                }
            }
        } catch (ActivityNotFoundException anfe) {
            //display an error message
            String errorMessage = "Whoops - your device doesn't support capturing";
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Starts a new gallery intent using {@link Intent} with {@link Intent#ACTION_GET_CONTENT}.
     */
    private void startGalleryIntent() {
        mCameraUri = null;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setType("image/png,image/jpg, image/jpeg");
        Intent chooser = Intent.createChooser(intent, "Open with");
        startActivityForResult(chooser, PHOTO_GALLERY_FRAGMENT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CAMERA_INTENT:
                    break;
                case PHOTO_GALLERY_FRAGMENT:
                    mCameraUri = data.getData();
                    break;
            }
            mListener.onCameraFinished(EVENT_OK, mCameraUri);
            mCameraUri = null;
        } else if (resultCode == Activity.RESULT_CANCELED) {
            mCameraUri = null;
            mListener.onCameraFinished(EVENT_CANCEL, null);
        }
    }

    //TODO see the android tutorial http://developer.android.com/training/camera/photobasics.html and http://labs.makemachine.net/2010/03/simple-android-photo-capture/
    // Some lifecycle callbacks so that the image can survive orientation change
    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mCameraUri != null) {
            outState.putString(CAPTURED_PHOTO_URI_KEY, mCameraUri.toString());
        }

        super.onSaveInstanceState(outState);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            String camerUriString = savedInstanceState.getString(CAPTURED_PHOTO_URI_KEY, null);
            if (camerUriString != null) {
                mCameraUri = Uri.parse(camerUriString);
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


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onCameraFinished(int event, Uri uri);
    }

    public class CameraFragmentEvent {
        public Uri uri;

        public CameraFragmentEvent(Uri uri) {
            this.uri = uri;
        }
    }
}
