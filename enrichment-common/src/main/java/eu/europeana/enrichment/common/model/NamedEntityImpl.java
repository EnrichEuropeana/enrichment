package eu.europeana.enrichment.common.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;

import eu.europeana.enrichment.common.definitions.NamedEntity;

public class NamedEntityImpl implements NamedEntity{

	//id will be used for storing MongoDB _id
	@Id
    public String _id;
	public String type;
	public String key;
	public List<Integer> positions;
	public List<String> europeanaIds;
	public List<String> wikidataIds;
	
	public NamedEntityImpl() {
		init();
	}
	
	public NamedEntityImpl(String key) {
		init();
		this.key = key;
	}
	
	void init() {
		key = "";
		positions = new ArrayList<Integer>();
		europeanaIds = new ArrayList<String>();
		wikidataIds = new ArrayList<String>();
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
	public void addPosition(Integer position) {
		positions.add(position);
	}
	@Override
	public List<Integer> getPositions() {
		return positions;
	}
	@Override
	public void setPositions(List<Integer> positions) {
		this.positions = positions;
	}

	@Override
	public String getId() {
		return _id;
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
}
