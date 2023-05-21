package eu.europeana.enrichment.solr.model;

import org.apache.solr.client.solrj.beans.Field;

import eu.europeana.enrichment.definitions.model.impl.TranslationEntityImpl;
import eu.europeana.enrichment.solr.model.vocabulary.TranslationsEntitySolrFields;

public class SolrTranslationsEntityImpl extends TranslationEntityImpl {

	public SolrTranslationsEntityImpl (TranslationEntityImpl copy) {
		this.setStoryId(copy.getStoryId());
		this.setItemId(copy.getItemId());
		this.setTranslatedText(copy.getTranslatedText());
		this.setLanguage(copy.getLanguage());
		this.setTool(copy.getTool());
		this.setType(copy.getType());
		this.key = copy.getKey();	
		this.setOriginLangGoogle(copy.getOriginLangGoogle());
	}
	
	
	@Override
	@Field(TranslationsEntitySolrFields.STORY_ID)
	public void setStoryId(String storyId) {
		super.setStoryId(storyId);
	}
	
	@Override
	@Field(TranslationsEntitySolrFields.ITEM_ID)
	public void setItemId(String itemId) {
		super.setItemId(itemId);
	}

	@Override
	@Field(TranslationsEntitySolrFields.TRANSLATION)
	public void setTranslatedText(String translatedText) {
		super.setTranslatedText(translatedText);
	}

	@Override
	@Field(TranslationsEntitySolrFields.LANGUAGE)
	public void setLanguage(String language) {
		super.setLanguage(language);
	}

	@Override
	@Field(TranslationsEntitySolrFields.TOOL)
	public void setTool(String tool) {
		super.setTool(tool);
	}

	@Override
	@Field(TranslationsEntitySolrFields.TYPE)
	public void setType(String type) {
		super.setType(type);
	}
	
	@Override
	@Field(TranslationsEntitySolrFields.KEY)
	public void setKey(String key) {
		this.key = key;
	}

	@Override
	@Field(TranslationsEntitySolrFields.ORIGIN_LANG_GOOGLE)
	public void setOriginLangGoogle(String originLangGoogle) {
		super.setOriginLangGoogle(originLangGoogle);
	}
}
