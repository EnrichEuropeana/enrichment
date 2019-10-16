package eu.europeana.enrichment.mongo.dao;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.mongo.model.DBItemEntityImpl;
import eu.europeana.enrichment.mongo.model.DBNamedEntityImpl;
import eu.europeana.enrichment.mongo.model.DBStoryEntityImpl;

public class ItemEntityDaoImpl implements ItemEntityDao{

	@Resource(name= "storyEntityDao")
	StoryEntityDao storyEntityDao;

	@Resource(name= "ItemEntityDao")
	ItemEntityDao ItemEntityDao;

	private Datastore datastore;
	
	private static Map<String, List<String>> nerToolsForItem;
	
	public ItemEntityDaoImpl(Datastore datastore) {
		this.datastore = datastore;
		nerToolsForItem = new HashMap<String, List<String>>();
	}
	
	private void addAdditionalInformation(ItemEntity dbEntity) {
		StoryEntity dbStoryEntity = storyEntityDao.findStoryEntity(dbEntity.getStoryId());
		dbEntity.setStoryEntity(dbStoryEntity);
	}
	
	@Override
	public ItemEntity findItemEntity(String key) {
		Query<DBItemEntityImpl> persistentStoryItemEntities = datastore.createQuery(DBItemEntityImpl.class);
		persistentStoryItemEntities.field("itemId").equal(key);
		List<DBItemEntityImpl> result = persistentStoryItemEntities.asList();
		if(result.size() == 0)
			return null;
		else {
			DBItemEntityImpl dbEntity = result.get(0);
			addAdditionalInformation(dbEntity);
			return dbEntity;
		}
	}
	
	@Override
	public List<ItemEntity> findAllItemEntities() {
		Query<DBItemEntityImpl> persistentItemEntities = datastore.createQuery(DBItemEntityImpl.class);		
		List<DBItemEntityImpl> result = persistentItemEntities.asList();
		if(result.size() == 0)
			return null;
		else
		{
			List<ItemEntity> tmpResult = new ArrayList<>();
			for(int index = result.size()-1; index >= 0; index--) {
				ItemEntity dbEntity = result.get(index);
				tmpResult.add(dbEntity);
			}
			return tmpResult;
		}
	}
	
	@Override
	public ItemEntity findItemEntityFromStory(String storyId, String itemId)
	{
		Query<DBItemEntityImpl> persistentItemEntities = datastore.createQuery(DBItemEntityImpl.class);
		persistentItemEntities.disableValidation().and(
				persistentItemEntities.criteria("storyId").equal(storyId),
				persistentItemEntities.criteria("itemId").equal(itemId)
				);
		List<DBItemEntityImpl> result = persistentItemEntities.asList();
		if(result.size() == 0)
			return null;
		else {
			DBItemEntityImpl dbEntity = result.get(0);
			addAdditionalInformation(dbEntity);
			return dbEntity;
		}

	}
	
	@Override
	public List<ItemEntity> findStoryItemEntitiesFromStory(String storyId){
		Query<DBItemEntityImpl> persistentStoryItemEntities = datastore.createQuery(DBItemEntityImpl.class);
		persistentStoryItemEntities.field("storyId").equal(storyId);
		List<DBItemEntityImpl> result = persistentStoryItemEntities.asList();
		List<ItemEntity> tmpResult = new ArrayList<>();
		for(int index = result.size()-1; index >= 0; index--) {
			DBItemEntityImpl dbEntity = result.get(index);
			addAdditionalInformation(dbEntity);
			tmpResult.add(dbEntity);
		}
		return tmpResult;
	}

	@Override
	public void saveItemEntity(ItemEntity entity) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		ItemEntity dbItemEntity = findItemEntity(entity.getItemId());
		if(dbItemEntity!=null)
		{
			dbItemEntity.setLanguage(entity.getLanguage());
			dbItemEntity.setTitle(entity.getTitle());
			dbItemEntity.setTranscription(entity.getTranscription());
			dbItemEntity.setType(entity.getType());
			dbItemEntity.setStoryId(entity.getStoryId());
			dbItemEntity.setItemId(entity.getItemId());
			dbItemEntity.setSource(entity.getSource());
			dbItemEntity.setDescription(entity.getDescription());
			this.datastore.save(dbItemEntity);
		}
		else
		{
			DBItemEntityImpl tmp = null;
			if(entity instanceof DBItemEntityImpl)
				tmp = (DBItemEntityImpl) entity;
			else {
				try {
					tmp = new DBItemEntityImpl(entity);
				} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {

					throw e;
				}
			}
			if(tmp != null)
				this.datastore.save(tmp);
		}
	}

	@Override
	public void deleteItemEntity(ItemEntity entity) {
		deleteItemEntityByStoryItemId(entity.getItemId());
	}

	@Override
	public void deleteItemEntityByStoryItemId(String key) {
		datastore.delete(datastore.find(DBItemEntityImpl.class).filter("itemId", key));
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
