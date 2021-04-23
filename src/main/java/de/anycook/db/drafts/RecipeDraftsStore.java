package de.anycook.db.drafts;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import de.anycook.db.drafts.mongo.MongoDBRecipeDraftsStore;
import de.anycook.drafts.RecipeDraft;

/**
 * @author Jan Graßegger<jan@anycook.de>
 */
public interface RecipeDraftsStore extends AutoCloseable {

    /**
     * All current drafts of a user.
     * @param userId The user id
     * @return all recipe drafts of a the user
     */
    public List<RecipeDraft> getDrafts(int userId) throws IOException;

    /**
     * returns a draft
     * @param id id of the draft
     * @param userId id of the user
     * @return the draft
     */
    public RecipeDraft getDraft(String id, int userId) throws DraftNotFoundException;

    /**
     * get the number of drafts for the user
     * @param userId id of the user
     * @return number of drafts as int
     */
    public int countDrafts(int userId) throws IOException;

    /**
     * Creates a new empty draft
     * @param userId user id
     * @return id of the new draft
     */
    public String newDraft(int userId) throws SQLException;

    /**
     * update draft with new version
     * @param id id of the draft
     * @param recipeDraft updated draft object
     */
    public void updateDraft(String id, RecipeDraft recipeDraft);

    /**
     * deletes a draft
     * @param id id of the draft
     * @param userId id of the user the draft belongs to
     */
    public void deleteDraft(String id, int userId) throws SQLException;

    public static RecipeDraftsStore getRecipeDraftStore() {
        return new MongoDBRecipeDraftsStore();
    }

    public static class DraftNotFoundException extends Exception {
        public DraftNotFoundException(String id, int userId) {
            super(String.format("unable to find draft %s for user %d", id, userId));
        }
    }
}
