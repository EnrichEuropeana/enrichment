package eu.europeana.enrichment.mongo.dao;

import java.util.List;

import eu.europeana.enrichment.model.NamedEntityAnnotation;

public interface NamedEntityAnnotationDao {
	
	public NamedEntityAnnotation findNamedEntityAnnotation(String id);
	public List<NamedEntityAnnotation> findNamedEntityAnnotationWithStoryAndItemId(String storyId, String itemId);
	public NamedEntityAnnotation findNamedEntityAnnotationWithStoryIdItemIdAndWikidataId(String storyId, String itemId, String wikidataId);
	public List<NamedEntityAnnotation> findNamedEntityAnnotation(String storyId, String itemId, String property, List<String> nerTools);
	public void saveNamedEntityAnnotation(NamedEntityAnnotation entity);
	public long deleteNamedEntityAnnotationById(String id);
	public long deleteNamedEntityAnnotation(String storyId, String itemId, String property);
	public void deleteAllNamedEntityAnnotation();

}
