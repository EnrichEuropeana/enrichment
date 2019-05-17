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
	public List<NamedEntity> findNamedEntitiesWithAdditionalInformation(String itemId, boolean translation);
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
	/*
	 * This method deletes named entities from the Mongo database
	 * 
	 * @param entity					named entity which should be deleted
	 * @return
	 */
	public void deleteNamedEntity(NamedEntity entity);
	
	/**
	 * Deletes all NamedEntities form the Mongo db
	 */
	public void deleteAllNamedEntities ();

}
