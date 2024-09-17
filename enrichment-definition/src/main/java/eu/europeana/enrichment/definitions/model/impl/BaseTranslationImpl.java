package eu.europeana.enrichment.definitions.model.impl;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import org.bson.types.ObjectId;

import dev.morphia.annotations.Id;
import eu.europeana.enrichment.common.commons.HelperFunctions;

public class BaseTranslationImpl extends BaseEntityImpl {

    protected String key;
    private String language;
    private String translatedText;
    private String tool;
    private String eTranslationId;
    private String storyId;
    private String itemId;
    private String originLangGoogle;
    private String type;
    private String userId;
    @Id
    private ObjectId _id;
    
    public BaseTranslationImpl(BaseTranslationImpl copy) {
        Date now = new Date();
        if(copy.getCreated() == null) {
            setCreated(now);
        } else {
            setCreated(copy.getCreated());
        }
        if(copy.getModified() == null) {
            setModified(now);
        } else {
          setModified(copy.getModified());  
        }
        this.key = copy.getKey();
        this.language = copy.getLanguage();
        this.translatedText = copy.getTranslatedText();
        this.tool = copy.getTool();
        this.eTranslationId = copy.getETranslationId();
        this.storyId = copy.getStoryId();
        this.itemId = copy.getItemId();
        this.originLangGoogle = copy.getOriginLangGoogle();
        this.type = copy.getType();
        this.userId = copy.getUserId();
        //NOTE: must not copy objectId
    }

    public BaseTranslationImpl() {
        Date now = new Date();
        this.setCreated(now);
        this.setModified(now);
    }

    public ObjectId getId() {
        return _id;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getETranslationId() {
        return eTranslationId;
    }

    public void setETranslationId(String eTranslationId) {
        this.eTranslationId = eTranslationId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        key = HelperFunctions.generateHashFromText(text);
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTranslatedText() {
        return translatedText;
    }

    public void setTranslatedText(String translatedText) {
        this.translatedText = translatedText;
    }

    public String getTool() {
        return tool;
    }

    public void setTool(String tool) {
        this.tool = tool;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStoryId() {
        return storyId;
    }

    public void setStoryId(String storyId) {
        this.storyId = storyId;
    }

    public String getOriginLangGoogle() {
        return originLangGoogle;
    }

    public void setOriginLangGoogle(String originLangGoogle) {
        this.originLangGoogle = originLangGoogle;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
