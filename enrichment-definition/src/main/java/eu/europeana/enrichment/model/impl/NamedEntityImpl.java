package eu.europeana.enrichment.model.impl;

import java.util.ArrayList;
import java.util.List;

import eu.europeana.enrichment.model.NamedEntity;
import eu.europeana.enrichment.model.PositionEntity;


public class NamedEntityImpl implements NamedEntity{

	protected String type;
	protected String key;
	protected List<String> europeanaIds;
	protected List<String> wikidataIds;
	protected List<String> dbpediaIds;
	protected List<String> dbpediaWikidataIds;
	protected List<String> preferredWikidataIds;
	protected List<PositionEntity> positionEntities;
	

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
		europeanaIds = new ArrayList<>();
		wikidataIds = new ArrayList<>();
		dbpediaIds = new ArrayList<>();
		preferredWikidataIds = new ArrayList<>();
		dbpediaWikidataIds = new ArrayList<>();
	}
	

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
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
