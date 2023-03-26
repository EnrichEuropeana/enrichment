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
import eu.europeana.enrichment.model.impl.ItemEntityImpl;
import eu.europeana.enrichment.mongo.utils.MorphiaUtils;

@Repository(EnrichmentConstants.BEAN_ENRICHMENT_ITEM_ENTITY_DAO)
public class ItemEntityDaoImpl implements ItemEntityDao{

	@Autowired
	StoryEntityDao storyEntityDao;

	@Autowired
	private Datastore enrichmentDatastore;
	
	private static Map<String, List<String>> nerToolsForItem = new HashMap<String, List<String>>();
	
	@Override
	public List<ItemEntityImpl> findAllItemEntities() {
		return enrichmentDatastore.find(ItemEntityImpl.class).iterator().toList();
	}
	
	@Override
	public ItemEntityImpl findItemEntityFromStory(String storyId, String itemId)
	{
		return enrichmentDatastore.find(ItemEntityImpl.class).filter(
            eq(EnrichmentConstants.STORY_ID, storyId),
            eq(EnrichmentConstants.ITEM_ID, itemId))
			.first();		
	}
	
	@Override
	public ItemEntityImpl findItemEntity(String itemId)
	{
		return enrichmentDatastore.find(ItemEntityImpl.class).filter(
            eq(EnrichmentConstants.ITEM_ID, itemId))
            .first();		
	}
	
	@Override
	public List<ItemEntityImpl> findItemEntitiesFromStory(String storyId, String itemId)
	{
		return enrichmentDatastore.find(ItemEntityImpl.class).filter(
            eq(EnrichmentConstants.STORY_ID, storyId),
            eq(EnrichmentConstants.ITEM_ID, itemId))
            .iterator().toList();
	}
	
	@Override
	public List<ItemEntityImpl> findStoryItemEntitiesFromStory(String storyId){
		return enrichmentDatastore.find(ItemEntityImpl.class).filter(
                eq(EnrichmentConstants.STORY_ID, storyId))
                .iterator()
                .toList();
	}

	@Override
	public void saveItemEntity(ItemEntityImpl entity) {
		this.enrichmentDatastore.save(entity);
	}

	@Override
	public void deleteItemEntity(ItemEntityImpl entity) {
		enrichmentDatastore.find(ItemEntityImpl.class).filter(
            eq(EnrichmentConstants.OBJECT_ID,entity.getId()))
			.delete();
	}

	@Override
	public long deleteAllItemsOfStory(String storyId) {
		return enrichmentDatastore.find(ItemEntityImpl.class).filter(
                eq(EnrichmentConstants.STORY_ID,storyId))
                .delete(MorphiaUtils.MULTI_DELETE_OPTS)
                .getDeletedCount();
	}
	
	@Override
	public void updateNerToolsForItem(String itemId, String nerTool) {

		if(nerToolsForItem.get(itemId)==null)
		{
			List<String> toolsForNer = new ArrayList<String>();
			toolsForNer.add(nerTool);
			nerToolsForItem.put(itemId, toolsForNer);
		}
		else if(!nerToolsForItem.get(itemId).contains(nerTool))
		{
			nerToolsForItem.get(itemId).add(nerTool);
		}

	}

	@Override
	public List<String> getNerToolsForItem(String itemId) {
		return nerToolsForItem.get(itemId);
	}

}
