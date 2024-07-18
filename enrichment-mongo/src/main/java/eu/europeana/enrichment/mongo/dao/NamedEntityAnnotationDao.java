package eu.europeana.enrichment.mongo.dao;

import java.util.List;

import eu.europeana.enrichment.definitions.model.impl.NamedEntityAnnotationImpl;

public interface NamedEntityAnnotationDao {
	
	public List<NamedEntityAnnotationImpl> getAllNamedEntityAnnotations();
	public NamedEntityAnnotationImpl saveNamedEntityAnnotation(NamedEntityAnnotationImpl entity);
	public long deleteNamedEntityAnnotation(String storyId, String itemId, String property, String wikidataId);
	public List<NamedEntityAnnotationImpl> findAnnotations(String storyId, String itemId, String property, String wikidataId, List<String> linkedByNerTools);
}
