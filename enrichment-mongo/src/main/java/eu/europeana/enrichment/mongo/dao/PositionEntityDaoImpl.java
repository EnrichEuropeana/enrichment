package eu.europeana.enrichment.mongo.dao;

import static dev.morphia.query.experimental.filters.Filters.eq;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import dev.morphia.Datastore;
import dev.morphia.query.experimental.filters.Filter;
import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.model.impl.PositionEntityImpl;
import eu.europeana.enrichment.model.vocabulary.EntityFields;

@Repository(EnrichmentConstants.BEAN_ENRICHMENT_POSITION_ENTITY_DAO)
public class PositionEntityDaoImpl {

	@Autowired
	private Datastore enrichmentDatastore; 
	
	Logger logger = LogManager.getLogger(getClass());
		
	public List<PositionEntityImpl> findPositionEntities(ObjectId namedEntityId) {
		return enrichmentDatastore.find(PositionEntityImpl.class).filter(
                eq(EntityFields.POSITION_NAMED_ENTITY, namedEntityId))
				.iterator()
				.toList();
	}
	
	public PositionEntityImpl findPositionEntitiesForNerTool(String storyId, String itemId, String fieldForNer, String nerTool) {
		return enrichmentDatastore.find(PositionEntityImpl.class).filter(
				eq(EntityFields.STORY_ID, storyId),
				eq(EntityFields.ITEM_ID, itemId),
				eq(EntityFields.FIELD_USED_FOR_NER, fieldForNer),
                eq(EntityFields.NER_TOOLS, nerTool))
				.first();
	}
	
	public void savePositionEntity(PositionEntityImpl position) {
		this.enrichmentDatastore.save(position);
	}
	
	public PositionEntityImpl findPositionEntities(ObjectId namedEntityId, String storyId, String itemId, int offsetTranslatedText, String fieldForNer) {
	    List<Filter> filters = new ArrayList<>();
	    filters.add(eq(EntityFields.POSITION_NAMED_ENTITY, namedEntityId));
	    filters.add(eq(EntityFields.STORY_ID, storyId));
	    if(itemId!=null) {
	    	filters.add(eq(EntityFields.ITEM_ID, itemId));
	    }
	    filters.add(eq(EntityFields.FIELD_USED_FOR_NER, fieldForNer));
    	filters.add(eq(EntityFields.OFFSETS_TRANSLATED_TEXT, offsetTranslatedText));
	    
		return enrichmentDatastore
				.find(PositionEntityImpl.class)
				.filter(filters.toArray(Filter[]::new))
				.first();
	}
	
	public List<PositionEntityImpl> getAllPositionEntities() {
		List<PositionEntityImpl> queryResult = enrichmentDatastore.find(PositionEntityImpl.class).iterator().toList();
		if(queryResult.size()>0)
		{
			List<PositionEntityImpl> tmpResult = new ArrayList<>();
			for(int index = queryResult.size()-1; index >= 0; index--) {
				PositionEntityImpl dbEntity = queryResult.get(index);
				tmpResult.add(dbEntity);
			}
			return tmpResult;
		}
		else {
			return queryResult;
		}
	}

}
