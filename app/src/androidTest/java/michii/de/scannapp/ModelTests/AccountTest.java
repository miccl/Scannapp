package michii.de.scannapp.ModelTests;

import android.content.Context;
import android.support.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import co.uk.rushorm.core.RushSearch;
import michii.de.scannapp._view.activities.MainActivity;
import michii.de.scannapp.model.DatamodelApi;
import michii.de.scannapp.model.business_logic.document.Account;
import michii.de.scannapp.rest.tests.TestUtils;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

/**
 * TODO: Add a class header comment!
 *
 * @author Michii
 * @since 16.07.2015
 */
@RunWith(JUnit4.class)
public class AccountTest {

    @Rule
    public ActivityTestRule activityTestRule = new ActivityTestRule<>(MainActivity.class);

    private DatamodelApi mDatamodelApi;
    private Context mContext;


    @Before
    public void setup() throws Exception {
        mContext = activityTestRule.getActivity();
        mDatamodelApi = new DatamodelApi(activityTestRule.getActivity());

        TestUtils.cleanDatabase(mContext);
    }

    @Test
    public void testAddAccount() throws Exception {

        // Add a account
        String name = "Heins";
        String email = "heins@mail.com";
        Account acc = mDatamodelApi.addAccount(name, email);
        assertNotNull("Account should be added", acc);
        assertNotNull("Account should be added to database", new RushSearch().whereId(acc.getId()).findSingle(Account.class));
        assertEquals("Account have the defined name", name, acc.getName());
        assertEquals("Account have the defined email", email, acc.getEmail());

        // Add a account with a already taken name
        Account acc2 = mDatamodelApi.addAccount("Heins", "heins123@mail.com");
        assertNull("Account should not be added", acc2);

        // Add a account with no email
        Account acc3 = mDatamodelApi.addAccount("Hamsel", null);
        assertNull("Account should not be added", acc3);

    }

    @Test
    public void testSetAccount() throws Exception {
        // Add a account
        String name = "Heins";
        String email = "heins@mail.com";
        Account acc = mDatamodelApi.addAccount(name, email);

        // Update the account
        String name2 = "Humpel";
        String email2 = "heins123@mail.com";
        acc = mDatamodelApi.setAccount(acc, name2, email2, null, null);
        assertNotNull(acc);
        assertEquals("Name should be changed", name2, acc.getName());
        assertEquals("Mail should be changed", email2, acc.getEmail());

        // Update to a name that is already taken
        String email3= "heins123@mail.com";
        String name3 = "Hamsel";
        Account acc2 = mDatamodelApi.addAccount(name3, email3);
        assertNull("Account should not be added", mDatamodelApi.setAccount(acc2, name2, email, null, null));

        // Update to a account without email
        assertNull("Account should not be added", mDatamodelApi.setAccount(acc2, name3, null, null, null));

    }

    @Test
    public void TestDeleteDocument() throws Exception {
        // Add a account
        String name = "Heins";
        String email = "heins@mail.com";
        Account acc = mDatamodelApi.addAccount(name, email);
        assertNotNull("Account should be added to database", new RushSearch().whereId(acc.getId()).findSingle(Account.class));

        //Delete the account
        mDatamodelApi.delete(acc);
        assertNull("Account should be remove from database", new RushSearch().whereId(acc.getId()).findSingle(Account.class));
    }
}
