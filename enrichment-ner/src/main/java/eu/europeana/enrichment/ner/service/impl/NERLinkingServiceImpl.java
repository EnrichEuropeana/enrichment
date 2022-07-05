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
//				List<String> wikidataLabelAndTypeMatchIDs = null;
//				List<String> wikidataLabelAltLabelAndTypeMatchIDs = null;
//				List<String> wikidataLabelMatchIDs = null;				
				List<String> wikidataLabelAltLabelMatchIDs = null;

//				if(namedEntity.getType().equals(NERClassification.AGENT.toString())) {
//					String namedEntityKey = namedEntity.getLabel();
//					//Agents with only first name or last name will not be searched
//					if(StringUtils.containsWhitespace(namedEntityKey)) {
//						wikidataLabelAndTypeMatchIDs = wikidataService.getWikidataAgentIdWithLabel(namedEntity.getLabel(), sourceLanguage);
//						wikidataLabelAltLabelAndTypeMatchIDs = wikidataService.getWikidataAgentIdWithLabelAltLabel(namedEntity.getLabel(), sourceLanguage);
//					}
//				}
//				else if(namedEntity.getType().equals(NERClassification.PLACE.toString())) {
//					wikidataLabelAndTypeMatchIDs = wikidataService.getWikidataPlaceIdWithLabel(namedEntity.getLabel(), sourceLanguage);
//					wikidataLabelAltLabelAndTypeMatchIDs = wikidataService.getWikidataPlaceIdWithLabelAltLabel(namedEntity.getLabel(), sourceLanguage);
//				}	
//				else {
//					wikidataLabelAndTypeMatchIDs = wikidataService.getWikidataIdWithLabel(namedEntity.getLabel(), sourceLanguage);
//					wikidataLabelAltLabelAndTypeMatchIDs = wikidataService.getWikidataIdWithLabelAltLabel(namedEntity.getLabel(), sourceLanguage);
//				}
//				if(wikidataLabelAndTypeMatchIDs!=null) {
//					namedEntity.setWikidataLabelAndTypeMatchIds(wikidataLabelAndTypeMatchIDs);
//				}
//				if(wikidataLabelAltLabelAndTypeMatchIDs!=null) {
//					namedEntity.setWikidataLabelAltLabelAndTypeMatchIds(wikidataLabelAltLabelAndTypeMatchIDs);
//				}
//				
//				//for the non AGENT and non PLACE types (MISC types etc.) the wikidata ids without the type match are already set
//				if(namedEntity.getType().equals(NERClassification.AGENT.toString()) 
//						|| namedEntity.getType().equals(NERClassification.PLACE.toString())) {				
//					wikidataLabelMatchIDs = wikidataService.getWikidataIdWithLabel(namedEntity.getLabel(), sourceLanguage);
//					wikidataLabelAltLabelMatchIDs = wikidataService.getWikidataIdWithLabelAltLabel(namedEntity.getLabel(), sourceLanguage);
//					if(wikidataLabelMatchIDs!=null)
//						namedEntity.setWikidataLabelMatchIds(wikidataLabelMatchIDs);
//					if(wikidataLabelAltLabelMatchIDs!=null)
//						namedEntity.setWikidataLabelAltLabelMatchIds(wikidataLabelAltLabelMatchIDs);
//				}
				
				wikidataLabelAltLabelMatchIDs = wikidataService.getWikidataIdWithWikidataSearch(namedEntity.getLabel());
				if(wikidataLabelAltLabelMatchIDs!=null)
				namedEntity.setWikidataLabelAltLabelMatchIds(wikidataLabelAltLabelMatchIDs);
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
				List<String> preferredWikidataIds = new ArrayList<String>();
//				if(namedEntity.getWikidataLabelAndTypeMatchIds() != null) {
//					for(String wikidataID : namedEntity.getWikidataLabelAndTypeMatchIds()) {
//						if(namedEntity.getDbpediaWikidataIds()!=null && namedEntity.getDbpediaWikidataIds().contains(wikidataID)) {
//							preferredWikidataIds.add(wikidataID);
//						}
//					}
//				}
//				if(preferredWikidataIds.size()==0) {
//					if(namedEntity.getWikidataLabelMatchIds() != null) {
//						for(String wikidataID : namedEntity.getWikidataLabelMatchIds()) {
//							if(namedEntity.getDbpediaWikidataIds()!=null && namedEntity.getDbpediaWikidataIds().contains(wikidataID)) {
//								preferredWikidataIds.add(wikidataID);
//							}
//						}
//					}
//				}
//				if(preferredWikidataIds.size()==0) {
//					if(namedEntity.getWikidataLabelAltLabelAndTypeMatchIds() != null) {
//						for(String wikidataID : namedEntity.getWikidataLabelAltLabelAndTypeMatchIds()) {
//							if(namedEntity.getDbpediaWikidataIds()!=null && namedEntity.getDbpediaWikidataIds().contains(wikidataID)) {
//								preferredWikidataIds.add(wikidataID);
//							}
//						}
//					}
//				}
//				if(preferredWikidataIds.size()==0) {
//					if(namedEntity.getWikidataLabelAltLabelMatchIds() != null) {
//						for(String wikidataID : namedEntity.getWikidataLabelAltLabelMatchIds()) {
//							if(namedEntity.getDbpediaWikidataIds()!=null && namedEntity.getDbpediaWikidataIds().contains(wikidataID)) {
//								preferredWikidataIds.add(wikidataID);
//							}
//						}
//					}
//				}
				
				if(namedEntity.getWikidataLabelAltLabelMatchIds()!=null) {
					List<String> wikidataLabelAltLabelMatchIds = namedEntity.getWikidataLabelAltLabelMatchIds();
					for(String wikidataId : wikidataLabelAltLabelMatchIds) {
						if(namedEntity.getDbpediaWikidataIds()!=null && namedEntity.getDbpediaWikidataIds().contains(wikidataId)) {
							preferredWikidataIds.add(wikidataId);
						}
					}
				}
				if(preferredWikidataIds.size()==0 && namedEntity.getDbpediaWikidataIds()!=null) {
					preferredWikidataIds.addAll(namedEntity.getDbpediaWikidataIds());
				}
				
				if(preferredWikidataIds.size()>0)
					namedEntity.setPreferredWikidataIds(preferredWikidataIds);
			}
			
			//populate the preferred wikidata id as the main, one id
			String preferedWikidataId = computePreferedWikidataId(namedEntity);
			if(preferedWikidataId!=null)
				namedEntity.setPreferedWikidataId(preferedWikidataId);
		}
	}
	
	String computePreferedWikidataId(NamedEntityImpl namedEntity) {
		//if there is a value in the preferred ids take the first valid one
		if(namedEntity.getPreferredWikidataIds()!=null) {
			for(String wikidataId : namedEntity.getPreferredWikidataIds()) {
				String wikidataJSONResponse = wikidataService.getWikidataJSONFromWikidataID(wikidataId);
				if(wikidataService.validWikidataPage(wikidataJSONResponse)) {
					return wikidataId;
				}
			}
		}

		//get the first wikidata id which is not the "disambiguation page"
//		if(namedEntity.getWikidataLabelAndTypeMatchIds()!=null) {
//			for(String wikidataId : namedEntity.getWikidataLabelAndTypeMatchIds()) {
//				if(wikidataService.validWikidataId(wikidataId)) {
//					return wikidataId;
//				}
//			}
//		}
//		if(namedEntity.getWikidataLabelMatchIds()!=null) {
//			for(String wikidataId : namedEntity.getWikidataLabelMatchIds()) {
//				if(wikidataService.validWikidataId(wikidataId)) {
//					return wikidataId;
//				}
//			}
//		}
//		if(namedEntity.getWikidataLabelAltLabelAndTypeMatchIds()!=null) {
//			for(String wikidataId : namedEntity.getWikidataLabelAltLabelAndTypeMatchIds()) {
//				if(wikidataService.validWikidataId(wikidataId)) {
//					return wikidataId;
//				}
//			}
//		}
//		if(namedEntity.getWikidataLabelAltLabelMatchIds()!=null) {
//			for(String wikidataId : namedEntity.getWikidataLabelAltLabelMatchIds()) {
//				if(wikidataService.validWikidataId(wikidataId)) {
//					return wikidataId;
//				}
//			}
//		}

		//check if there is a wikidata id which contains the exact label name in its prefLabel or altLabel, if yes pick the first one
		if(namedEntity.getWikidataLabelAltLabelMatchIds()!=null) {
			List<String> wikidataLabelAltLabelMachIds = namedEntity.getWikidataLabelAltLabelMatchIds();
			for(String wikidataId : wikidataLabelAltLabelMachIds) {
				String wikidataJSONResponse = wikidataService.getWikidataJSONFromWikidataID(wikidataId);
				if(wikidataService.validWikidataPage(wikidataJSONResponse)
					&& wikidataService.containsNameInLabelOrAltLabelField(wikidataJSONResponse, namedEntity.getLabel())) {
					return wikidataId;
				}
			}
		}
		
		return null;
	}
	
}
