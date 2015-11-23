package michii.de.scannapp._view.adapters;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.pdf.PrintedPdfDocument;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Image;

import java.io.FileOutputStream;
import java.io.IOException;

import michii.de.scannapp.model.business_logic.document.Document;
import michii.de.scannapp.model.business_logic.document.Picture;
import michii.de.scannapp.model.data.file.BitmapUtil;
import michii.de.scannapp.model.data.file.FileUtil;

/**
 * Implementation of a {@link PrintDocumentAdapter} to manage the print lifecycle.
 *
 * @author Michii
 * @since 24.05.2015
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class MyPrintDocumentAdapter extends PrintDocumentAdapter {

    private Document mDocument;
    private PrintedPdfDocument mPdfDocument;
    private Context mContext;
    private int pageHeight;
    private int pageWidth;
    private int totalPages;

    /**
     * Constructs a adapter using the specified context and document.
     * @param context the execution context
     * @param document the document to print
     */
    public MyPrintDocumentAdapter(Context context, Document document) {
        mContext = context;
        mDocument = document;
    }

    @Override
    public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {
        // Create a new PdfDocument with the requested page attributes
        mPdfDocument = new PrintedPdfDocument(mContext, newAttributes);

        pageHeight =
                newAttributes.getMediaSize().getHeightMils()/1000 * 72;
        pageWidth =
                newAttributes.getMediaSize().getWidthMils()/1000 * 72;

        // Respond to cancellation request
        if (cancellationSignal.isCanceled() ) {
            callback.onLayoutCancelled();
            return;
        }

        // Compute the expected number of printed pages
        //TODO auf bestimmte dinge achten
//        int totalPages = computePageCount(newAttributes);
        totalPages = mDocument.getAttachedPictures().size();
        if (totalPages > 0) {
            // Return print information to print framework
            PrintDocumentInfo.Builder builder = new PrintDocumentInfo
                    .Builder("print_output.pdf")
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(totalPages);

            PrintDocumentInfo info = builder.build();
            // Content layout reflow is complete
            callback.onLayoutFinished(info, true);
        } else {
            // Otherwise report an error to the print framework
            callback.onLayoutFailed("Page count calculation failed.");
        }

    }

    @Override
    public void onWrite(PageRange[] pageRanges, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
        // Iterate over each page of the document,
        // check if it's in the output range.
        for (int i = 0; i < totalPages; i++) {
            // Check to see if this page is in the output range.
            if (pageInRange(pageRanges, i)) {
                // If so, add it to writtenPagesArray. writtenPagesArray.size()
                // is used to compute the next output page index.
                PdfDocument.PageInfo newPage = new PdfDocument.PageInfo.Builder(pageWidth,
                        pageHeight, i).create();
                PdfDocument.Page page = mPdfDocument.startPage(newPage);


                // check for cancellation
                if (cancellationSignal.isCanceled()) {
                    callback.onWriteCancelled();
                    mPdfDocument.close();
                    mPdfDocument = null;
                    return;
                }
                // Draw page content for printing
                try {
                    drawPage(page, i);
                } catch (BadElementException | IOException e) {
                    e.printStackTrace();
                }

                // Rendering is complete, so page can be finalized.
                mPdfDocument.finishPage(page);
            }
        }

        // Write PDF document to file
        try {
            mPdfDocument.writeTo(new FileOutputStream(
                    destination.getFileDescriptor()));
        } catch (IOException e) {
            callback.onWriteFailed(e.toString());
            return;
        } finally {
            mPdfDocument.close();
            mPdfDocument = null;
        }
//        PageRange[] writtenPages = computeWrittenPages();
        // Signal the print framework the document is complete
        callback.onWriteFinished(pageRanges);

    }

    private int computePageCount(PrintAttributes printAttributes) {
        int itemsPerPage = 1; // default item count for portrait mode

        PrintAttributes.MediaSize pageSize = printAttributes.getMediaSize();
        if (!pageSize.isPortrait()) {
            // Six items per page in landscape orientation
            itemsPerPage = 2;
        }

        // Determine number of print items
        int printItemCount = mDocument.getAttachedPictures().size();
        return (int) Math.ceil(printItemCount / itemsPerPage);
    }


    /**
     * Wether or not the given page is in the given pageRanges.
     * @param pageRanges the pagerrange
     * @param page the position of the page
     * @return {@code true} if page in pageRanges, otherwise {@code false}
     */
    private boolean pageInRange(PageRange[] pageRanges, int page)
    {
        for (PageRange pageRange : pageRanges) {
            if ((page >= pageRange.getStart()) &&
                    (page <= pageRange.getEnd()))
                return true;
        }
        return false;
    }

    /**
     * Draws the current picture with the given pagenumber and the the given specification of page.
     * @param page page specification
     * @param pagenumber number of the page
     * @throws BadElementException
     * @throws IOException
     */
    private void drawPage(PdfDocument.Page page,
                          int pagenumber) throws BadElementException, IOException {
        Canvas canvas = page.getCanvas();
        PdfDocument.PageInfo pageInfo = page.getInfo();

//        pagenumber++; // Make sure page numbers start at 1

        // page measurements
        int titleBaseLine = 72;
        int leftMargin = 54;

        Rect rect = pageInfo.getContentRect();


        Picture pic = mDocument.getAttachedPictures().get(pagenumber);
        Uri uri = Uri.parse(pic.getUriString());
        String path = FileUtil.getRealPathFromURI(mContext, uri);

        Image image = Image.getInstance(path);
        //Compute page measurements
        float documentWidth = pageInfo.getPageWidth() - 2*leftMargin;
        float documentHeight = pageInfo.getPageHeight() - 2*titleBaseLine;
        image.scaleToFit(documentWidth, documentHeight);

        //Create the bitmap from the given path
        Bitmap bmp = BitmapFactory.decodeFile(path);

        //resize the Bitmap, if the bitmap is to huge for the page
        if(bmp.getHeight() > documentHeight || bmp.getWidth() > documentWidth) {

            bmp = BitmapUtil.getResizedBitmap(bmp, image.getScaledWidth(), image.getScaledHeight());
        }

        // Compute the measurements from the produced bitmap to stay in center
        float spaceLeft = (pageInfo.getPageWidth() - bmp.getWidth())/2;
        float spaceTop = (pageInfo.getPageHeight() -  bmp.getHeight())/2;

        //draw the bitmap on the page
        canvas.drawBitmap(bmp, spaceLeft, spaceTop, null);
    }

}
