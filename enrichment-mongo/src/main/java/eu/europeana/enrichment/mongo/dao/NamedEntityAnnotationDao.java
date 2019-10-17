package eu.europeana.enrichment.mongo.dao;

import java.util.List;

import eu.europeana.enrichment.model.NamedEntityAnnotation;

public interface NamedEntityAnnotationDao {
	
	public NamedEntityAnnotation findNamedEntityAnnotation(String id);
	public List<NamedEntityAnnotation> findNamedEntityAnnotationWithStoryAndItemId(String storyId, String itemId);
	public NamedEntityAnnotation findNamedEntityAnnotationWithStoryIdItemIdAndWikidataId(String storyId, String itemId, String wikidataId);
	public void saveNamedEntityAnnotation(NamedEntityAnnotation entity);
	public void deleteNamedEntityAnnotation(NamedEntityAnnotation entity);
	public void deleteNamedEntityAnnotationById(String id);
	public void deleteNamedEntityAnnotation(String storyId,String itemId);
	public void deleteAllNamedEntityAnnotation();

}
