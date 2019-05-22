package eu.europeana.enrichment.ner.linking.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import eu.europeana.enrichment.model.PositionEntity;
import eu.europeana.enrichment.model.impl.NamedEntityImpl;

public class StanfordNamedEntity extends NamedEntityImpl {


	public void setType(String classificationtype) {
		super.setType(classificationtype);
	}
	
	public void setKey(String key) {
		super.setKey(key);
	}
	
	public void setPositionEntities(List<PositionEntity> positions) {
		super.setPositionEntities(positions);
	}
	
	public void setEuropeanaIds(List<String> ids) {
		super.setEuropeanaIds(ids);
	}
	
	public void setWikidataIds(List<String> ids) {
		super.setWikidataIds(ids);
	}
	
	public void setDbpediaWikidataIds(List<String> ids) {
		super.setDbpediaWikidataIds(ids);
	}
	
	public void setPreferedWikidataIds(List<String> ids) {
		super.setPreferedWikidataIds(ids);
	}
	
	public void setDBpediaIds(List<String> ids) {
		super.setDBpediaIds(ids);
	}
	
}
