package eu.europeana.enrichment.common.definitions;

import java.util.List;

public interface NamedEntity {
	
	String getId();
	String getClassificationType();
	void setClassificationType(String classificationtype);
	String getKey();
	void setKey(String key);
	void addPosition(Integer position);
	List<Integer> getPositions();
	void setPositions(List<Integer> positions);
	
	List<String> getEuropeanaIds();
	void setEuropeanaIds(List<String> ids);
	void addEuopeanaId(String id);
	List<String> getWikidataIds();
	void setWikidataIds(List<String> ids);
	void addWikidataId(String id);
	
}
