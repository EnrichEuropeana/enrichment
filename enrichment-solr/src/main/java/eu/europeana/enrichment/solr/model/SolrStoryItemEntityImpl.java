package eu.europeana.enrichment.solr.model;

import org.apache.solr.client.solrj.beans.Field;
import org.bson.types.ObjectId;

import eu.europeana.enrichment.model.StoryItemEntity;
import eu.europeana.enrichment.mongo.model.StoryItemEntityImpl;
import eu.europeana.enrichment.solr.model.vocabulary.StoryItemEntitySolrFields;

public class SolrStoryItemEntityImpl extends StoryItemEntityImpl implements StoryItemEntity {
	
	public SolrStoryItemEntityImpl (StoryItemEntity copy) {
		this.setStoryId(copy.getStoryEntity().getStoryId());
		this.setStoryItemId(copy.getStoryItemId());
		this.setLanguage(copy.getLanguage());
		this.setText(copy.getText());
	}

	@Override
	@Field(StoryItemEntitySolrFields.STORY_ID)
	public void setStoryId(String storyId) {
		super.setStoryId(storyId);
	}

	@Override
	@Field(StoryItemEntitySolrFields.STORY_ITEM_ID)
	public void setStoryItemId(String storyItemId) {
		super.setStoryItemId(storyItemId);
	}


	@Override
	@Field(StoryItemEntitySolrFields.LANGUAGE)
	public void setLanguage(String language) {
		super.setLanguage(language);
	}

	
	@Override
	@Field(StoryItemEntitySolrFields.TEXT)
	public void setText(String text) {
		super.setText(text);
	}
	
}
