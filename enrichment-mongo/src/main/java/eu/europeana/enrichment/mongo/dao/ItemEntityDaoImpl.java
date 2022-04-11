package eu.europeana.enrichment.mongo.dao;

import static dev.morphia.query.experimental.filters.Filters.eq;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import dev.morphia.Datastore;
import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.impl.ItemEntityImpl;
import eu.europeana.enrichment.mongo.utils.MorphiaUtils;

@Repository(EnrichmentConstants.BEAN_ENRICHMENT_ITEM_ENTITY_DAO)
public class ItemEntityDaoImpl implements ItemEntityDao{

	@Autowired
	StoryEntityDao storyEntityDao;

	@Autowired
	private Datastore enrichmentDatastore;
	
	private static Map<String, List<String>> nerToolsForItem = new HashMap<String, List<String>>();
	
	private void addAdditionalInformation(ItemEntity dbEntity) {
		StoryEntity dbStoryEntity = storyEntityDao.findStoryEntity(dbEntity.getStoryId());
		dbEntity.setStoryEntity(dbStoryEntity);
	}
	
	@Override
	public List<ItemEntity> findAllItemEntities() {
		List<ItemEntityImpl> queryResult = enrichmentDatastore.find(ItemEntityImpl.class).iterator().toList();
		if(queryResult == null)
			return null;
		else
		{
			List<ItemEntity> tmpResult = new ArrayList<>();
			for(int index = queryResult.size()-1; index >= 0; index--) {
				ItemEntity dbEntity = queryResult.get(index);
				tmpResult.add(dbEntity);
			}
			return tmpResult;
		}
	}
	
	@Override
	public ItemEntity findItemEntityFromStory(String storyId, String itemId)
	{
		ItemEntityImpl dbEntity = enrichmentDatastore.find(ItemEntityImpl.class).filter(
                eq(EntityFields.STORY_ID, storyId),
                eq(EntityFields.ITEM_ID, itemId)
                )
                .first();		
		if (dbEntity!=null) 
			addAdditionalInformation(dbEntity);		
		return dbEntity;
	}
	
	@Override
	public ItemEntity findItemEntity(String itemId)
	{
		ItemEntityImpl dbEntity = enrichmentDatastore.find(ItemEntityImpl.class).filter(
                eq(EntityFields.ITEM_ID, itemId)
                )
                .first();		
		return dbEntity;
	}
	
	@Override
	public List<ItemEntityImpl> findItemEntitiesFromStory(String storyId, String itemId)
	{
		return enrichmentDatastore.find(ItemEntityImpl.class).filter(
            eq(EntityFields.STORY_ID, storyId),
            eq(EntityFields.ITEM_ID, itemId))
            .iterator().toList();
	}
	
	@Override
	public List<ItemEntity> findStoryItemEntitiesFromStory(String storyId){
		List<ItemEntityImpl> queryResult = enrichmentDatastore.find(ItemEntityImpl.class).filter(
                eq(EntityFields.STORY_ID, storyId))
                .iterator()
                .toList();
		if(queryResult == null)
			return null;
		else
		{
			List<ItemEntity> tmpResult = new ArrayList<>();
			for(int index = queryResult.size()-1; index >= 0; index--) {
				ItemEntityImpl dbEntity = queryResult.get(index);
				addAdditionalInformation(dbEntity);
				tmpResult.add(dbEntity);
			}
			return tmpResult;
		}
	}

	@Override
	public void saveItemEntity(ItemEntity entity) {
		this.enrichmentDatastore.save(entity);
	}

	@Override
	public void deleteItemEntity(ItemEntity entity) {
		enrichmentDatastore.find(ItemEntityImpl.class).filter(
            eq(EntityFields.OBJECT_ID,entity.getId()))
			.delete();
	}

	@Override
	public long deleteItemEntityByStoryItemId(String key) {
		return enrichmentDatastore.find(ItemEntityImpl.class).filter(
                eq(EntityFields.ITEM_ID,key))
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

//	@Override
//	public int getNumerAnalysedNamedEntities(String field) {
//		return numberAnalysedNamedEntities.get(field).intValue();
//	}
//
//	@Override
//	public void setNumerAnalysedNamedEntities(String field, int num) {
//		numberAnalysedNamedEntities.put(field, Integer.valueOf(num));
//	}


}
