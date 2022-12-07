package eu.europeana.enrichment.web.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="ItemProperty")
public class KeywordItemView {

    @Id
    @Column(name = "ItemPropertyId")
    private long itemPropertyId;
    @Column(name = "ItemId")
    private long itemId;
    @Column(name = "PropertyId")
    private long keywordId;
    
    public KeywordItemView() {
        //default constructor for JPA
    }
    
    public KeywordItemView(long keywordId) {
        this.keywordId = keywordId;
    }
    
    public long getItemPropertyId() {
        return itemPropertyId;
    }
    public void setItemPropertyId(long itemPropertyId) {
        this.itemPropertyId = itemPropertyId;
    }
    public long getItemId() {
        return itemId;
    }
    public void setItemId(long itemId) {
        this.itemId = itemId;
    }
    public long getKeywordId() {
        return keywordId;
    }
    public void setKeywordId(long keywordId) {
        this.keywordId = keywordId;
    }
}
