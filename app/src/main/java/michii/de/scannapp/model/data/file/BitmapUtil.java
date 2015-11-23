package michii.de.scannapp.model.data.file;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;

import java.io.FileNotFoundException;

/**
 * Class who provides methods create and manipulate bitmaps.
 * @author Michii
 * @since 07.06.2015
 */
public class BitmapUtil {

    /**
     * Retrieve the bitmap of the image file specified by the given uri.
     * @param context the execution context
     * @param imageUri the image file's uri
     * @return the created bitmap
     * @throws FileNotFoundException
     */
    public static Bitmap getBitmap(Context context, Uri imageUri) throws FileNotFoundException {
        Bitmap bitmap = null;
        BitmapFactory.Options o = new BitmapFactory.Options();
        bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(imageUri), null, o);
        return bitmap;
    }


    /**
     * Reads the specified Bitmap from the ExternalStorage with given Uri and scales it down so that the resulting bitmap width nor height exceeds maxSize.
     * @param context the execution context
     * @param imageUri the file's uri
     * @param maxWidth the max width
     * @param maxHeight the max height
     * @return the scaled bitmap
     * @throws FileNotFoundException
     */
    public static Bitmap decodeFile(Context context, Uri imageUri, int maxWidth, int maxHeight) throws FileNotFoundException {
        Bitmap b = null;
        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;

        BitmapFactory.decodeStream(context.getContentResolver().openInputStream(imageUri), null, o);
        int scale = determineScaleFactor(o.outWidth, o.outHeight, maxWidth, maxHeight);

        // Decode with inSampleSize
        o.inSampleSize = scale;
        o.inJustDecodeBounds = false;
        o.inPreferredConfig = Bitmap.Config.RGB_565;
        b = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(imageUri), null, o);
        return b;
    }


    /**
     * Reads the specified Bitmap from the ExternalStorage and scales it down so that the resulting bitmap width nor height exceeds maxSize.
     * see also: <a href="http://stackoverflow.com/questions/477572/android-strange-out-of-memory-issue/823966#823966"> Stackoverflow </a>
     * @param imagePath the file's path on the externalStorage
     * @param maxHeight the maximal height
     * @param maxWidth the maximal width
     * @return the scaled bitmap
     */
    public static Bitmap decodeFile(String imagePath, int maxWidth, int maxHeight) {
        Bitmap b = null;
        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(imagePath, o);
        int scale = determineScaleFactor(o.outWidth, o.outHeight, maxWidth, maxHeight);

        // Decode with inSampleSize
        o.inSampleSize = scale;
        o.inJustDecodeBounds = false;
        o.inPreferredConfig = Bitmap.Config.RGB_565;
        b = BitmapFactory.decodeFile(imagePath, o);
        return b;
    }


    private static int determineScaleFactor(int w, int h, int maxWidth, int maxHeight) {
        int scale = 1;
        if (w > maxWidth || h > maxHeight) {
            scale = (int) Math.pow(2, (int) Math.round(Math.log(Math.max(maxWidth, maxHeight) / (double) Math.max(h, w)) / Math.log(0.5)));
        }
        return scale;
    }


    /**
     * Resizes given bitmap to the given width and height.
     * @param bm the bitmap to resize
     * @param newWidth the new width
     * @param newHeight  the new height
     * @return resized bitmap
     */
    public static Bitmap getResizedBitmap(Bitmap bm, float newWidth, float newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = (newWidth) / width;
        float scaleHeight = (newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);
        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }


    /**
     * Crops a given Bitmap to a given width and height
     * @param bitmap Bitmap to crop
     * @param w height of the Bitmap
     * @param h widht of the Bitmap
     * @return cropped Bitmap
     */
    public static Bitmap cropToSquare(Bitmap bitmap, int w, int h){
        int width  = w;
        int height = h;
        int newWidth = (height > width) ? width : height;
        int newHeight = (height > width)? height - ( height - width) : height;
        int cropW = (width - height) / 2;
        cropW = (cropW < 0)? 0: cropW;
        int cropH = (height - width) / 2;
        cropH = (cropH < 0)? 0: cropH;
        Bitmap cropImg = Bitmap.createBitmap(bitmap, cropW, cropH, newWidth, newHeight);

        return cropImg;
    }
}
