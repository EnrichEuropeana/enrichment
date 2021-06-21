package eu.europeana.enrichment.mongo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.europeana.enrichment.common.commons.AppConfigConstants;
import eu.europeana.enrichment.model.NamedEntity;
import eu.europeana.enrichment.mongo.dao.NamedEntityDao;

@Service(AppConfigConstants.BEAN_ENRICHMENT_PERSISTENT_NAMED_ENTITY_SERVICE)
public class PersistentNamedEntityServiceImpl implements PersistentNamedEntityService {

	//@Resource(name = "namedEntityDao")
	@Autowired
	NamedEntityDao namedEntityDao;
	
	@Override
	public NamedEntity findNamedEntity(String label) {
		return namedEntityDao.findNamedEntity(label);
	}
	@Override
	public List<NamedEntity> findNamedEntitiesWithAdditionalInformation(String storyId, String itemId, String type, boolean translation) {
		return namedEntityDao.findNamedEntitiesWithAdditionalInformation(storyId, itemId, type, translation);
	}
	
	@Override
	public List<NamedEntity> findNamedEntitiesWithAdditionalInformation(String storyId, String itemId, String type, List<String> nerTools) {
		return namedEntityDao.findNamedEntitiesWithAdditionalInformation(storyId, itemId, type, nerTools);
	}


	@Override
	public List<NamedEntity> findNamedEntitiesWithAdditionalInformation(String storyId, String itemId, boolean translation) {
		return namedEntityDao.findNamedEntitiesWithAdditionalInformation(storyId, itemId, translation);
	}


	@Override
	public List<NamedEntity> getAllNamedEntities() {
		return namedEntityDao.findAllNamedEntities();
	}

	@Override
	public void saveNamedEntity(NamedEntity entity) {
		namedEntityDao.saveNamedEntity(entity);
	}

	@Override
	public void saveNamedEntities(List<NamedEntity> entities) {
		for (NamedEntity namedEntity : entities) {
			saveNamedEntity(namedEntity);
		}
	}
	
	@Override
	public void deleteNamedEntity(NamedEntity entity) {
		namedEntityDao.deleteNamedEntity(entity);
	}
	
	@Override
	public void deletePositionEntitiesFromNamedEntity(String storyId,String itemId, String fieldUsedForNER) {
		namedEntityDao.deletePositionEntitiesFromNamedEntity(storyId,itemId,fieldUsedForNER);
	}

	
	@Override
	public void deleteAllNamedEntities() {
		namedEntityDao.deleteAllNamedEntities();
		
	}


}
