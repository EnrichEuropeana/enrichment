package eu.europeana.enrichment.mongo.dao;

import eu.europeana.enrichment.model.TranslationEntity;

/*
 * This interface defines database actions for translation entries
 */
public interface TranslationEntityDao {

	public TranslationEntity findTranslationEntity(String key);
	public TranslationEntity findTranslationEntityWithStoryInformation(String storyItemId, String tool, String language);
	//public List<TranslationEntity> getAllTranslationEntities();
	public void saveTranslationEntity(TranslationEntity entity);
	public void deleteTranslationEntity(TranslationEntity entity);
	public void deleteTranslationEntityByKey(String key);
	
}
