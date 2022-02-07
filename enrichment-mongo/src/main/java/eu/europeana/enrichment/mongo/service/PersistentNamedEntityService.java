package eu.europeana.enrichment.mongo.service;

import java.util.List;

import eu.europeana.enrichment.model.NamedEntity;

public interface PersistentNamedEntityService {
	
	/*
	 * This method retrieves a named entity from the Mongo 
	 * database based on their key
	 * 
	 * @param key						label of the named entity
	 * @return							a database named entity 
	 */
	public NamedEntity findNamedEntity(String key);
	public List<NamedEntity> findNamedEntitiesWithAdditionalInformation(String storyId, String itemId, String type);

	/*
	 * This method retrieves all named entities from the Mongo database
	 * 
	 * @return							list of database named entities
	 */
	public List<NamedEntity> getAllNamedEntities();
	/*
	 * This method saves and updates named entities into the Mongo database
	 * 
	 * @param entity					named entity which should be saved
	 * 									or updated
	 * @return
	 */
	public void saveNamedEntity(NamedEntity entity);
	/*
	 * This method saves and updates a list of named entities into the Mongo database
	 * 
	 * @param entities					a list of named entities which should
	 * 									be saved or updated
	 * @return
	 */
	public void saveNamedEntities(List<NamedEntity> entities);

	/**
	 * Deletes all NamedEntities form the Mongo db
	 */
	public void deleteAllNamedEntities ();
	
	public void deletePositionEntitiesFromNamedEntity(String storyId,String itemId, String fieldUsedForNER);
	List<NamedEntity> findNamedEntitiesWithAdditionalInformation(String storyId, String itemId, String type,
			List<String> nerTools);

}
