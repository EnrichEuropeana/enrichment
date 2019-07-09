package eu.europeana.enrichment.web.model;

/*
 * This class represents the Rest Post body structure for
 * end point /enrichment/translation. The request body will
 * be parsed into this class.
 */
public class EnrichmentTranslationRequest {

	public static final String PARAM_STORY_OR_ITEM_ID = "storyId or itemId";
	public static final String PARAM_STORY_ID = "storyId";
	public static final String PARAM_STORY_ITEM_ID = "itemId";
	public static final String PARAM_TEXT = "text";
	public static final String PARAM_ORIGINAL_TEXT = "originalText";	
	public static final String PARAM_TYPE = "type";
	public static final String PARAM_TRANSLATION_TOOL = "translationTools";
	public static final String PARAM_SOURCE_LANGUAGE = "sourceLanguage";
	public static final String PARAM_SEND_REQUEST = "sendRequest";
	
	public String storyId;
	public String itemId;
	public String text;
	public String type;
	public String translationTool;
	public String originalText;

//	public Boolean sendRequest;
//	
//	public Boolean getSendRequest() {
//		return sendRequest;
//	}
//	public void setSendRequest(Boolean sendRequest) {
//		this.sendRequest = sendRequest;
//	}
	public String getStoryId() {
		return storyId;
	}
	public void setStoryId(String storyId) {
		this.storyId = storyId;
	}
	public String getItemId() {
		return itemId;
	}
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

		
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTranslationTool() {
		return translationTool;
	}
	public void setTranslationTool(String tool) {
		this.translationTool = tool;
	}
	
	public String getOriginalText() {
		return originalText;
	}
	public void setOriginalText(String originalText) {
		this.originalText = originalText;
	}

	
}
