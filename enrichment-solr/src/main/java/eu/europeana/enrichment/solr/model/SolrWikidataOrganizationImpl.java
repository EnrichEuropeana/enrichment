package eu.europeana.enrichment.solr.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.beans.Field;

import eu.europeana.enrichment.common.commons.HelperFunctions;
import eu.europeana.enrichment.definitions.model.WikidataOrganization;
import eu.europeana.enrichment.definitions.model.impl.WikidataOrganizationImpl;
import eu.europeana.enrichment.solr.model.vocabulary.EntitySolrFields;

public class SolrWikidataOrganizationImpl extends WikidataOrganizationImpl implements WikidataOrganization{
	
	public SolrWikidataOrganizationImpl () {
		
	}
	public SolrWikidataOrganizationImpl (WikidataOrganization copy) {
		if(copy.getAltLabel()!=null) this.setAltLabel(new HashMap<String, List<String>>(copy.getAltLabel()));
		this.setCountry(copy.getCountry());
		this.setDepiction(copy.getDepiction());
		if(copy.getDescription()!=null) this.setDescription(new HashMap<String, List<String>>(copy.getDescription()));
		this.setEntityId(copy.getEntityId());
		this.setInternalType(copy.getInternalType());
		this.setModificationDate(copy.getModificationDate());
		if(copy.getPrefLabel()!=null) this.setPrefLabel(new HashMap<String, List<String>>(copy.getPrefLabel()));
		if(copy.getSameAs()!=null) this.setSameAs(Arrays.copyOf(copy.getSameAs(), copy.getSameAs().length));
		
		if(copy.getOfficialWebsite()!=null) this.setOfficialWebsite(Arrays.copyOf(copy.getOfficialWebsite(), copy.getOfficialWebsite().length));
		this.setVIAF_ID(copy.getVIAF_ID());
		this.setISNI(copy.getISNI());
		this.setLogo(copy.getLogo());
		this.setInception(copy.getInception());
		this.setHeadquartersLoc(copy.getHeadquartersLoc());
		this.setHeadquartersPostalCode(copy.getHeadquartersPostalCode());
		this.setHeadquartersStreetAddress(copy.getHeadquartersStreetAddress());
		this.setHeadquartersLatitude(copy.getHeadquartersLatitude());
		this.setHeadquartersLongitude(copy.getHeadquartersLongitude());
		if(copy.getIndustry()!=null) this.setIndustry(Arrays.copyOf(copy.getIndustry(), copy.getIndustry().length));
		if(copy.getPhone()!=null) this.setPhone(Arrays.copyOf(copy.getPhone(), copy.getPhone().length));
	}
	
	@Override
	@Field(EntitySolrFields.PREF_LABEL_ALL)
	public void setPrefLabel(Map<String, List<String>> prefLabel) {
		Map<String, List<String>> normalizedPrefLabel = prefLabel;
		if(prefLabel!=null && !prefLabel.isEmpty())
		{		
			//normalizedPrefLabel = SolrUtils.normalizeStringMap(EntitySolrFields.PREF_LABEL, prefLabel);
			normalizedPrefLabel = HelperFunctions.normalizeStringListMapByAddingPrefix(EntitySolrFields.PREF_LABEL+".",prefLabel);
		}
		super.setPrefLabel(normalizedPrefLabel);
	}

	@Override
	@Field(EntitySolrFields.ALT_LABEL_ALL)
	public void setAltLabel(Map<String, List<String>> altLabel) {
		
		Map<String, List<String>> normalizedAltLabel = altLabel;
		if(altLabel!=null && !altLabel.isEmpty())
		{
			normalizedAltLabel = HelperFunctions.normalizeStringListMapByAddingPrefix(EntitySolrFields.ALT_LABEL+".", altLabel);
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
			normalizedDescription = HelperFunctions.normalizeStringListMapByAddingPrefix(EntitySolrFields.DC_DESCRIPTION+".",dcDescription);
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
	@Field(EntitySolrFields.OFFICIAL_WEBSITE)
	public void setOfficialWebsite(String[] officialWebsite) {
		super.setOfficialWebsite(officialWebsite);
	}
	
	@Override
	@Field(EntitySolrFields.VIAF_ID)
	public void setVIAF_ID(String viafId) {
		super.setVIAF_ID(viafId);
	}

	@Override
	@Field(EntitySolrFields.ISNI)
	public void setISNI(String isniId) {
		super.setISNI(isniId);
	}
	
	@Override
	@Field(EntitySolrFields.LOGO)
	public void setLogo(String logo) {
		super.setLogo(logo);
	}
	
	@Override
	@Field(EntitySolrFields.INCEPTION)
	public void setInception(String inception) {
		super.setInception(inception);
	}

	@Override
	@Field(EntitySolrFields.HEADQUARTERS_LOC)
	public void setHeadquartersLoc(String headquartersLoc) {
		super.setHeadquartersLoc(headquartersLoc);
	}

	@Override
	@Field(EntitySolrFields.HEADQUARTERS_POSTAL_CODE)
	public void setHeadquartersPostalCode(String headquartersPostalCode) {
		super.setHeadquartersPostalCode(headquartersPostalCode);
	}
	
	@Override
	@Field(EntitySolrFields.HEADQUARTERS_STREET_ADDRESS)
	public void setHeadquartersStreetAddress(String headquartersStreetAddress) {
		super.setHeadquartersStreetAddress(headquartersStreetAddress);
	}

	@Override
	@Field(EntitySolrFields.HEADQUARTERS_LATITUDE)
	public void setHeadquartersLatitude(Float headquartersLatitude) {
		super.setHeadquartersLatitude(headquartersLatitude);
	}

	@Override
	@Field(EntitySolrFields.HEADQUARTERS_LONGITUDE)
	public void setHeadquartersLongitude(Float headquartersLongitude) {
		super.setHeadquartersLongitude(headquartersLongitude);
	}

	@Override
	@Field(EntitySolrFields.INDUSTRY)
	public void setIndustry(String[] industry) {
		super.setIndustry(industry);
	}

	@Override
	@Field(EntitySolrFields.PHONE)
	public void setPhone(String[] phone) {
		super.setPhone(phone);
	}

}
