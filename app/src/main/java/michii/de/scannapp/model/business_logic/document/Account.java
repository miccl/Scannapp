package michii.de.scannapp.model.business_logic.document;

import co.uk.rushorm.core.annotations.RushTableAnnotation;
import michii.de.scannapp.model.data.database.DatabaseHandler;

/**
 * Class to present a specific user account.
 */
@RushTableAnnotation
public class Account extends DatabaseHandler {

    /**
     * The name of the account.
     * Must be unique in database.
     */
    private String name;
    /**
     * The email of the account.
     */
    private String email;
    /**
     * The cloud of the account.
     */
    private String cloud;
    /**
     * The signatur of the account.
     */
    private String signatur;


    public Account(){
        /* Empty constructor required */
    }

    /**
     * Constructs a new {@link Account} using the specified name and email.
     * @param name the account's name
     * @param email the account's email
     */
    public Account(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCloud() {
        return cloud;
    }

    public void setCloud(String cloud) {
        this.cloud = cloud;
    }

    public String getSignatur() {
        return signatur;
    }

    public void setSignatur(String signatur) {
        this.signatur = signatur;
    }

    @Override
    public String toString() {
        return "Account{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", cloud='" + cloud + '\'' +
                ", signatur='" + signatur + '\'' +
                '}';
    }

    public boolean equals(Account account) {
        if (this == account) return true;
        if (account == null) return false;

//        if (getId() != null ? !getId().equals(account.getId()) : account.getId() != null) return false;
        return name.equals(account.name);
    }
}
