package michii.de.scannapp._view.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import de.greenrobot.event.EventBus;


/**
 * Dialog to choose the share medium.
 * Possibilities are PDF, JPG and OCR.
 * @author Michii
 * @since 24.06.2015
 */
public class SharePickerDialogFragment extends DialogFragment {

    public static final int PDF = 0;
    public static final int JPG = 1;
    public static final int OCR = 2;

    public static SharePickerDialogFragment newInstance() {
        return new SharePickerDialogFragment();
    }

    public SharePickerDialogFragment() {
        // Required empty public constructor
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setTitle("Share")
                .setItems(getData(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EventBus.getDefault().post(new SharePickerDialogEvent(which));
                    }
                });

        return builder.create();
    }

    private String[] getData() {
        String[] s = new String[3];
        s[PDF] = "Pdf";
        s[JPG] = "Jpg";
        s[OCR] = "Ocr";
        return s;
    }

    public class SharePickerDialogEvent {
        public final int item;

        public SharePickerDialogEvent(int item) {
            this.item = item;
        }
    }

}
