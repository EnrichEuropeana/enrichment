package eu.europeana.enrichment.mongo.dao;

import static dev.morphia.query.experimental.filters.Filters.eq;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import dev.morphia.Datastore;
import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.model.impl.StoryEntityImpl;
import eu.europeana.enrichment.mongo.utils.MorphiaUtils;
@Repository(EnrichmentConstants.BEAN_ENRICHMENT_STORY_ENTITY_DAO)
public class StoryEntityDaoImpl implements StoryEntityDao{

	@Autowired
	private Datastore enrichmentDatastore; 
	
	private static Map<String, List<String>> nerToolsForStory = new HashMap<String, List<String>>();	
	
	@Override
	public StoryEntityImpl findStoryEntity(String key) {
		return enrichmentDatastore.find(StoryEntityImpl.class).filter(
                eq(EnrichmentConstants.STORY_ID, key))
                .first();
	}
	
	@Override
	public List<StoryEntityImpl> findStoryEntities(String key) {
		return enrichmentDatastore.find(StoryEntityImpl.class).filter(
                eq(EnrichmentConstants.STORY_ID, key))
				.iterator().toList();
	}

	@Override
	public void saveStoryEntity(StoryEntityImpl entity) {
		if(entity==null) return;
		this.enrichmentDatastore.save(entity);
	}

	@Override
	public void deleteStoryEntity(StoryEntityImpl entity) {
		enrichmentDatastore.find(StoryEntityImpl.class).filter(
            eq(EnrichmentConstants.OBJECT_ID,entity.getId()))
			.delete();
	}

	@Override
	public long deleteStoryEntityByStoryId(String key) {
		return enrichmentDatastore.find(StoryEntityImpl.class).filter(
                eq(EnrichmentConstants.STORY_ID,key))
                .delete(MorphiaUtils.MULTI_DELETE_OPTS)
                .getDeletedCount();
	}

	@Override
	public List<StoryEntityImpl> findAllStoryEntities() {
		return enrichmentDatastore.find(StoryEntityImpl.class).iterator().toList();
	}

	@Override
	public void updateNerToolsForStory(String storyId, String nerTool) {

		if(nerToolsForStory.get(storyId)==null)
		{
			List<String> toolsForNer = new ArrayList<String>();
			toolsForNer.add(nerTool);
			nerToolsForStory.put(storyId, toolsForNer);
		}
		else if(!nerToolsForStory.get(storyId).contains(nerTool))
		{
			nerToolsForStory.get(storyId).add(nerTool);
		}

	}

	@Override
	public List<String> getNerToolsForStory(String storyId) {
		return nerToolsForStory.get(storyId);
	}
}
