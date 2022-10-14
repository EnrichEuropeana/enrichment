package eu.europeana.enrichment.model.impl;

import java.util.Set;

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
	private String typeFoundByNER;

	private String preferredWikidataId;
	private KeywordWikidataEntity prefferedWikidataEntity;
	
	private String approvedWikidataId;
	private KeywordWikidataEntity approvedWikidataEntity;
	
	private KeywordPosition keywordPosition;
	
	private Set<String> wikidataLabelAltLabelMatchIds;
	private String dbpediaId;
	private Set<String> dbpediaWikidataIds;
	private Set<String> preferredWikidataIds;

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

	public KeywordPosition getKeywordPosition() {
		return keywordPosition;
	}

	public void setKeywordPosition(KeywordPosition keywordPosition) {
		this.keywordPosition = keywordPosition;
	}

	public Set<String> getWikidataLabelAltLabelMatchIds() {
		return wikidataLabelAltLabelMatchIds;
	}

	public void setWikidataLabelAltLabelMatchIds(Set<String> wikidataLabelAltLabelMatchIds) {
		this.wikidataLabelAltLabelMatchIds = wikidataLabelAltLabelMatchIds;
	}

	public String getDbpediaId() {
		return dbpediaId;
	}

	public void setDbpediaId(String dbpediaId) {
		this.dbpediaId = dbpediaId;
	}

	public Set<String> getDbpediaWikidataIds() {
		return dbpediaWikidataIds;
	}

	public void setDbpediaWikidataIds(Set<String> dbpediaWikidataIds) {
		this.dbpediaWikidataIds = dbpediaWikidataIds;
	}

	public Set<String> getPreferredWikidataIds() {
		return preferredWikidataIds;
	}

	public void setPreferredWikidataIds(Set<String> preferredWikidataIds) {
		this.preferredWikidataIds = preferredWikidataIds;
	}
	
	public String getTypeFoundByNER() {
		return typeFoundByNER;
	}

	public void setTypeFoundByNER(String typeFoundByNER) {
		this.typeFoundByNER = typeFoundByNER;
	}

}
