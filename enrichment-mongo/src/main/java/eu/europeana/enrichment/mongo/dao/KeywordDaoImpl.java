package eu.europeana.enrichment.mongo.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import dev.morphia.Datastore;
import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.model.impl.Keyword;

@Repository(EnrichmentConstants.BEAN_ENRICHMENT_KEYWORD_DAO)
public class KeywordDaoImpl {

	@Autowired
	private Datastore enrichmentDatastore; 
	
	Logger logger = LogManager.getLogger(getClass());

	public void saveKeyword(Keyword keyword) {
		this.enrichmentDatastore.save(keyword);
	}
	
	public List<Keyword> findAllKeywords() {
		return enrichmentDatastore.find(Keyword.class).iterator().toList();
	}	

}
