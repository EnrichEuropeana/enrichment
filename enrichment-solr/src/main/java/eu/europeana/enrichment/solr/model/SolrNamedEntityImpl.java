package eu.europeana.enrichment.solr.model;

import java.util.List;

import org.apache.solr.client.solrj.beans.Field;

import eu.europeana.enrichment.model.NamedEntity;
import eu.europeana.enrichment.model.PositionEntity;
import eu.europeana.enrichment.mongo.model.NamedEntityImpl;
import eu.europeana.enrichment.solr.model.vocabulary.ConceptSolrFields;

public class SolrNamedEntityImpl extends NamedEntityImpl implements NamedEntity {

	@Override
	@Field(ConceptSolrFields.RDF_ABOUT)
	public void setType(String classificationtype) {
		this.type = classificationtype;
	}

	@Override
	@Field(ConceptSolrFields.RDF_ABOUT)
	public void setKey(String key) {
		this.key = key;
	}

	@Override
	@Field(ConceptSolrFields.RDF_ABOUT)
	public void setEuropeanaIds(List<String> ids) {
		this.europeanaIds = ids;
	}

	@Override
	@Field(ConceptSolrFields.RDF_ABOUT)
	public void setWikidataIds(List<String> ids) {
		this.wikidataIds = ids;
	}

	@Override
	@Field(ConceptSolrFields.RDF_ABOUT)
	public void setPositionEntities(List<PositionEntity> positions) {
		positionEntities = positions;
	}


}
