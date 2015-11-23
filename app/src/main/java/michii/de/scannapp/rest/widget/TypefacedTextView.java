package michii.de.scannapp.rest.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import michii.de.scannapp.R;


/**
 * Widget to create fonts in XML code.
 * @see <a href="http://stackoverflow.com/questions/13539688/how-to-use-roboto-font-in-android-project">Stackoverflow</a>
 * @author Michii
 * @since 27.04.2015
 */
public class TypefacedTextView extends TextView {

    private String fontPath = "fonts/";

    public TypefacedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //Typeface.createFromAsset doesn't work in the layout editor. Skipping...
        if (isInEditMode()) {
            return;
        }

        TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.TypefacedTextView);
        String fontName = styledAttrs.getString(R.styleable.TypefacedTextView_typeface);
        styledAttrs.recycle();

        if (fontName != null) {
            Typeface typeface = Typeface.createFromAsset(context.getAssets(), fontPath + fontName);
            setTypeface(typeface);
        }
    }

}
