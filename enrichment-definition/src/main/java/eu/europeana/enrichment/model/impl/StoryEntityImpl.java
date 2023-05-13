package eu.europeana.enrichment.model.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.bson.types.ObjectId;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexed;

@Entity(value="StoryEntityImpl")
public class StoryEntityImpl extends BaseEntityImpl {

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
    private ObjectId _id;
	
	
	public ObjectId getId() {
		return _id;
	}
	
	public StoryEntityImpl (StoryEntityImpl copy)
	{
		Date now = new Date();
		this.setCreated(now);
		this.setModified(now);
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
		if(copy.getCompletionStatus()!=null) this.completionStatus = new HashMap<>(copy.getCompletionStatus());
		if(copy.getTranscriptionLanguages()!=null) this.transcriptionLanguages = new ArrayList<>(copy.getTranscriptionLanguages()); 
		this.itemCount = copy.getItemCount();
	}
	
	public StoryEntityImpl() {
		Date now = new Date();
		this.setCreated(now);
		this.setModified(now);
	}

	
	public String getStoryId() {
		return storyId;
	}

	
	public void setStoryId(String storyId) {
		this.storyId = storyId;
	}

	
	public String getTitle() {
		return title;
	}

	
	public void setTitle(String storyTitle) {
		this.title=storyTitle;
		
	}

	
	public String getSource() {
		return source;
	}

	
	public void setSource(String storySource) {
		this.source=storySource;		
	}

	
	public String getDescription() {
		return description;
	}

	
	public void setDescription(String description) {
		this.description=description;
	}
	
	
	public String getDescriptionEn() {
		return descriptionEn;
	}

	
	public void setDescriptionEn(String descriptionEn) {
		this.descriptionEn=descriptionEn;
	}

	
	public String getSummary() {
		return summary;
	}

	
	public void setSummary(String storySummary) {
		this.summary=storySummary;	
	}
	
	
	public String getSummaryEn() {
		return summaryEn;
	}

	
	public void setSummaryEn(String summaryEn) {
		this.summaryEn=summaryEn;	
	}

	
	public String getTranscriptionText() {
		return transcriptionText;
	}

	
	public void setTranscriptionText(String storyTranscription) {
		this.transcriptionText=storyTranscription;
	}

	
	public String getLanguageDescription() {
		return languageDescription;
	}

	
	public void setLanguageDescription(String storyLanguageDescription) {
		this.languageDescription=storyLanguageDescription;
	}

	
	public String getLanguageSummary() {
		return languageSummary;
	}

	
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

	
	public void copyFromStory(StoryEntityImpl story) {
		this.setModified(new Date());		
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
		if(story.getCompletionStatus()!=null) this.completionStatus = new HashMap<>(story.getCompletionStatus());
		if(story.getTranscriptionLanguages()!=null) this.transcriptionLanguages = new ArrayList<>(story.getTranscriptionLanguages()); 
		this.itemCount = story.getItemCount();
	}
	
	@Override
    public boolean equals(Object story){ 
  
        if (story == this) { 
            return true; 
        } 
        if (!(story instanceof StoryEntityImpl)) { 
            return false; 
        }                   
        StoryEntityImpl story_new = (StoryEntityImpl) story; 
        
        if(transcriptionLanguages!=null) {
        	Collections.sort(transcriptionLanguages);
        }
        if(story_new.getTranscriptionLanguages()!=null) {
        	Collections.sort(story_new.getTranscriptionLanguages());
        }

        // Compare the data members and return accordingly  
        return Objects.equals(story_new.getStoryId(), storyId)
        		&& Objects.equals(story_new.getTitle(), title)
        		&& Objects.equals(story_new.getSource(), source)
        		&& Objects.equals(story_new.getDescription(), description)
        		&& Objects.equals(story_new.getSummary(), summary)
        		&& Objects.equals(story_new.getLanguageDescription(), languageDescription)
        		&& Objects.equals(story_new.getLanguageSummary(), languageSummary)
        		&& Objects.equals(story_new.getCompletionStatus(), completionStatus)
        		&& Objects.equals(story_new.getTranscriptionLanguages(), transcriptionLanguages)
        		&& Objects.equals(story_new.getItemCount(), itemCount);
    }     
    
	@Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + storyId.hashCode();
        if(title!=null) {
        	result = 31 * result + title.hashCode();
        }
        if(source!=null) {
        	result = 31 * result + source.hashCode();
        }
        if(description!=null) {
        	result = 31 * result + description.hashCode();
        }
        if(summary!=null) {
        	result = 31 * result + summary.hashCode();
        }
        if(languageDescription!=null) {
        	result = 31 * result + languageDescription.hashCode();
        }
        if(languageSummary!=null) {
        	result = 31 * result + languageSummary.hashCode();
        }
        if(completionStatus!=null) {
        	result = 31 * result + completionStatus.hashCode();
        }
        if(transcriptionLanguages!=null) {
        	result = 31 * result + transcriptionLanguages.hashCode();
        }
       	result = 31 * result + itemCount;
        return result;
    }
	
}
