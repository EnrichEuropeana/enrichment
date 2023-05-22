package eu.europeana.enrichment.mongo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.europeana.api.commons.nosql.entity.ApiWriteLock;
import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.mongo.dao.ApiWriteLockDao;

@Service(EnrichmentConstants.BEAN_ENRICHMENT_PERSISTENT_API_WRITE_LOCK_SERVICE)
public class PersistentApiWriteLockServiceImpl implements PersistentApiWriteLockService {

	@Autowired
	ApiWriteLockDao apiWriteLockDao;

	@Override
	public ApiWriteLock lock(String storyId, String itemId, String property, String entityType) {
		return apiWriteLockDao.lock(storyId, itemId, property, entityType);
	}

	@Override
	public void unlock(ApiWriteLock writeLock) {
		apiWriteLockDao.unlock(writeLock);		
	}

	@Override
	public ApiWriteLock getActiveLock(String storyId, String itemId, String property, String entityType) {
		return apiWriteLockDao.getActiveLock(storyId, itemId, property, entityType);
	}

	@Override
	public long deleteAllLocks() {
		return apiWriteLockDao.deleteAllLocks();
	}		

}
