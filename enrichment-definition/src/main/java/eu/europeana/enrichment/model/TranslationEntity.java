package eu.europeana.enrichment.model;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface TranslationEntity {

	/*
	 * Database ID
	 */
	String getId();
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
	 * Transcribathon and Europeana StoryItem
	 */
	StoryEntity getStoryEntity();
	void setStoryEntity(StoryEntity ItemEntity);
	String getStoryId();
	void setStoryId(String storyId);
}
