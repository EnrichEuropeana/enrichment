package eu.europeana.enrichment.model.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonIgnore;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexed;
import eu.europeana.enrichment.model.RecordTranslation;

@Entity(value = "recordTranslationEval")
public class RecordTranslationEval implements RecordTranslation {

    @Id
    @JsonIgnore
    private ObjectId _id;
    @Indexed(options = @IndexOptions(unique = true))
    private String recordId;
    @Indexed(options = @IndexOptions(unique = true))
    private String identifier;
    private List<String> language;
    Map<String, List<String>> dcDescriptionLangAware;
	private List<String> description;
    private List<String> translation;
    private List<String> googleTranslation;
    private String googleDetectedLang;
	private List<String> etTranslation;
    private List<String> deeplTranslation;
    private String translationStatus;

    public RecordTranslationEval() {
    }
    
	public String getGoogleDetectedLang() {
		return googleDetectedLang;
	}

	public void setGoogleDetectedLang(String googleDetectedLang) {
		this.googleDetectedLang = googleDetectedLang;
	}
    
	@Override
	public void setLanguage(List<String> language) {
		this.language=language;
	}

	@Override
	public List<String> getLanguage() {
		return language;
	}

    public Map<String, List<String>> getDcDescriptionLangAware() {
		return dcDescriptionLangAware;
	}

	public void setDcDescriptionLangAware(Map<String, List<String>> dcDescriptionLangAware) {
		this.dcDescriptionLangAware = dcDescriptionLangAware;
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
    public List<String> getTranslation() {
        return translation;
    }

    @Override
    public void setTranslation(List<String> translation) {
        this.translation = translation;
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
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

	@Override
	public void setTool(String tool) {
	}

	@Override
	public String getTool() {
		return null;
	}

	@Override
	public void addTranslation(String sourceLanguage, String translatedText) {
	}
    
    public List<String> getGoogleTranslation() {
		return googleTranslation;
	}

	public void setGoogleTranslation(List<String> googleTranslation) {
		this.googleTranslation = googleTranslation;
	}

	public List<String> getEtTranslation() {
		return etTranslation;
	}

	public void setEtTranslation(List<String> eTranslation) {
		this.etTranslation = eTranslation;
	}

	public List<String> getDeeplTranslation() {
		return deeplTranslation;
	}

	public void setDeeplTranslation(List<String> deeplTranslation) {
		this.deeplTranslation = deeplTranslation;
	}

	@Override
	public List<RecordTranslationDetectedLang> getDescriptionDetectedLang() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDescriptionDetectedLang(List<RecordTranslationDetectedLang> descriptionDetectedLang) {
		// TODO Auto-generated method stub
	}

    @Override
    public void setModified(Date modified) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Date getModified() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setRecordDclanguage(List<String> recordDclanguage) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<String> getRecordDclanguage() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setCreated(Date created) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Date getCreated() {
        // TODO Auto-generated method stub
        return null;
    }
	
}
