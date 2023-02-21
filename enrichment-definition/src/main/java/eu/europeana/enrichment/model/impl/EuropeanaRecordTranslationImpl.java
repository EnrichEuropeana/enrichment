package eu.europeana.enrichment.model.impl;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonIgnore;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexed;
import eu.europeana.enrichment.model.RecordTranslation;

@Entity(value = "recordTranslationEntity")
public class EuropeanaRecordTranslationImpl implements RecordTranslation {

    @Id
    @JsonIgnore
    private ObjectId _id;
    private List<String> description;
    private List<String> language;
    private List<String> translation;
    private String tool;
    @Indexed(options = @IndexOptions(unique = true))
    private String recordId;
    @Indexed(options = @IndexOptions(unique = true))
    private String identifier;
    private String translationStatus;
    private List<RecordTranslationDetectedLang> descriptionDetectedLanguage;


    public EuropeanaRecordTranslationImpl() {

    }


    @Override
    public List<String> getDescription() {
        return description;
    }


    @Override
    public void setDescription(List<String> description) {
        this.description = description;
    }


    @Override
    public List<String> getLanguage() {
        return language;
    }


    @Override
    public void setLanguage(List<String> language) {
        this.language = language;
    }


    @Override
    public List<String> getTranslation() {
        return translation;
    }


    @Override
    public void setTranslation(List<String> translation) {
        this.translation = translation;
    }


    @Override
    public String getTool() {
        return tool;
    }


    @Override
    public void setTool(String tool) {
        this.tool = tool;
    }


    @Override
    public String getRecordId() {
        return recordId;
    }


    @Override
    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }


    @JsonIgnore
    public ObjectId getObjectId() {
        return _id;
    }


    public void setObjectId(ObjectId _id) {
        this._id = _id;
    }


    @Override
    public String getTranslationStatus() {
        return translationStatus;
    }


    @Override
    public void setTranslationStatus(String translationStatus) {
        this.translationStatus = translationStatus;
    }


    @Override
    @JsonIgnore
    public boolean isTranslationComplete() {
        return RecordTranslation.TRANSLATION_STATUS_COMPLETE.equals(getTranslationStatus());
    }


    @Override
    public void addTranslation(String sourceLanguage, String translatedText) {
        if(getLanguage() == null) {
            setLanguage(new ArrayList<String>());
        }
        getLanguage().add(sourceLanguage);
        
        if(getTranslation() == null) {
            setTranslation(new ArrayList<String>());
        }
        getTranslation().add(translatedText);
    }


    @Override
    public String getIdentifier() {
        return identifier;
    }


    @Override
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Override
	public List<RecordTranslationDetectedLang> getDescriptionDetectedLang() {
		return descriptionDetectedLanguage;
	}

    @Override
	public void setDescriptionDetectedLang(List<RecordTranslationDetectedLang> descriptionDetectedLang) {
		this.descriptionDetectedLanguage = descriptionDetectedLang;
	}
    
}
