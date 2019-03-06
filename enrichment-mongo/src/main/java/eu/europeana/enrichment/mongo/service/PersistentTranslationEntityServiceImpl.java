package eu.europeana.enrichment.mongo.service;

import java.util.List;

import javax.annotation.Resource;

import eu.europeana.enrichment.model.TranslationEntity;
import eu.europeana.enrichment.mongo.dao.TranslationEntityDaoImpl;;

public class PersistentTranslationEntityServiceImpl implements PersistentTranslationEntityService {

	@Resource(name = "translationEntityDao")
	TranslationEntityDaoImpl translationEntityDao;
	
	@Override
	public TranslationEntity findTranslationEntity(String key) {
		return translationEntityDao.findTranslationEntity(key);
	}
	@Override
	public TranslationEntity findTranslationEntityWithStoryInformation(String storyId, String tool, String language) {
		return translationEntityDao.findTranslationEntityWithStoryInformation(storyId, tool, language);
	}

	@Override
	public List<TranslationEntity> getAllTranslationEntities() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveTranslationEntity(TranslationEntity entity) {
		translationEntityDao.saveTranslationEntity(entity);
	}

	@Override
	public void saveTranslationEntities(List<TranslationEntity> entities) {
		for (TranslationEntity translationEntity : entities) {
			saveTranslationEntity(translationEntity);
		}
	}

	@Override
	public void deleteTranslationEntity(TranslationEntity entity) {
		translationEntityDao.deleteTranslationEntity(entity);
	}

}
