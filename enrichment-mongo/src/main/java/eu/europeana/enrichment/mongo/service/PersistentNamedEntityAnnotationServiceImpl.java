package eu.europeana.enrichment.mongo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.definitions.model.impl.NamedEntityAnnotationImpl;
import eu.europeana.enrichment.mongo.dao.NamedEntityAnnotationDao;
@Service(EnrichmentConstants.BEAN_ENRICHMENT_PERSISTENT_NAMED_ENTITY_ANNOTATION_SERVICE)
public class PersistentNamedEntityAnnotationServiceImpl implements PersistentNamedEntityAnnotationService {

	//@Resource(name = "namedEntityAnnotationDao")
	@Autowired
	NamedEntityAnnotationDao namedEntityAnnotationDao;

	@Override
	public List<NamedEntityAnnotationImpl> findAnnotations(String storyId, String itemId, String property, String wikidataId, List<String> linkedByNerTools) {
		return namedEntityAnnotationDao.findAnnotations(storyId, itemId, property, wikidataId, linkedByNerTools);
	}

	@Override
	public void saveNamedEntityAnnotation(NamedEntityAnnotationImpl entity) {
		namedEntityAnnotationDao.saveNamedEntityAnnotation(entity);		
	}
	
	@Override
	public void deleteNamedEntityAnnotation(String storyId,String itemId, String property, String wikidataId) {
		namedEntityAnnotationDao.deleteNamedEntityAnnotation(storyId,itemId, property, wikidataId);
	}
}
