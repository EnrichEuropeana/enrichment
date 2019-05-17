package eu.europeana.enrichment.solr.model;

import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.beans.Field;

import eu.europeana.enrichment.model.WikidataPlace;
import eu.europeana.enrichment.model.impl.WikidataPlaceImpl;
import eu.europeana.enrichment.solr.model.vocabulary.EntitySolrFields;

public class SolrWikidataPlaceImpl extends WikidataPlaceImpl implements WikidataPlace {

	public SolrWikidataPlaceImpl (WikidataPlace copy) {
		this.setAltLabel(copy.getAltLabel());
		this.setCountry(copy.getCountry());
		this.setDepiction(copy.getDepiction());
		this.setDescription(copy.getDescription());
		this.setEntityId(copy.getEntityId());
		this.setInternalType(copy.getInternalType());
		this.setModificationDate(copy.getModificationDate());
		this.setPrefLabel(copy.getPrefLabel());
		this.setSameAs(copy.getSameAs());
		this.setLogo(copy.getLogo());
		this.setLatitude(copy.getLatitude());
		this.setLongitude(copy.getLongitude());
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
	@Field(EntitySolrFields.LOGO)
	public void setLogo(String setLogo) {
		super.setLogo(setLogo);		
	}


	@Override
	@Field(EntitySolrFields.LATITUDE)
	public void setLatitude(float setLatitude) {
		super.setLatitude(setLatitude);
	}


	@Override
	@Field(EntitySolrFields.LONGITUDE)
	public void setLongitude(float setLongitude) {
		super.setLongitude(setLongitude);		
	}
	
}
