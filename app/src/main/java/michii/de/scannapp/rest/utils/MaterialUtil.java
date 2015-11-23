package michii.de.scannapp.rest.utils;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import michii.de.scannapp.R;

/**
 * U
 * @author Michii
 * @since 09.06.2015
 */
public class MaterialUtil {

    /**
     * Setups the toolbar for the given context with the given title.
     * @param context the execution title
     * @param title the toolbar's title
     * @return the toolbar
     */
    public static Toolbar setupToolbar(Context context, String title) {
        AppCompatActivity activity = (AppCompatActivity) context;
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);

        if(activity.getSupportActionBar() == null) {
            return null;
        }

        activity.getSupportActionBar().setTitle(title);
        activity.getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setPadding(0, getStatusBarHeight(context), 0, 0);
        return toolbar;
    }


    private static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

}
