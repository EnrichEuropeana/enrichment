package eu.europeana.enrichment.mongo.dao;

import static dev.morphia.query.experimental.filters.Filters.eq;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import dev.morphia.Datastore;
import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.definitions.model.impl.Keyword;

@Repository(EnrichmentConstants.BEAN_ENRICHMENT_KEYWORD_DAO)
public class KeywordDaoImpl {

	@Autowired
	@Qualifier(EnrichmentConstants.BEAN_ENRICHMENT_DATASTORE)
	private Datastore enrichmentDatastore; 
	
	Logger logger = LogManager.getLogger(getClass());

	public Keyword saveKeyword(Keyword keyword) {
		return this.enrichmentDatastore.save(keyword);
	}
	
	public List<Keyword> findAllKeywords() {
		return enrichmentDatastore.find(Keyword.class).iterator().toList();
	}	

	public Keyword findByObjectId(String objectId) {
	    return enrichmentDatastore.find(Keyword.class).filter(
	                eq(EnrichmentConstants.OBJECT_ID, new ObjectId(objectId)))
	                .first();
    }
	
	public Keyword findByPropertyId(String propertyId) {
		return enrichmentDatastore.find(Keyword.class).filter(
                eq(EnrichmentConstants.KEYWORD_PROPERTY_ID, propertyId))
                .first();
	}
}
