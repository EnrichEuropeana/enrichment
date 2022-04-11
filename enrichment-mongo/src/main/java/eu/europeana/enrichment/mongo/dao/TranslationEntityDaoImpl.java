package eu.europeana.enrichment.mongo.dao;

import static dev.morphia.query.experimental.filters.Filters.eq;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import dev.morphia.Datastore;
import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.TranslationEntity;
import eu.europeana.enrichment.model.impl.TranslationEntityImpl;
import eu.europeana.enrichment.mongo.utils.MorphiaUtils;

@Repository(EnrichmentConstants.BEAN_ENRICHMENT_TRANSLATION_ENTITY_DAO)
public class TranslationEntityDaoImpl implements TranslationEntityDao {

	@Autowired
	StoryEntityDao storyEntityDao;
	
	@Autowired
	private Datastore enrichmentDatastore; 
	
	private void addItemEntity(TranslationEntityImpl dbEntity) {
		StoryEntity dbItemEntity = storyEntityDao.findStoryEntity(dbEntity.getStoryId());
		dbEntity.setStoryEntity(dbItemEntity);
	}
	
	@Override
	public TranslationEntity findTranslationEntity(String key) {
		TranslationEntityImpl dbEntity = enrichmentDatastore.find(TranslationEntityImpl.class).filter(
                eq(EntityFields.KEY, key))
                .first();
		if(dbEntity==null)
			return null;
		else {
			addItemEntity(dbEntity);
			return dbEntity;
		}
	}
	
	@Override
	public List<TranslationEntity> findAllTranslationEntities() {
		List<TranslationEntityImpl> queryResult = enrichmentDatastore.find(TranslationEntityImpl.class).iterator().toList();
		if(queryResult == null)
			return null;
		else
		{
			List<TranslationEntity> tmpResult = new ArrayList<>();
			for(int index = queryResult.size()-1; index >= 0; index--) {
				TranslationEntity dbEntity = queryResult.get(index);
				tmpResult.add(dbEntity);
			}
			return tmpResult;
		}
	}

	@Override
	public TranslationEntity findTranslationEntityWithAllAditionalInformation(String storyId, String itemId, String tool, String language, String type, String key) {
		return enrichmentDatastore.find(TranslationEntityImpl.class).filter(
                eq(EntityFields.STORY_ID, storyId),
                eq(EntityFields.ITEM_ID, itemId),
                eq(EntityFields.TOOL, tool),
                eq(EntityFields.LANGUAGE, language),
                eq(EntityFields.TYPE, type),
                eq(EntityFields.KEY, key)
                )
                .first();
	}
	
	@Override
	public TranslationEntity findTranslationEntityWithAditionalInformation(String storyId, String itemId,
			String tool, String language, String type) {
		return enrichmentDatastore.find(TranslationEntityImpl.class).filter(
                eq(EntityFields.STORY_ID, storyId),
                eq(EntityFields.ITEM_ID, itemId),
                eq(EntityFields.TOOL, tool),
                eq(EntityFields.LANGUAGE, language),
                eq(EntityFields.TYPE, type)
                )
                .first();
	}
	
	@Override
	public List<TranslationEntityImpl> findTranslationEntitiesWithAditionalInformation(String storyId, String itemId, String tool, String language, String type) {
		return enrichmentDatastore.find(TranslationEntityImpl.class).filter(
                eq(EntityFields.STORY_ID, storyId),
                eq(EntityFields.ITEM_ID, itemId),
                eq(EntityFields.TOOL, tool),
                eq(EntityFields.LANGUAGE, language),
                eq(EntityFields.TYPE, type)
                )
                .iterator().toList();
	}

	@Override
	public void saveTranslationEntity(TranslationEntity entity) {
		this.enrichmentDatastore.save(entity);
	}

	@Override
	public void deleteTranslationEntity(TranslationEntity entity) {
		enrichmentDatastore.find(TranslationEntityImpl.class).filter(
			eq(EntityFields.OBJECT_ID,entity.getId()))
			.delete();			
	}

	@Override
	public long deleteTranslationEntityByKey(String key) {
		return enrichmentDatastore.find(TranslationEntityImpl.class).filter(
                eq(EntityFields.KEY,key))
                .delete(MorphiaUtils.MULTI_DELETE_OPTS)
                .getDeletedCount();
	}
	
	@Override
	public long deleteTranslationEntity(String storyId, String itemId, String type) {
		return enrichmentDatastore.find(TranslationEntityImpl.class).filter(
                eq(EntityFields.STORY_ID,storyId),
                eq(EntityFields.ITEM_ID,itemId),
                eq(EntityFields.TYPE,type)
                )
                .delete(MorphiaUtils.MULTI_DELETE_OPTS)
                .getDeletedCount();
		
	}


	

}
