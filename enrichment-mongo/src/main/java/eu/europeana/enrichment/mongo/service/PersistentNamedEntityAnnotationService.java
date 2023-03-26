package eu.europeana.enrichment.mongo.service;

import java.util.List;

import eu.europeana.enrichment.model.impl.NamedEntityAnnotationImpl;

public interface PersistentNamedEntityAnnotationService {


	/**
	 * This method retrieves an entity from the Mongo 
	 * database based on their id
	 * 
	 * @param id
	 * @return
	 */
	public NamedEntityAnnotationImpl findNamedEntityAnnotation(String id);
	
	/**
	 * This method retrieves an entity from the Mongo 
	 * database based on their storyId
	 * 
	 * @param storyId
	 * @param itemId
	 * @return
	 */
	public List<NamedEntityAnnotationImpl> findNamedEntityAnnotationWithStoryAndItemId(String storyId, String itemId);

	/**
	 * This method retrieves an entity from the Mongo 
	 * database based on the given parameters.
	 * 
	 * @param storyId
	 * @param itemId
	 * @param property
	 * @param wikidataId
	 * @param nerTools
	 * @return
	 */
	public List<NamedEntityAnnotationImpl> findNamedEntityAnnotation(String storyId, String itemId, String property, String wikidataId, List<String> linkedByNerTools);
	/**
	 * This method retrieves an entity from the Mongo 
	 * database based on their storyId and wikidataId
	 * 
	 * @param storyId
	 * @param itemId 
	 * @param wikidataId
	 * @return
	 */
	
	public NamedEntityAnnotationImpl findNamedEntityAnnotationWithStoryIdItemIdAndWikidataId(String storyId, String itemId, String wikidataId);

    /**
     * This method saves and updates entities into the Mongo database
     * @param entity
     */
	
	public void saveNamedEntityAnnotation(NamedEntityAnnotationImpl entity);
	
	/**
	 * This method deletes an entity from the Mongo database base on its id
	 */
	public void deleteNamedEntityAnnotationById(String id);
	
	public void deleteNamedEntityAnnotation(String storyId,String itemId, String property, String wikidataId);

	
}
