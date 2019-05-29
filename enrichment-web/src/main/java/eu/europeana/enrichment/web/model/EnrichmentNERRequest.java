package eu.europeana.enrichment.web.model;

import java.util.List;

/**
 * This class represents the Rest Post body structure for
 * end point /enrichment/entities. The request body will
 * be parsed into this class. An example of the request body:
 * {
 * "linking": ["Wikidata"],
 * "nerTools": ["Stanford_NER"],
 *	"translationTool": "eTranslation",
 *	"translationLanguage": "en",
 *	"storyId": "1495",
 *	"fieldForNER": "description"
 * }
 * @author StevaneticS
 *
 */
public class EnrichmentNERRequest {

	public static final String PARAM_STORY_ID = "storyId";
	public static final String PARAM_STORY_TITLE = "storyTitle";
	public static final String PARAM_STORY_SOURCE = "storySource";
	public static final String PARAM_STORY_DESCRIPTION = "storyDescription";
	public static final String PARAM_STORY_SUMMARY = "storySummary";
	public static final String PARAM_STORY_LANGUAGE = "storyLanguage";
	public static final String PARAM_STORY_TRANSCRIPTION = "storyTranscription";

	public static final String PARAM_ITEM_TITLE = "itemTitle";
	public static final String PARAM_ITEM_TYPE = "itemType";
	public static final String PARAM_ITEM_ID = "itemId";
	public static final String PARAM_ITEM_LANGUAGE = "itemLanguage";
	public static final String PARAM_ITEM_TRANSCRIPTION = "itemTranscription";

	
	public static final String PARAM_STORY_ITEM_IDS = "storyItemIds";
	public static final String PARAM_NER_TOOL = "nerTool";
	public static final String PARAM_LINKING = "linking";
	public static final String PARAM_TRANSLATION_TOOL = "translationTool";
	public static final String PARAM_TRANSLATION_LANGUAGE = "translationLanguage";
	
	public String storyId;
	public List<String> storyItemIds;
	public List<String> nerTools;
	public List<String> linking;
	public String translationTool;
	public String translationLanguage;
	public String fieldForNER;
	
	
	public String getFieldForNER() {
		return fieldForNER;
	}
	public void setFieldForNER(String fieldToTranslate) {
		this.fieldForNER = fieldToTranslate;
	}
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
	public List<String> getNERTools() {
		return nerTools;
	}
	public void setNERTools(List<String> tools) {
		this.nerTools = tools;
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
