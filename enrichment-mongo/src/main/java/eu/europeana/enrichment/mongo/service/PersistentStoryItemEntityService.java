package eu.europeana.enrichment.mongo.service;

import java.util.List;

import eu.europeana.enrichment.model.StoryItemEntity;

public interface PersistentStoryItemEntityService {

	/*
	 * This method retrieves a story item entity from the Mongo 
	 * database based on their key
	 * 
	 * @param storyItemId				story item id of the story item entity
	 * @return							a database story item entity 
	 */
	public StoryItemEntity findStoryItemEntity(String storyItemId);
	public List<StoryItemEntity> findStoryItemEntitiesFromStory(String storyId);
	/*
	 * This method retrieves all story item entities from the Mongo database
	 * 
	 * @return							list of database story item entities
	 */
	public List<StoryItemEntity> getAllStoryItemEntities();
	/*
	 * This method saves and updates story item entities into the Mongo database
	 * 
	 * @param entity					story item entity which should be saved
	 * 									or updated
	 * @return
	 */
	public void saveStoryItemEntity(StoryItemEntity entity);
	/*
	 * This method saves and updates a list of story item entities into the Mongo database
	 * 
	 * @param entities					a list of story item entities which should
	 * 									be saved or updated
	 * @return
	 */
	public void saveStoryItemEntities(List<StoryItemEntity> entities);
	/*
	 * This method deletes story item entities from the Mongo database
	 * 
	 * @param entity					story item entity which should be deleted
	 * @return
	 */
	public void deleteStoryItemEntity(StoryItemEntity entity);

}
