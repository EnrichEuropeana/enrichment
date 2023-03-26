package eu.europeana.enrichment.mongo.dao;

import java.util.List;

import eu.europeana.enrichment.model.impl.StoryEntityImpl;

/*
 * This interface defines database actions for stories
 */
public interface StoryEntityDao {

	public StoryEntityImpl findStoryEntity(String key);
	public List<StoryEntityImpl> findStoryEntities(String key);
	public List<StoryEntityImpl> findAllStoryEntities();
	public void saveStoryEntity(StoryEntityImpl entity);
	public void deleteStoryEntity(StoryEntityImpl entity);
	public long deleteStoryEntityByStoryId(String key);
	public void updateNerToolsForStory(String storyId, String nerTool);
	public List<String> getNerToolsForStory(String storyId);
//	public int getNumerAnalysedNamedEntities(String field);
//	public void setNumerAnalysedNamedEntities(String field, int num);
}
