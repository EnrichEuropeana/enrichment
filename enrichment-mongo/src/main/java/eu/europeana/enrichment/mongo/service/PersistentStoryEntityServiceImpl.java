package eu.europeana.enrichment.mongo.service;

import java.util.List;

import javax.annotation.Resource;

import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.mongo.dao.StoryEntityDao;

public class PersistentStoryEntityServiceImpl implements PersistentStoryEntityService {

	@Resource(name = "storyEntityDao")
	StoryEntityDao storyEntityDao;
	
	@Override
	public StoryEntity findStoryEntity(String storyId) {
		return storyEntityDao.findStoryEntity(storyId);
	}

	@Override
	public List<StoryEntity> getAllStoryEntities() {
		return storyEntityDao.findAllStoryEntities();
	}

	@Override
	public void saveStoryEntity(StoryEntity entity) {
		storyEntityDao.saveStoryEntity(entity);
	}

	@Override
	public void saveStoryEntities(List<StoryEntity> entities) {
		for(StoryEntity entity : entities) {
			saveStoryEntity(entity);
		}
	}

	@Override
	public void deleteStoryEntity(StoryEntity entity) {
		storyEntityDao.deleteStoryEntity(entity);
	}
	
	@Override
	public void updateNerToolsForStory(String storyId, String nerTool) {
		storyEntityDao.updateNerToolsForStory(storyId, nerTool);
	}

//	@Override
//	public List<String> getNerToolsForStory(String storyId) {
//		return storyEntityDao.getNerToolsForStory(storyId);
//	}
//
//	@Override
//	public int getNumerAnalysedNamedEntities(String field) {
//		return storyEntityDao.getNumerAnalysedNamedEntities(field);
//	}


}
