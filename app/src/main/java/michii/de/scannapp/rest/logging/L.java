package michii.de.scannapp.rest.logging;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

/**
 *
 * @author Michii
 * @since 02.05.2015
 */
public class L {
    public static void m(String message) {
        Log.d("Scanapp", "" + message);
    }

    public static void t(Context context, String message) {
        Toast.makeText(context, message + "", Toast.LENGTH_SHORT).show();
    }
    public static void T(Context context, String message) {
        Toast.makeText(context, message + "", Toast.LENGTH_LONG).show();
    }

    public static void To(Context context, String message) {
        Toast toast = Toast.makeText(context, message + "", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}