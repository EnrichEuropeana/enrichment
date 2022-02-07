package eu.europeana.enrichment.ner.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.europeana.enrichment.common.commons.AppConfigConstants;
import eu.europeana.enrichment.model.NamedEntity;
import eu.europeana.enrichment.ner.enumeration.NERClassification;
import eu.europeana.enrichment.ner.linking.DBpediaSpotlight;
import eu.europeana.enrichment.ner.linking.EuropeanaEntityService;
import eu.europeana.enrichment.ner.linking.WikidataService;
import eu.europeana.enrichment.ner.linking.model.DBpediaResponse;
import eu.europeana.enrichment.ner.service.NERLinkingService;
@Service(AppConfigConstants.BEAN_ENRICHMENT_NER_LINKING_SERVICE)
public class NERLinkingServiceImpl implements NERLinkingService {

	Logger logger = LogManager.getLogger(getClass());
	
	@Autowired
	EuropeanaEntityService europeanaEntityService;

	@Autowired
	WikidataService wikidataService;
	
	@Autowired
	DBpediaSpotlight dbpediaSpotlight;
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
		if(europeana && namedEntity.getEuropeanaIds()!=null && namedEntity.getEuropeanaIds().size() == 0) {
			/*
			 * Agents with only first name or last name will not be searched
			 */
			if((namedEntity.getType() == NERClassification.AGENT.toString() && namedEntity.getLabel()!=null && namedEntity.getLabel().split(" ").length > 1) ||
					namedEntity.getType() != NERClassification.AGENT.toString())
			{
				List<String> europeanaIDs = europeanaEntityService.getEntitySuggestions(namedEntity.getLabel(), "all", "en");//classification);
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
			if(namedEntity.getDbpediaWikidataIds()!=null && namedEntity.getDbpediaWikidataIds().size() == 0 && namedEntity.getDBpediaIds()!=null) {
				for(String dbpediaUri : namedEntity.getDBpediaIds()) {
					try {
						DBpediaResponse response = dbpediaSpotlight.getDBpediaResponse(dbpediaUri);
						if(response != null)
						{
							for(String id : response.getWikidataUrls()) namedEntity.addDbpediaWikidataId(id);
						}
							
					} catch (JAXBException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						//throw e;
					}
				}
			}
			
			List<String> wikidataIDs = new ArrayList<>();
			
			if(namedEntity.getWikidataIds()!=null && namedEntity.getWikidataIds().size()>0)
			{
				wikidataIDs = namedEntity.getWikidataIds();
			}
			else if(namedEntity.getWikidataIds()!=null && namedEntity.getWikidataIds().size() == 0) {
				//TODO: implement information retrieval from Wikidata
				if(namedEntity.getType().equals(NERClassification.AGENT.toString())) {
					String namedEntityKey = namedEntity.getLabel();
					/*
					 * Agents with only first name or last name will not be searched
					 */
					if(namedEntityKey.split(" ").length > 1)
						wikidataIDs = wikidataService.getWikidataAgentIdWithLabel(namedEntity.getLabel(), "en");
				}
				else if(namedEntity.getType().equals(NERClassification.PLACE.toString()))
					wikidataIDs = wikidataService.getWikidataPlaceIdWithLabelAltLabel(namedEntity.getLabel(), sourceLanguage);
			}
			
			
			if(namedEntity.getPreferredWikidataIds()!=null && namedEntity.getPreferredWikidataIds().size() == 0) {
				if(wikidataIDs != null && wikidataIDs.size() > 0) {
					for(String wikidataID : wikidataIDs) {
						if(namedEntity.getDbpediaWikidataIds()!=null && namedEntity.getDbpediaWikidataIds().contains(wikidataID))
							namedEntity.addPreferredWikidataId(wikidataID);
						
						if(namedEntity.getWikidataIds()!=null && !namedEntity.getWikidataIds().contains(wikidataID)) namedEntity.addWikidataId(wikidataID);
					}
				}
			}
			// TODO: else block if no entry was found then with sourceLanguage flag
		}
	}

}
