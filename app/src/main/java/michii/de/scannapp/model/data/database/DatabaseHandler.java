package michii.de.scannapp.model.data.database;

import co.uk.rushorm.core.Rush;
import co.uk.rushorm.core.RushCallback;
import co.uk.rushorm.core.RushCore;
import co.uk.rushorm.core.RushSearch;

/**
 * Class to save, update and delete the business classes.
 * {@link Rush} is used for the implementation.
 * @see <a href="http://www.rushorm.com/">Rush ORM</a>.
 * @author Michii
 * @since 22.05.2015
 */
public class DatabaseHandler implements Rush {

    /**
     * Inserts or Updates the given instance into the database synchronously.
     */
    @Override
    public void save() {
        RushCore.getInstance().save(this);
    }

    /**
     * Inserts or Updates the given instance into the database asynchronously using the given callback.
     * @param callback the callback
     */
    @Override
    public void save(RushCallback callback) {
        RushCore.getInstance().save(this, callback);
    }

    /**
     * Deletes the given instance from the database synchronously.
     */
    @Override
    public void delete() {
        RushCore.getInstance().delete(this);
    }

    /**
     * Deletes the given instance from the database asynchronously using the given callback.
     * @param callback
     */
    @Override
    public void delete(RushCallback callback) {
        RushCore.getInstance().delete(this, callback);
    }

    /**
     * Retrieves the id of instance.
     * Return {@code null}, if the instance isn't in the database.
     * @return id, if the instance is in database, otherwise {@code null}
     */
    @Override
    public String getId() {
        return RushCore.getInstance().getId(this);
    }

    /**
     * Retrieves the creation time in long of the instance.
     * @return the creation time
     */
    public long getCreated() {
        return RushCore.getInstance().getMetaData(this).getCreated();
    }

    /**
     * Retrieves the update time in long of the instance.
     * @return the update time
     */
    public long getUpdated() {
        return RushCore.getInstance().getMetaData(this).getUpdated();
    }

    /**
     * Whether or not the object is already saved in database.
     * @return {@code true} if it is in database, otherwise {@code false}
     */
    public boolean isInDatabase() {
        return this.getId() != null &&
                new RushSearch().whereId(this.getId()).count(this.getClass()) > 0;
    }

}

