package eu.europeana.enrichment.web.model;

/*
 * This class represents the Rest Post body structure for
 * end point /enrichment/translation. The request body will
 * be parsed into this class.
 */
public class TranslationUpdateRequest extends BaseTranslationRequest {

    private String userId;
    private String property;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

}
