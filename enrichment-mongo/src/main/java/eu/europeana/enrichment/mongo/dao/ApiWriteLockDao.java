package eu.europeana.enrichment.mongo.dao;

import eu.europeana.api.commons.nosql.entity.ApiWriteLock;

public interface ApiWriteLockDao {

	ApiWriteLock lock(String storyId, String itemId, String property, String entityType);
	
	void unlock(ApiWriteLock writeLock);
	
	ApiWriteLock getActiveLock(String storyId, String itemId, String property, String entityType);
	
	long deleteAllLocks();
	
}
