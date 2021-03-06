package eu.europeana.enrichment.model;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public interface ItemEntity {
	
	/*
	 * Database ID
	 */
	String getId();
	
	/*
	 * Transcribathon and Europeana information
	 */
	StoryEntity getStoryEntity();
	void setStoryEntity(StoryEntity storyEntity);
	String getItemId();
	void setItemId(String storyItemId);
	
	/*
	 * Text information original language, type (Title, Description, 
	 * StoryItem,..)
	 */
	String getLanguage();
	void setLanguage(String language);
	String getType();
	void setType(String textType);
	String getTranscriptionText();
	void setTranscriptionText(String transcriptionText);
	
	String getDescription();
	void setDescription(String descriptionText);

	String getSource();
	void setSource(String sourceParam);
	
	String getTitle();
	void setTitle(String storyItemTitle);
	
	/*
	 * Generates a string hash without whitespace which is used for comparison
	 */
	String getKey();
	void setKey(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException;

	String getStoryId();

	void setStoryId(String storyId);

}
