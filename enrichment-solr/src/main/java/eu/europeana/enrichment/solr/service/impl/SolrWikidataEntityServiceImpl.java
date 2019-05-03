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
		
		if(type.compareToIgnoreCase("agent")==0)
		{
			WikidataAgent newWikidataAgent = new WikidataAgentImpl ();
			
			newWikidataAgent.setAltLabel(wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataAgent.getAltLabel_jsonProp()));

			newWikidataAgent.setCountry(wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataAgent.getCountry_jsonProp()).get(0).get(0));

			newWikidataAgent.setDateOfBirth(wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataAgent.getDateOfBirth_jsonProp()).get(0).get(0));

			newWikidataAgent.setDateOfDeath(wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataAgent.getDateOfDeath_jsonProp()).get(0).get(0));
		
			newWikidataAgent.setDepiction(wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataAgent.getDepiction_jsonProp()).get(0).get(0));
		
			newWikidataAgent.setDescription(wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataAgent.getDescription_jsonProp()));
			
			newWikidataAgent.setEntityId(wikidataURL);
			
			newWikidataAgent.setInternalType(type);
			
			newWikidataAgent.setModificationDate(wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataAgent.getModificationDate_jsonProp()).get(0).get(0));
		
			newWikidataAgent.setOccupation(wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataAgent.getProfessionOrOccupation_jsonProp()).get(0).get(0));
		
			newWikidataAgent.setPrefLabel(wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataAgent.getPrefLabel_jsonProp()));
			
			newWikidataAgent.setSameAs(wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataAgent.getSameAs_jsonProp()));
				
			store(solrCore, newWikidataAgent, true);
		}
		else if (type.compareToIgnoreCase("place")==0)
		{
			
			WikidataPlace newWikidataPlace = new WikidataPlaceImpl ();
			
			newWikidataPlace.setAltLabel(wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataPlace.getAltLabel_jsonProp()));

			newWikidataPlace.setCountry(wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataPlace.getCountry_jsonProp()).get(0).get(0));

			newWikidataPlace.setLatitude(wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataPlace.getLatitude_jsonProp()).get(0).get(0));

			newWikidataPlace.setLongitude(wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataPlace.getLongitude_jsonProp()).get(0).get(0));
		
			newWikidataPlace.setDepiction(wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataPlace.getDepiction_jsonProp()).get(0).get(0));
		
			newWikidataPlace.setDescription(wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataPlace.getDescription_jsonProp()));
			
			newWikidataPlace.setEntityId(wikidataURL);
			
			newWikidataPlace.setInternalType(type);
			
			newWikidataPlace.setModificationDate(wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataPlace.getModificationDate_jsonProp()).get(0).get(0));
		
			newWikidataPlace.setLogo(wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataPlace.getLogo_jsonProp()).get(0).get(0));
		
			newWikidataPlace.setPrefLabel(wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataPlace.getPrefLabel_jsonProp()));
			
			newWikidataPlace.setSameAs(wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataPlace.getSameAs_jsonProp()));

			store(solrCore, newWikidataPlace, true);
			
		}
	
		
		
	}
}
