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
			//converting the "jsonElement" to the appropriate object to be saved in Solr
			if(jsonElement!=null && !jsonElement.isEmpty()) 
			{
				Map<String,List<String>> altLabelMap = convertListOfListOfStringToMapOfStringAndListOfString(jsonElement);
				newWikidataAgent.setAltLabel(altLabelMap);
			}

			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataAgent.getCountry_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) newWikidataAgent.setCountry(jsonElement.get(0).get(0));

			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataAgent.getDateOfBirth_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) 
			{
				String [] dateBirthArray = new String [1];
				dateBirthArray[0]=jsonElement.get(0).get(0);
				newWikidataAgent.setDateOfBirth(dateBirthArray);
			}
			
			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataAgent.getDateOfDeath_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty())
			{
				String [] dateDeathArray = new String [jsonElement.size()];
				for(int i=0;i<jsonElement.size();i++)
				{
					dateDeathArray[i]=jsonElement.get(i).get(0);
				}				
				newWikidataAgent.setDateOfDeath(dateDeathArray);
			}
			
			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataAgent.getDepiction_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) newWikidataAgent.setDepiction(jsonElement.get(0).get(0));
			
			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataAgent.getDescription_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty())
			{
				Map<String,String> descriptionsMap = convertListOfListOfStringToMapOfStringAndString(jsonElement);
				newWikidataAgent.setDescription(descriptionsMap);
			}
			
			newWikidataAgent.setEntityId(wikidataURL);			
			
			newWikidataAgent.setInternalType(type);
			
			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataAgent.getModificationDate_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) newWikidataAgent.setModificationDate(jsonElement.get(0).get(0));
			
			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataAgent.getProfessionOrOccupation_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) 
			{
				String [] occupationArray = new String [jsonElement.size()];
				for(int i=0;i<jsonElement.size();i++)
				{
					occupationArray[i]=jsonElement.get(i).get(0);
				}				
				newWikidataAgent.setProfessionOrOccupation(occupationArray);
			}
			
			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataAgent.getPrefLabel_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty())
			{ 
				Map<String,String> prefLabelMap = convertListOfListOfStringToMapOfStringAndString(jsonElement);
				newWikidataAgent.setPrefLabelStringMap(prefLabelMap);
			}
			
			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataAgent.getSameAs_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty())	
			{
				String [] sameAsArray = new String [jsonElement.size()];
				for(int i=0;i<jsonElement.size();i++)
				{
					sameAsArray[i]=jsonElement.get(i).get(0);
				}				
				newWikidataAgent.setSameAs(sameAsArray);
			}
			
			store(solrCore, newWikidataAgent, true);
		}
		else if (type.compareToIgnoreCase("place")==0)
		{
			
			WikidataPlace newWikidataPlace = new WikidataPlaceImpl ();
			
			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataPlace.getAltLabel_jsonProp()); 
			if(jsonElement!=null && !jsonElement.isEmpty())
			{
				Map<String,List<String>> altLabelMap = convertListOfListOfStringToMapOfStringAndListOfString(jsonElement);
				newWikidataPlace.setAltLabel(altLabelMap);
			}

			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataPlace.getCountry_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) newWikidataPlace.setCountry(jsonElement.get(0).get(0));
			
			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataPlace.getLatitude_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) newWikidataPlace.setLatitude(Float.valueOf(jsonElement.get(0).get(0)));

			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataPlace.getLongitude_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) newWikidataPlace.setLongitude(Float.valueOf(jsonElement.get(0).get(0)));

			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataPlace.getDepiction_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) newWikidataPlace.setDepiction(jsonElement.get(0).get(0));
		
			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataPlace.getDescription_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) 
			{
				Map<String,String> descriptionsMap = convertListOfListOfStringToMapOfStringAndString(jsonElement);
				newWikidataPlace.setDescription(descriptionsMap);
			}
			
			newWikidataPlace.setEntityId(wikidataURL);
			
			newWikidataPlace.setInternalType(type);
			
			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataPlace.getModificationDate_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) newWikidataPlace.setModificationDate(jsonElement.get(0).get(0));
		
			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataPlace.getLogo_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) newWikidataPlace.setLogo(jsonElement.get(0).get(0));
		
			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataPlace.getPrefLabel_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty())
			{
				Map<String,String> prefLabelMap = convertListOfListOfStringToMapOfStringAndString(jsonElement);
				newWikidataPlace.setPrefLabelStringMap(prefLabelMap);
			}
			
			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataPlace.getSameAs_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) 
			{
				String [] sameAsArray = new String [jsonElement.size()];
				for(int i=0;i<jsonElement.size();i++)
				{
					sameAsArray[i]=jsonElement.get(i).get(0);
				}		
				newWikidataPlace.setSameAs(sameAsArray);
			}

			store(solrCore, newWikidataPlace, true);
			
		}	
		
	}
	private Map<String, List<String>> convertListOfListOfStringToMapOfStringAndListOfString (List<List<String>> jsonElement)
	{
		Map<String,List<String>> altLabelMap = new HashMap<String,List<String>>();
		for (List<String> altLabelElem : jsonElement)
		{
			if(altLabelMap.containsKey(altLabelElem.get(0)))
			{
				List<String> altLabelMapValue = altLabelMap.get(altLabelElem.get(0));
				altLabelMapValue.add(altLabelElem.get(1));
				altLabelMap.put(altLabelElem.get(0), altLabelMapValue);
			}
			else
			{
				List<String> newaltLabelMapValue = new ArrayList<String>();
				newaltLabelMapValue.add(altLabelElem.get(1));
				altLabelMap.put(altLabelElem.get(0), newaltLabelMapValue);
			}
			
		}
		return altLabelMap;
	}
	
	private Map<String, String> convertListOfListOfStringToMapOfStringAndString (List<List<String>> jsonElement)
	{
		Map<String,String> altLabelMap = new HashMap<String,String>();
		for (List<String> altLabelElem : jsonElement)
		{
			altLabelMap.put(altLabelElem.get(0), altLabelElem.get(1));
					
		}
		return altLabelMap;
	}
}
