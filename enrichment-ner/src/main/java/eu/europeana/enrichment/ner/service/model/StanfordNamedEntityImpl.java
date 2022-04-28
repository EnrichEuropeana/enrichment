package eu.europeana.enrichment.ner.service.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonSetter;

import eu.europeana.enrichment.model.impl.NamedEntityImpl;
import eu.europeana.enrichment.model.impl.PositionEntityImpl;

public class StanfordNamedEntityImpl extends NamedEntityImpl {

	@JsonSetter("type")
	@Override
	public void setType(String classificationtype) {
		this.type = classificationtype;
	}

	@JsonSetter("key")
	@Override
	public void setLabel(String key) {
		this.label = key;
	}
	
	
	@JsonSetter("positionEntities")
	public void setPositionEntitiesImpl(List<PositionEntityImpl> positions) {
		this.positionEntities = new ArrayList<>();
		for(PositionEntityImpl pos : positions) {
			positionEntities.add(pos);
		}
	}
	
	@JsonSetter("dbpediaWikidataIds")
	@Override
	public void setDbpediaWikidataIds(List<String> ids) {
		this.dbpediaWikidataIds = ids;
	}

	@JsonSetter("europeanaIds")
	@Override
	public void setEuropeanaIds(List<String> ids) {
		this.europeanaIds = ids;
	}

	@JsonSetter("wikidataIds")
	@Override
	public void setWikidataLabelAndTypeMatchIds(List<String> ids) {
		this.wikidataLabelAndTypeMatchIds = ids;
	}
	

	@JsonSetter("preferedWikidataIds")
	@Override
	public void setPreferredWikidataIds(List<String> ids) {
		this.preferredWikidataIds = ids;
	}

	@JsonSetter("DBpediaIds")
	@Override
	public void setDBpediaIds(List<String> ids) {
		this.dbpediaIds = ids;
	}

	
}
