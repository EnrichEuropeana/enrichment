package eu.europeana.enrichment.mongo.dao;

import static dev.morphia.query.experimental.filters.Filters.all;
import static dev.morphia.query.experimental.filters.Filters.elemMatch;
import static dev.morphia.query.experimental.filters.Filters.eq;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import dev.morphia.Datastore;
import dev.morphia.query.Query;
import eu.europeana.enrichment.common.commons.AppConfigConstants;
import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.model.NamedEntity;
import eu.europeana.enrichment.model.PositionEntity;
import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.TranslationEntity;
import eu.europeana.enrichment.model.impl.NamedEntityImpl;
import eu.europeana.enrichment.mongo.utils.MorphiaUtils;

@Repository(AppConfigConstants.BEAN_ENRICHMENT_NAMED_ENTITY_DAO)
public class NamedEntityDaoImpl implements NamedEntityDao {

	@Autowired
	StoryEntityDao storyEntityDao;
	@Autowired
	ItemEntityDao itemEntityDao;
	@Autowired
	TranslationEntityDao translationEntityDao;
	
	@Autowired
	private Datastore enrichmentDatastore; 
	
	Logger logger = LogManager.getLogger(getClass());
	
	private void addAdditonalInformation(NamedEntity dbEntity) {
		List<PositionEntity> positions = dbEntity.getPositionEntities();
		for(int index = positions.size()-1; index >= 0; index--) {
			PositionEntity dbPositionEntity = positions.get(index);
			String storyId = dbPositionEntity.getStoryId();
			String itemId = dbPositionEntity.getItemId();
			String translationKey = dbPositionEntity.getTranslationKey();
			if(storyId != null && !storyId.isEmpty()) {
				StoryEntity dbStoryEntity = storyEntityDao.findStoryEntity(storyId);
				dbPositionEntity.setStoryEntity(dbStoryEntity);
			}
			if(itemId != null && !itemId.isEmpty()) {
				ItemEntity dbItemEntity = itemEntityDao.findItemEntityFromStory(itemId, storyId);
				dbPositionEntity.setItemEntity(dbItemEntity);
			}
			if(translationKey != null && !translationKey.isEmpty()) {
				TranslationEntity dbTranslationEntity = translationEntityDao.findTranslationEntity(translationKey);
				dbPositionEntity.setTranslationEntity(dbTranslationEntity);
			}
		}
	}
	
	@Override
	public NamedEntity findNamedEntity(String label) {
		return enrichmentDatastore.find(NamedEntityImpl.class).filter(
                eq(EntityFields.LABEL, label))
                .first();
	}
	
	@Override
	public List<NamedEntity> findAllNamedEntities() {
		List<NamedEntityImpl> queryResult = enrichmentDatastore.find(NamedEntityImpl.class).iterator().toList();
		if(queryResult == null)
			return null;
		else
		{
			List<NamedEntity> tmpResult = new ArrayList<>();
			for(int index = queryResult.size()-1; index >= 0; index--) {
				NamedEntity dbEntity = queryResult.get(index);
				tmpResult.add(dbEntity);
			}
			return tmpResult;
		}
	}	

	@Override
	public List<NamedEntity> findNamedEntitiesWithAdditionalInformation(String storyId, String itemId, String type, boolean translation) {
		List<NamedEntityImpl> queryResult = null;
		if(translation) {
			queryResult = enrichmentDatastore.find(NamedEntityImpl.class).filter(
					elemMatch(EntityFields.POSITION_ENTITIES,
	                eq(EntityFields.TRANSLATION_KEY, storyId),
	                eq(EntityFields.FIELD_USED_FOR_NER, type)	            
					))
					.iterator()
					.toList();
		} else {
			queryResult = enrichmentDatastore.find(NamedEntityImpl.class).filter(
					elemMatch(EntityFields.POSITION_ENTITIES,
	                eq(EntityFields.STORY_ID, storyId),
	                eq(EntityFields.ITEM_ID, itemId),
	                eq(EntityFields.FIELD_USED_FOR_NER, type)	            
					))
					.iterator()
					.toList();
		}
		
		if(queryResult == null)
			return null;
		else
		{
			List<NamedEntity> tmpResult = new ArrayList<>();
			for(int index = queryResult.size()-1; index >= 0; index--) {
				NamedEntity dbEntity = queryResult.get(index);
				//commented out addAdditonalInformation() function from performance reasons becuase it slows down the db operations in case of many NamedEntities
				//addAdditonalInformation(dbEntity);
				tmpResult.add(dbEntity);
			}
			return tmpResult;
		}	
	}

	@Override
	public List<NamedEntity> findNamedEntitiesWithAdditionalInformation(String storyId, String itemId, String type, List<String> nerTools) {

		List<NamedEntityImpl> result = enrichmentDatastore.find(NamedEntityImpl.class).filter(
				elemMatch(EntityFields.POSITION_ENTITIES,
                eq(EntityFields.STORY_ID, storyId),
                eq(EntityFields.ITEM_ID, itemId),
                eq(EntityFields.FIELD_USED_FOR_NER, type),
                all(EntityFields.NER_TOOLS, nerTools)
				))
				.iterator()
				.toList();
		
		List<NamedEntity> tmpResult = new ArrayList<>();
		for(int index = result.size()-1; index >= 0; index--) {
			NamedEntity dbEntity = result.get(index);
			//commented out addAdditonalInformation() function from performance reasons becuase it slows down the db operations in case of many NamedEntities
			//addAdditonalInformation(dbEntity);
			tmpResult.add(dbEntity);
		}
		return tmpResult;
	}

	
	@Override
	public List<NamedEntity> findNamedEntitiesWithAdditionalInformation(String storyId, String itemId, boolean translation) {
		List<NamedEntityImpl> queryResult = null;
		if(translation) {
			queryResult = enrichmentDatastore.find(NamedEntityImpl.class).filter(
					elemMatch(EntityFields.POSITION_ENTITIES,
	                eq(EntityFields.TRANSLATION_KEY, storyId)
					))
					.iterator()
					.toList();
		} else {
			queryResult = enrichmentDatastore.find(NamedEntityImpl.class).filter(
					elemMatch(EntityFields.POSITION_ENTITIES,
	                eq(EntityFields.STORY_ID, storyId),
	                eq(EntityFields.ITEM_ID, itemId)
					))
					.iterator()
					.toList();
		}
		
		if(queryResult == null)
			return null;
		else
		{
			List<NamedEntity> tmpResult = new ArrayList<>();
			for(int index = queryResult.size()-1; index >= 0; index--) {
				NamedEntity dbEntity = queryResult.get(index);
				//commented out addAdditonalInformation() function from performance reasons becuase it slows down the db operations in case of many NamedEntities
				//addAdditonalInformation(dbEntity);
				tmpResult.add(dbEntity);
			}
			return tmpResult;
		}
	}

	@Override
	public void saveNamedEntity(NamedEntity entity) {
		NamedEntity dbNamedEntity = findNamedEntity(entity.getLabel());
		if(dbNamedEntity!=null)
		{
			dbNamedEntity.setDBpediaIds(entity.getDBpediaIds());
			dbNamedEntity.setDbpediaWikidataIds(entity.getDbpediaWikidataIds());
			dbNamedEntity.setEuropeanaIds(entity.getEuropeanaIds());
			dbNamedEntity.setLabel(entity.getLabel());
			dbNamedEntity.setPositionEntities(entity.getPositionEntities());
			dbNamedEntity.setPreferredWikidataIds(entity.getPreferredWikidataIds());
			dbNamedEntity.setType(entity.getType());
			dbNamedEntity.setWikidataIds(entity.getWikidataIds());
			this.enrichmentDatastore.save(dbNamedEntity);
		}
		else
		{	
			NamedEntityImpl tmp = null;
			if(entity instanceof NamedEntityImpl)
				tmp = (NamedEntityImpl) entity;
			else {
				tmp = new NamedEntityImpl(entity);
			}
			if(tmp != null)
				this.enrichmentDatastore.save(tmp);
		}
	}

	@Override
	public void deleteNamedEntity(NamedEntity entity) {
		deleteNamedEntityByKey(entity.getLabel());
	}

	@Override
	public long deleteNamedEntityByKey(String label) {
		return enrichmentDatastore.find(NamedEntityImpl.class).filter(
                eq(EntityFields.LABEL, label)
                )
                .delete(MorphiaUtils.MULTI_DELETE_OPTS)
                .getDeletedCount();
	}

	@Override
	public long deleteAllNamedEntities() {
		return enrichmentDatastore.find(NamedEntityImpl.class)
                .delete(MorphiaUtils.MULTI_DELETE_OPTS)
                .getDeletedCount();
	}

	@Override
	public void deletePositionEntitiesFromNamedEntity(String storyId, String itemId, String fieldUsedForNER) {
		List<NamedEntityImpl> queryResult = enrichmentDatastore.find(NamedEntityImpl.class).filter(
				elemMatch(EntityFields.POSITION_ENTITIES,
		                eq(EntityFields.STORY_ID, storyId),
		                eq(EntityFields.ITEM_ID, itemId),
		                eq(EntityFields.FIELD_USED_FOR_NER, fieldUsedForNER)
						))
						.iterator()
						.toList();	

		//fetching the PositionEntity-ies to be deleted from the NamedEntity-ies
		for(int index = queryResult.size()-1; index >= 0; index--) {
			List<PositionEntity> positionEntityList = queryResult.get(index).getPositionEntities();
			int posIndex = 0;
			while(posIndex<positionEntityList.size())
			{
				if(positionEntityList.get(posIndex).getStoryId().equals(storyId) && positionEntityList.get(posIndex).getItemId().equals(itemId)
						&& positionEntityList.get(posIndex).getFieldUsedForNER().equals(fieldUsedForNER)) 
				{
					positionEntityList.remove(posIndex);
				}
				else
				{
					posIndex++;
				}
			}
		}

		for(int i=0;i<queryResult.size();i++)
		{
			saveNamedEntity(queryResult.get(i));
		}
		
		
//		persistentPositionEntitiesQuery.disableValidation();
//		persistentPositionEntitiesQuery.filter("positionEntities.storyId", storyId);
//		persistentPositionEntitiesQuery.filter("positionEntities.itemId", itemId);
//		persistentPositionEntitiesQuery.filter("positionEntities.fieldUsedForNER", fieldUsedForNER);
		
//		final UpdateResults results = datastore.update(datastore.createQuery(DBNamedEntityImpl.class),
//				datastore.createUpdateOperations(DBNamedEntityImpl.class).removeAll("positionEntities", positionEntitiesDelete));
//		logger.info("Number of updated NamedEntity-s is: " + results.getUpdatedCount());
		
//		persistentNamedEntitiesQuery.disableValidation();
//		persistentNamedEntitiesQuery.filter("positionEntities.storyId", storyId);
//		persistentNamedEntitiesQuery.filter("positionEntities.itemId", itemId);
//		persistentNamedEntitiesQuery.filter("positionEntities.fieldUsedForNER", fieldUsedForNER);
		
	}


	

}
