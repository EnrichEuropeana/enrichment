package eu.europeana.enrichment.solr.model;

import org.apache.solr.client.solrj.beans.Field;

import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.mongo.model.DBStoryEntityImpl;
import eu.europeana.enrichment.solr.model.vocabulary.StoryEntitySolrFields;

public class SolrStoryEntityImpl extends DBStoryEntityImpl implements StoryEntity {

	public SolrStoryEntityImpl (StoryEntity copy) {
		this.setStoryId(copy.getStoryId());
		this.setTranscription(copy.getTranscription());
		this.setDescription(copy.getDescription());
		this.setSummary(copy.getSummary());
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
	@Field(StoryEntitySolrFields.TEXT)
	public void setTranscription(String text) {
		super.setTranscription(text);
	}


}
