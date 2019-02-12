package eu.europeana.enrichment.mongo.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import eu.europeana.enrichment.model.NamedEntity;
import eu.europeana.enrichment.model.PositionEntity;


public class NamedEntityImpl implements NamedEntity{

	//id will be used for storing MongoDB _id
	@Id
    public String _id;
	public String type;
	public String key;
	public List<String> europeanaIds;
	public List<String> wikidataIds;
	public List<PositionEntity> positionEntities;
	
	public NamedEntityImpl() {
		init();
	}
	
	public NamedEntityImpl(String key) {
		init();
		this.key = key;
	}
	
	void init() {
		key = "";
		positionEntities = new ArrayList<>();
		europeanaIds = new ArrayList<String>();
		wikidataIds = new ArrayList<String>();
	}
	
	public String getId() {
		return _id;
	}
	
	@Override
	public String toString() {
		return key;
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
	public String getKey() {
		return key;
	}
	@Override
	public void setKey(String key) {
		this.key = key;
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
}
