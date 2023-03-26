package eu.europeana.enrichment.mongo.dao;

import java.util.List;

import eu.europeana.enrichment.model.impl.NamedEntityAnnotationImpl;

public interface NamedEntityAnnotationDao {
	
	public List<NamedEntityAnnotationImpl> getAllNamedEntityAnnotations();
	public NamedEntityAnnotationImpl findNamedEntityAnnotation(String id);
	public List<NamedEntityAnnotationImpl> findNamedEntityAnnotationWithStoryAndItemId(String storyId, String itemId);
	public NamedEntityAnnotationImpl findNamedEntityAnnotationWithStoryIdItemIdAndWikidataId(String storyId, String itemId, String wikidataId);
	public List<NamedEntityAnnotationImpl> findNamedEntityAnnotation(String storyId, String itemId, String property, String wikidataId, List<String> linkedByNerTools);
	public void saveNamedEntityAnnotation(NamedEntityAnnotationImpl entity);
	public long deleteNamedEntityAnnotationById(String id);
	public long deleteNamedEntityAnnotation(String storyId, String itemId, String property, String wikidataId);
	public void deleteAllNamedEntityAnnotation();

}
