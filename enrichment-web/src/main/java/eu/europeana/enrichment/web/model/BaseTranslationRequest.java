package eu.europeana.enrichment.web.model;

public class BaseTranslationRequest {

    public static final String PARAM_STORY_OR_ITEM_ID = "storyId or itemId";
    public static final String PARAM_STORY_ID = "storyId";
    public static final String PARAM_STORY_ITEM_ID = "itemId";
    public static final String PARAM_TEXT = "text";
    public static final String PARAM_ORIGINAL_TEXT = "originalText";
    public static final String PARAM_TYPE = "type";
    public static final String PARAM_TRANSLATION_TOOL = "translationTool";
    public static final String PARAM_SOURCE_LANGUAGE = "sourceLanguage";
    public static final String PARAM_SEND_REQUEST = "sendRequest";

    private String storyId;
    private String itemId;
    private String text;
    private String translationTool;

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

    public String getTranslationTool() {
        return translationTool;
    }

    public void setTranslationTool(String tool) {
        this.translationTool = tool;
    }
}
