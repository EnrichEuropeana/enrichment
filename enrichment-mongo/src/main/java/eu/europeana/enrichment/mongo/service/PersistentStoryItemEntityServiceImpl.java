package eu.europeana.enrichment.mongo.service;

import java.util.List;

import javax.annotation.Resource;

import eu.europeana.enrichment.model.StoryItemEntity;
import eu.europeana.enrichment.mongo.dao.StoryItemEntityDao;

public class PersistentStoryItemEntityServiceImpl implements PersistentStoryItemEntityService {

	@Resource(name = "storyItemEntityDao")
	StoryItemEntityDao storyItemEntityDao;
	
	@Override
	public StoryItemEntity findStoryItemEntity(String storyItemId) {
		return storyItemEntityDao.findStoryItemEntity(storyItemId);
	}

	@Override
	public List<StoryItemEntity> findStoryItemEntitiesFromStory(String storyId) {
		// TODO Auto-generated method stub
		return storyItemEntityDao.findStoryItemEntitiesFromStory(storyId);
	}

	@Override
	public List<StoryItemEntity> getAllStoryItemEntities() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveStoryItemEntity(StoryItemEntity entity) {
		storyItemEntityDao.saveStoryItemEntity(entity);
	}

	@Override
	public void saveStoryItemEntities(List<StoryItemEntity> entities) {
		for(StoryItemEntity entity : entities) {
			saveStoryItemEntity(entity);
		}
	}

	@Override
	public void deleteStoryItemEntity(StoryItemEntity entity) {
		storyItemEntityDao.deleteStoryItemEntity(entity);
	}

}
