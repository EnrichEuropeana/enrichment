package eu.europeana.enrichment.model.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexed;
import eu.europeana.enrichment.model.StoryEntity;

@Entity(value="StoryEntityImpl")
public class StoryEntityImpl implements StoryEntity {

	@Indexed(options = @IndexOptions(unique = true))
	private String storyId;
	
	private String title;
	private String source;
	private String description;
	private String summary;
	private List<String> transcriptionLanguages;
	private String languageDescription;
	private String languageSummary;
	private String transcriptionText;
	private String descriptionEn;
	private String summaryEn;
	private List<String> keywords;
	private Map<String, Integer> completionStatus;
	private int itemCount;

	@Id
    private String _id = new ObjectId().toString();
	
	@Override
	public String getId() {
		return _id;
	}
	
	public StoryEntityImpl (StoryEntity copy)
	{
		this.storyId = copy.getStoryId();
		this.title = copy.getTitle();
		this.source = copy.getSource();
		this.description = copy.getDescription();
		this.descriptionEn = copy.getDescriptionEn();
		this.summary = copy.getSummary();
		this.summaryEn = copy.getSummaryEn();
		this.languageDescription = copy.getLanguageDescription();
		this.languageSummary = copy.getLanguageSummary();
		this.transcriptionText = copy.getTranscriptionText();
		if(copy.getKeywords()!=null) this.keywords = new ArrayList<String>(copy.getKeywords());
		if(copy.getCompletionStatus()!=null) this.completionStatus = new HashMap<String, Integer>(copy.getCompletionStatus());
		if(copy.getTranscriptionLanguages()!=null) this.transcriptionLanguages = new ArrayList<String>(copy.getTranscriptionLanguages()); 
		this.itemCount = copy.getItemCount();
	}
	
	public StoryEntityImpl() {
		
	}

	@Override
	public String getStoryId() {
		return storyId;
	}

	@Override
	public void setStoryId(String storyId) {
		this.storyId = storyId;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public void setTitle(String storyTitle) {
		this.title=storyTitle;
		
	}

	@Override
	public String getSource() {
		return source;
	}

	@Override
	public void setSource(String storySource) {
		this.source=storySource;		
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description=description;
	}
	
	@Override
	public String getDescriptionEn() {
		return descriptionEn;
	}

	@Override
	public void setDescriptionEn(String descriptionEn) {
		this.descriptionEn=descriptionEn;
	}

	@Override
	public String getSummary() {
		return summary;
	}

	@Override
	public void setSummary(String storySummary) {
		this.summary=storySummary;	
	}
	
	@Override
	public String getSummaryEn() {
		return summaryEn;
	}

	@Override
	public void setSummaryEn(String summaryEn) {
		this.summaryEn=summaryEn;	
	}

	@Override
	public String getTranscriptionText() {
		return transcriptionText;
	}

	@Override
	public void setTranscriptionText(String storyTranscription) {
		this.transcriptionText=storyTranscription;
	}

	@Override
	public String getLanguageDescription() {
		return languageDescription;
	}

	@Override
	public void setLanguageDescription(String storyLanguageDescription) {
		this.languageDescription=storyLanguageDescription;
	}

	@Override
	public String getLanguageSummary() {
		return languageSummary;
	}

	@Override
	public void setLanguageSummary(String storyLanguageSummary) {
		this.languageSummary=storyLanguageSummary;
	}
	
	public List<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}
	
	public Map<String, Integer> getCompletionStatus() {
		return completionStatus;
	}

	public void setCompletionStatus(Map<String, Integer> completionStatus) {
		this.completionStatus = completionStatus;
	}

	public int getItemCount() {
		return itemCount;
	}

	public void setItemCount(int itemCount) {
		this.itemCount = itemCount;
	}
	
	public List<String> getTranscriptionLanguages() {
		return transcriptionLanguages;
	}

	public void setTranscriptionLanguages(List<String> transcriptionLanguages) {
		this.transcriptionLanguages = transcriptionLanguages;
	}

	@Override
	public void copyFromStory(StoryEntity story) {
		this.storyId = story.getStoryId();
		this.title = story.getTitle();
		this.source = story.getSource();
		this.description = story.getDescription();
		this.descriptionEn = story.getDescriptionEn();
		this.summary = story.getSummary();
		this.summaryEn = story.getSummaryEn();
		this.languageDescription = story.getLanguageDescription();
		this.languageSummary = story.getLanguageSummary();
		this.transcriptionText = story.getTranscriptionText();
		if(story.getKeywords()!=null) this.keywords = new ArrayList<String>(story.getKeywords());
		if(story.getCompletionStatus()!=null) this.completionStatus = new HashMap<String, Integer>(story.getCompletionStatus());
		if(story.getTranscriptionLanguages()!=null) this.transcriptionLanguages = new ArrayList<String>(story.getTranscriptionLanguages()); 
		this.itemCount = story.getItemCount();
	}
}
