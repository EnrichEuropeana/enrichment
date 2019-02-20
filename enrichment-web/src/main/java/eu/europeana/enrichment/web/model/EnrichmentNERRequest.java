package eu.europeana.enrichment.web.model;

import java.util.List;

/*
 * This class represents the Rest Post body structure for
 * end point /enrichment/entities. The request body will
 * be parsed into this class.
 */
public class EnrichmentNERRequest {

	public static final String PARAM_STORY_ID = "storyId";
	public static final String PARAM_STORY_ITEM_IDS = "storyItemIds";
	public static final String PARAM_NER_TOOL = "nerTool";
	public static final String PARAM_LINKING = "linking";
	public static final String PARAM_TRANSLATION_TOOL = "translationTool";
	public static final String PARAM_TRANSLATION_LANGUAGE = "translationLanguage";
	
	public String storyId;
	public List<String> storyItemIds;
	public String nerTool;
	public List<String> linking;
	public String translationTool;
	public String translationLanguage;
	
	public String getStoryId() {
		return storyId;
	}
	public void setStoryId(String storyId) {
		this.storyId = storyId;
	}
	public List<String> getStoryItemIds() {
		return storyItemIds;
	}
	public void setStoryItemIds(List<String> storyItemIds) {
		this.storyItemIds = storyItemIds;
	}
	public String getNERTool() {
		return nerTool;
	}
	public void setNERTool(String tool) {
		this.nerTool = tool;
	}
	public List<String> getLinking(){
		return linking;
	}
	public void setLinking(List<String> linking) {
		this.linking = linking;
	}
	public String getTranslationTool() {
		return translationTool;
	}
	public void setTranslationTool(String translationTool) {
		this.translationTool = translationTool;
	}
	public String getTranslationLanguage() {
		return translationLanguage;
	}
	public void setTranslationlanguage(String translationLanguage) {
		this.translationLanguage = translationLanguage;
	}
}
