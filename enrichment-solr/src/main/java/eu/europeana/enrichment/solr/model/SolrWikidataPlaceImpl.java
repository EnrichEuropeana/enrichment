package eu.europeana.enrichment.solr.model;

import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.beans.Field;

import eu.europeana.enrichment.model.WikidataPlace;
import eu.europeana.enrichment.model.impl.WikidataPlaceImpl;
import eu.europeana.enrichment.solr.commons.SolrUtils;
import eu.europeana.enrichment.solr.model.vocabulary.EntitySolrFields;
import eu.europeana.entity.definitions.model.vocabulary.PlaceSolrFields;


public class SolrWikidataPlaceImpl extends WikidataPlaceImpl implements WikidataPlace {

	public SolrWikidataPlaceImpl () {
		
	}
	public SolrWikidataPlaceImpl (WikidataPlace copy) {
		this.setAltLabel(copy.getAltLabel());
		this.setCountry(copy.getCountry());
		this.setDepiction(copy.getDepiction());
		this.setDescription(copy.getDescription());
		this.setEntityId(copy.getEntityId());
		this.setInternalType(copy.getInternalType());
		this.setModificationDate(copy.getModificationDate());
		this.setPrefLabelStringMap(copy.getPrefLabelStringMap());
		this.setSameAs(copy.getSameAs());
		this.setLogo(copy.getLogo());
		this.setLatitude(copy.getLatitude());
		this.setLongitude(copy.getLongitude());
	}
	
	@Override
	@Field(EntitySolrFields.PREF_LABEL_ALL)
	public void setPrefLabelStringMap(Map<String, String> prefLabel) {
		Map<String, String> normalizedPrefLabel = prefLabel;
		if(prefLabel!=null && !prefLabel.isEmpty())
		{		
			//normalizedPrefLabel = SolrUtils.normalizeStringMap(EntitySolrFields.PREF_LABEL, prefLabel);
			normalizedPrefLabel = SolrUtils.normalizeStringMapByAddingPrefix(EntitySolrFields.PREF_LABEL+".",prefLabel);
		}
		super.setPrefLabelStringMap(normalizedPrefLabel);
	}

	@Override
	@Field(EntitySolrFields.ALT_LABEL_ALL)
	public void setAltLabel(Map<String, List<String>> altLabel) {
		Map<String, List<String>> normalizedAltLabel = altLabel;
		if(altLabel!=null && !altLabel.isEmpty())
		{
			normalizedAltLabel = SolrUtils.normalizeStringListMapByAddingPrefix(EntitySolrFields.ALT_LABEL+".", altLabel);
			//normalizedAltLabel = SolrUtils.normalizeStringListMap(EntitySolrFields.ALT_LABEL, altLabel);
		}
				
		super.setAltLabel(normalizedAltLabel);
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
	@Field(EntitySolrFields.DC_DESCRIPTION_ALL)
	public void setDescription(Map<String, List<String>> dcDescription) {
		Map<String, List<String>> normalizedDescription = dcDescription;
		if(dcDescription!=null && !dcDescription.isEmpty())
		{
			normalizedDescription = SolrUtils.normalizeStringListMapByAddingPrefix(EntitySolrFields.DC_DESCRIPTION+".",dcDescription);
			//normalizedDescription = SolrUtils.normalizeStringMap(EntitySolrFields.DC_DESCRIPTION, dcDescription);
		}
	    super.setDescription(normalizedDescription);
	}

	@Override
	@Field(EntitySolrFields.COUNTRY)
	public void setCountry(String country) {
		super.setCountry(country);		
	}	

	@Override
	@Field(EntitySolrFields.SAME_AS)
	public void setSameAs(String[] wikidataURLs) {
		super.setSameAs(wikidataURLs);		
	}


	@Override
	@Field(EntitySolrFields.LOGO)
	public void setLogo(String setLogo) {
		super.setLogo(setLogo);		
	}
	
	@Override
	@Field(EntitySolrFields.LATITUDE)
	public void setLatitude(Float latitude) {
		super.setLatitude(latitude);
	}
	
	@Override
	@Field(EntitySolrFields.LONGITUDE)
	public void setLongitude(Float longitude) {
		super.setLongitude(longitude);
	}

}
