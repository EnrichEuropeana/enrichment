package eu.europeana.enrichment.ner.model;

import java.util.ArrayList;

public class NEREntry {

	public String key;
	public ArrayList<Integer> positions; //Offset position
	
	public NEREntry() {
		init();
	}
	
	public NEREntry(String key) {
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
