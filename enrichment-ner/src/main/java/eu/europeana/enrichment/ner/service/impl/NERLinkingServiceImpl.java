package eu.europeana.enrichment.ner.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.common.commons.HelperFunctions;
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

	@Override
	public void addLinkingInformation(NamedEntityImpl newNamedEntity, NamedEntityImpl dbNamedEntity, List<String> linkingTools, String sourceLanguage, String nerTool, boolean matchType) throws Exception {
		// TODO: change classification and language from all to specific
		if(linkingTools.contains(NERLinkingService.TOOL_EUROPEANA) && newNamedEntity.getEuropeanaIds()==null) {
			/*
			 * Agents with only first name or last name will not be searched
			 */
			if((newNamedEntity.getType() == NERClassification.AGENT.toString() && newNamedEntity.getLabel()!=null && HelperFunctions.toArray(newNamedEntity.getLabel(),null).length > 1) ||
					newNamedEntity.getType() != NERClassification.AGENT.toString())
			{
				List<String> europeanaIDs = europeanaEntityService.getEntitySuggestions(newNamedEntity.getLabel(), "all", sourceLanguage);//classification);
				if(europeanaIDs != null && europeanaIDs.size() > 0) {
					newNamedEntity.setEuropeanaIds(europeanaIDs);
				}
			}
			//TODO: else block if no entry was found then with sourceLanguage flag
		}
		
		if(linkingTools.contains(NERLinkingService.TOOL_WIKIDATA)) {
			if(dbNamedEntity==null) {
				
				setWikidataIdsAndDbpediaWikidataIds(newNamedEntity, nerTool);
				
				//compute the preferred wikidata id as the main, one id
				String preferedWikidataId = wikidataService.computePreferedWikidataId(newNamedEntity, matchType);
				if(preferedWikidataId!=null) { 
					newNamedEntity.setPreferedWikidataId(preferedWikidataId);
				}
			}
		}
	}
	
	private void setWikidataIdsAndDbpediaWikidataIds(NamedEntityImpl ne, String nerTool) throws Exception {
		if(nerTool.equalsIgnoreCase(NERConstants.dbpediaSpotlightName)) {
			//set the dbpedia wikidata ids
			List<String> dbpediaWikidataIds = new ArrayList<String>();
			DBpediaResponse dbpediaResponse = dbpediaSpotlight.getDBpediaResponse(ne.getDBpediaId());
			if(dbpediaResponse!=null && dbpediaResponse.getWikidataUrls()!=null) {
				dbpediaWikidataIds.addAll(dbpediaResponse.getWikidataUrls().stream().collect(Collectors.toSet()));
			}
			if(dbpediaWikidataIds.size()>0) {
				ne.setDbpediaWikidataIds(dbpediaWikidataIds);
			}
			//in a very rare situations it can be the case that the dbpediaId exists but no dbpedia wikidata ids are found
			else {
				//fetch the wikidata ids from the wikidata search
				List<String> wikidataLabelAltLabelAndTypeMatchIDs = wikidataService.getWikidataIdWithWikidataSearch(ne.getLabel());
				if(wikidataLabelAltLabelAndTypeMatchIDs.size()>0) {
					ne.setWikidataLabelAltLabelAndTypeMatchIds(wikidataLabelAltLabelAndTypeMatchIDs);
				}				
			}
		}
		else if(nerTool.equalsIgnoreCase(NERConstants.stanfordNer)) {
			//fetch the wikidata ids from the wikidata search
			List<String> wikidataLabelAltLabelAndTypeMatchIDs = wikidataService.getWikidataIdWithWikidataSearch(ne.getLabel());
			if(wikidataLabelAltLabelAndTypeMatchIDs.size()>0) {
				ne.setWikidataLabelAltLabelAndTypeMatchIds(wikidataLabelAltLabelAndTypeMatchIDs);
			}
		}
	}
		
}
