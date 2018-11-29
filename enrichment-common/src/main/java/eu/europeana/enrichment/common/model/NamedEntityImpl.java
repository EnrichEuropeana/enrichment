package eu.europeana.enrichment.common.model;

import java.util.ArrayList;

import eu.europeana.enrichment.common.definitions.NamedEntity;

public class NamedEntityImpl implements NamedEntity{

	public String key;
	public ArrayList<Integer> positions; //Offset position
	
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
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
	public void addPosition(Integer position) {
		positions.add(position);
	}
	
	public ArrayList<Integer> getPositions() {
		return positions;
	}
	public void setPositions(ArrayList<Integer> positions) {
		this.positions = positions;
	}
}
