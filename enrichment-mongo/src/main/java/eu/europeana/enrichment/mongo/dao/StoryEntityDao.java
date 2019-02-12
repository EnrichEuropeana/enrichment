package eu.europeana.enrichment.mongo.dao;

import eu.europeana.enrichment.model.StoryEntity;

/*
 * This interface defines database actions for stories
 */
public interface StoryEntityDao {

	public StoryEntity findStoryEntity(String key);
	//public List<NamedEntity> getAllStoryEntities();
	public void saveStoryEntity(StoryEntity entity);
	public void deleteStoryEntity(StoryEntity entity);
	public void deleteStoryEntityByStoryId(String key);
}
