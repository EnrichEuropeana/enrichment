package eu.europeana.enrichment.mongo.service;

import java.util.List;

import eu.europeana.enrichment.model.ItemEntity;

public interface PersistentItemEntityService {

	/*
	 * This method retrieves a story item entity from the Mongo 
	 * database based on their key
	 * 
	 * @param itemId				story item id of the story item entity
	 * @return							a database story item entity 
	 */
	public ItemEntity findItemEntity(String storyItemId);
	
	/**
	 * This method retrieves ItemEntity from the db for the given story
	 * @param storyId
	 * @param itemId
	 * @return
	 */
	public ItemEntity findItemEntityFromStory(String storyId, String itemId);
	
	public List<ItemEntity> findStoryItemEntitiesFromStory(String storyId);
	/*
	 * This method retrieves all story item entities from the Mongo database
	 * 
	 * @return							list of database story item entities
	 */
	public List<ItemEntity> getAllStoryItemEntities();
	/*
	 * This method saves and updates story item entities into the Mongo database
	 * 
	 * @param entity					story item entity which should be saved
	 * 									or updated
	 * @return
	 */
	public void saveItemEntity(ItemEntity entity) throws Exception;
	/*
	 * This method saves and updates a list of story item entities into the Mongo database
	 * 
	 * @param entities					a list of story item entities which should
	 * 									be saved or updated
	 * @return
	 */
	public void saveStoryItemEntities(List<ItemEntity> entities) throws Exception;
	/*
	 * This method deletes story item entities from the Mongo database
	 * 
	 * @param entity					story item entity which should be deleted
	 * @return
	 */
	public void deleteItemEntity(ItemEntity entity);

	/**
	 * This function updates the NER tools list that are already applied to the given story
	 * 
	 * @param itemId
	 * @param nerTool
	 */
	void updateNerToolsForItem(String itemId, String nerTool);
	
	/**
	 * This function retrieves the NER tools list that are already applied to the given story
	 * 
	 * @param itemId
	 * @return
	 */
	
//	int getNumerAnalysedNamedEntities(String field);
//	
//	List<String> getNerToolsForItem(String itemId);

}
