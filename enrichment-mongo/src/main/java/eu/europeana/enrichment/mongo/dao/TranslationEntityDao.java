package eu.europeana.enrichment.mongo.dao;

import java.util.List;

import eu.europeana.enrichment.definitions.model.impl.TranslationEntityImpl;

/*
 * This interface defines database actions for translation entries
 */
public interface TranslationEntityDao {
	public List<TranslationEntityImpl> findAllTranslationEntities(boolean onlyItems, boolean onlyStories);
	public List<TranslationEntityImpl> findTranslationEntitiesWithAditionalInformation(String storyId, String itemId, String tool, String language, String type);
	public void saveTranslationEntity(TranslationEntityImpl entity);
	public void deleteTranslationEntity(TranslationEntityImpl entity);
	public long deleteTranslationEntity(String storyId, String itemId, String type);
	
}
