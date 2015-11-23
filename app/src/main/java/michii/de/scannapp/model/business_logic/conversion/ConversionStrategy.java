package michii.de.scannapp.model.business_logic.conversion;

import android.content.Context;

import michii.de.scannapp.model.business_logic.document.Conversion;
import michii.de.scannapp.model.business_logic.document.Document;

/**
 * Abstract strategy class to execute concrete conversion strategy's.
 * {@see {@link ConcreteStrategy_Pdf}, {@link ConcreteStrategy_Ocr_Text}}
 * @author Michii
 * @since 02.06.2015
 */
public abstract class ConversionStrategy {

    private String title;
    private Context context;

    public ConversionStrategy(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public abstract Conversion convert(Document document);
}
