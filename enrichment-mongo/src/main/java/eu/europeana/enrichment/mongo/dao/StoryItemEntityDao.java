package eu.europeana.enrichment.mongo.dao;

import java.util.List;

import eu.europeana.enrichment.model.StoryItemEntity;

/*
 * This interface defines database actions for story items
 */
public interface StoryItemEntityDao {

	public StoryItemEntity findStoryItemEntity(String key);
	public List<StoryItemEntity> findStoryItemEntitiesFromStory(String storyId);
	//public List<NamedEntity> getAllStoryItemEntities();
	public void saveStoryItemEntity(StoryItemEntity entity);
	public void deleteStoryItemEntity(StoryItemEntity entity);
	public void deleteStoryItemEntityByStoryItemId(String key);
}
