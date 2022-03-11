package eu.europeana.enrichment.mongo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.europeana.enrichment.common.commons.AppConfigConstants;
import eu.europeana.enrichment.model.impl.NamedEntityImpl;
import eu.europeana.enrichment.mongo.dao.NamedEntityDao;

@Service(AppConfigConstants.BEAN_ENRICHMENT_PERSISTENT_NAMED_ENTITY_SERVICE)
public class PersistentNamedEntityServiceImpl implements PersistentNamedEntityService {

	//@Resource(name = "namedEntityDao")
	@Autowired
	NamedEntityDao namedEntityDao;
	
	public NamedEntityImpl findNamedEntity(String label) {
		return namedEntityDao.findNamedEntity(label);
	}
	
	public List<NamedEntityImpl> findNamedEntitiesWithAdditionalInformation(String storyId, String itemId, String type) {
		return namedEntityDao.findNamedEntitiesWithAdditionalInformation(storyId, itemId, type);
	}
	
	
	public List<NamedEntityImpl> findNamedEntitiesWithAdditionalInformation(String storyId, String itemId, String type, List<String> nerTools) {
		return namedEntityDao.findNamedEntitiesWithAdditionalInformation(storyId, itemId, type, nerTools);
	}

	
	public List<NamedEntityImpl> getAllNamedEntities() {
		return namedEntityDao.findAllNamedEntities();
	}

	
	public void saveNamedEntity(NamedEntityImpl entity) {
		namedEntityDao.saveNamedEntity(entity);
	}

	
	public void saveNamedEntities(List<NamedEntityImpl> entities) {
		for (NamedEntityImpl namedEntity : entities) {
			saveNamedEntity(namedEntity);
		}
	}
	
	
	public void deletePositionEntitiesFromNamedEntity(String storyId,String itemId, String fieldUsedForNER) {
		namedEntityDao.deletePositionEntitiesFromNamedEntity(storyId,itemId,fieldUsedForNER);
	}

	
	
	public void deleteAllNamedEntities() {
		namedEntityDao.deleteAllNamedEntities();
		
	}


}
