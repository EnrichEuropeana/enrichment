package eu.europeana.enrichment.model;

public interface StoryEntity {

	/*
	 * Database ID
	 */
	String getId();
	
	/*
	 * Transcribathon and Europeana StoryId
	 */
	String getStoryId();
	void setStoryId(String storyId);
	
	String getStoryTitle();
	void setStoryTitle(String storyTitle);
	
	String getStorySource();
	void setStorySource(String storySource);

	String getStoryDescription();
	void setStoryDescription(String storyDescription);

	String getStorySummary();
	void setStorySummary(String storySummary);
	
	String getStoryLanguage();
	void setStoryLanguage(String storyLanguage);

	String getStoryTranscription();
	void setStoryTranscription(String storyTranscription);
	
}
