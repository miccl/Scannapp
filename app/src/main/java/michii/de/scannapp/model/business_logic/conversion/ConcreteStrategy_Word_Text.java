package michii.de.scannapp.model.business_logic.conversion;

import android.content.Context;

import michii.de.scannapp.model.business_logic.document.Conversion;
import michii.de.scannapp.model.business_logic.document.Document;

/**
 *
 * @author Michii
 * @since 27.05.2015
 */
public class ConcreteStrategy_Word_Text extends ConversionStrategy {

    public ConcreteStrategy_Word_Text(Context context) {
        super(context);
    }

    @Override
    public Conversion convert(Document document) {
        //check for existing URI docs
        return null;
    }

}

