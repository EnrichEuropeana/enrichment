package eu.europeana.enrichment.solr.model;

import org.apache.solr.client.solrj.beans.Field;

import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.mongo.model.StoryEntityImpl;
import eu.europeana.enrichment.solr.model.vocabulary.StoryEntitySolrFields;

public class SolrStoryEntityImpl extends StoryEntityImpl implements StoryEntity {

	public SolrStoryEntityImpl (StoryEntity copy) {
		this.setStoryId(copy.getStoryId());
		this.setStoryTranscription(copy.getStoryTranscription());
	}

	@Override
	@Field(StoryEntitySolrFields.STORY_ID)
	public void setStoryId(String storyId) {
		super.setStoryId(storyId);
	}
	
	@Override
	@Field(StoryEntitySolrFields.TEXT)
	public void setStoryTranscription(String text) {
		super.setStoryTranscription(text);
	}


}
