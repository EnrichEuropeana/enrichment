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

import eu.europeana.enrichment.common.commons.AppConfigConstants;
import eu.europeana.enrichment.model.impl.NamedEntityImpl;
import eu.europeana.enrichment.mongo.service.PersistentNamedEntityService;
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
	
	@Autowired
	PersistentNamedEntityService persistentNamedEntityService;
	/*
	 * Tool names for named entity linking defined 
	 */
	
	@Override
	public void addLinkingInformation(NamedEntityImpl namedEntity, List<String> linkingTools, String sourceLanguage) throws IOException {
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

		NamedEntityImpl dbNamedEntity = persistentNamedEntityService.findNamedEntity(namedEntity.getLabel());
		
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
			
			//populate the dbpediaWikidataIds
			if(namedEntity.getDBpediaIds()!=null) {
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
			
			if(namedEntity.getWikidataIds()==null) {
				//populate the wikidataIds
				List<String> wikidataIDs = null;
				//TODO: implement information retrieval from Wikidata
				if(namedEntity.getType().equals(NERClassification.AGENT.toString())) {
					String namedEntityKey = namedEntity.getLabel();
					/*
					 * Agents with only first name or last name will not be searched
					 */
					if(StringUtils.containsWhitespace(namedEntityKey))
						wikidataIDs = wikidataService.getWikidataAgentIdWithLabel(namedEntity.getLabel(), sourceLanguage);
				}
				else if(namedEntity.getType().equals(NERClassification.PLACE.toString())) {
					wikidataIDs = wikidataService.getWikidataPlaceIdWithLabelAltLabel(namedEntity.getLabel(), sourceLanguage);
				}	
				
				if(wikidataIDs!=null) {
					namedEntity.setWikidataIds(wikidataIDs);
				}
			}
			
			//populate the preferredWikidataIds
			List<String> preferredWikidataIds = new ArrayList<String>();
			if(namedEntity.getWikidataIds() != null) {
				for(String wikidataID : namedEntity.getWikidataIds()) {
					if(namedEntity.getDbpediaWikidataIds()!=null && namedEntity.getDbpediaWikidataIds().contains(wikidataID)) {
						preferredWikidataIds.add(wikidataID);
					}
				}
			}
			if(preferredWikidataIds.size()>0) {
				namedEntity.setPreferredWikidataIds(preferredWikidataIds);
			}

			// TODO: else block if no entry was found then with sourceLanguage flag
		}
	}

}
