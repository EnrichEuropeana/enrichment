package eu.europeana.enrichment.mongo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.model.impl.NamedEntityAnnotationImpl;
import eu.europeana.enrichment.mongo.dao.NamedEntityAnnotationDao;
@Service(EnrichmentConstants.BEAN_ENRICHMENT_PERSISTENT_NAMED_ENTITY_ANNOTATION_SERVICE)
public class PersistentNamedEntityAnnotationServiceImpl implements PersistentNamedEntityAnnotationService {

	//@Resource(name = "namedEntityAnnotationDao")
	@Autowired
	NamedEntityAnnotationDao namedEntityAnnotationDao;
	
	@Override
	public NamedEntityAnnotationImpl findNamedEntityAnnotation(String id) {
		
		return namedEntityAnnotationDao.findNamedEntityAnnotation(id);
	}

	@Override
	public List<NamedEntityAnnotationImpl> findNamedEntityAnnotationWithStoryAndItemId(String storyId, String itemId) {
		
		return namedEntityAnnotationDao.findNamedEntityAnnotationWithStoryAndItemId(storyId, itemId);
	}
	
	@Override
	public List<NamedEntityAnnotationImpl> findNamedEntityAnnotation(String storyId, String itemId, String property, String wikidataId, List<String> linkedByNerTools) {
		return namedEntityAnnotationDao.findNamedEntityAnnotation(storyId, itemId, property, wikidataId, linkedByNerTools);
	}

	@Override
	public NamedEntityAnnotationImpl findNamedEntityAnnotationWithStoryIdItemIdAndWikidataId(String storyId, String itemId, String wikidataId) {
		
		return namedEntityAnnotationDao.findNamedEntityAnnotationWithStoryIdItemIdAndWikidataId(storyId, itemId, wikidataId);
	}

	@Override
	public void saveNamedEntityAnnotation(NamedEntityAnnotationImpl entity) {
		namedEntityAnnotationDao.saveNamedEntityAnnotation(entity);		
	}

	@Override
	public void deleteNamedEntityAnnotationById(String id) {
		
		namedEntityAnnotationDao.deleteNamedEntityAnnotationById(id);		
	}
	
	@Override
	public void deleteNamedEntityAnnotation(String storyId,String itemId, String property, String wikidataId) {
		namedEntityAnnotationDao.deleteNamedEntityAnnotation(storyId,itemId, property, wikidataId);
	}
}
