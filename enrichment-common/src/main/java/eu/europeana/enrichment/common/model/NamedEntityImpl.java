package eu.europeana.enrichment.common.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;

import eu.europeana.enrichment.common.definitions.NamedEntity;

public class NamedEntityImpl implements NamedEntity{

	//id will be used for storing MongoDB _id
	@Id
    public String _id;
	public String classificationtype;
	public String key;
	public List<Integer> positions; //Offset position
	
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
	}
	
	public String toString() {
		return key;
	}
	
	public String getClassificationType() {
		return this.classificationtype;
	}
	
	public void setClassificationType(String classificationtype) {
		this.classificationtype = classificationtype;
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
	public void addPosition(Integer position) {
		positions.add(position);
	}
	
	public List<Integer> getPositions() {
		return positions;
	}
	public void setPositions(List<Integer> positions) {
		this.positions = positions;
	}

	@Override
	public String getId() {
		return null;
	}
}
