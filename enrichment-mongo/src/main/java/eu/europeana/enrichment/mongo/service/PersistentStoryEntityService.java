package eu.europeana.enrichment.mongo.service;

import java.util.List;

import eu.europeana.enrichment.definitions.model.impl.StoryEntityImpl;

public interface PersistentStoryEntityService {
	/*
	 * This method retrieves a story entity from the Mongo 
	 * database based on their key
	 * 
	 * @param storyId					story id of the story entity
	 * @return							a database story entity 
	 */
	public StoryEntityImpl findStoryEntity(String storyId);
	
	/*
	 * This method retrieves all story entities from the Mongo database
	 * 
	 * @return							list of database story entities
	 */
	public List<StoryEntityImpl> get_N_StoryEntities(int limit, int skip);
	/*
	 * This method saves and updates story entities into the Mongo database
	 * 
	 * @param entity					story entity which should be saved
	 * 									or updated
	 * @return
	 */
	public StoryEntityImpl saveStoryEntity(StoryEntityImpl entity);
	/*
	 * This method saves and updates a list of story entities into the Mongo database
	 * 
	 * @param entities					a list of story entities which should
	 * 									be saved or updated
	 * @return
	 */
	public void saveStoryEntities(List<StoryEntityImpl> entities);
	/*
	 * This method deletes story entities from the Mongo database
	 * 
	 * @param entity					story entity which should be deleted
	 * @return
	 */
	public void deleteStoryEntity(StoryEntityImpl entity);
	
	/**
	 * This function updates the NER tools list that are already applied to the given story
	 * @param storyId
	 * @param nerTool
	 */
	void updateNerToolsForStory(String storyId, String nerTool);
	
	/**
	 * This function retrieves the NER tools list that are already applied to the given story
	 * @param storyId
	 * @return
	 */
//	List<String> getNerToolsForStory(String storyId);
//	
//	int getNumerAnalysedNamedEntities(String field);

}
