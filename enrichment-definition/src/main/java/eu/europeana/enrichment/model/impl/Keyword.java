package eu.europeana.enrichment.model.impl;

import java.util.List;

import org.bson.types.ObjectId;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;

@Entity(value="keyword")
public class Keyword {

	private String propertyId;
	private String propertyTypeId;
	private String value;
	private String translatedValue;
	private String detectedOriginalLanguage;
	
	private String preferredWikidataId;
	private KeywordWikidataEntity prefferedWikidataEntity;
	
	private String approvedWikidataId;
	private KeywordWikidataEntity approvedWikidataEntity;
	
	private KeywordPosition keywordNamedEntity;
	
	private List<String> wikidataPossibleMatchIds;

	//id will be used for storing MongoDB _id
	@Id
    private ObjectId _id;
	
	public ObjectId get_id() {
		return _id;
	}
		
	public Keyword() {
	}
	
	public String getPropertyId() {
		return propertyId;
	}

	public void setPropertyId(String propertyId) {
		this.propertyId = propertyId;
	}

	public String getPropertyTypeId() {
		return propertyTypeId;
	}

	public void setPropertyTypeId(String propertyTypeId) {
		this.propertyTypeId = propertyTypeId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getTranslatedValue() {
		return translatedValue;
	}

	public void setTranslatedValue(String translatedValue) {
		this.translatedValue = translatedValue;
	}

	public String getDetectedOriginalLanguage() {
		return detectedOriginalLanguage;
	}

	public void setDetectedOriginalLanguage(String detectedOriginalLanguage) {
		this.detectedOriginalLanguage = detectedOriginalLanguage;
	}

	public String getPreferredWikidataId() {
		return preferredWikidataId;
	}

	public void setPreferredWikidataId(String preferredWikidataId) {
		this.preferredWikidataId = preferredWikidataId;
	}

	public KeywordWikidataEntity getPrefferedWikidataEntity() {
		return prefferedWikidataEntity;
	}

	public void setPrefferedWikidataEntity(KeywordWikidataEntity prefferedWikidataEntity) {
		this.prefferedWikidataEntity = prefferedWikidataEntity;
	}

	public String getApprovedWikidataId() {
		return approvedWikidataId;
	}

	public void setApprovedWikidataId(String approvedWikidataId) {
		this.approvedWikidataId = approvedWikidataId;
	}

	public KeywordWikidataEntity getApprovedWikidataEntity() {
		return approvedWikidataEntity;
	}

	public void setApprovedWikidataEntity(KeywordWikidataEntity approvedWikidataEntity) {
		this.approvedWikidataEntity = approvedWikidataEntity;
	}

	public KeywordPosition getKeywordNamedEntity() {
		return keywordNamedEntity;
	}

	public void setKeywordNamedEntity(KeywordPosition keywordNamedEntity) {
		this.keywordNamedEntity = keywordNamedEntity;
	}

	public List<String> getWikidataPossibleMatchIds() {
		return wikidataPossibleMatchIds;
	}

	public void setWikidataPossibleMatchIds(List<String> wikidataPossibleMatchIds) {
		this.wikidataPossibleMatchIds = wikidataPossibleMatchIds;
	}

}
