package eu.europeana.enrichment.solr.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.beans.Field;

import eu.europeana.enrichment.model.WikidataAgent;
import eu.europeana.enrichment.model.WikidataEntity;
import eu.europeana.enrichment.model.impl.WikidataAgentImpl;
import eu.europeana.enrichment.model.impl.WikidataEntityImpl;
import eu.europeana.enrichment.solr.model.vocabulary.EntitySolrFields;

public class SolrWikidataAgentImpl extends WikidataAgentImpl implements WikidataAgent{
	

	public SolrWikidataAgentImpl (WikidataAgent copy) {
		this.setAltLabel(copy.getAltLabel());
		this.setCountry(copy.getCountry());
		this.setDepiction(copy.getDepiction());
		this.setDescription(copy.getDescription());
		this.setEntityId(copy.getEntityId());
		this.setInternalType(copy.getInternalType());
		this.setModificationDate(copy.getModificationDate());
		this.setPrefLabel(copy.getPrefLabel());
		this.setSameAs(copy.getSameAs());
		this.setDateOfBirth(copy.getDateOfBirth());
		this.setDateOfDeath(copy.getDateOfDeath());
		this.setOccupation(copy.getOccupation());
	}
	
	@Override
	@Field(EntitySolrFields.PREF_LABEL)
	public void setPrefLabel(List<List<String>> prefLabel) {
		super.setPrefLabel(prefLabel);
	}

	@Override
	@Field(EntitySolrFields.ALT_LABEL)
	public void setAltLabel(List<List<String>> altLabel) {
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
	public void setDescription(List<List<String>> description) {
		super.setDescription(description);
	}

	@Override
	@Field(EntitySolrFields.COUNTRY)
	public void setCountry(String country) {
		super.setCountry(country);		
	}	

	@Override
	@Field(EntitySolrFields.SAME_AS)
	public void setSameAs(List<List<String>> wikidataURLs) {
		super.setSameAs(wikidataURLs);		
	}


	@Override
	@Field(EntitySolrFields.DATE_OF_BIRTH)
	public void setDateOfBirth(String setDateOfBirth) {
		super.setDateOfBirth(setDateOfBirth);
		
	}

	@Override
	@Field(EntitySolrFields.DATE_OF_DEATH)
	public void setDateOfDeath(String setDateOfDeath) {
		super.setDateOfDeath(setDateOfDeath);
	}


	@Override
	@Field(EntitySolrFields.OCCUPATION)
	public void setOccupation(String setOccupation) {
		super.setOccupation(setOccupation);
	}

}
