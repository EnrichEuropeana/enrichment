package eu.europeana.enrichment.mongo.dao;

import static dev.morphia.query.experimental.filters.Filters.eq;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import dev.morphia.Datastore;
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
	
	public void savePositionEntity(PositionEntityImpl position) {
		this.enrichmentDatastore.save(position);
	}

}
