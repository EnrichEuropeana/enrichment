package eu.europeana.enrichment.solr.model;

import java.util.List;

import org.apache.solr.client.solrj.beans.Field;

import eu.europeana.enrichment.model.NamedEntity;
import eu.europeana.enrichment.model.PositionEntity;
import eu.europeana.enrichment.mongo.model.DBNamedEntityImpl;
import eu.europeana.enrichment.solr.model.vocabulary.NamedEntitySolrFields;

public class SolrNamedEntityImpl extends DBNamedEntityImpl implements NamedEntity {

	@Override
	@Field(NamedEntitySolrFields.TYPE)
	public void setType(String classificationtype) {
		super.setType(classificationtype);
	}

	@Override
	@Field(NamedEntitySolrFields.KEY)
	public void setKey(String key) {
		super.setKey(key);
	}

	@Override
	//@Field(NamedEntitySolrFields.EUROPEANA_IDS)
	public void setEuropeanaIds(List<String> ids) {
		super.setEuropeanaIds(ids);
	}

	@Override
	//@Field(NamedEntitySolrFields.WIKIPEDIA_IDS)
	public void setWikidataIds(List<String> ids) {
		super.setWikidataIds(ids);
	}

	@Override
	//@Field(NamedEntitySolrFields.POSITION_ENTITIES)
	public void setPositionEntities(List<PositionEntity> positions) {
		super.setPositionEntities(positions);
	}


}
