package eu.europeana.enrichment.solr.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.beans.Field;

import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.impl.StoryEntityImpl;
import eu.europeana.enrichment.solr.model.vocabulary.StoryEntitySolrFields;

public class SolrStoryEntityImpl extends StoryEntityImpl implements StoryEntity {

	public SolrStoryEntityImpl (StoryEntity copy) {
		this.setStoryId(copy.getStoryId());
		this.setTranscriptionText(copy.getTranscriptionText());
		this.setDescription(copy.getDescription());
		this.setSummary(copy.getSummary());
		this.setDescriptionEn(copy.getDescriptionEn());
		this.setSummaryEn(copy.getSummaryEn());		
		this.setSource(copy.getSource());
		this.setTitle(copy.getTitle());
		this.setLanguageDescription(copy.getLanguageDescription());
		this.setLanguageSummary(copy.getLanguageSummary());
		if(copy.getCompletionStatus()!=null) this.setCompletionStatus(new HashMap<String,Integer>(copy.getCompletionStatus()));
		if(copy.getKeywords()!=null) this.setKeywords(new ArrayList<String> (copy.getKeywords()));
		if(copy.getTranscriptionLanguages()!=null) this.setTranscriptionLanguages(new ArrayList<String>(copy.getTranscriptionLanguages()));
		this.setItemCount(copy.getItemCount());
	}
	
	
	@Override
	@Field(StoryEntitySolrFields.DESCRIPTION)
	public void setDescription(String storyDescription) {
		super.setDescription(storyDescription);
	}

	@Override
	@Field(StoryEntitySolrFields.SUMMARY)
	public void setSummary(String storySummary) {
		super.setSummary(storySummary);
	}
	
	@Override
	@Field(StoryEntitySolrFields.DESCRIPTION_EN)
	public void setDescriptionEn(String storyDescriptionEn) {
		super.setDescriptionEn(storyDescriptionEn);
	}

	@Override
	@Field(StoryEntitySolrFields.SUMMARY_EN)
	public void setSummaryEn(String storySummaryEn) {
		super.setSummaryEn(storySummaryEn);
	}

	@Override
	@Field(StoryEntitySolrFields.STORY_ID)
	public void setStoryId(String storyId) {
		super.setStoryId(storyId);
	}
	
	@Override
	@Field(StoryEntitySolrFields.TRANSCRIPTION)
	public void setTranscriptionText(String text) {
		super.setTranscriptionText(text);
	}
	
	@Override
	@Field(StoryEntitySolrFields.SOURCE)
	public void setSource(String source) {
		super.setSource(source);
	}

	@Override
	@Field(StoryEntitySolrFields.TITLE)
	public void setTitle(String title) {
		super.setTitle(title);
	}

	@Override
	@Field(StoryEntitySolrFields.TRANSCRIPTION_LANGUAGES)
	public void setTranscriptionLanguages(List<String> transcriptionLanguages) {
		super.setTranscriptionLanguages(transcriptionLanguages);
	}
	
	@Override
	@Field(StoryEntitySolrFields.KEYWORDS)
	public void setKeywords(List<String> keywords) {
		super.setKeywords(keywords);
	}
	
	@Override
	@Field(StoryEntitySolrFields.LANGUAGE_DESCRIPTION)
	public void setLanguageDescription(String language) {
		super.setLanguageDescription(language);
	}
	
	@Override
	@Field(StoryEntitySolrFields.LANGUAGE_SUMMARY)
	public void setLanguageSummary(String language) {
		super.setLanguageSummary(language);
	}
	
	@Override
	@Field(StoryEntitySolrFields.COMPLETION_STATUS)
	public void setCompletionStatus(Map<String, Integer> completionStatus) {
		super.setCompletionStatus(getCompletionStatus());
	}

	@Override
	@Field(StoryEntitySolrFields.ITEM_COUNT)
	public void setItemCount(int itemCount) {
		super.setItemCount(itemCount);
	}
}
