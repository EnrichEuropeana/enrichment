package eu.europeana.enrichment.web.model;

/*
 * This class represents the Rest Post body structure for
 * end point /enrichment/translation. The request body will
 * be parsed into this class.
 */
public class TranslationRequest extends BaseTranslationRequest {

    private String originalText;
    private String type;

    public String getOriginalText() {
        return originalText;
    }

    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
