package eu.europeana.enrichment.common.definitions;

import java.util.List;

public interface NamedEntity {
	
	/*
	 * Database ID
	 */
	String getId();
	/*
	 * Classification type (e.g. agent, place, ..)
	 */
	String getType();
	void setType(String classificationtype);
	/*
	 * Named entity label (e.g. Vienna, Max Mustermann, ..)
	 */
	String getKey();
	void setKey(String key);
	/*
	 * Named entity position at the original text
	 * Position list contains only offset
	 */
	void addPosition(Integer position);
	List<Integer> getPositions();
	void setPositions(List<Integer> positions);
	
	/*
	 * Named entity linking information
	 */
	List<String> getEuropeanaIds();
	void setEuropeanaIds(List<String> ids);
	void addEuopeanaId(String id);
	List<String> getWikidataIds();
	void setWikidataIds(List<String> ids);
	void addWikidataId(String id);
	
}
