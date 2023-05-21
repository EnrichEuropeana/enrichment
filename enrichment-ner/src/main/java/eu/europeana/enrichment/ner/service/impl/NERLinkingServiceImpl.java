package eu.europeana.enrichment.ner.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.definitions.model.impl.NamedEntityImpl;
import eu.europeana.enrichment.definitions.model.impl.PositionEntityImpl;
import eu.europeana.enrichment.definitions.model.vocabulary.NerTools;
import eu.europeana.enrichment.mongo.service.PersistentNamedEntityService;
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
	public boolean addLinkingInformation(NamedEntityImpl namedEntity, List<PositionEntityImpl> positions, List<String> linkingTools) throws Exception {
		//TODO: change classification and language from all to specific
//		if(linkingTools.contains(NERLinkingService.TOOL_EUROPEANA)) {
			/*
			 * Agents with only first name or last name will not be searched
			 */
//			if(currentNamedEntity.getEuropeanaIds()==null) {
//				if((currentNamedEntity.getType() == NERClassification.AGENT.toString() && currentNamedEntity.getLabel()!=null && HelperFunctions.toArray(currentNamedEntity.getLabel(),null).length > 1) ||
//						currentNamedEntity.getType() != NERClassification.AGENT.toString())
//				{
//					List<String> europeanaIDs = europeanaEntityService.getEntitySuggestions(currentNamedEntity.getLabel(), "all", sourceLanguage);//classification);
//					if(europeanaIDs != null && europeanaIDs.size() > 0) {
//						currentNamedEntity.setEuropeanaIds(europeanaIDs);
//					}
//				}
//				//TODO: else block if no entry was found then with sourceLanguage flag
//			}
//		}
		
		if(linkingTools.contains(EnrichmentConstants.WIKIDATA_LINKING)) {	
			return setWikidataIdsAndDbpediaWikidataIds(namedEntity, positions);
		}
		return false;
	}
	
	private boolean setWikidataIdsAndDbpediaWikidataIds(NamedEntityImpl ne, List<PositionEntityImpl> positions) throws Exception {
		//returns if the named entity has been changed to save it later
		boolean updated=false;
		
		//check for which tools we need linking
		Set<String> nerTools = computeNerToolsForLinking(positions);
		
		if(nerTools.contains(NerTools.Dbpedia.getStringValue()) && ne.getDbpediaWikidataIds()==null) {
			//set the dbpedia wikidata ids
			List<String> dbpediaWikidataIds = new ArrayList<String>();
			DBpediaResponse dbpediaResponse = dbpediaSpotlight.getDBpediaResponse(ne.getDBpediaId());
			if(dbpediaResponse!=null && dbpediaResponse.getWikidataUrls().size()>0) {
				dbpediaWikidataIds.addAll(dbpediaResponse.getWikidataUrls());
			}
			
			if(dbpediaWikidataIds.size()>0) {
				ne.setDbpediaWikidataIds(dbpediaWikidataIds);
				updated=true;
			}			
		}
		
		if(nerTools.contains(NerTools.Stanford.getStringValue()) && ne.getWikidataSearchIds()==null) {
			//fetch the wikidata search ids
			List<String> wikidataSearchIds = wikidataService.getWikidataIdWithWikidataSearch(ne.getLabel());
			if(wikidataSearchIds.size()>0) {
				ne.setWikidataSearchIds(wikidataSearchIds);
				updated=true;
			}
		}
		return updated;
	}
	
	private Set<String> computeNerToolsForLinking(List<PositionEntityImpl> positions) {
		Set<String> nerTools = new HashSet<>();
		for(PositionEntityImpl pos : positions) {
			Map<Integer,String> offsets = pos.getOffsetsTranslatedText();
			for(Map.Entry<Integer, String> entry : offsets.entrySet()) {
				if(entry.getValue().contains(NerTools.Stanford.getStringValue())) {
					nerTools.add(NerTools.Stanford.getStringValue());
				}
				if(entry.getValue().contains(NerTools.Dbpedia.getStringValue())) {
					nerTools.add(NerTools.Dbpedia.getStringValue());
				}
				if(nerTools.size() == NerTools.values().length) {
					break;
				}
			}
			if(nerTools.size() == NerTools.values().length) {
				break;
			}
		}
		return nerTools;
	}
}
