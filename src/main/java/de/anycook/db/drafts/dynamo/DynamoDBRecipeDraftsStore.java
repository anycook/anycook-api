package de.anycook.db.drafts.dynamo;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import de.anycook.conf.Configuration;
import de.anycook.db.drafts.RecipeDraftsStore;
import de.anycook.drafts.RecipeDraft;
import de.anycook.newrecipe.DraftNumberProvider;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Jan Graßegger<jan@anycook.de>
 */
public class DynamoDBRecipeDraftsStore implements RecipeDraftsStore{

    private final DynamoDBMapper mapper;
    private final Logger logger;

    public DynamoDBRecipeDraftsStore() {
        this.logger = Logger.getLogger(getClass());
        AWSCredentials credentials = new BasicAWSCredentials(Configuration.getInstance().getDynamoDbAccessKey(),
            Configuration.getInstance().getDynamoDbAccessSecret());
        AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient(credentials);
        dynamoDBClient.setRegion(Region.getRegion(Regions.EU_WEST_1));

        String dynamoDbEndpoint = Configuration.getInstance().getDynamoDbEndpoint();
        if (dynamoDbEndpoint != null) {
            dynamoDBClient.setEndpoint(dynamoDbEndpoint);
        }

        this.mapper = new DynamoDBMapper(dynamoDBClient,
            new DynamoDBMapperConfig(DynamoDBMapperConfig.SaveBehavior.UPDATE_SKIP_NULL_ATTRIBUTES));
    }

    @Override
    public List<RecipeDraft> getDrafts(int userId) throws IOException {
        RecipeDraft recipeDraft = new RecipeDraft();
        recipeDraft.setUserId(userId);

        DynamoDBQueryExpression<RecipeDraft> queryExpression = new DynamoDBQueryExpression<>();
        queryExpression.withHashKeyValues(recipeDraft);
        return mapper.query(RecipeDraft.class, queryExpression);
    }

    @Override
    public RecipeDraft getDraft(String id, int userId) throws DraftNotFoundException {
        RecipeDraft recipeDraft = new RecipeDraft();
        recipeDraft.setId(id);
        recipeDraft.setUserId(userId);

        try {
            return mapper.load(RecipeDraft.class, userId, id);
        } catch (ResourceNotFoundException e) {
            logger.warn(e, e);
            throw new DraftNotFoundException(id, userId);
        }
    }

    @Override
    public int countDrafts(int userId) throws IOException {
        RecipeDraft recipeDraft = new RecipeDraft();
        recipeDraft.setUserId(userId);

        DynamoDBQueryExpression<RecipeDraft> queryExpression = new DynamoDBQueryExpression<>();
        queryExpression.withHashKeyValues(recipeDraft);
        return mapper.count(RecipeDraft.class, queryExpression);
    }

    @Override
    public String newDraft(int userId) throws SQLException {
        String newKey = Long.toString(System.currentTimeMillis());
        RecipeDraft recipeDraft = new RecipeDraft();
        recipeDraft.setId(newKey);
        recipeDraft.setUserId(userId);
        recipeDraft.setTimestamp(System.currentTimeMillis());
        updateDraft(newKey, recipeDraft);

        DraftNumberProvider.INSTANCE.wakeUpSuspended(userId);

        return newKey;
    }

    @Override
    public void updateDraft(String id, RecipeDraft recipeDraft) {
        recipeDraft.setId(id);
        DynamoDBSaveExpression saveExpression = new DynamoDBSaveExpression();
        mapper.save(recipeDraft);
    }

    @Override
    public void deleteDraft(String id, int userId) throws SQLException {
        RecipeDraft recipeDraft = new RecipeDraft();
        recipeDraft.setId(id);
        recipeDraft.setUserId(userId);

        mapper.delete(recipeDraft);
        DraftNumberProvider.INSTANCE.wakeUpSuspended(userId);
    }

    @Override
    public void close() throws Exception {
        //nothing to do
    }
}
