package eu.europeana.enrichment.mongo.service;

import java.util.List;

import eu.europeana.enrichment.model.impl.NamedEntityAnnotationImpl;

public interface PersistentNamedEntityAnnotationService {

	public List<NamedEntityAnnotationImpl> findAnnotations(String storyId, String itemId, String property, String wikidataId, List<String> linkedByNerTools);
	public void saveNamedEntityAnnotation(NamedEntityAnnotationImpl entity);	
	public void deleteNamedEntityAnnotation(String storyId,String itemId, String property, String wikidataId);

	
}
