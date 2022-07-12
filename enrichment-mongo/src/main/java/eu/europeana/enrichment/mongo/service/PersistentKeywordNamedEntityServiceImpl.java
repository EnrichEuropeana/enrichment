package eu.europeana.enrichment.mongo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.model.impl.KeywordNamedEntity;
import eu.europeana.enrichment.mongo.dao.KeywordNamedEntityDaoImpl;

@Service(EnrichmentConstants.BEAN_ENRICHMENT_PERSISTENT_KEYWORD_NAMED_ENTITY_SERVICE)
public class PersistentKeywordNamedEntityServiceImpl {

	@Autowired
	KeywordNamedEntityDaoImpl keywordNamedEntityDao;
	
	public void saveKeywordNamedEntity(KeywordNamedEntity entity) {
		keywordNamedEntityDao.saveKeywordNamedEntity(entity);
	}
	
	public List<KeywordNamedEntity> getAllKeywordNamedEntities() {
		return keywordNamedEntityDao.findAllKeywordNamedEntities();
	}

}
