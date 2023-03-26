package eu.europeana.enrichment.mongo.service;

import java.util.List;

import eu.europeana.enrichment.model.impl.TranslationEntityImpl;

public interface PersistentTranslationEntityService {

	/*
	 * This method retrieves a translation entry from the Mongo database 
	 * based on their SHA256 key
	 * 
	 * @param key						SHA256 key without spaces
	 * @return							a database translation entry 
	 */
	public TranslationEntityImpl findTranslationEntity(String key);
	
	public TranslationEntityImpl findTranslationEntityWithAditionalInformation(String storyId, String itemId, String tool, String language, String type);
	
	public List<TranslationEntityImpl> findTranslationEntitiesWithAditionalInformation(String storyId, String itemId, String tool, String language, String type);
	
	public TranslationEntityImpl findTranslationEntityWithAllAditionalInformation(String storyId, String itemId, String tool, String language, String type, String key);
	/*
	 * This method retrieves all translation entries from the Mongo database
	 * 
	 * @return							list of database translation entries
	 */
	public List<TranslationEntityImpl> getAllTranslationEntities(boolean onlyItems, boolean onlyStories);
	/*
	 * This method saves and updates translation entries into the Mongo database
	 * 
	 * @param entity					translation entry which should be saved
	 * 									or updated
	 * @return
	 */
	public void saveTranslationEntity(TranslationEntityImpl entity);
	/*
	 * This method saves and updates a list of translation entries into the Mongo database
	 * 
	 * @param entities					a list of translation entries which should
	 * 									be saved or updated
	 * @return
	 */
	public void saveTranslationEntities(List<TranslationEntityImpl> entities) throws Exception;
	/*
	 * This method deletes translation entries from the Mongo database
	 * 
	 * @param entity					translation entry which should be deleted
	 * @return
	 */
	public void deleteTranslationEntity(TranslationEntityImpl entity);
	
	public void deleteTranslationEntity(String storyId,String itemId, String type);
	
}
