package eu.europeana.enrichment.ner.service.impl;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;

import eu.europeana.enrichment.common.definitions.NamedEntity;
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
	private String wikidataName = "wikidata";
	private String europeanaName = "europeana";
	
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
				// TODO: change classificiation and laguage from all to specific
				if(europeana) {
					String europeanaResponse = europeanaEntityService.getEntitySuggestions(entity.getKey(), "all", "en");//classification);
					System.out.println("Europeana response: " + europeanaResponse);
					entity.addEuopeanaId(europeanaResponse);
				}
				if(wikidata) {
					List<String> wikidataIds = wikidataService.getWikidataIdWithLabel(entity.getKey(), "en");
					System.out.println("Wikidata response size: " + wikidataIds.size());
					entity.addWikidataId("");
				}
			}
		}
		
	}

}
