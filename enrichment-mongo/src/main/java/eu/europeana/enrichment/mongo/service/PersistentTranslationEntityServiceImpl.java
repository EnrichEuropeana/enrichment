package eu.europeana.enrichment.mongo.service;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.definitions.exceptions.EntityRetrievalException;
import eu.europeana.enrichment.definitions.model.impl.TranslationEntityImpl;
import eu.europeana.enrichment.definitions.model.impl.TranslationVersionImpl;
import eu.europeana.enrichment.mongo.dao.TranslationEntityDao;
import eu.europeana.enrichment.mongo.dao.TranslationVersionDao;

@Service(EnrichmentConstants.BEAN_ENRICHMENT_PERSISTENT_TRANSLATION_ENTITY_SERVICE)
public class PersistentTranslationEntityServiceImpl implements PersistentTranslationEntityService {

    @Autowired
    @Qualifier(EnrichmentConstants.BEAN_ENRICHMENT_TRANSLATION_ENTITY_DAO)
    TranslationEntityDao translationEntityDao;
    
    @Autowired
    @Qualifier(EnrichmentConstants.BEAN_ENRICHMENT_TRANSLATION_VERSION_DAO)
    TranslationVersionDao translationVersionDao;

    @Override
    public List<TranslationEntityImpl> findTranslationEntitiesWithAditionalInformation(String storyId, String itemId,
            String tool, String language, String type) {
        return translationEntityDao.findTranslationEntitiesWithAditionalInformation(storyId, itemId, tool, language,
                type);
    }

    @Override
    public TranslationEntityImpl findTranslation(String storyId, String itemId, String tool, String language,
            String type) throws EntityRetrievalException {
        List<TranslationEntityImpl> translations = findTranslationEntitiesWithAditionalInformation(storyId, itemId,
                tool, language, type);
        if (translations == null || translations.isEmpty()) {
            return null;
        } else if (translations.size() > 1) {
            throw new EntityRetrievalException("Expected to retrieve one translation for property: " + type
                    + " of item: " + itemId + " but found: " + translations.size());
        }
        return translations.get(0);
    }

    @Override
    public List<TranslationEntityImpl> getAllTranslationEntities(boolean onlyItems, boolean onlyStories) {
        // TODO Auto-generated method stub
        return translationEntityDao.findAllTranslationEntities(onlyItems, onlyStories);
    }

    @Override
    public TranslationEntityImpl saveTranslationEntity(TranslationEntityImpl entity) {
        return translationEntityDao.saveTranslationEntity(entity);
    }

    @Override
    public void saveTranslationEntities(List<TranslationEntityImpl> entities)
            throws NoSuchAlgorithmException, UnsupportedEncodingException {
        for (TranslationEntityImpl translationEntity : entities) {
            saveTranslationEntity(translationEntity);
        }
    }

    @Override
    public void deleteTranslationEntity(TranslationEntityImpl entity) {
        translationEntityDao.deleteTranslationEntity(entity);
    }

    @Override
    public void deleteTranslationEntity(String storyId, String itemId, String type) {
        translationEntityDao.deleteTranslationEntity(storyId, itemId, type);
    }

    @Override
    public void saveTranslationVersion(TranslationVersionImpl version) {
        translationVersionDao.saveTranslationVersion(version);
    }

}
