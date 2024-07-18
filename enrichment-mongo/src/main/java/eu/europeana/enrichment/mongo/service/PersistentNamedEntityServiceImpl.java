package eu.europeana.enrichment.mongo.service;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.definitions.model.impl.NamedEntityImpl;
import eu.europeana.enrichment.mongo.dao.NamedEntityDao;
import eu.europeana.enrichment.mongo.dao.PositionEntityDaoImpl;

@Service(EnrichmentConstants.BEAN_ENRICHMENT_PERSISTENT_NAMED_ENTITY_SERVICE)
public class PersistentNamedEntityServiceImpl implements PersistentNamedEntityService {

	//@Resource(name = "namedEntityDao")
	@Autowired
	NamedEntityDao namedEntityDao;
	
	@Autowired
	PositionEntityDaoImpl positionEntityDao;
		
	public NamedEntityImpl findNamedEntity(ObjectId objId) {
		return namedEntityDao.findNamedEntity(objId);
	}
	
	public List<NamedEntityImpl> findNamedEntities(String label, String type, String dbpediaId) {
		return namedEntityDao.findNamedEntities(label, type, dbpediaId);
	}
	
	public NamedEntityImpl findEqualNamedEntity(NamedEntityImpl ne) {
		return namedEntityDao.findEqualNamedEntity(ne);
	}
	
	public List<NamedEntityImpl> findNamedEntitiesWithAdditionalInformation(String storyId, String itemId, String type, List<String> nerTools, boolean matchNerToolsExactly) {
		return namedEntityDao.findNamedEntitiesWithAdditionalInformation(storyId, itemId, type, nerTools, matchNerToolsExactly);
	}
	
	public List<NamedEntityImpl> get_N_NamedEntities(int limit, int skip) {
		return namedEntityDao.get_N_NamedEntities(limit, skip);
	}
	
	public NamedEntityImpl saveNamedEntity(NamedEntityImpl entity) {
		return namedEntityDao.saveNamedEntity(entity);
	}
	
	public void saveNamedEntities(List<NamedEntityImpl> entities) {
		for (NamedEntityImpl namedEntity : entities) {
			saveNamedEntity(namedEntity);
		}
	}
	
	public void deletePositionEntitiesAndNamedEntities(String storyId,String itemId, String fieldUsedForNER) {
		namedEntityDao.deletePositionEntitiesAndNamedEntities(storyId,itemId,fieldUsedForNER);
	}
	
	public void deleteAllNamedEntities() {
		namedEntityDao.deleteAllNamedEntities();
	}

}
