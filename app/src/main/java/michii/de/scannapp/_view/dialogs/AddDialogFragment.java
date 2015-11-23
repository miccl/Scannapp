package michii.de.scannapp._view.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import de.greenrobot.event.EventBus;
import michii.de.scannapp.R;

/**
 * Dialog to add or edit objects.
 * Therefore one EditText is provided in the layout.
 */
public class AddDialogFragment extends DialogFragment {
    private static final String ARG_TITLE = "arg_title";
    private static final String ARG_VALUE = "arg_value";
     /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */


    // Use this instance of the interface to deliver action events
    private String mTitle;
    private String mValue;

    /**
     * Constructs a new Dialog using the specified title.
     * @param title the dialog's title
     */
    public static AddDialogFragment newInstance(String title) {
        AddDialogFragment addDialogFragment = new AddDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_VALUE, null);
        addDialogFragment.setArguments(args);
        return addDialogFragment;
    }

    /**
     * Constructs a new Dialog using specified title and value.
     * @param title the dialog's title
     * @param value the dialog's Edittext value
     */
    public static AddDialogFragment newInstance(String title, String value) {
        AddDialogFragment addDialogFragment = new AddDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_VALUE, value);
        addDialogFragment.setArguments(args);
        return addDialogFragment;
    }

    public AddDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTitle = getArguments().getString(ARG_TITLE);
            mValue = getArguments().getString(ARG_VALUE);
        }

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        @SuppressLint("InflateParams") View layout = inflater.inflate(R.layout.dialog_add, null);

        final EditText input1 = (EditText) layout.findViewById(R.id.tv_title);
        if(mValue != null) {
            input1.setText(mValue);
        }
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout

        builder.setView(layout)
                .setTitle(mTitle)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Send the positive button event back to the host activity
                        String title = input1.getText().toString();
                        EventBus.getDefault().post(new AddDialogFragmentEvent(title));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Send the negative button event back to the host activity
                        EventBus.getDefault().post(new AddDialogFragmentEvent(null));
                    }
                });
        return builder.create();
    }

    public class AddDialogFragmentEvent {
        public final String title;

        public AddDialogFragmentEvent(String title) {
            this.title = title;
        }
    }

}
