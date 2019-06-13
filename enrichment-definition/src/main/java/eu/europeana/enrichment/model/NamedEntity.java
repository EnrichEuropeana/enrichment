package eu.europeana.enrichment.model;

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
	 */
	void addPositionEntity(PositionEntity positionEntity);
	List<PositionEntity> getPositionEntities();
	void setPositionEntities(List<PositionEntity> positions);
	
	/*
	 * Named entity linking information
	 */
	List<String> getEuropeanaIds();
	void setEuropeanaIds(List<String> ids);
	void addEuopeanaId(String id);
	List<String> getWikidataIds();
	void setWikidataIds(List<String> ids);
	void addWikidataId(String id);
	List<String> getDbpediaWikidataIds();
	void setDbpediaWikidataIds(List<String> ids);
	void addDbpediaWikidataId(String id);
	List<String> getPreferredWikidataIds();
	void setPreferredWikidataIds(List<String> ids);
	void addPreferredWikidataId(String id);
	
	List<String> getDBpediaIds();
	void setDBpediaIds(List<String> ids);
	void addDBpediaId(String id);
	
}
