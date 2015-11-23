package michii.de.scannapp._view.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;

import michii.de.scannapp.model.data.file.BitmapUtil;

/**
 * Created by Michii on 05.06.2015.
 * Source: http://developer.android.com/training/displaying-bitmaps/process-bitmap.html
 * Funkt net
 */
public class BitmapWorkerTask extends AsyncTask<Uri, Integer, Bitmap> {

    private final WeakReference<ImageView> imageViewReference;
    private final int mSize;
    private Uri uri;
    private Context context;


    public BitmapWorkerTask(Context context, int size, ImageView imageView) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        imageViewReference = new WeakReference<>(imageView);
        mSize = size;
    }

    // Decode image in background.
    @Override
    protected Bitmap doInBackground(Uri... params) {
        uri = params[0];
        try {
            return BitmapUtil.decodeFile(context, uri, mSize, mSize);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (imageViewReference != null && bitmap != null) {
            final ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }





}
