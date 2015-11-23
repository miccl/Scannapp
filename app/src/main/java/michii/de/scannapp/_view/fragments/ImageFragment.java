package michii.de.scannapp._view.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;


/**
 * Functional fragment to do automatic image processing using <a href="http://docs.opencv.org/doc/tutorials/introduction/android_binary_package/O4A_SDK.html">OpenCV4Android</a>.
 * @author Michii
 * @since 12.06.2015
 */
public class ImageFragment extends Fragment {

    private static final String TAG = CropFragment.class.getSimpleName();
    private static final String ARG_IMAGE_URI = "arg_image_uri";
    private OnFragmentInteractionListener mListener;
    private Uri mImageUri;
    private Uri mCropUri;

    public ImageFragment() {

    }

    /**
     * Constructs a new instance using the specified image uri.
     * @param uri the image to process
     * @return a new instance with the given uri
     */
    public static ImageFragment newInstance(Uri uri) {
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_IMAGE_URI, uri.toString());
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mImageUri = Uri.parse(getArguments().getString(ARG_IMAGE_URI));
        }

        if(mImageUri != null) {
            startImageProcessing(mImageUri);
        }


    }

    private void startImageProcessing(Uri mImageUri) {
        //Correct Perspective
        // Correct
        // Search document
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
        void onImageFinished(Uri uri);
    }

    public class ImageFragmentEvent {
        public Uri uri;

        public ImageFragmentEvent(Uri uri) {
            this.uri = uri;
        }
    }
}
