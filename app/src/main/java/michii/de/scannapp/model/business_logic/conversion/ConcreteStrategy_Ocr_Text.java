package michii.de.scannapp.model.business_logic.conversion;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import michii.de.scannapp.R;
import michii.de.scannapp.model.business_logic.document.Conversion;
import michii.de.scannapp.model.business_logic.document.Document;
import michii.de.scannapp.model.business_logic.document.Picture;
import michii.de.scannapp.model.data.file.DateUtil;
import michii.de.scannapp.model.data.file.FileHandler;
import michii.de.scannapp.model.data.file.FileUtil;
import michii.de.scannapp.model.data.settings.SettingsHandler;

/**
 * Concrete Strategy to execute text recognition on a document with Tesseract-Ocr.
 * Most of the code is taken from {@link <a href="http://gaut.am/making-an-ocr-android-app-using-tesseract/"> gaut.am</a>}
 *
 * @author Michii
 * @since 28.06.2015
 */
public class ConcreteStrategy_Ocr_Text extends ConversionStrategy {

    public static final String TYPE_OCR = "ocr";
    private static final String TAG = ConcreteStrategy_Ocr_Text.class.getSimpleName();
    private final FileHandler mFileHandler;
    private String mLanguage;
    private SettingsHandler mSettingsHandler;

    /**
     * Constructs a new ConcreteStrategy_Ocr_Text using specified context.
     * @param context the executing context
     */
    public ConcreteStrategy_Ocr_Text(Context context) {
        super(context);
        mFileHandler = new FileHandler(context);
        mSettingsHandler = new SettingsHandler(context);
    }

    /**
     * Returns the created {@link Conversion} from the text recognition of the given document.
     * @param document the document to convert
     * @return Conversion, if the document gets converted, otherwise {@code null}
     */
    @Override
    public Conversion convert(Document document) {

        Conversion conversion = getConversion(document);
//        mLanguage = getLanguage();
        mLanguage = getLanguage();
        if (setupTrainedData()) {
            String ocr_text = createOcr(document);
            if (ocr_text != null) {
                File ocr_file = mFileHandler.writeText(ocr_text, Uri.parse(conversion.getUriString()));
                if (ocr_file != null) {
                    conversion.setUriString(Uri.fromFile(ocr_file).toString());
                    conversion.setSize(ocr_file.length());
                    conversion.setDate(ocr_file.lastModified());
                    return conversion;
                }
            }
        }
        return null;
    }

    /**
     * Returns the conversion of the given document.
     * If the document has no ocr-conversion, a new conversion gets created.
     * @param document the document to convert
     * @return the new created conversion, if the document has no attached ocr-conversion, otherwise the existing ocr-conversion.
     */
    private Conversion getConversion(Document document) {
        Conversion conv = document.getConversion(TYPE_OCR);
        if (conv != null) {
            return conv;
        }
        return addNewConversion(document);
    }

    /**
     * Creates a new {@link Conversion} using the specified document information's.
     * @param document the document to convert
     * @return created conversion
     */
    private Conversion addNewConversion(Document document) {
        String title = document.getTitle();
        String type = TYPE_OCR;
        File file = FileHandler.createOcrFile(document.getTitle());
        String uriString = Uri.fromFile(file).toString();
        long date = DateUtil.getCurrentDate();

        return new Conversion(title, type, date, uriString, 0);
    }

    /**
     * Returns the language which is saved in SharedPreferences.
     * The default language is english.
     * @return specified language
     */
    private String getLanguage() {
        String pref_key = getContext().getResources().getString(R.string.pref_key_ocr_language);
        String defaultValue = getContext().getResources().getString(R.string.pref_default_ocr_language);
        return mSettingsHandler.readFromPreferences(pref_key, defaultValue);
    }

    /**
     * Creates the recognized text from the attached pictures of the given document.
     * TessBaseAPI is used for text recognition
     * @param document the document to convert
     * @return string of the recognized text
     */
    private String createOcr(Document document) {
        //get the path
        List<Picture> pics = document.getAttachedPictures();
        String[] texts = new String[pics.size()];

        TessBaseAPI mTessBaseApi = new TessBaseAPI();
        mTessBaseApi.setDebug(true);
        mTessBaseApi.init(FileHandler.EXTERNAL_APP_DIRECTORY, mLanguage);

        for (Picture pic : pics) {
            File file = mFileHandler.getFile(Uri.parse(pic.getUriString()));
            Bitmap bitmap = getBitmap(file.getAbsolutePath());
            String recognizedText = getOcrText(mTessBaseApi, bitmap);
            texts[pics.indexOf(pic)] = recognizedText;
        }

        mTessBaseApi.end();

//        String finalText = "Ocr (" + mLanguage + ") \n";
        String finalText = "";
        for (String text : texts) {
            finalText += text;
            finalText += "\n";
        }
        return finalText;

    }

    private String getOcrText(TessBaseAPI mTessBaseApi, Bitmap bitmap) {
        mTessBaseApi.setImage(bitmap);
        String recognizedText = mTessBaseApi.getUTF8Text();
        if (mLanguage.equalsIgnoreCase("eng")) {
            recognizedText = recognizedText.replaceAll("[^a-zA-Z0-9]+", " ");
        }
        recognizedText = recognizedText.trim();
        return recognizedText;
    }

    /**
     * Creates the bitmap from the specified file path.
     * @param path the file path where the image is stored
     * @return the created bitmap
     */
    private Bitmap getBitmap(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        bitmap = rotateBitmap(bitmap, path);
        // Convert to ARGB_8888, required by tess
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        return bitmap;
    }

    /**
     * Creates the need trainedata file in ExternalStorage.
     * @return {@code true} if trainedata got created, otherwise {@code false}
     */
    private boolean setupTrainedData() {
        File dir = FileUtil.createFolder(FileHandler.EXTERNAL_APP_DIRECTORY + File.separator + "tessdata");
        if (dir == null) {
            return false;
        }
        if (new File(dir.getAbsolutePath() + File.separator + mLanguage + ".traineddata").exists()) {
            return true;
        }

        try {
            AssetManager assetManager = getContext().getAssets();
            InputStream in = assetManager.open("tessdata/" + mLanguage + ".traineddata");
            //GZIPInputStream gin = new GZIPInputStream(in);
            OutputStream out = new FileOutputStream(dir.getAbsolutePath() + File.separator + mLanguage + ".traineddata");

            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            //while ((lenf = gin.read(buff)) > 0) {
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            //gin.close();
            out.close();
            Toast.makeText(getContext(), "Copied " + mLanguage + " traineddata", Toast.LENGTH_LONG).show();
            return true;
        } catch (IOException e) {
            Log.d(TAG, "Was unable to copy " + mLanguage + " traineddata " + e.toString());
        }
        return false;
    }

    /**
     * Rotates the bitmap to match the image rotation
     * @param bitmap the bitmap to rotate
     * @param path the file path of the image
     * @return the rotated bitmap
     */
    private Bitmap rotateBitmap(Bitmap bitmap, String path) {
        try {
            ExifInterface exif = new ExifInterface(path);
            int exifOrientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            Log.v(TAG, "Orient: " + exifOrientation);

            int rotation = 0;

            switch (exifOrientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotation = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotation = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotation = 270;
                    break;

            }

            Log.v(TAG, "Rotation: " + rotation);

            if (rotation != 0) {

                // Getting width & height of the given image.
                int w = bitmap.getWidth();
                int h = bitmap.getHeight();

                // Setting pre rotate
                Matrix mtx = new Matrix();
                mtx.preRotate(rotation);

                // Rotating Bitmap
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
            }


        } catch (IOException e) {
            Log.e(TAG, "Couldn't correct orientation: " + e.toString());
        }
        return bitmap;
    }
}
