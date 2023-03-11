package eu.europeana.enrichment.mongo.dao;

import java.util.List;

import eu.europeana.enrichment.model.NamedEntityAnnotation;
import eu.europeana.enrichment.model.impl.NamedEntityAnnotationImpl;

public interface NamedEntityAnnotationDao {
	
	public List<NamedEntityAnnotationImpl> getAllNamedEntityAnnotations();
	public NamedEntityAnnotation findNamedEntityAnnotation(String id);
	public List<NamedEntityAnnotation> findNamedEntityAnnotationWithStoryAndItemId(String storyId, String itemId);
	public NamedEntityAnnotation findNamedEntityAnnotationWithStoryIdItemIdAndWikidataId(String storyId, String itemId, String wikidataId);
	public List<NamedEntityAnnotation> findNamedEntityAnnotation(String storyId, String itemId, String property, String wikidataId, List<String> nerTools);
	public void saveNamedEntityAnnotation(NamedEntityAnnotation entity);
	public long deleteNamedEntityAnnotationById(String id);
	public long deleteNamedEntityAnnotation(String storyId, String itemId, String property, String wikidataId);
	public void deleteAllNamedEntityAnnotation();

}
