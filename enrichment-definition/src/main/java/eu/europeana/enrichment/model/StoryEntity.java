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
	
	String getTitle();
	void setTitle(String storyTitle);
	
	String getSource();
	void setSource(String storySource);

	String getDescription();
	void setDescription(String storyDescription);

	String getSummary();
	void setSummary(String storySummary);
	
	String getLanguage();
	void setLanguage(String storyLanguage);

	String getTranscription();
	void setTranscription(String storyTranscription);
	
}
