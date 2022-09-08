package eu.europeana.enrichment.web.model;

import org.springframework.data.annotation.Id;
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
    @Id
    private String id;
    private String keyword;
    private String lang;
    private String keywordEn;
    private String prefLabelEn;
    private String type;
    private int position;
    private String wikidataTypes;
    private String dbpId;
    private String dbpWkdId;
    private String prefWkdId;
    private String wkdIds;
    private String wkdAltLabelIds;

    public KeywordView() {
    }

//    public KeywordView(KeywordNamedEntity kne) {
//        this.kne = kne;
//    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getKeywordEn() {
        return keywordEn;
    }

    public void setKeywordEn(String keywordEn) {
        this.keywordEn = keywordEn;
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

    public String getDbpId() {
        return dbpId;
    }

    public void setDbpId(String dbpId) {
        this.dbpId = dbpId;
    }

    public String getDbpWkdId() {
        return dbpWkdId;
    }

    public void setDbpWkdId(String dbpWkdId) {
        this.dbpWkdId = dbpWkdId;
    }

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

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

}