package eu.europeana.enrichment.model.impl;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Field;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Index;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexes;
import eu.europeana.enrichment.model.NamedEntity;
import eu.europeana.enrichment.model.PositionEntity;

@Entity(value="NamedEntityImpl")
public class NamedEntityImpl implements NamedEntity{

	protected String type;
	protected String label;
	protected List<String> europeanaIds;
	protected List<String> wikidataIds;
	protected List<String> dbpediaIds;
	protected List<String> dbpediaWikidataIds;
	protected List<String> preferredWikidataIds;
	
	protected List<PositionEntity> positionEntities;

	//id will be used for storing MongoDB _id
	@Id
    public String _id = new ObjectId().toString();
	
	@Override
	public String getId() {
		return _id;
	}
	
	public NamedEntityImpl (NamedEntity copy)
	{
		this.type=copy.getType();
		this.label=copy.getLabel();
		if(copy.getEuropeanaIds()!=null) this.europeanaIds = new ArrayList<String>(copy.getEuropeanaIds());
		if(copy.getWikidataIds()!=null) this.wikidataIds = new ArrayList<String>(copy.getWikidataIds());
		if(copy.getDBpediaIds()!=null) this.dbpediaIds = new ArrayList<String>(copy.getDBpediaIds());
		if(copy.getDbpediaWikidataIds()!=null) this.dbpediaWikidataIds = new ArrayList<String>(copy.getDbpediaWikidataIds());
		if(copy.getPreferredWikidataIds()!=null) this.preferredWikidataIds = new ArrayList<String>(copy.getPreferredWikidataIds());
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
	@Override
	public String getType() {
		return this.type;
	}
	@Override
	public void setType(String classificationtype) {
		this.type = classificationtype;
	}
	@Override
	public String getLabel() {
		return label;
	}
	@Override
	public void setLabel(String key) {
		this.label = key;
	}

	@Override
	public List<String> getEuropeanaIds() {
		return europeanaIds;
	}

	@Override
	public void setEuropeanaIds(List<String> ids) {
		this.europeanaIds = ids;
	}

	@Override
	public void addEuopeanaId(String id) {
		this.europeanaIds.add(id);
	}

	@Override
	public List<String> getWikidataIds() {
		return wikidataIds;
	}

	@Override
	public void setWikidataIds(List<String> ids) {
		this.wikidataIds = ids;
	}

	@Override
	public void addWikidataId(String id) {
		wikidataIds.add(id);
	}
	
	@Override
	public List<String> getDbpediaWikidataIds(){
		return dbpediaWikidataIds;
	}
	
	@Override
	public void setDbpediaWikidataIds(List<String> ids) {
		dbpediaWikidataIds = ids;
	}
	
	@Override
	public void addDbpediaWikidataId(String id) {
		dbpediaWikidataIds.add(id);
	}
	
	@Override
	public List<String> getPreferredWikidataIds(){
		return preferredWikidataIds;
	}
	
	@Override
	public void setPreferredWikidataIds(List<String> ids) {
		preferredWikidataIds = ids;
	}
	
	@Override
	public void addPreferredWikidataId(String id) {
		preferredWikidataIds.add(id);
	}

	@Override
	public void addPositionEntity(PositionEntity positionEntity) {
		positionEntities.add(positionEntity);
	}

	@Override
	public void setPositionEntities(List<PositionEntity> positions) {
		positionEntities = positions;
	}

	@Override
	public List<PositionEntity> getPositionEntities() {
		return positionEntities;
	}

	@Override
	public List<String> getDBpediaIds() {
		return dbpediaIds;
	}

	@Override
	public void setDBpediaIds(List<String> ids) {
		dbpediaIds = ids;
	}

	@Override
	public void addDBpediaId(String id) {
		dbpediaIds.add(id);
	}


}
