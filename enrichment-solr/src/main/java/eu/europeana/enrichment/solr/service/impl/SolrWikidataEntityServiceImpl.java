package eu.europeana.enrichment.solr.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.WikidataAgent;
import eu.europeana.enrichment.model.WikidataEntity;
import eu.europeana.enrichment.model.WikidataPlace;
import eu.europeana.enrichment.model.impl.WikidataAgentImpl;
import eu.europeana.enrichment.model.impl.WikidataEntityImpl;
import eu.europeana.enrichment.model.impl.WikidataPlaceImpl;
import eu.europeana.enrichment.ner.linking.WikidataService;
import eu.europeana.enrichment.solr.exception.SolrNamedEntityServiceException;
import eu.europeana.enrichment.solr.model.SolrStoryEntityImpl;
import eu.europeana.enrichment.solr.model.SolrWikidataAgentImpl;
import eu.europeana.enrichment.solr.model.SolrWikidataEntityImpl;
import eu.europeana.enrichment.solr.model.SolrWikidataPlaceImpl;
import eu.europeana.enrichment.solr.service.SolrBaseClientService;
import eu.europeana.enrichment.solr.service.SolrWikidataEntityService;
import riotcmd.json;

public class SolrWikidataEntityServiceImpl implements SolrWikidataEntityService {

	@Resource(name = "solrBaseClientService")
	SolrBaseClientService solrBaseClientService;
	
	@Resource(name = "wikidataService")
	WikidataService wikidataService;	

	private String solrCore = "wikidata";
	
	private final Logger log = LogManager.getLogger(getClass());
	
	@Override
	public void store(String solrCollection, WikidataEntity wikidataEntity, boolean doCommit) throws SolrNamedEntityServiceException {

		log.debug("store: " + wikidataEntity.toString());	
		
		if(wikidataEntity instanceof WikidataAgent)
		{
			WikidataAgent agentLocal = (WikidataAgent) wikidataEntity;
			SolrWikidataAgentImpl solrWikidataAgent = null;
			
			if(agentLocal instanceof SolrWikidataAgentImpl) {
				solrWikidataAgent=(SolrWikidataAgentImpl) agentLocal;
			}
			else {
				solrWikidataAgent=new SolrWikidataAgentImpl(agentLocal);
			}
			
			solrBaseClientService.store(solrCollection, solrWikidataAgent, doCommit);
		}
		else if (wikidataEntity instanceof WikidataPlace)
		{
			WikidataPlace placeLocal = (WikidataPlace) wikidataEntity;
			SolrWikidataPlaceImpl solrWikidataPlace = null;		
			
			if(placeLocal instanceof SolrWikidataPlaceImpl) {
				solrWikidataPlace=(SolrWikidataPlaceImpl) placeLocal;
			}
			else {
				solrWikidataPlace=new SolrWikidataPlaceImpl(placeLocal);
			}
			
			solrBaseClientService.store(solrCollection, solrWikidataPlace, doCommit);
		}
	}

	@Override
	public void storeWikidataFromURL(String wikidataURL, String type) throws SolrNamedEntityServiceException {
		
		String WikidataJSON = wikidataService.getWikidataJSONFromWikidataID(wikidataURL);
		List<List<String>> jsonElement;
		
		if(type.compareToIgnoreCase("agent")==0)
		{
			WikidataAgent newWikidataAgent = new WikidataAgentImpl ();
			
			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataAgent.getAltLabel_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) newWikidataAgent.setAltLabel(jsonElement);

			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataAgent.getCountry_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) newWikidataAgent.setCountry(jsonElement.get(0).get(0));

			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataAgent.getDateOfBirth_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) newWikidataAgent.setDateOfBirth(jsonElement.get(0).get(0));
			
			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataAgent.getDateOfDeath_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) newWikidataAgent.setDateOfDeath(jsonElement.get(0).get(0));
			
			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataAgent.getDepiction_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) newWikidataAgent.setDepiction(jsonElement.get(0).get(0));
			
			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataAgent.getDescription_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) newWikidataAgent.setDescription(jsonElement);
			
			newWikidataAgent.setEntityId(wikidataURL);			
			
			newWikidataAgent.setInternalType(type);
			
			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataAgent.getModificationDate_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) newWikidataAgent.setModificationDate(jsonElement.get(0).get(0));
			
			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataAgent.getProfessionOrOccupation_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) newWikidataAgent.setOccupation(jsonElement.get(0).get(0));
			
			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataAgent.getPrefLabel_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty())	newWikidataAgent.setPrefLabel(jsonElement);
			
			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataAgent.getSameAs_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty())	newWikidataAgent.setSameAs(jsonElement);
			
			store(solrCore, newWikidataAgent, true);
		}
		else if (type.compareToIgnoreCase("place")==0)
		{
			
			WikidataPlace newWikidataPlace = new WikidataPlaceImpl ();
			
			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataPlace.getAltLabel_jsonProp()); 
			if(jsonElement!=null && !jsonElement.isEmpty()) newWikidataPlace.setAltLabel(jsonElement);

			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataPlace.getCountry_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) newWikidataPlace.setCountry(jsonElement.get(0).get(0));
			
			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataPlace.getLatitude_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) newWikidataPlace.setLatitude(Float.valueOf(jsonElement.get(0).get(0)));

			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataPlace.getLongitude_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) newWikidataPlace.setLongitude(Float.valueOf(jsonElement.get(0).get(0)));

			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataPlace.getDepiction_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) newWikidataPlace.setDepiction(jsonElement.get(0).get(0));
		
			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataPlace.getDescription_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) newWikidataPlace.setDescription(jsonElement);
			
			newWikidataPlace.setEntityId(wikidataURL);
			
			newWikidataPlace.setInternalType(type);
			
			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataPlace.getModificationDate_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) newWikidataPlace.setModificationDate(jsonElement.get(0).get(0));
		
			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataPlace.getLogo_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) newWikidataPlace.setLogo(jsonElement.get(0).get(0));
		
			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataPlace.getPrefLabel_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) newWikidataPlace.setPrefLabel(jsonElement);
			
			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataPlace.getSameAs_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty())  newWikidataPlace.setSameAs(jsonElement);

			store(solrCore, newWikidataPlace, true);
			
		}	
		
	}
}
