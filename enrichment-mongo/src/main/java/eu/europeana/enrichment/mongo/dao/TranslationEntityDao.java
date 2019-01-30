package eu.europeana.enrichment.mongo.dao;

import eu.europeana.enrichment.common.definitions.TranslationEntity;;

/*
 * This interface defines database actions for translation entries
 */
public interface TranslationEntityDao {

	public TranslationEntity findTranslationEntity(String key);
	//public List<TranslationEntity> getAllTranslationEntities();
	public void saveTranslationEntity(TranslationEntity entity);
	public void deleteTranslationEntity(TranslationEntity entity);
	public void deleteByKey(String key);
	
}
