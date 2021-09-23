package eu.europeana.enrichment.solr.model;

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
		this.setSource(copy.getSource());
		this.setTitle(copy.getTitle());
		this.setLanguageTranscription(copy.getLanguageTranscription());
		this.setLanguageDescription(copy.getLanguageDescription());
		this.setLanguageSummary(copy.getLanguageSummary());
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
	@Field(StoryEntitySolrFields.LANGUAGE_TRANSCRIPTION)
	public void setLanguageTranscription(String language) {
		super.setLanguageTranscription(language);
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

}
