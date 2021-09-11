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
	
	String getLanguageTranscription();
	void setLanguageTranscription(String storyLanguageTranscription);
	
	String getLanguageDescription();
	void setLanguageDescription(String storyLanguageDescription);
	
	String getLanguageSummary();
	void setLanguageSummary(String storyLanguageSummary);

	String getTranscriptionText();
	void setTranscriptionText(String storyTranscription);

	String getDescriptionEn();

	void setDescriptionEn(String descriptionEn);

	String getSummaryEn();

	void setSummaryEn(String summaryEn);
	
}
