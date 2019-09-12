package eu.europeana.enrichment.mongo.dao;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import eu.europeana.enrichment.model.TranslationEntity;

/*
 * This interface defines database actions for translation entries
 */
public interface TranslationEntityDao {

	public TranslationEntity findTranslationEntity(String key);
	public TranslationEntity findTranslationEntityWithAditionalInformation(String storyId, String itemId, String tool, String language, String type);
	public TranslationEntity findTranslationEntityWithAllAditionalInformation(String storyId, String itemId, String tool, String language, String type, String key);
	//public List<TranslationEntity> getAllTranslationEntities();
	public void saveTranslationEntity(TranslationEntity entity) throws Exception;
	public void deleteTranslationEntity(TranslationEntity entity);
	public void deleteTranslationEntityByKey(String key);
	
}
