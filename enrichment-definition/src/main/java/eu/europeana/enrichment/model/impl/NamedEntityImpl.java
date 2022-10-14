package eu.europeana.enrichment.model.impl;

import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Transient;

@Entity(value="NamedEntityImpl")
public class NamedEntityImpl {

	protected String type;
	protected String label;
	protected List<String> europeanaIds;
	protected Set<String> wikidataLabelAltLabelAndTypeMatchIds;
	protected String dbpediaId;
	protected Set<String> dbpediaWikidataIds;
	protected Set<String> preferredWikidataIds;
	String preferedWikidataId;

	@Transient
	protected PositionEntityImpl positionEntity;

	//id will be used for storing MongoDB _id
	@Id
    private ObjectId _id;
	
	public ObjectId get_id() {
		return _id;
	}
	
	public NamedEntityImpl() {
	}
	
	public NamedEntityImpl(String key) {
		this.label = key;
	}

	@Override
	public String toString() {
		return label;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String classificationtype) {
		this.type = classificationtype;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String key) {
		this.label = key;
	}
	
	public List<String> getEuropeanaIds() {
		return europeanaIds;
	}
	
	public void setEuropeanaIds(List<String> ids) {
		this.europeanaIds = ids;
	}
	
	public void addEuopeanaId(String id) {
		this.europeanaIds.add(id);
	}
	
	public Set<String> getDbpediaWikidataIds(){
		return dbpediaWikidataIds;
	}	
	
	public void setDbpediaWikidataIds(Set<String> ids) {
		dbpediaWikidataIds = ids;
	}
	
	public void setPositionEntity(PositionEntityImpl position) {
		positionEntity = position;
	}
	
	public PositionEntityImpl getPositionEntity() {
		return positionEntity;
	}
	
	public String getDBpediaId() {
		return dbpediaId;
	}
	
	public void setDBpediaId(String id) {
		dbpediaId = id;
	}

	public String getPreferedWikidataId() {
		return preferedWikidataId;
	}

	public void setPreferedWikidataId(String preferedWikidataId) {
		this.preferedWikidataId = preferedWikidataId;
	}
	
	public Set<String> getWikidataLabelAltLabelAndTypeMatchIds() {
		return wikidataLabelAltLabelAndTypeMatchIds;
	}

	public void setWikidataLabelAltLabelAndTypeMatchIds(Set<String> wikidataLabelAltLabelAndTypeMatchIds) {
		this.wikidataLabelAltLabelAndTypeMatchIds = wikidataLabelAltLabelAndTypeMatchIds;
	}

	public Set<String> getPreferredWikidataIds() {
		return preferredWikidataIds;
	}

	public void setPreferredWikidataIds(Set<String> preferredWikidataIds) {
		this.preferredWikidataIds = preferredWikidataIds;
	}

}
