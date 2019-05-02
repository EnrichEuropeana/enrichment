package eu.europeana.enrichment.solr.model;

import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.beans.Field;

import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.WikidataEntity;
import eu.europeana.enrichment.model.impl.WikidataEntityImpl;
import eu.europeana.enrichment.solr.model.vocabulary.EntitySolrFields;


public class SolrWikidataEntityImpl extends WikidataEntityImpl implements WikidataEntity {

	public SolrWikidataEntityImpl (WikidataEntity copy) {
		this.setAltLabel(copy.getAltLabel());
		this.setCountry(copy.getCountry());
		this.setDepiction(copy.getDepiction());
		this.setDescription(copy.getDescription());
		this.setEntityId(copy.getEntityId());
		this.setInternalType(copy.getInternalType());
		this.setModificationDate(copy.getModificationDate());
		this.setPrefLabel(copy.getPrefLabel());
		this.setSameAs(copy.getSameAs());
	}
	
	@Override
	@Field(EntitySolrFields.PREF_LABEL)
	public void setPrefLabel(Map<String, String> prefLabel) {
		super.setPrefLabel(prefLabel);
	}

	@Override
	@Field(EntitySolrFields.ALT_LABEL)
	public void setAltLabel(Map<String, List<String>> altLabel) {
		super.setAltLabel(altLabel);
	}

	@Override
	@Field(EntitySolrFields.ID)
	public void setEntityId(String enitityId) {
		super.setEntityId(enitityId);
	}
	
	@Override
	@Field(EntitySolrFields.INTERNAL_TYPE)
	public void setInternalType(String entityType) {
		super.setInternalType(entityType);		
	}

	@Override
	@Field(EntitySolrFields.MODIFIED)
	public void setModificationDate(String date) {
		super.setModificationDate(date);		
	}

	@Override
	@Field(EntitySolrFields.DEPICTION)
	public void setDepiction(String depiction) {
		super.setDepiction(depiction);
		
	}

	@Override
	@Field(EntitySolrFields.DESCRIPTION)
	public void setDescription(Map<String, String> description) {
		super.setDescription(description);
	}

	@Override
	@Field(EntitySolrFields.COUNTRY)
	public void setCountry(String country) {
		super.setCountry(country);		
	}	

	@Override
	@Field(EntitySolrFields.SAME_AS)
	public void setSameAs(Map<String, String> wikidataURLs) {
		super.setSameAs(wikidataURLs);		
	}

	
}
