package eu.europeana.enrichment.model;

import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

public interface StoryEntity {

	/*
	 * Database ID
	 */
	ObjectId getId();
	
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

	List<String> getKeywords() ;

	void setKeywords(List<String> keywords);
	
	Map<String, Integer> getCompletionStatus();

	void setCompletionStatus(Map<String, Integer> completionStatus);
	
	int getItemCount();
	
	void setItemCount(int itemCount);
	
	List<String> getTranscriptionLanguages();

	void setTranscriptionLanguages(List<String> transcriptionLanguages);
	
	void copyFromStory(StoryEntity story);

}
