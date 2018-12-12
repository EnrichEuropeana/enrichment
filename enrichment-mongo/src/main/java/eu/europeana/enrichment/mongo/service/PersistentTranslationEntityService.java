package eu.europeana.enrichment.mongo.service;

import java.util.List;

import eu.europeana.enrichment.common.definitions.TranslationEntity;

public interface PersistentTranslationEntityService {

	public TranslationEntity findTranslationEntity(String key);
	public List<TranslationEntity> getAllTranslationEntities();
	public void saveTranslationEntity(TranslationEntity entity);
	public void saveTranslationEntities(List<TranslationEntity> entities);
	public void deleteTranslationEntity(TranslationEntity entity);
	
}
