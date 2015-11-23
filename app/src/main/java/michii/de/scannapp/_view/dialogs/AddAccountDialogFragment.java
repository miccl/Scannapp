package michii.de.scannapp._view.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import org.jetbrains.annotations.NotNull;

import de.greenrobot.event.EventBus;
import michii.de.scannapp.R;

/**
 * Dialog to add and edit accounts.
 * Therefore two EditTexts to configure the name and email are provided.
 * @author Michii
 * @since 19.06.2015
 */
public class AddAccountDialogFragment extends DialogFragment {


    private static final String ARG_TITLE = "arg_title";
    private static final String ARG_NAME = "arg_name";
    private static final String ARG_EMAIL = "arg_email";
    private String mTitle;
    private String mName;
    private String mEmail;

    /**
     * Constructs the dialog with the given title.
     * @param title the title of the dialog
     */
    public static AddAccountDialogFragment newInstance(String title) {
        AddAccountDialogFragment addAccountDialogFragment = new AddAccountDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_NAME, null);
        args.putString(ARG_EMAIL, null);
        addAccountDialogFragment.setArguments(args);
        return addAccountDialogFragment;
    }

    /**
     * Constructs a new dialog instance with given title, name and email.
     * @param title the title of the dialog
     * @param name the name to show
     * @param email  the email to show
     * @return new instance
     */
    public static AddAccountDialogFragment newInstance(String title, String name, String email) {
        AddAccountDialogFragment addAccountDialogFragment = new AddAccountDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_NAME, name);
        args.putString(ARG_EMAIL, email);
        addAccountDialogFragment.setArguments(args);
        return addAccountDialogFragment;
    }


    public AddAccountDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTitle = getArguments().getString(ARG_TITLE);
            mName = getArguments().getString(ARG_NAME);
            mEmail = getArguments().getString(ARG_EMAIL);
        }

    }

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        @SuppressLint("InflateParams") View layout = inflater.inflate(R.layout.dialog_add_profile, null);

        final EditText et_username = (EditText) layout.findViewById(R.id.et_username);
        final EditText et_mail = (EditText) layout.findViewById(R.id.et_mail);

        if(mName!= null && mEmail != null) {
            et_username.setText(mName);
            et_mail.setText(mEmail);
        }
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout

        builder.setView(layout)
                .setTitle(mTitle)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Send the positive button event back to the host activity
                        String username = et_username.getText().toString();
                        String mail = et_mail.getText().toString();
                        EventBus.getDefault().post(new AddAccountDialogEvent(username, mail));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });


        return builder.create();
    }

    public class AddAccountDialogEvent {
        public final String name;
        public final String email;

        public AddAccountDialogEvent(String name, String email) {
            this.name = name;
            this.email = email;
        }

    }


}
