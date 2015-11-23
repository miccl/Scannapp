package michii.de.scannapp._view.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import de.greenrobot.event.EventBus;

/**
 * TODO: Add a class header comment!
 *
 * @author Michii
 * @since 21.07.2015
 */
public class DeleteDialogFragment extends DialogFragment {
    private static final String ARG_MESSAGE = "arg_message";

    private String mMessage;

    public static DeleteDialogFragment newInstance(String text) {
        DeleteDialogFragment deleteDialogFragment = new DeleteDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, text);
        deleteDialogFragment.setArguments(args);
        return deleteDialogFragment;
    }


    public DeleteDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMessage = getArguments().getString(ARG_MESSAGE);
        }

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout

        builder.setTitle("Delete")
                .setMessage(mMessage)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Send the positive button event back to the host activity
                        EventBus.getDefault().post(new DeleteDialogFragmentEvent(true));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Send the negative button event back to the host activity
                        EventBus.getDefault().post(new DeleteDialogFragmentEvent(false));
                    }
                });
        return builder.create();
    }

    public class DeleteDialogFragmentEvent {
        public boolean result;

        public DeleteDialogFragmentEvent(boolean result) {
            this.result = result;
        }
    }

}
