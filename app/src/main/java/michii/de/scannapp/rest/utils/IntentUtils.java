package michii.de.scannapp.rest.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import java.util.ArrayList;

import michii.de.scannapp.model.business_logic.document.Picture;

/**
 * Provides several intent usages.
 * @author Michii
 * @since 26.06.2015
 */
public class IntentUtils {

    /**
     * Starts a image view intent from the given context using the specified attributes.
     * @param context the execution context
     * @param uri the image file's uri
     */
    public static void viewImage(Context context, Uri uri) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "image/*");
        context.startActivity(intent);
    }

    /**
     * Starts a pdf view intent from the given context using the specified attributes.
     * @param context the execution context
     * @param uri the pdf file's uri
     */
    public static void viewPdf(Context context, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intent);
    }

    /**
     * Starts a text view intent from the given context using the specified attributes.
     * @param context the execution context
     * @param uri the text file's uri
     */
    public static void viewText(Context context, Uri uri) {
        Intent txtIntent = new Intent();
        txtIntent.setAction(Intent.ACTION_VIEW);
        txtIntent.setDataAndType(uri, "text/plain");
        try {
            context.startActivity(txtIntent);
        } catch (ActivityNotFoundException e) {
            txtIntent.setType("text/*");
            context.startActivity(txtIntent);
        }
    }

    /**
     * Starts a image share intent from the given context using the specified attributes.
     * @param context the execution context
     * @param uriList the image's uris
     */
    public static void shareImageList(Context context, ArrayList<Uri> uriList) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Here are some files.");
        intent.setType("image/jpeg"); /* This example is sharing jpeg images. */
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList);
        context.startActivity(Intent.createChooser(intent, "Share via"));
    }

    /**
     * Starts a text share intent from the given context using the specified attributes.
     * @param context the execution context
     * @param subject the share subject
     * @param uri the text file's uri
     */
    public static void shareText(Context context, String subject, Uri uri) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("plain/text");
        context.startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    /**
     * Starts a pdf share intent from the given context using the specified attributes.
     * @param context the execution context
     * @param subject the share subject
     * @param uri the pdf file's uri
     */
    public static void sharePdf(Context context, String subject, Uri uri) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("application/pdf");
        context.startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    /**
     * Starts a email intent from the given context using the specified attributes.
     * @param context the execution context
     * @param subject the email's subject
     * @param uri the content's uri
     * @param email the email address
     */
    public static void sendEmail(Context context, String subject, Uri uri, String email) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + email));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
        context.startActivity(Intent.createChooser(emailIntent, "Send email ..."));

    }


}
