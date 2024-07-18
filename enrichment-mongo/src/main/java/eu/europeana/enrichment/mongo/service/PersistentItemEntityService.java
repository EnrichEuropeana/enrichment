package eu.europeana.enrichment.mongo.service;

import java.util.List;

import eu.europeana.enrichment.definitions.model.impl.ItemEntityImpl;

public interface PersistentItemEntityService {
	
	public List<ItemEntityImpl> get_N_ItemEntities(int limit, int skip);
	public ItemEntityImpl findItemEntity(String storyId, String itemId);
	public List<ItemEntityImpl> findAllItemsOfStory(String storyId);
	/*
	 * This method saves and updates story item entities into the Mongo database
	 * 
	 * @param entity					story item entity which should be saved
	 * 									or updated
	 * @return
	 */
	public ItemEntityImpl saveItemEntity(ItemEntityImpl entity);
	/*
	 * This method saves and updates a list of story item entities into the Mongo database
	 * 
	 * @param entities					a list of story item entities which should
	 * 									be saved or updated
	 * @return
	 */
	public void saveStoryItemEntities(List<ItemEntityImpl> entities);
	/*
	 * This method deletes story item entities from the Mongo database
	 * 
	 * @param entity					story item entity which should be deleted
	 * @return
	 */
	public void deleteItemEntity(ItemEntityImpl entity);
	
	public void deleteAllItemsOfStory(String storyId);

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

}
