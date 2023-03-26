package eu.europeana.enrichment.mongo.dao;

import java.util.List;

import eu.europeana.enrichment.model.impl.TranslationEntityImpl;

/*
 * This interface defines database actions for translation entries
 */
public interface TranslationEntityDao {

	public TranslationEntityImpl findTranslationEntity(String key);
	public List<TranslationEntityImpl> findAllTranslationEntities(boolean onlyItems, boolean onlyStories);
	public TranslationEntityImpl findTranslationEntityWithAditionalInformation(String storyId, String itemId, String tool, String language, String type);
	public List<TranslationEntityImpl> findTranslationEntitiesWithAditionalInformation(String storyId, String itemId, String tool, String language, String type);
	public TranslationEntityImpl findTranslationEntityWithAllAditionalInformation(String storyId, String itemId, String tool, String language, String type, String key);
	//public List<TranslationEntity> getAllTranslationEntities();
	public void saveTranslationEntity(TranslationEntityImpl entity);
	public void deleteTranslationEntity(TranslationEntityImpl entity);
	public long deleteTranslationEntityByKey(String key);
	public long deleteTranslationEntity(String storyId, String itemId, String type);
	
}
