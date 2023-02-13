package eu.europeana.enrichment.mongo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.model.impl.Keyword;
import eu.europeana.enrichment.mongo.dao.KeywordDaoImpl;

@Service(EnrichmentConstants.BEAN_ENRICHMENT_PERSISTENT_KEYWORD_SERVICE)
public class PersistentKeywordServiceImpl {

	@Autowired
	KeywordDaoImpl keywordDao;
	
	public void saveKeyword(Keyword keyword) {
		keywordDao.saveKeyword(keyword);
	}
	
	public List<Keyword> getAllKeywords() {
		return keywordDao.findAllKeywords();
	}

	public Keyword findByPropertyId(String propertyId) {
		return keywordDao.findByPropertyId(propertyId);
	}
}
