package eu.europeana.enrichment.mongo.dao;

import java.util.List;

import eu.europeana.enrichment.definitions.model.impl.TranslationEntityImpl;

/*
 * This interface defines database actions for translation entries
 */
public interface TranslationEntityDao {
	public List<TranslationEntityImpl> findAllTranslationEntities(boolean onlyItems, boolean onlyStories);
	public List<TranslationEntityImpl> findTranslationEntitiesWithAditionalInformation(String storyId, String itemId, String tool, String language, String type);
	public TranslationEntityImpl saveTranslationEntity(TranslationEntityImpl entity);
	public void deleteTranslationEntity(TranslationEntityImpl entity);
	/**
	 * Delete translation of a given item property
	 * @param storyId TP StoryID
	 * @param itemId TP ItemID
	 * @param type Item property 
	 * @return number of deleted records (max 1)
	 */
	public long deleteTranslationEntity(String storyId, String itemId, String type);
	/**
         * Delete translation of a given item property
         * @param storyId TP StoryID
         * @param itemId TP ItemID
         * @param type Item property 
         * @param includeManual indicates if manual translation should be deleted
         * @return number of deleted records (max 1)
         */
	public long deleteTranslationEntity(String storyId, String itemId, String type, boolean includeManual);
	
}
