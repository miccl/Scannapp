package michii.de.scannapp.model.business_logic.conversion;



import android.content.Context;
import android.net.Uri;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import michii.de.scannapp.R;
import michii.de.scannapp.model.business_logic.document.Conversion;
import michii.de.scannapp.model.business_logic.document.Document;
import michii.de.scannapp.model.business_logic.document.Picture;
import michii.de.scannapp.model.data.file.DateUtil;
import michii.de.scannapp.model.data.file.FileHandler;
import michii.de.scannapp.model.data.file.FileUtil;

/**
 * Concrete Strategy to executed a pdf conversion.
 * For the pdf conversion iText is used.
 * @author Michii
 * @since 23.05.2015
 */
public class ConcreteStrategy_Pdf extends ConversionStrategy {

    private static final String TYPE_PDF = "pdf";
    private final FileHandler mFileHandler;

    public ConcreteStrategy_Pdf(Context context) {
        super(context);
        mFileHandler = new FileHandler(context);
    }

    @Override
    public Conversion convert(Document document) {
        //Test if conversion already exists?
        Conversion conversion = getConversion(document);
        File file = createImagePdf(getContext(), document, conversion);
        if (file != null) {
            conversion.setUriString(Uri.fromFile(file).toString());
            conversion.setSize(file.length());
            conversion.setDate(file.lastModified());
            return conversion;
        }
        return null;
    }

    private Conversion getConversion(Document document) {
        Conversion conv = document.getConversion(TYPE_PDF);
        if(conv != null) {
            return conv;
        }
        return addNewConversion(document);
    }

    private Conversion addNewConversion(Document document) {
        String title = document.getTitle();
        String type = TYPE_PDF;
        File file = FileHandler.createPdfFile(document.getTitle());
        String uriString = Uri.fromFile(file).toString();
        long date = DateUtil.getCurrentDate();

        return new Conversion(title, type, date, uriString, 0);
    }
    private File createImagePdf(Context context, Document document, Conversion conversion) {

        File outputFile = mFileHandler.getFile(Uri.parse(conversion.getUriString()));
        try {
            com.itextpdf.text.Document pdf_doc = new com.itextpdf.text.Document();
            PdfWriter.getInstance(pdf_doc, new FileOutputStream(outputFile));
            pdf_doc.open();

            //TODO man koennten noch auf die Orientierung achten
            float documentWidth = pdf_doc.getPageSize().getWidth() - pdf_doc.leftMargin() - pdf_doc.rightMargin();
            float documentHeight = pdf_doc.getPageSize().getHeight() - pdf_doc.topMargin() - pdf_doc.bottomMargin();

            for (Picture picture : document.getAttachedPictures()) {
                Uri image_uri = Uri.parse(picture.getUriString());
                String image_path = FileUtil.getRealPathFromURI(context, image_uri);
                Image image = Image.getInstance(image_path);
                image.scaleToFit(documentWidth, documentHeight);
                image.setAlignment(Element.ALIGN_CENTER);
                pdf_doc.add(image);
            }
            addMetaData(pdf_doc, document);


            pdf_doc.close();
            return outputFile;
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void addMetaData(com.itextpdf.text.Document pdf_doc, Document document) {
        //meta data, cann be viewed in under File Properties
        pdf_doc.addTitle(document.getTitle());
        pdf_doc.addSubject("");
        pdf_doc.addKeywords("PDF");
        pdf_doc.addAuthor(getContext().getString(R.string.app_name));
        pdf_doc.addCreator(getContext().getString(R.string.app_name));
    }


}
