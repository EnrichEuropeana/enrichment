package eu.europeana.enrichment.mongo.dao;

import java.util.List;

import eu.europeana.enrichment.model.ItemEntity;

/*
 * This interface defines database actions for story items
 */
public interface ItemEntityDao {

	public ItemEntity findItemEntity(String key);
	public ItemEntity findItemEntityFromStory (String itemId, String storyId);
	public List<ItemEntity> findStoryItemEntitiesFromStory(String storyId);
	//public List<NamedEntity> getAllStoryItemEntities();
	public void saveItemEntity(ItemEntity entity);
	public void deleteItemEntity(ItemEntity entity);
	public void deleteItemEntityByStoryItemId(String key);
}
