package eu.europeana.enrichment.model;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

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
	String getType();
	void setType(String textType);
	String getTranscriptionText();
	void setTranscriptionText(String transcriptionText);

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

	List<String> getKeywords() ;

	void setKeywords(List<String> keywords);
	
	List<String> getTranscriptionLanguages();

	void setTranscriptionLanguages(List<String> transcriptionLanguages);
	
	void copyFromItem(ItemEntity item) throws NoSuchAlgorithmException, UnsupportedEncodingException;

}
