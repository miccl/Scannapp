package michii.de.scannapp._view.fragments;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintManager;
import android.support.v4.app.Fragment;
import android.support.v4.print.PrintHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import co.uk.rushorm.core.RushSearch;
import michii.de.scannapp.R;
import michii.de.scannapp._view.adapters.MyPrintDocumentAdapter;
import michii.de.scannapp.model.business_logic.document.Document;
import michii.de.scannapp.model.business_logic.document.Picture;
import michii.de.scannapp.model.data.file.FileUtil;

/**
 * Functional fragment to execute the print of a {@link Document} or {@link Picture}.
 * Uses {@link PrintManager} to start the print job.
 * Uses {@link MyPrintDocumentAdapter} to handle the lifecycle of the print.
 */
public class PrintFragment extends Fragment {


    public static final int PRINT_MODE_PICTURE = 0;
    public static final int PRINT_MODE_PDF = 1;
    private static final String ARG_DOCUMENT = "document";
    private static final String ARG_PRINT_MODE = "print mode";
    private int mPrintMode;
    private PrintManager mPrintManager;
    private MyPrintDocumentAdapter mPrintAdapter;

    public PrintFragment() {
        // Required empty public constructor
    }

    /**
     * Constructs a new instance using the specified document id and print mode.
     * The document id stands for a specific document.
     * The print mode specifies if a {@link Document} ({@link PrintFragment#PRINT_MODE_PDF}) or a single {@link Picture} gets printed ({@link PrintFragment#PRINT_MODE_PICTURE}).
     * The document can have multiple attached pictures.
     *
     * @param doc_id    the document's id
     * @param printMode the choosen print
     * @return a new instance
     */
    public static PrintFragment newInstance(String doc_id, int printMode) {
        PrintFragment fragment = new PrintFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DOCUMENT, doc_id);
        args.putInt(ARG_PRINT_MODE, printMode);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String res_id = null;
        if (getArguments() != null) {
            res_id = getArguments().getString(ARG_DOCUMENT);
            mPrintMode = getArguments().getInt(ARG_PRINT_MODE);

        }
        if (res_id == null) {
            return;
        }

        switch (mPrintMode) {
            case PRINT_MODE_PICTURE:
                Picture pic = new RushSearch().whereId(res_id).findSingle(Picture.class);
                doPicturePrint(pic);
                break;
            case PRINT_MODE_PDF:
                Document doc = new RushSearch().whereId(res_id).findSingle(Document.class);
                doPdfPrint(doc);
                break;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_print, container, false);
    }

    /**
     * Starts a new print job for the given {@link Document}.
     * Therefore the {@link PrintManager} start a print job with job name of the document's title.
     * The {@link MyPrintDocumentAdapter} gets constructed with the job name and the given document.
     *
     * @param doc the document to print
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void doPdfPrint(Document doc) {
        // Get a PrintManager instance
        mPrintManager = (PrintManager) getActivity()
                .getSystemService(Context.PRINT_SERVICE);

        // Set job name, which will be displayed in the print queue
        String jobName = getActivity().getString(R.string.app_name) + ": " + doc.getTitle();

        // Start a print job, passing in a PrintDocumentAdapter implementation
        // to handle the generation of a print document
        mPrintAdapter = new MyPrintDocumentAdapter(getActivity(), doc);
        mPrintManager.print(jobName, mPrintAdapter, null);
    }


    /**
     * Start a new print job for the given {@link Picture}.
     * Therefore a {@link PrintHelper} is used with is started with the bitmap of the given picture.
     *
     * @param picture the picture to print
     */
    private void doPicturePrint(Picture picture) {
        PrintHelper photoPrinter = new PrintHelper(getActivity());
        photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);

        Uri uri = Uri.parse(picture.getUriString());
        String path = FileUtil.getRealPathFromURI(getActivity(), uri);
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        photoPrinter.printBitmap("droids.jpg - test print", bitmap);
    }

}
