package eu.europeana.enrichment.mongo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.model.impl.StoryEntityImpl;
import eu.europeana.enrichment.mongo.dao.StoryEntityDao;

@Service(EnrichmentConstants.BEAN_ENRICHMENT_PERSISTENT_STORY_ENTITY_SERVICE)
public class PersistentStoryEntityServiceImpl implements PersistentStoryEntityService {

	@Autowired
	StoryEntityDao storyEntityDao;
	
	@Override
	public StoryEntityImpl findStoryEntity(String storyId) {
		return storyEntityDao.findStoryEntity(storyId);
	}
	
	@Override
	public List<StoryEntityImpl> findStoryEntities(String storyId) {
		return storyEntityDao.findStoryEntities(storyId);
	}

	@Override
	public List<StoryEntityImpl> getAllStoryEntities() {
		return storyEntityDao.findAllStoryEntities();
	}

	@Override
	public void saveStoryEntity(StoryEntityImpl entity) {
		storyEntityDao.saveStoryEntity(entity);
	}

	@Override
	public void saveStoryEntities(List<StoryEntityImpl> entities) {
		for(StoryEntityImpl entity : entities) {
			saveStoryEntity(entity);
		}
	}

	@Override
	public void deleteStoryEntity(StoryEntityImpl entity) {
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
