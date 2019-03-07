package eu.europeana.enrichment.solr.model;

import org.apache.solr.client.solrj.beans.Field;
import org.bson.types.ObjectId;

import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.mongo.model.ItemEntityImpl;
import eu.europeana.enrichment.solr.model.vocabulary.ItemEntitySolrFields;

public class SolrItemEntityImpl extends ItemEntityImpl implements ItemEntity {
	
	public SolrItemEntityImpl (ItemEntity copy) {
		this.setStoryId(copy.getStoryEntity().getStoryId());
		this.setItemId(copy.getItemId());
		this.setLanguage(copy.getLanguage());
		this.setTranscription(copy.getTranscription());
	}

	@Override
	@Field(ItemEntitySolrFields.STORY_ID)
	public void setStoryId(String storyId) {
		super.setStoryId(storyId);
	}

	@Override
	@Field(ItemEntitySolrFields.STORY_ITEM_ID)
	public void setItemId(String storyItemId) {
		super.setItemId(storyItemId);
	}


	@Override
	@Field(ItemEntitySolrFields.LANGUAGE)
	public void setLanguage(String language) {
		super.setLanguage(language);
	}

	
	@Override
	@Field(ItemEntitySolrFields.TEXT)
	public void setTranscription(String text) {
		super.setTranscription(text);
	}
	
}
