package eu.europeana.enrichment.mongo.service;

import java.util.List;

import eu.europeana.enrichment.definitions.exceptions.EntityRetrievalException;
import eu.europeana.enrichment.definitions.model.impl.TranslationEntityImpl;
import eu.europeana.enrichment.definitions.model.impl.TranslationVersionImpl;

public interface PersistentTranslationEntityService {

    public List<TranslationEntityImpl> findTranslationEntitiesWithAditionalInformation(String storyId, String itemId,
            String tool, String language, String type);

    public TranslationEntityImpl findTranslation(String storyId, String itemId, String tool, String language,
            String type) throws EntityRetrievalException;

    /**
     * This method retrieves all translation entries from the Mongo database
     * 
     * @return list of database translation entries
     */
    public List<TranslationEntityImpl> getAllTranslationEntities(boolean onlyItems, boolean onlyStories);

    /**
     * This method saves and updates translation entries into the Mongo database
     * 
     * @param entity translation entry which should be saved or updated
     * 
     * @return
     */
    public TranslationEntityImpl saveTranslationEntity(TranslationEntityImpl entity);

    /**
     * This method saves and updates a list of translation entries into the Mongo
     * database
     * 
     * @param entities a list of translation entries which should be saved or
     * updated
     * 
     * @return
     */
    public void saveTranslationEntities(List<TranslationEntityImpl> entities) throws Exception;

    /**
     * This method deletes translation entries from the Mongo database
     * 
     * @param entity translation entry which should be deleted
     * 
     * @return
     */
    public void deleteTranslationEntity(TranslationEntityImpl entity);

    public void deleteTranslationEntity(String storyId, String itemId, String type);

    /**
     * This method saves and updates translation entries into the Mongo database
     * @param version object to be saved into the database
     */
    public void saveTranslationVersion(TranslationVersionImpl version);

}
