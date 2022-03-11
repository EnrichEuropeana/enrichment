package eu.europeana.enrichment.solr.model;

import java.util.List;

import org.apache.solr.client.solrj.beans.Field;

import eu.europeana.enrichment.model.impl.NamedEntityImpl;
import eu.europeana.enrichment.model.impl.PositionEntityImpl;
import eu.europeana.enrichment.solr.model.vocabulary.NamedEntitySolrFields;

public class SolrNamedEntityImpl extends NamedEntityImpl {

	@Override
	@Field(NamedEntitySolrFields.TYPE)
	public void setType(String classificationtype) {
		super.setType(classificationtype);
	}

	@Override
	@Field(NamedEntitySolrFields.KEY)
	public void setLabel(String key) {
		super.setLabel(key);
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
	public void setPositionEntities(List<PositionEntityImpl> positions) {
		super.setPositionEntities(positions);
	}


}
