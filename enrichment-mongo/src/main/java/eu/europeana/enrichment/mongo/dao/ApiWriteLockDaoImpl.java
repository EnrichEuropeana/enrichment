package eu.europeana.enrichment.mongo.dao;

import static dev.morphia.query.experimental.filters.Filters.eq;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import dev.morphia.Datastore;
import eu.europeana.api.commons.nosql.entity.ApiWriteLock;
import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.definitions.model.impl.EnrichmentApiWriteLockImpl;
import eu.europeana.enrichment.mongo.utils.MorphiaUtils;

@Repository(EnrichmentConstants.BEAN_ENRICHMENT_API_WRITE_LOCK_DAO)
public class ApiWriteLockDaoImpl implements ApiWriteLockDao {

	@Autowired
	@Qualifier(EnrichmentConstants.BEAN_ENRICHMENT_DATASTORE)
	private Datastore enrichmentDatastore; 
	
	Logger logger = LogManager.getLogger(getClass());
		
	@Override
	public ApiWriteLock lock(String storyId, String itemId, String property, String entityType) {		
		ApiWriteLock writeLock = new EnrichmentApiWriteLockImpl(storyId, itemId, property, entityType);
		return enrichmentDatastore.save(writeLock);
	}
	
	@Override 
	public ApiWriteLock getActiveLock(String storyId, String itemId, String property, String entityType) {
		String name=EnrichmentApiWriteLockImpl.createWriteLockName(storyId, itemId, property, entityType);
		return enrichmentDatastore.find(EnrichmentApiWriteLockImpl.class).filter(
				eq(ApiWriteLock.FIELD_NAME, name),
				eq(ApiWriteLock.FIELD_ENDED, null))
				.first();
	}

	@Override
	public void unlock(ApiWriteLock writeLock) {
		writeLock.setEnded(new Date());
		enrichmentDatastore.save(writeLock);
	}
	
	@Override
	public long deleteAllLocks() {
		return enrichmentDatastore.find(EnrichmentApiWriteLockImpl.class)
		.delete(MorphiaUtils.MULTI_DELETE_OPTS)
        .getDeletedCount();
	}
	
}
