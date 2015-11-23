package michii.de.scannapp._view.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import michii.de.scannapp.model.DatamodelApi;
import michii.de.scannapp.model.business_logic.document.Category;
import michii.de.scannapp.model.business_logic.document.Document;

/**
 * Dialog to choose the attached categories of a document within a multi choice list.
 * @author Michii
 * @since 24.06.2015
 */
public class CategoryPickerDialogFragment extends DialogFragment {
    private static final String ARG_DOCUMENT_ID = "arg_document_id";
     /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */


    // Use this instance of the interface to deliver action events
    private Document mDocument;
    private DatamodelApi mDatamodelApi;
    private List<Category> mCategories;
    private List<Category> mSelectedCategories;
    private List<Integer> mSelectedIndexes;

    /**
     * Constructs a new dialog using the specified document id.
     * @param doc_id document's id
     * @return new dialog
     */
    public static CategoryPickerDialogFragment newInstance(String doc_id) {
        CategoryPickerDialogFragment categoryPickerDialogFragment = new CategoryPickerDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DOCUMENT_ID, doc_id);
        categoryPickerDialogFragment.setArguments(args);
        return categoryPickerDialogFragment;
    }

    public CategoryPickerDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatamodelApi = new DatamodelApi(getActivity());
        if (getArguments() != null) {
            String doc_id = getArguments().getString(ARG_DOCUMENT_ID);
            mDocument = mDatamodelApi.getDocumentById(doc_id);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        mCategories = mDatamodelApi.getCategories();
        mSelectedCategories = mDatamodelApi.getCategoriesByDocument(mDocument);
        String[] categoryString = getCategorieArray();
        boolean[] isSelectedArray = getSelectedArray();

        mSelectedIndexes = getSelectedIndexes(isSelectedArray);

        builder.setTitle("Pick your categories")
                .setMultiChoiceItems(categoryString, isSelectedArray, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        Category selectedItem = mCategories.get(which);
                        Log.d("DETAIL", "Selected Item: " + selectedItem.toString());
                        if (isChecked) {
                            // If the user checked the item, add it to the selected items
                            mSelectedIndexes.add(which);
//                            mSelectedCategories.add(selectedItem);
                        } else if (mSelectedIndexes.contains(which)) {
                            // Else, if the item is already in the array, remove it
                            mSelectedIndexes.remove(Integer.valueOf(which));
                        }
                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Send the positive button event back to the host activity
                        EventBus.getDefault().post(new CategoryPickerEvent(mSelectedIndexes));
                    }
                })
                .setNeutralButton("Add Category", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        AddDialogFragment dialogFragment = AddDialogFragment.newInstance("Add category");
                        dialogFragment.show(fragmentManager, "dialog");
                        }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        return builder.create();
    }

    private List<Integer> getSelectedIndexes(boolean[] isSelectedArray) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < isSelectedArray.length; i++) {
            if(isSelectedArray[i]) {
                list.add(i);
            }
        }
        return list;
    }

    private String[] getCategorieArray() {
        String[] s = new String[mCategories.size()];
        for (int i = 0; i < s.length; i++) {
            s[i] = mCategories.get(i).getTitle();
        }
        return s;
    }

    private boolean[] getSelectedArray() {
        boolean[] bool = new boolean[mCategories.size()];
        for (int i = 0; i < bool.length; i++) {
            Category current_cat = mCategories.get(i);
            bool[i] = current_cat.partOf(mSelectedCategories);
            Log.d("DETAIL", "bool" + i + ":" + bool[i]);

        }
        return bool;
    }


    public class CategoryPickerEvent {
        public final List<Integer> selectedIndex;

        public CategoryPickerEvent(List<Integer> selectedIndex) {
            this.selectedIndex = selectedIndex;
        }
    }


}
