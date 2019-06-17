package eu.europeana.enrichment.solr.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.beans.Field;

import eu.europeana.enrichment.model.WikidataAgent;
import eu.europeana.enrichment.model.WikidataEntity;
import eu.europeana.enrichment.model.impl.WikidataAgentImpl;
import eu.europeana.enrichment.model.impl.WikidataEntityImpl;
import eu.europeana.entity.definitions.model.vocabulary.AgentSolrFields;
import eu.europeana.enrichment.solr.commons.SolrUtils;
import eu.europeana.enrichment.solr.model.vocabulary.EntitySolrFields;
import eu.europeana.entity.definitions.model.vocabulary.ConceptSolrFields;
import eu.europeana.entity.definitions.model.vocabulary.OrganizationSolrFields;
//import eu.europeana.entity.definitions.model.vocabulary.SolrConceptSchemeConstants;

public class SolrWikidataAgentImpl extends WikidataAgentImpl implements WikidataAgent{
	
	public SolrWikidataAgentImpl () {
		
	}
	public SolrWikidataAgentImpl (WikidataAgent copy) {
		this.setAltLabel(copy.getAltLabel());
		this.setCountry(copy.getCountry());
		this.setDepiction(copy.getDepiction());
		this.setDescription(copy.getDescription());
		this.setEntityId(copy.getEntityId());
		this.setInternalType(copy.getInternalType());
		this.setModificationDate(copy.getModificationDate());
		this.setPrefLabelStringMap(copy.getPrefLabelStringMap());
		this.setSameAs(copy.getSameAs());
		this.setDateOfBirth(copy.getDateOfBirth());
		this.setDateOfDeath(copy.getDateOfDeath());
		this.setProfessionOrOccupation(copy.getProfessionOrOccupation());
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
	@Field(EntitySolrFields.DATE_OF_BIRTH_ALL)
	public void setDateOfBirth(String[] dateOfBirth) {
		String[] normalizedDateOfBirth = dateOfBirth;
//		if(dateOfBirth!=null && dateOfBirth.length>0)
//		{
//				normalizedDateOfBirth = SolrUtils.normalizeStringList(EntitySolrFields.DATE_OF_BIRTH_ALL, Arrays.asList(dateOfBirth));
//		}
		super.setDateOfBirth(normalizedDateOfBirth);
	}

	@Override
	@Field(EntitySolrFields.DATE_OF_DEATH_ALL)
	public void setDateOfDeath(String[] dateOfDeath) {
		String[] normalizedDateOfDeath = dateOfDeath;
//		if(dateOfDeath!=null && dateOfDeath.length>0)
//		{
//			normalizedDateOfDeath = SolrUtils.normalizeStringList(EntitySolrFields.DATE_OF_DEATH_ALL, Arrays.asList(dateOfDeath));
//		}				
		super.setDateOfDeath(normalizedDateOfDeath);
	}


	@Override
	@Field(EntitySolrFields.PROFESSION_OR_OCCUPATION_ALL)
	public void setProfessionOrOccupation(String[] professionOrOccupation) {
		String[] normalizedProfessionOrOccupation = professionOrOccupation;
//		if(professionOrOccupation!=null && professionOrOccupation.length>0)
//		{
//			normalizedProfessionOrOccupation = SolrUtils.normalizeStringList(EntitySolrFields.PROFESSION_OR_OCCUPATION, Arrays.asList(professionOrOccupation));
//		}				
		super.setProfessionOrOccupation(normalizedProfessionOrOccupation);
	}


}
