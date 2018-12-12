package eu.europeana.enrichment.mongo.dao;

import eu.europeana.enrichment.common.definitions.TranslationEntity;;

public interface TranslationEntityDao {

	public TranslationEntity findTranslationEntity(String key);
	//public List<TranslationEntity> getAllTranslationEntities();
	public void saveTranslationEntity(TranslationEntity entity);
	public void deleteTranslationEntity(TranslationEntity entity);
	public void deleteByKey(String key);
	
}
