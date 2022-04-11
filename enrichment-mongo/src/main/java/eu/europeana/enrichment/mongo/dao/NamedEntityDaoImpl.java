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
import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.TranslationEntity;
import eu.europeana.enrichment.model.impl.NamedEntityImpl;
import eu.europeana.enrichment.model.impl.PositionEntityImpl;
import eu.europeana.enrichment.mongo.utils.MorphiaUtils;

@Repository(EnrichmentConstants.BEAN_ENRICHMENT_NAMED_ENTITY_DAO)
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
	
	private void addAdditonalInformation(NamedEntityImpl dbEntity) {
		List<PositionEntityImpl> positions = dbEntity.getPositionEntities();
		if(positions==null) return;
		for(int index = positions.size()-1; index >= 0; index--) {
			PositionEntityImpl dbPositionEntity = positions.get(index);
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
	public NamedEntityImpl findNamedEntity(String label) {
		return enrichmentDatastore.find(NamedEntityImpl.class).filter(
                eq(EntityFields.LABEL, label))
                .first();
	}
	
	@Override
	public NamedEntityImpl findNamedEntity(String label, String type) {
		return enrichmentDatastore.find(NamedEntityImpl.class).filter(
                eq(EntityFields.LABEL, label),
                eq(EntityFields.TYPE, type))
                .first();
	}
	
	@Override
	public List<NamedEntityImpl> findAllNamedEntities() {
		List<NamedEntityImpl> queryResult = enrichmentDatastore.find(NamedEntityImpl.class).iterator().toList();
		if(queryResult == null)
			return null;
		else
		{
			List<NamedEntityImpl> tmpResult = new ArrayList<>();
			for(int index = queryResult.size()-1; index >= 0; index--) {
				NamedEntityImpl dbEntity = queryResult.get(index);
				tmpResult.add(dbEntity);
			}
			return tmpResult;
		}
	}	

	@Override
	public List<NamedEntityImpl> findNamedEntitiesWithAdditionalInformation(String storyId, String itemId, String type) {
		List<NamedEntityImpl> queryResult = null;
		queryResult = enrichmentDatastore.find(NamedEntityImpl.class).filter(
				elemMatch(EntityFields.POSITION_ENTITIES,
                eq(EntityFields.STORY_ID, storyId),
                eq(EntityFields.ITEM_ID, itemId),
                eq(EntityFields.FIELD_USED_FOR_NER, type)	            
				))
				.iterator()
				.toList();

		if(queryResult == null)
			return null;
		else
		{
			List<NamedEntityImpl> tmpResult = new ArrayList<>();
			for(int index = queryResult.size()-1; index >= 0; index--) {
				NamedEntityImpl dbEntity = queryResult.get(index);
				//commented out addAdditonalInformation() function from performance reasons becuase it slows down the db operations in case of many NamedEntities
				//addAdditonalInformation(dbEntity);
				tmpResult.add(dbEntity);
			}
			return tmpResult;
		}	
	}

	@Override
	public List<NamedEntityImpl> findNamedEntitiesWithAdditionalInformation(String storyId, String itemId, String type, List<String> nerTools) {

		List<NamedEntityImpl> result = enrichmentDatastore.find(NamedEntityImpl.class).filter(
				elemMatch(EntityFields.POSITION_ENTITIES,
                eq(EntityFields.STORY_ID, storyId),
                eq(EntityFields.ITEM_ID, itemId),
                eq(EntityFields.FIELD_USED_FOR_NER, type),
                all(EntityFields.NER_TOOLS, nerTools)
				))
				.iterator()
				.toList();
		
		List<NamedEntityImpl> tmpResult = new ArrayList<>();
		for(int index = result.size()-1; index >= 0; index--) {
			NamedEntityImpl dbEntity = result.get(index);
			//commented out addAdditonalInformation() function from performance reasons becuase it slows down the db operations in case of many NamedEntities
			//addAdditonalInformation(dbEntity);
			tmpResult.add(dbEntity);
		}
		return tmpResult;
	}

	@Override
	public void saveNamedEntity(NamedEntityImpl entity) {
		this.enrichmentDatastore.save(entity);
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
			List<PositionEntityImpl> positionEntityList = queryResult.get(index).getPositionEntities();
			if(positionEntityList==null) continue;
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
