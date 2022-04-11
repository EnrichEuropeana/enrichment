package eu.europeana.enrichment.ner.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.model.impl.NamedEntityImpl;
import eu.europeana.enrichment.model.vocabulary.NERConstants;
import eu.europeana.enrichment.mongo.service.PersistentNamedEntityService;
import eu.europeana.enrichment.ner.enumeration.NERClassification;
import eu.europeana.enrichment.ner.linking.DBpediaSpotlight;
import eu.europeana.enrichment.ner.linking.EuropeanaEntityService;
import eu.europeana.enrichment.ner.linking.WikidataService;
import eu.europeana.enrichment.ner.linking.model.DBpediaResponse;
import eu.europeana.enrichment.ner.service.NERLinkingService;
@Service(EnrichmentConstants.BEAN_ENRICHMENT_NER_LINKING_SERVICE)
public class NERLinkingServiceImpl implements NERLinkingService {

	Logger logger = LogManager.getLogger(getClass());
	
	@Autowired
	EuropeanaEntityService europeanaEntityService;

	@Autowired
	WikidataService wikidataService;
	
	@Autowired
	DBpediaSpotlight dbpediaSpotlight;
	
	@Autowired
	PersistentNamedEntityService persistentNamedEntityService;
	/*
	 * Tool names for named entity linking defined 
	 */
	
	@Override
	public void addLinkingInformation(NamedEntityImpl namedEntity, List<String> linkingTools, String sourceLanguage, String nerTool) throws IOException {
		if(namedEntity == null || linkingTools == null || linkingTools.size() == 0 || sourceLanguage==null)
			return;
		
		boolean wikidata = false;
		boolean europeana = false;
		
		for(String linkingTool : linkingTools) {
			if(linkingTool.equals(NERLinkingService.TOOL_WIKIDATA))
				wikidata = true;
			else if(linkingTool.equals(NERLinkingService.TOOL_EUROPEANA))
				europeana = true;
		}

		NamedEntityImpl dbNamedEntity = persistentNamedEntityService.findNamedEntity(namedEntity.getLabel(), namedEntity.getType());
		
		// TODO: change classification and language from all to specific
		if(europeana && namedEntity.getEuropeanaIds()==null) {
			/*
			 * Agents with only first name or last name will not be searched
			 */
			if((namedEntity.getType() == NERClassification.AGENT.toString() && namedEntity.getLabel()!=null && namedEntity.getLabel().split(" ").length > 1) ||
					namedEntity.getType() != NERClassification.AGENT.toString())
			{
				List<String> europeanaIDs = europeanaEntityService.getEntitySuggestions(namedEntity.getLabel(), "all", sourceLanguage);//classification);
				if(europeanaIDs != null && europeanaIDs.size() > 0) {
					namedEntity.setEuropeanaIds(europeanaIDs);
				}
			}
			//TODO: else block if no entry was found then with sourceLanguage flag
		}
		if(wikidata) {
			//populate the wikidataIds
			if(dbNamedEntity==null) {
				List<String> wikidataLabelAndTypeMatchIDs = null;
				List<String> wikidataLabelMatchIDs = null;
				//TODO: implement information retrieval from Wikidata
				if(namedEntity.getType().equals(NERClassification.AGENT.toString())) {
					String namedEntityKey = namedEntity.getLabel();
					//Agents with only first name or last name will not be searched
					if(StringUtils.containsWhitespace(namedEntityKey)) {
						wikidataLabelAndTypeMatchIDs = wikidataService.getWikidataAgentIdWithLabelAltLabel(namedEntity.getLabel(), sourceLanguage);
					}
				}
				else if(namedEntity.getType().equals(NERClassification.PLACE.toString())) {
					wikidataLabelAndTypeMatchIDs = wikidataService.getWikidataPlaceIdWithLabelAltLabel(namedEntity.getLabel(), sourceLanguage);
				}	
				else {
					wikidataLabelAndTypeMatchIDs = wikidataService.getWikidataIdWithLabelAltLabel(namedEntity.getLabel(), sourceLanguage);
					wikidataLabelMatchIDs=wikidataLabelAndTypeMatchIDs;
				}
				if(wikidataLabelAndTypeMatchIDs!=null) {
					namedEntity.setWikidataLabelAndTypeMatchIds(wikidataLabelAndTypeMatchIDs);
				}
				
				if(wikidataLabelMatchIDs==null) {
					wikidataLabelMatchIDs = wikidataService.getWikidataIdWithLabelAltLabel(namedEntity.getLabel(), sourceLanguage);
				}
				if(wikidataLabelMatchIDs!=null) {
					namedEntity.setWikidataLabelMatchIds(wikidataLabelMatchIDs);
				}
			}
			
			//populate the dbpediaWikidataIds
			if(nerTool.equals(NERConstants.dbpediaSpotlightName) && namedEntity.getDBpediaIds()!=null) {
				for(String dbpediaUri : namedEntity.getDBpediaIds()) {
					//only fetch the dbpedia ids that are new, i.e. do not exist in the dbpedia ids of the entity from the db
					if(dbNamedEntity==null || 
						dbNamedEntity.getDBpediaIds()==null || 
						!dbNamedEntity.getDBpediaIds().contains(dbpediaUri)) {
						try {
							DBpediaResponse response = dbpediaSpotlight.getDBpediaResponse(dbpediaUri);
							if(response != null && response.getWikidataUrls()!=null && response.getWikidataUrls().size()>0)
							{				
								if(namedEntity.getDbpediaWikidataIds()==null) {
									namedEntity.setDbpediaWikidataIds(response.getWikidataUrls());
								}
								else {
									for(String wikidataUrl : response.getWikidataUrls()) {
										if(!namedEntity.getDbpediaWikidataIds().contains(wikidataUrl)) {
											namedEntity.getDbpediaWikidataIds().add(wikidataUrl);
										}
									}
								}
							}
								
						} catch (JAXBException e) {
							logger.log(Level.ERROR, "Exception during the deserialization of the dbpedia response.", e);
						}
					}
				}		
			}
			
			//populate the preferred WikidataIds
			if(nerTool.equals(NERConstants.dbpediaSpotlightName)) {
				//setting the prefered wikidata ids where the label and type match
				List<String> preferredWikidataIdsLabelAndTypeMatch = new ArrayList<String>();
				if(namedEntity.getWikidataLabelAndTypeMatchIds() != null) {
					for(String wikidataID : namedEntity.getWikidataLabelAndTypeMatchIds()) {
						if(namedEntity.getDbpediaWikidataIds()!=null && namedEntity.getDbpediaWikidataIds().contains(wikidataID)) {
							preferredWikidataIdsLabelAndTypeMatch.add(wikidataID);
						}
					}
				}
				if(preferredWikidataIdsLabelAndTypeMatch.size()>0) {
					namedEntity.setPreferredWikidataLabelAndTypeMatchIds(preferredWikidataIdsLabelAndTypeMatch);
					namedEntity.setPreferedWikidataId(preferredWikidataIdsLabelAndTypeMatch.get(0));
				}
				
				//setting the prefered wikidata ids where the label matches
				List<String> preferredWikidataIdsLabelMatch = new ArrayList<String>();
				if(namedEntity.getWikidataLabelMatchIds() != null) {
					for(String wikidataID : namedEntity.getWikidataLabelMatchIds()) {
						if(namedEntity.getDbpediaWikidataIds()!=null && namedEntity.getDbpediaWikidataIds().contains(wikidataID)) {
							preferredWikidataIdsLabelMatch.add(wikidataID);
						}
					}
				}
				if(preferredWikidataIdsLabelMatch.size()>0) {
					namedEntity.setPreferredWikidataLabelMatchIds(preferredWikidataIdsLabelMatch);
					if(namedEntity.getPreferedWikidataId()==null) {
						namedEntity.setPreferedWikidataId(preferredWikidataIdsLabelMatch.get(0));
					}
				}
			}
			
			//populate the preferred wikidata id as the main, one id
			if(namedEntity.getPreferedWikidataId()==null) {
				if(namedEntity.getWikidataLabelAndTypeMatchIds()!=null) {
					namedEntity.setPreferedWikidataId(namedEntity.getWikidataLabelAndTypeMatchIds().get(0));
				}
				else if(namedEntity.getWikidataLabelMatchIds()!=null) {
					namedEntity.setPreferedWikidataId(namedEntity.getWikidataLabelMatchIds().get(0));
				}
			}
		}
	}

}
