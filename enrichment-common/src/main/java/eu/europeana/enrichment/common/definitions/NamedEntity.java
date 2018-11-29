package eu.europeana.enrichment.common.definitions;

import java.util.ArrayList;

public interface NamedEntity {
	
	String getKey();
	void setKey(String key);
	void addPosition(Integer position);
	ArrayList<Integer> getPositions();
	void setPositions(ArrayList<Integer> positions);
}
