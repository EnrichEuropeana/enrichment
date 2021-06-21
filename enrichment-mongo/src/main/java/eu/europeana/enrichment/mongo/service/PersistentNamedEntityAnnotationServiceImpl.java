package eu.europeana.enrichment.mongo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.europeana.enrichment.common.commons.AppConfigConstants;
import eu.europeana.enrichment.model.NamedEntityAnnotation;
import eu.europeana.enrichment.mongo.dao.NamedEntityAnnotationDao;
@Service(AppConfigConstants.BEAN_ENRICHMENT_PERSISTENT_NAMED_ENTITY_ANNOTATION_SERVICE)
public class PersistentNamedEntityAnnotationServiceImpl implements PersistentNamedEntityAnnotationService {

	//@Resource(name = "namedEntityAnnotationDao")
	@Autowired
	NamedEntityAnnotationDao namedEntityAnnotationDao;
	
	@Override
	public NamedEntityAnnotation findNamedEntityAnnotation(String id) {
		
		return namedEntityAnnotationDao.findNamedEntityAnnotation(id);
	}

	@Override
	public List<NamedEntityAnnotation> findNamedEntityAnnotationWithStoryAndItemId(String storyId, String itemId) {
		
		return namedEntityAnnotationDao.findNamedEntityAnnotationWithStoryAndItemId(storyId, itemId);
	}
	
	@Override
	public List<NamedEntityAnnotation> findNamedEntityAnnotationWithStoryItemIdAndProperty(String storyId, String itemId, String property) {
		
		return namedEntityAnnotationDao.findNamedEntityAnnotationWithStoryItemIdAndProperty(storyId, itemId, property);
	}


	@Override
	public NamedEntityAnnotation findNamedEntityAnnotationWithStoryIdItemIdAndWikidataId(String storyId, String itemId, String wikidataId) {
		
		return namedEntityAnnotationDao.findNamedEntityAnnotationWithStoryIdItemIdAndWikidataId(storyId, itemId, wikidataId);
	}

	@Override
	public void saveNamedEntityAnnotation(NamedEntityAnnotation entity) {

		namedEntityAnnotationDao.saveNamedEntityAnnotation(entity);		
	}

	@Override
	public void deleteNamedEntityAnnotationById(String id) {
		
		namedEntityAnnotationDao.deleteNamedEntityAnnotationById(id);		
	}
	
	@Override
	public void deleteNamedEntityAnnotation(String storyId,String itemId) {
		namedEntityAnnotationDao.deleteNamedEntityAnnotation(storyId,itemId);
	}



}
