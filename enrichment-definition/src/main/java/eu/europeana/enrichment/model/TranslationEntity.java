package eu.europeana.enrichment.model;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import org.bson.types.ObjectId;

public interface TranslationEntity {

	/*
	 * Database ID
	 */
	ObjectId getId();
	/*
	 * Generates a string hash without whitespace which is also used as the key
	 * and for comparison
	 */
	String getKey();
	void setKey(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException;
	/*
	 * Translated text and language
	 */
	String getLanguage();
	void setLanguage(String language);
	String getTranslatedText();
	void setTranslatedText(String translatedText);
	/*
	 * Tool information which was used for translation (e.g. eTranslation, Google, ..)
	 */
	String getTool();
	void setTool(String tool);
	
	/*
	 * Transcribathon and Europeana Story
	 */
	StoryEntity getStoryEntity();
	void setStoryEntity(StoryEntity ItemEntity);
	String getStoryId();
	void setStoryId(String storyId);
	
	/*
	 * Transcribathon and Europeana Item
	 */
	ItemEntity getItemEntity();
	void setItemEntity(ItemEntity itemEntity);
	String getItemId();
	void setItemId(String itemId);

	
	String getType();
	void setType(String type);
	
	String getETranslationId();	
	void setETranslationId(String eTranslationId);
	
	String getOriginLangGoogle();
	
	void setOriginLangGoogle(String originLangGoogle);

}
