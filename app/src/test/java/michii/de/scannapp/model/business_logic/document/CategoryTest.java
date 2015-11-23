package michii.de.scannapp.model.business_logic.document;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.*;

/**
 * TODO: Add a class header comment!
 *
 * @author Michii
 * @since 16.07.2015
 */
public class CategoryTest {

    @Test
    public void testEquals() throws Exception {
        Category cat1 = new Category("Humpel");
        Category cat2 = new Category("Wumpel");
        Category cat3 = new Category("Humpel");

        assertFalse(cat1.equals(cat2));
        assertTrue(cat1.equals(cat3));
    }

    @Test
    public void testContains() throws Exception {
        Category cat1 = new Category("Humpel");
        Category cat2 = new Category("Wumpel");
        Category cat3 = new Category("Rumpel");

        List<Category> cats = new ArrayList<>();
        cats.add(cat1);
        cats.add(cat3);

        assertTrue(cats.contains(cat1));
        assertFalse(cats.contains(cat2));
        assertTrue(cats.contains(cat3));
    }

}