package eu.europeana.enrichment.model.impl;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;

@Entity(value="NamedEntityImpl")
public class NamedEntityImpl {

	protected String type;
	protected String label;
	protected List<String> europeanaIds;
	protected List<String> wikidataLabelMatchIds;
	protected List<String> wikidataLabelAndTypeMatchIds;
	protected List<String> wikidataLabelAltLabelMatchIds;
	protected List<String> wikidataLabelAltLabelAndTypeMatchIds;
	protected List<String> dbpediaIds;
	protected List<String> dbpediaWikidataIds;
	protected List<String> preferredWikidataIds;
	String preferedWikidataId;

	protected List<PositionEntityImpl> positionEntities;

	//id will be used for storing MongoDB _id
	@Id
    private ObjectId _id;
	
	public ObjectId get_id() {
		return _id;
	}
	
	public NamedEntityImpl (NamedEntityImpl copy)
	{
		this.type=copy.getType();
		this.label=copy.getLabel();
		this.preferedWikidataId=copy.getPreferedWikidataId();
		if(copy.getEuropeanaIds()!=null) this.europeanaIds = new ArrayList<String>(copy.getEuropeanaIds());
		if(copy.getWikidataLabelMatchIds()!=null) this.wikidataLabelMatchIds = new ArrayList<String>(copy.getWikidataLabelMatchIds());
		if(copy.getWikidataLabelAndTypeMatchIds()!=null) this.wikidataLabelAndTypeMatchIds = new ArrayList<String>(copy.getWikidataLabelAndTypeMatchIds());
		if(copy.getWikidataLabelAltLabelMatchIds()!=null) this.wikidataLabelAltLabelMatchIds = new ArrayList<String>(copy.getWikidataLabelAltLabelMatchIds());
		if(copy.getWikidataLabelAltLabelAndTypeMatchIds()!=null) this.wikidataLabelAltLabelAndTypeMatchIds = new ArrayList<String>(copy.getWikidataLabelAltLabelAndTypeMatchIds());
		if(copy.getDBpediaIds()!=null) this.dbpediaIds = new ArrayList<String>(copy.getDBpediaIds());
		if(copy.getDbpediaWikidataIds()!=null) this.dbpediaWikidataIds = new ArrayList<String>(copy.getDbpediaWikidataIds());
		if(copy.getPreferredWikidataIds()!=null) this.preferredWikidataIds = new ArrayList<String>(copy.getPreferredWikidataIds());
		if(copy.getPositionEntities()!=null) {
			this.positionEntities = new ArrayList<PositionEntityImpl>();
			for(PositionEntityImpl pe : copy.getPositionEntities()) {
				PositionEntityImpl pe_new = new PositionEntityImpl(pe);
				this.positionEntities.add(pe_new);
			}
		}
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
	
	public List<String> getWikidataLabelAndTypeMatchIds() {
		return wikidataLabelAndTypeMatchIds;
	}
	
	public void setWikidataLabelAndTypeMatchIds(List<String> ids) {
		this.wikidataLabelAndTypeMatchIds = ids;
	}	
	
	public List<String> getDbpediaWikidataIds(){
		return dbpediaWikidataIds;
	}	
	
	public void setDbpediaWikidataIds(List<String> ids) {
		dbpediaWikidataIds = ids;
	}

	public void addPositionEntity(PositionEntityImpl positionEntity) {
		positionEntities.add(positionEntity);
	}
	
	public void setPositionEntities(List<PositionEntityImpl> positions) {
		positionEntities = positions;
	}
	
	public List<PositionEntityImpl> getPositionEntities() {
		return positionEntities;
	}
	
	public List<String> getDBpediaIds() {
		return dbpediaIds;
	}
	
	public void setDBpediaIds(List<String> ids) {
		dbpediaIds = ids;
	}
	
	public void addDBpediaId(String id) {
		dbpediaIds.add(id);
	}

	public List<String> getWikidataLabelMatchIds() {
		return wikidataLabelMatchIds;
	}

	public void setWikidataLabelMatchIds(List<String> wikidataLabelMatchIds) {
		this.wikidataLabelMatchIds = wikidataLabelMatchIds;
	}

	public String getPreferedWikidataId() {
		return preferedWikidataId;
	}

	public void setPreferedWikidataId(String preferedWikidataId) {
		this.preferedWikidataId = preferedWikidataId;
	}
	
	public List<String> getWikidataLabelAltLabelMatchIds() {
		return wikidataLabelAltLabelMatchIds;
	}

	public void setWikidataLabelAltLabelMatchIds(List<String> wikidataLabelAltLabelMatchIds) {
		this.wikidataLabelAltLabelMatchIds = wikidataLabelAltLabelMatchIds;
	}

	public List<String> getWikidataLabelAltLabelAndTypeMatchIds() {
		return wikidataLabelAltLabelAndTypeMatchIds;
	}

	public void setWikidataLabelAltLabelAndTypeMatchIds(List<String> wikidataLabelAltLabelAndTypeMatchIds) {
		this.wikidataLabelAltLabelAndTypeMatchIds = wikidataLabelAltLabelAndTypeMatchIds;
	}

	public List<String> getPreferredWikidataIds() {
		return preferredWikidataIds;
	}

	public void setPreferredWikidataIds(List<String> preferredWikidataIds) {
		this.preferredWikidataIds = preferredWikidataIds;
	}

}
