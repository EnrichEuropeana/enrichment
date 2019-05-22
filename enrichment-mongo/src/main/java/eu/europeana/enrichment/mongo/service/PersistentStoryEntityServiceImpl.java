package eu.europeana.enrichment.mongo.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.mongodb.morphia.query.Query;

import eu.europeana.enrichment.model.NamedEntity;
import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.mongo.dao.StoryEntityDao;
import eu.europeana.enrichment.mongo.model.DBNamedEntityImpl;

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

}
