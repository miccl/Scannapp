package michii.de.scannapp._view.provider;

import android.content.SearchRecentSuggestionsProvider;

/**
 * TODO: Add a class header comment!
 *
 * @author Michii
 * @since 02.07.2015
 */
public class SearchRecentSuggestionProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "com.example.SuggestionProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public SearchRecentSuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }

}
