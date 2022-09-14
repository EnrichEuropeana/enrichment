package eu.europeana.enrichment.mongo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.model.impl.NamedEntityImpl;
import eu.europeana.enrichment.mongo.dao.NamedEntityDao;
import eu.europeana.enrichment.mongo.dao.PositionEntityDaoImpl;

@Service(EnrichmentConstants.BEAN_ENRICHMENT_PERSISTENT_NAMED_ENTITY_SERVICE)
public class PersistentNamedEntityServiceImpl implements PersistentNamedEntityService {

	//@Resource(name = "namedEntityDao")
	@Autowired
	NamedEntityDao namedEntityDao;
	
	@Autowired
	PositionEntityDaoImpl positionEntityDao;
	
	public NamedEntityImpl findNamedEntityByLabel(String label) {
		return namedEntityDao.findNamedEntityByLabel(label);
	}
	
	public NamedEntityImpl findNamedEntityByLabelAndType(String label, String type) {
		return namedEntityDao.findNamedEntityByLabelAndType(label, type);
	}
	
	public List<NamedEntityImpl> findNamedEntitiesWithAdditionalInformation(String storyId, String itemId, String type) {
		return namedEntityDao.findNamedEntitiesWithAdditionalInformation(storyId, itemId, type);
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
	
	public void deletePositionEntitiesAndNamedEntity(String storyId,String itemId, String fieldUsedForNER) {
		namedEntityDao.deletePositionEntitiesAndNamedEntity(storyId,itemId,fieldUsedForNER);
	}
	
	public void deleteAllNamedEntities() {
		namedEntityDao.deleteAllNamedEntities();
	}

}
