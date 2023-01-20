package eu.europeana.enrichment.web.model;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.datatables.DataTablesOutput.View;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonView;

//@Entity(value = "KeywordNamedEntity")
@Document
@JsonInclude(Include.NON_NULL)
@JsonView(value= {View.class})
public class KeywordView {

//    private KeywordNamedEntity kne;
//    @Id
    private String objectId;
    private String propertyId;
    //keyword -> value
    private String value;
    //keywordEn -> translatedValue
    private String translatedValue;
    //lang -> detectedOriginalLanguage
    private String detectedOriginalLanguage;
    //prefferedWikidataEntity.prefLabel.en
    private String prefLabelEn;
    //typeFoundByNER: agent/place
    private String type;
    //keywordPosition.offsetsTranslatedText
    private int position;
    //prefferedWikidataEntity.wikidataType.*.value
    private String wikidataTypes;
    //private String dbpId;
    //private String dbpWkdId;
    //dbpediaWikidataIds
    private List<String> dbpediaWikidataIds;
    //preferedWikidataId
    private String prefWkdId;
    //??
    private String wkdIds;
    private List<String> preferredWikidataIds;
    //wikidataLabelAltLabelMatchIds
    private String wkdAltLabelIds;
    //top5(tpItemIds)
    private List<Long> tpItemIds;
    //status
    private String status;
    

    public KeywordView() {
    }

//    public KeywordView(KeywordNamedEntity kne) {
//        this.kne = kne;
//    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String keyword) {
        this.value = keyword;
    }

    public String getTranslatedValue() {
        return translatedValue;
    }

    public void setTranslatedValue(String keywordEn) {
        this.translatedValue = keywordEn;
    }

    public String getPrefLabelEn() {
        return prefLabelEn;
    }

    public void setPrefLabelEn(String prefLabelEn) {
        this.prefLabelEn = prefLabelEn;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getWikidataTypes() {
        return wikidataTypes;
    }

    public void setWikidataTypes(String wikidataTypes) {
        this.wikidataTypes = wikidataTypes;
    }

//    public String getDbpId() {
//        return dbpId;
//    }
//
//    public void setDbpId(String dbpId) {
//        this.dbpId = dbpId;
//    }
//
//    public String getDbpWkdId() {
//        return dbpWkdId;
//    }
//
//    public void setDbpWkdId(String dbpWkdId) {
//        this.dbpWkdId = dbpWkdId;
//    }

    public String getPrefWkdId() {
        return prefWkdId;
    }

    public void setPrefWkdId(String prefWkdId) {
        this.prefWkdId = prefWkdId;
    }

    public String getWkdIds() {
        return wkdIds;
    }

    public void setWkdIds(String wkdIds) {
        this.wkdIds = wkdIds;
    }

    public String getWkdAltLabelIds() {
        return wkdAltLabelIds;
    }

    public void setWkdAltLabelIds(String wkdAltLabelIds) {
        this.wkdAltLabelIds = wkdAltLabelIds;
    }

    public String getDetectedOriginalLanguage() {
        return detectedOriginalLanguage;
    }

    public void setDetectedOriginalLanguage(String lang) {
        this.detectedOriginalLanguage = lang;
    }

    public List<Long> getTpItemIds() {
        return tpItemIds;
    }

    public void setTpItemIds(List<Long> tpItemIds) {
        this.tpItemIds = tpItemIds;
    }

    public List<String> getDbpediaWikidataIds() {
        return dbpediaWikidataIds;
    }

    public void setDbpediaWikidataIds(List<String> dbpediaWikidataIds) {
        this.dbpediaWikidataIds = dbpediaWikidataIds;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getPreferredWikidataIds() {
        return preferredWikidataIds;
    }

    public void setPreferredWikidataIds(List<String> preferredWikidataIds) {
        this.preferredWikidataIds = preferredWikidataIds;
    }

}
