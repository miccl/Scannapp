package michii.de.scannapp._view.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import org.jetbrains.annotations.NotNull;

import de.greenrobot.event.EventBus;
import michii.de.scannapp.R;

/**
 * Dialog used in the scan process to decided if another picture should be added or not.
 */
public class AddPageDialogFragment extends DialogFragment {
    private static final String ARG_NUMBER_OF_PAGES = "arg_number_of_pages";

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */


    // Use this instance of the interface to deliver action events
    private int mNumberOfPages;

    /**
     * Constructs a new Dialog using the specified page number of the document.
     * @param page_number the page_number of the document
     * @return created dialog
     */
    public static AddPageDialogFragment newInstance(int page_number) {
        AddPageDialogFragment addPageDialogFragment = new AddPageDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_NUMBER_OF_PAGES, page_number);
        addPageDialogFragment.setArguments(args);
        return addPageDialogFragment;
    }

    public AddPageDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mNumberOfPages = getArguments().getInt(ARG_NUMBER_OF_PAGES);
        }
    }

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        @SuppressLint("InflateParams") View layout = inflater.inflate(R.layout.dialog_finish, null);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(layout)
                .setTitle(R.string.dialog_finish_title)
                .setMessage("Number of pages: " + mNumberOfPages)
                .setNegativeButton(R.string.add_page, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Send the positive button event back to the host activity
                        EventBus.getDefault().post(new AddPageDialogEvent(true));
                    }
                })
                .setPositiveButton(R.string.finish, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Send the negative button event back to the host activity
                        EventBus.getDefault().post(new AddPageDialogEvent(false));
                    }
                });
        return builder.create();
    }



    public class AddPageDialogEvent {
        public boolean addPage;

        public AddPageDialogEvent(boolean addPage) {
            this.addPage = addPage;
        }
    }

}
