package eu.europeana.enrichment.mongo.service;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import eu.europeana.enrichment.model.TranslationEntity;

public interface PersistentTranslationEntityService {

	/*
	 * This method retrieves a translation entry from the Mongo database 
	 * based on their SHA256 key
	 * 
	 * @param key						SHA256 key without spaces
	 * @return							a database translation entry 
	 */
	public TranslationEntity findTranslationEntity(String key);
	
	public TranslationEntity findTranslationEntityWithAditionalInformation(String storyId, String itemId, String tool, String language, String type);
	
	public TranslationEntity findTranslationEntityWithAllAditionalInformation(String storyId, String itemId, String tool, String language, String type, String key);
	/*
	 * This method retrieves all translation entries from the Mongo database
	 * 
	 * @return							list of database translation entries
	 */
	public List<TranslationEntity> getAllTranslationEntities();
	/*
	 * This method saves and updates translation entries into the Mongo database
	 * 
	 * @param entity					translation entry which should be saved
	 * 									or updated
	 * @return
	 */
	public void saveTranslationEntity(TranslationEntity entity) throws NoSuchAlgorithmException, UnsupportedEncodingException;
	/*
	 * This method saves and updates a list of translation entries into the Mongo database
	 * 
	 * @param entities					a list of translation entries which should
	 * 									be saved or updated
	 * @return
	 */
	public void saveTranslationEntities(List<TranslationEntity> entities) throws Exception;
	/*
	 * This method deletes translation entries from the Mongo database
	 * 
	 * @param entity					translation entry which should be deleted
	 * @return
	 */
	public void deleteTranslationEntity(TranslationEntity entity);
	
	public void deleteTranslationEntity(String storyId,String itemId, String type);
	
}
