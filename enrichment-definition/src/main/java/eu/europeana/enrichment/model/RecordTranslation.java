package eu.europeana.enrichment.model;

import java.util.Date;
import java.util.List;

import eu.europeana.enrichment.model.impl.RecordTranslationDetectedLang;

public interface RecordTranslation {

    public static final String TRANSLATION_STATUS_COMPLETE = "COMPLETE";
    public static final String TOOL_GOOGLE = "Google Translate";
    
    void setRecordId(String recordId);

    String getRecordId();

    void setTool(String tool);

    String getTool();

    void setTranslation(List<String> translation);

    List<String> getTranslation();

    void setLanguage(List<String> language);

    List<String> getLanguage();

    void setDescription(List<String> description);

    List<String> getDescription();

    void setTranslationStatus(String translationStatus);

    String getTranslationStatus();
    
    boolean isTranslationComplete();

    void addTranslation(String sourceLanguage, String translatedText);

    void setIdentifier(String identifier);

    String getIdentifier();

	List<RecordTranslationDetectedLang> getDescriptionDetectedLang();
	
	void setDescriptionDetectedLang(List<RecordTranslationDetectedLang> descriptionDetectedLang);

    void setModified(Date modified);

    Date getModified();

    void setRecordDclanguage(List<String> recordDclanguage);

    List<String> getRecordDclanguage();

    void setCreated(Date created);

    Date getCreated();

}
