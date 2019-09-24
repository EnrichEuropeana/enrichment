package eu.europeana.enrichment.ner.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.europeana.enrichment.model.NamedEntity;
import eu.europeana.enrichment.ner.enumeration.NERClassification;
import eu.europeana.enrichment.ner.linking.DBpediaSpotlight;
import eu.europeana.enrichment.ner.linking.EuropeanaEntityService;
import eu.europeana.enrichment.ner.linking.WikidataService;
import eu.europeana.enrichment.ner.linking.model.DBpediaResponse;
import eu.europeana.enrichment.ner.service.NERLinkingService;

public class NERLinkingServiceImpl implements NERLinkingService {

	Logger logger = LogManager.getLogger(getClass());
	
	@Resource(name = "europeanaEntityService")
	EuropeanaEntityService europeanaEntityService;
	@Resource(name = "wikidataService")
	WikidataService wikidataService;
	/*
	 * Tool names for named entity linking defined 
	 */
	
	@Override
	public void addLinkingInformation(NamedEntity namedEntity, List<String> linkingTools, String sourceLanguage) throws IOException {
		if(linkingTools == null || linkingTools.size() == 0)
			return;
		
		boolean wikidata = false;
		boolean europeana = false;
		
		for(String linkingTool : linkingTools) {
			if(linkingTool.equals(NERLinkingService.TOOL_WIKIDATA))
				wikidata = true;
			else if(linkingTool.equals(NERLinkingService.TOOL_EUROPEANA))
				europeana = true;
		}

		// TODO: change classification and language from all to specific
		if(europeana && namedEntity.getEuropeanaIds().size() == 0) {
			/*
			 * Agents with only first name or last name will not be searched
			 */
			if((namedEntity.getType() == NERClassification.AGENT.toString() && namedEntity.getKey().split(" ").length > 1) ||
					namedEntity.getType() != NERClassification.AGENT.toString())
			{
				List<String> europeanaIDs = europeanaEntityService.getEntitySuggestions(namedEntity.getKey(), "all", "en");//classification);
				if(europeanaIDs != null && europeanaIDs.size() > 0) {
					for(String europeanaID : europeanaIDs) {
						namedEntity.addEuopeanaId(europeanaID);
					}
				}
			}
			//TODO: else block if no entry was found then with sourceLanguage flag
		}
		if(wikidata) {
			//TODO: check if there are dbpedia links
			if(namedEntity.getDbpediaWikidataIds().size() == 0) {
				for(String dbpediaUri : namedEntity.getDBpediaIds()) {
					DBpediaSpotlight dbpediaSpotlight = new DBpediaSpotlight();
					try {
						DBpediaResponse response = dbpediaSpotlight.getDBpediaResponse(dbpediaUri);
						if(response != null)
						{
							for(String id : response.getWikidataUrls()) namedEntity.addDbpediaWikidataId(id);
						}
							
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						throw e;
					}
				}
			}
			
			List<String> wikidataIDs = new ArrayList<>();
			if(namedEntity.getWikidataIds().size() == 0) {
				//TODO: implement information retrieval from Wikidata
				if(namedEntity.getType().equals(NERClassification.AGENT.toString())) {
					String namedEntityKey = namedEntity.getKey();
					/*
					 * Agents with only first name or last name will not be searched
					 */
					if(namedEntityKey.split(" ").length > 1)
						wikidataIDs = wikidataService.getWikidataAgentIdWithLabel(namedEntity.getKey(), "en");
				}
				else if(namedEntity.getType().equals(NERClassification.PLACE.toString()))
					wikidataIDs = wikidataService.getWikidataPlaceIdWithLabelAltLabel(namedEntity.getKey(), sourceLanguage);
			}
			
			if(namedEntity.getPreferredWikidataIds().size() == 0) {
				if(wikidataIDs != null && wikidataIDs.size() > 0) {
					for(String wikidataID : wikidataIDs) {
						if(namedEntity.getDbpediaWikidataIds().contains(wikidataID))
							namedEntity.addPreferredWikidataId(wikidataID);
						namedEntity.addWikidataId(wikidataID);
					}
				}
			}
			// TODO: else block if no entry was found then with sourceLanguage flag
		}
	}

}
