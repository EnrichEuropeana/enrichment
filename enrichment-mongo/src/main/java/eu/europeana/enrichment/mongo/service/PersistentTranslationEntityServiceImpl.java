package eu.europeana.enrichment.mongo.service;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.europeana.enrichment.common.commons.AppConfigConstants;
import eu.europeana.enrichment.model.TranslationEntity;
import eu.europeana.enrichment.mongo.dao.TranslationEntityDao;

@Service(AppConfigConstants.BEAN_ENRICHMENT_PERSISTENT_TRANSLATION_ENTITY_SERVICE)
public class PersistentTranslationEntityServiceImpl implements PersistentTranslationEntityService {

	@Autowired
	TranslationEntityDao translationEntityDao;
	
	@Override
	public TranslationEntity findTranslationEntity(String key) {
		return translationEntityDao.findTranslationEntity(key);
	}
	@Override
	public TranslationEntity findTranslationEntityWithAditionalInformation(String storyId, String itemId, String tool, String language, String type) {
		return translationEntityDao.findTranslationEntityWithAditionalInformation(storyId, itemId, tool, language, type);
	}
	
	@Override
	public TranslationEntity findTranslationEntityWithAllAditionalInformation(String storyId, String itemId, String tool, String language, String type, String key) {
		return translationEntityDao.findTranslationEntityWithAllAditionalInformation(storyId, itemId, tool, language, type, key);
	}

	@Override
	public List<TranslationEntity> getAllTranslationEntities() {
		// TODO Auto-generated method stub
		return translationEntityDao.findAllTranslationEntities();
	}

	@Override
	public void saveTranslationEntity(TranslationEntity entity) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		translationEntityDao.saveTranslationEntity(entity);
	}

	@Override
	public void saveTranslationEntities(List<TranslationEntity> entities) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		for (TranslationEntity translationEntity : entities) {
			saveTranslationEntity(translationEntity);
		}
	}

	@Override
	public void deleteTranslationEntity(TranslationEntity entity) {
		translationEntityDao.deleteTranslationEntity(entity);
	}

	@Override
	public void deleteTranslationEntity(String storyId,String itemId, String type) {
		translationEntityDao.deleteTranslationEntity(storyId,itemId,type);
	}

}
