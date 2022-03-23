package eu.europeana.enrichment.mongo.dao;

import java.util.List;

import eu.europeana.enrichment.model.TranslationEntity;
import eu.europeana.enrichment.model.impl.TranslationEntityImpl;

/*
 * This interface defines database actions for translation entries
 */
public interface TranslationEntityDao {

	public TranslationEntity findTranslationEntity(String key);
	public List<TranslationEntity> findAllTranslationEntities();
	public TranslationEntity findTranslationEntityWithAditionalInformation(String storyId, String itemId, String tool, String language, String type);
	public List<TranslationEntityImpl> findTranslationEntitiesWithAditionalInformation(String storyId, String itemId, String tool, String language, String type);
	public TranslationEntity findTranslationEntityWithAllAditionalInformation(String storyId, String itemId, String tool, String language, String type, String key);
	//public List<TranslationEntity> getAllTranslationEntities();
	public void saveTranslationEntity(TranslationEntity entity);
	public void deleteTranslationEntity(TranslationEntity entity);
	public long deleteTranslationEntityByKey(String key);
	public long deleteTranslationEntity(String storyId, String itemId, String type);
	
}
