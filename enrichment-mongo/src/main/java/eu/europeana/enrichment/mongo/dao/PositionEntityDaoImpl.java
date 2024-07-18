package eu.europeana.enrichment.mongo.dao;

import static dev.morphia.query.experimental.filters.Filters.eq;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import dev.morphia.Datastore;
import dev.morphia.query.FindOptions;
import dev.morphia.query.experimental.filters.Filter;
import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.definitions.model.impl.PositionEntityImpl;

@Repository(EnrichmentConstants.BEAN_ENRICHMENT_POSITION_ENTITY_DAO)
public class PositionEntityDaoImpl {

	@Autowired
	@Qualifier(EnrichmentConstants.BEAN_ENRICHMENT_DATASTORE)
	private Datastore enrichmentDatastore; 
	
	Logger logger = LogManager.getLogger(getClass());
		
	public List<PositionEntityImpl> findPositionEntities(ObjectId namedEntityId) {
		return enrichmentDatastore.find(PositionEntityImpl.class).filter(
                eq(EnrichmentConstants.POSITION_NAMED_ENTITY, namedEntityId))
				.iterator()
				.toList();
	}
		
	public void savePositionEntity(PositionEntityImpl position) {
		this.enrichmentDatastore.save(position);
	}
	
	public List<PositionEntityImpl> findPositionEntities(ObjectId namedEntityId, String storyId, String itemId, String fieldForNer) {
	    List<Filter> filters = new ArrayList<>();
	    if(! EnrichmentConstants.MONGO_SKIP_OBJECT_ID_FIELD.equals(namedEntityId)) {
	    	filters.add(eq(EnrichmentConstants.POSITION_NAMED_ENTITY, namedEntityId));
	    }
	    if(! EnrichmentConstants.MONGO_SKIP_FIELD.equals(storyId)) {
	    	filters.add(eq(EnrichmentConstants.STORY_ID, storyId));
	    }
	    if(! EnrichmentConstants.MONGO_SKIP_FIELD.equals(itemId)) {
	    	filters.add(eq(EnrichmentConstants.ITEM_ID, itemId));
	    }
	    if(! EnrichmentConstants.MONGO_SKIP_FIELD.equals(fieldForNer)) {
	    	filters.add(eq(EnrichmentConstants.FIELD_USED_FOR_NER, fieldForNer));
	    }
	    
		return enrichmentDatastore
				.find(PositionEntityImpl.class)
				.filter(filters.toArray(Filter[]::new))
				.iterator()
				.toList();
	}

	public List<PositionEntityImpl> get_N_PositionEntities(int limit, int skip) {
		return enrichmentDatastore.find(PositionEntityImpl.class)
				.iterator(new FindOptions()
					    .skip(skip)
					    .limit(limit))
				.toList();
	}

}
