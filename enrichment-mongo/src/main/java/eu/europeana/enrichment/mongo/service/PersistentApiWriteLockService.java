package eu.europeana.enrichment.mongo.service;

import eu.europeana.api.commons.nosql.entity.ApiWriteLock;

public interface PersistentApiWriteLockService {
	
	ApiWriteLock lock(String storyId, String itemId, String property, String entityType);
	
	void unlock(ApiWriteLock writeLock);
	
	ApiWriteLock getActiveLock(String storyId, String itemId, String property, String entityType);
	
	long deleteAllLocks();

}
