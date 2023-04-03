package eu.europeana.enrichment.mongo.service;

import java.util.List;

import org.bson.types.ObjectId;
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
	
	public NamedEntityImpl findNamedEntity(ObjectId objId) {
		return namedEntityDao.findNamedEntity(objId);
	}
	
	public NamedEntityImpl findNamedEntity(String label, String type, String dbpediaId) {
		return namedEntityDao.findNamedEntity(label, type, dbpediaId);
	}
	
	public NamedEntityImpl findNamedEntitiesByNerTool(NamedEntityImpl ne) {
		return namedEntityDao.findNamedEntityByNerTool(ne);
	}
	
	public List<NamedEntityImpl> findAllNamedEntitiesByLabelAndType(String label, String type) {
		return namedEntityDao.findAllNamedEntitiesByLabelAndType(label, type);
	}
	
	public List<NamedEntityImpl> findNamedEntitiesWithAdditionalInformation(String storyId, String itemId, String type, List<String> nerTools, boolean matchNerToolsExactly) {
		return namedEntityDao.findNamedEntitiesWithAdditionalInformation(storyId, itemId, type, nerTools, matchNerToolsExactly);
	}
	
	public List<NamedEntityImpl> get_N_NamedEntities(int limit, int skip) {
		return namedEntityDao.get_N_NamedEntities(limit, skip);
	}
	
	public void saveNamedEntity(NamedEntityImpl entity) {
		namedEntityDao.saveNamedEntity(entity);
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
