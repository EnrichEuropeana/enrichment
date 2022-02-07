package eu.europeana.enrichment.solr.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.beans.Field;

import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.model.impl.ItemEntityImpl;
import eu.europeana.enrichment.solr.model.vocabulary.ItemEntitySolrFields;

public class SolrItemEntityImpl extends ItemEntityImpl implements ItemEntity {
	
	public SolrItemEntityImpl (ItemEntity copy) {
		this.setStoryId(copy.getStoryId());
		this.setItemId(copy.getItemId());
		this.setTranscriptionText(copy.getTranscriptionText());
		this.setSource(copy.getSource());
		this.setTitle(copy.getTitle());
		if (copy.getKeywords()!=null) this.setKeywords(new ArrayList<>(copy.getKeywords()));
		if (copy.getTranscriptionLanguages()!=null) this.setTranscriptionLanguages(new ArrayList<>(copy.getTranscriptionLanguages()));
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
	@Field(ItemEntitySolrFields.TRANSCRIPTION_LANGUAGES)
	public void setTranscriptionLanguages(List<String> transcriptionLanguages) {
		super.setTranscriptionLanguages(transcriptionLanguages);
	}

	
	@Override
	@Field(ItemEntitySolrFields.TRANSCRIPTION)
	public void setTranscriptionText(String text) {
		super.setTranscriptionText(text);
	}
	
	@Override
	@Field(ItemEntitySolrFields.SOURCE)
	public void setSource(String source) {
		super.setSource(source);
	}

	@Override
	@Field(ItemEntitySolrFields.TITLE)
	public void setTitle(String title) {
		super.setTitle(title);
	}

}
