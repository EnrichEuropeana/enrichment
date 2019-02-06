package eu.europeana.enrichment.ner.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;

import eu.europeana.enrichment.model.NamedEntity;
import eu.europeana.enrichment.ner.enumeration.NERClassification;
import eu.europeana.enrichment.ner.linking.EuropeanaEntityService;
import eu.europeana.enrichment.ner.linking.WikidataService;
import eu.europeana.enrichment.ner.service.NERLinkingService;

public class NERLinkingServiceImpl implements NERLinkingService {

	@Resource(name = "europeanaEntityService")
	EuropeanaEntityService europeanaEntityService;
	@Resource(name = "wikidataService")
	WikidataService wikidataService;
	/*
	 * Tool names for named entity linking defined 
	 */
	private String wikidataName = "Wikidata";
	private String europeanaName = "Europeana";
	
	@Override
	public void addLinkingInformation(TreeMap<String, List<NamedEntity>> findings, List<String> linkingTools, String sourceLanguage) {
		if(linkingTools == null || linkingTools.size() == 0)
			return;
		
		boolean wikidata = false;
		boolean europeana = false;
		
		for(String linkingTool : linkingTools) {
			if(linkingTool.equals(wikidataName))
				wikidata = true;
			else if(linkingTool.equals(europeanaName))
				europeana = true;
		}
			
		for (Map.Entry<String, List<NamedEntity>> classificiationDict : findings.entrySet()) {
			String classification = classificiationDict.getKey();
			List<NamedEntity> entities = classificiationDict.getValue();
			
			for(NamedEntity entity : entities) {
				// TODO: change classification and language from all to specific
				if(europeana) {
					List<String> europeanaIDs = europeanaEntityService.getEntitySuggestions(entity.getKey(), "all", "en");//classification);
					if(europeanaIDs != null && europeanaIDs.size() > 0) {
						for(String europeanaID : europeanaIDs) {
							entity.addEuopeanaId(europeanaID);
						}
					}
					//TODO: else block if no entry was found then with sourceLanguage flag
				}
				if(wikidata) {
					//TODO: implement information retrieval from Wikidata
					List<String> wikidataIDs = new ArrayList<>();
					if(classification == NERClassification.AGENT.toString())
						wikidataIDs = wikidataService.getWikidataAgentIdWithLabel(entity.getKey(), "en");
					else if(classification == NERClassification.PLACE.toString())
						wikidataIDs = wikidataService.getWikidataPlaceIdWithLabel(entity.getKey(), "en");
					if(wikidataIDs != null && wikidataIDs.size() > 0) {
						for(String wikidataID : wikidataIDs) {
							entity.addWikidataId(wikidataID);
						}
					}
					// TODO: else block if no entry was found then with sourceLanguage flag
				}
			}
		}
		
	}

}
