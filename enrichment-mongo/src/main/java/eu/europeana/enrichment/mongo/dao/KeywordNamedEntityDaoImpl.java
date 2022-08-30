package eu.europeana.enrichment.mongo.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import dev.morphia.Datastore;
import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.model.impl.KeywordNamedEntity;

@Repository(EnrichmentConstants.BEAN_ENRICHMENT_KEYWORD_NAMED_ENTITY_DAO)
public class KeywordNamedEntityDaoImpl {

	@Autowired
	private Datastore enrichmentDatastore; 
	
	Logger logger = LogManager.getLogger(getClass());

	public void saveKeywordNamedEntity(KeywordNamedEntity entity) {
		this.enrichmentDatastore.save(entity);
	}
	
	public List<KeywordNamedEntity> findAllKeywordNamedEntities() {
		List<KeywordNamedEntity> result = this.enrichmentDatastore.find(KeywordNamedEntity.class).iterator().toList();
		return result;
	}	

}
