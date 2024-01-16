package eu.europeana.enrichment.mongo.service;

import java.util.List;

import org.bson.types.ObjectId;

import eu.europeana.enrichment.definitions.model.impl.NamedEntityImpl;

public interface PersistentNamedEntityService {
	
	public NamedEntityImpl findNamedEntity(ObjectId objId);
	
	public List<NamedEntityImpl> findNamedEntities(String label, String type, String dbpediaId);
	
	public NamedEntityImpl findEqualNamedEntity(NamedEntityImpl ne);
	
	public List<NamedEntityImpl> findNamedEntitiesWithAdditionalInformation(String storyId, String itemId, String type, List<String> nerTools, boolean matchNerToolsExactly);

	/*
	 * This method retrieves all named entities from the Mongo database
	 * 
	 * @return							list of database named entities
	 */
	public List<NamedEntityImpl> get_N_NamedEntities(int limit, int skip);
	/*
	 * This method saves and updates named entities into the Mongo database
	 * 
	 * @param entity					named entity which should be saved
	 * 									or updated
	 * @return
	 */
	public NamedEntityImpl saveNamedEntity(NamedEntityImpl entity);
	/*
	 * This method saves and updates a list of named entities into the Mongo database
	 * 
	 * @param entities					a list of named entities which should
	 * 									be saved or updated
	 * @return
	 */
	public void saveNamedEntities(List<NamedEntityImpl> entities);

	/**
	 * Deletes all NamedEntities form the Mongo db
	 */
	public void deleteAllNamedEntities ();
	
	public void deletePositionEntitiesAndNamedEntities(String storyId,String itemId, String fieldUsedForNER);

}
