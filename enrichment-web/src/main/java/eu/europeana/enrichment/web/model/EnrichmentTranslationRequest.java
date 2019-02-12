package eu.europeana.enrichment.web.model;

/*
 * This class represents the Rest Post body structure for
 * end point /enrichment/translation. The request body will
 * be parsed into this class.
 */
public class EnrichmentTranslationRequest {

	public String storyId;
	public String storyItemId;
	public String text;
	public String type;
	public String tool;
	public String sourceLanguage;
	
	public String getStoryId() {
		return storyId;
	}
	public void setStoryId(String storyId) {
		this.storyId = storyId;
	}
	public String getStoryItemId() {
		return storyItemId;
	}
	public void setStoryItemId(String storyItemId) {
		this.storyItemId = storyItemId;
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
	public String getTool() {
		return tool;
	}
	public void setTool(String tool) {
		this.tool = tool;
	}
	public String getSourceLanguage() {
		return sourceLanguage;
	}
	public void setSourceLanguage(String sourceLangauge) {
		this.sourceLanguage = sourceLangauge;
	}
	
}
