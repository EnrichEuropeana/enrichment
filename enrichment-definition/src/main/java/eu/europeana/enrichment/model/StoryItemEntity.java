package eu.europeana.enrichment.model;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface StoryItemEntity {
	
	/*
	 * Database ID
	 */
	String getId();
	
	/*
	 * Transcribathon and Europeana information
	 */
	StoryEntity getStoryEntity();
	void setStoryEntity(StoryEntity storyEntity);
	String getStoryItemId();
	void setStoryItemId(String storyItemId);
	
	/*
	 * Text information original language, type (Title, Description, 
	 * StoryItem,..)
	 */
	String getLanguage();
	void setLanguage(String language);
	String getType();
	void setType(String textType);
	String getText();
	void setText(String text);
	/*
	 * Generates a string hash without whitespace which is used for comparison
	 */
	String getKey();
	void setKey(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException;

}
