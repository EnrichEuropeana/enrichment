package eu.europeana.enrichment.mongo.service;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.model.impl.PositionEntityImpl;
import eu.europeana.enrichment.mongo.dao.PositionEntityDaoImpl;

@Service(EnrichmentConstants.BEAN_ENRICHMENT_PERSISTENT_POSITION_ENTITY_SERVICE)
public class PersistentPositionEntityServiceImpl {

	@Autowired
	PositionEntityDaoImpl positionEntityDao;
	
	public List<PositionEntityImpl> findPositionEntities(ObjectId namedEntityId) {
		return positionEntityDao.findPositionEntities(namedEntityId);
	}

	public PositionEntityImpl findPositionEntitiesForNerTool(String storyId, String itemId, String fieldForNer, String nerTool) {
		return positionEntityDao.findPositionEntitiesForNerTool(storyId, itemId, fieldForNer, nerTool);
	}
	
	public void savePositionEntity(PositionEntityImpl position) {
		positionEntityDao.savePositionEntity(position);
	}
	
	public PositionEntityImpl findPositionEntities(ObjectId namedEntityId, String storyId, String itemId, int offsetTranslatedText, String fieldForNer) {
		return positionEntityDao.findPositionEntities(namedEntityId, storyId, itemId, offsetTranslatedText, fieldForNer);
	}
	
	public List<PositionEntityImpl> getAllPositionEntities() {
		return positionEntityDao.getAllPositionEntities();
	}

}
