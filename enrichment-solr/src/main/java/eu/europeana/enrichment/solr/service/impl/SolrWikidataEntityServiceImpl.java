package eu.europeana.enrichment.solr.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import eu.europeana.api.commons.definitions.search.ResultSet;
import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.WikidataAgent;
import eu.europeana.enrichment.model.WikidataEntity;
import eu.europeana.enrichment.model.WikidataPlace;
import eu.europeana.enrichment.model.impl.WikidataAgentImpl;
import eu.europeana.enrichment.model.impl.WikidataEntityImpl;
import eu.europeana.enrichment.model.impl.WikidataPlaceImpl;
import eu.europeana.enrichment.ner.linking.WikidataService;
import eu.europeana.enrichment.solr.commons.WikidataEntitySerializer;
import eu.europeana.enrichment.solr.exception.SolrNamedEntityServiceException;
import eu.europeana.enrichment.solr.model.SolrStoryEntityImpl;
import eu.europeana.enrichment.solr.model.SolrWikidataAgentImpl;
import eu.europeana.enrichment.solr.model.SolrWikidataPlaceImpl;
import eu.europeana.enrichment.solr.model.vocabulary.EntitySolrFields;
import eu.europeana.enrichment.solr.service.SolrBaseClientService;
import eu.europeana.enrichment.solr.service.SolrWikidataEntityService;

import eu.europeana.entity.definitions.model.Entity;
import eu.europeana.entity.definitions.model.vocabulary.ConceptSolrFields;

import riotcmd.json;

public class SolrWikidataEntityServiceImpl implements SolrWikidataEntityService {

	@Resource(name = "solrBaseClientService")
	SolrBaseClientService solrBaseClientService;
	
	@Resource(name = "wikidataEntitySerializer")
	WikidataEntitySerializer wikidataEntitySerializer;
	
	
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
			
			solrBaseClientService.storeWikidataEntity(solrCollection, solrWikidataAgent, doCommit);
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
			
			solrBaseClientService.storeWikidataEntity(solrCollection, solrWikidataPlace, doCommit);
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
			Map<String,List<String>> altLabelMap = null;
			if(jsonElement!=null && !jsonElement.isEmpty()) 
			{
				altLabelMap = convertListOfListOfStringToMapOfStringAndListOfString(jsonElement);
			}
			newWikidataAgent.setAltLabel(altLabelMap);

			String country = null;
			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataAgent.getCountry_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty())
			{
				country="http://www.wikidata.org/entity/" + jsonElement.get(0).get(0);
			}
			newWikidataAgent.setCountry(country);

			String [] dateBirthArray = new String [1];
			dateBirthArray[0] = null;
			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataAgent.getDateOfBirth_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) 
			{				
				dateBirthArray[0]=jsonElement.get(0).get(0);				
			}
			newWikidataAgent.setDateOfBirth(dateBirthArray);
			
			String [] dateDeathArray = null;
			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataAgent.getDateOfDeath_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty())
			{
				dateDeathArray = new String [jsonElement.size()];
				for(int i=0;i<jsonElement.size();i++)
				{
					dateDeathArray[i]=jsonElement.get(i).get(0);
				}				
			}
			newWikidataAgent.setDateOfDeath(dateDeathArray);
			
			String depiction = null;
			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataAgent.getDepiction_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty())
			{
				depiction = jsonElement.get(0).get(0);
			}
			newWikidataAgent.setDepiction(depiction);
			
			Map<String,List<String>> descriptionsMap = null;
			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataAgent.getDescription_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty())
			{
				descriptionsMap = convertListOfListOfStringToMapOfStringAndListOfString(jsonElement);
			}
			newWikidataAgent.setDescription(descriptionsMap);
			
			newWikidataAgent.setEntityId(wikidataURL);			
			
			newWikidataAgent.setInternalType(type);
			
			
			String modificationDate = null;
			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataAgent.getModificationDate_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) 
			{
				modificationDate = jsonElement.get(0).get(0);
			}
			newWikidataAgent.setModificationDate(modificationDate);
			
			
			String [] occupationArray = null;
			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataAgent.getProfessionOrOccupation_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) 
			{
				occupationArray = new String [jsonElement.size()];
				for(int i=0;i<jsonElement.size();i++)
				{
					occupationArray[i]="http://www.wikidata.org/entity/" + jsonElement.get(i).get(0);
				}				
			}
			newWikidataAgent.setProfessionOrOccupation(occupationArray);
			
			
			Map<String,List<String>> prefLabelMap = null;
			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataAgent.getPrefLabel_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty())
			{ 
				prefLabelMap = convertListOfListOfStringToMapOfStringAndListOfString(jsonElement);
			}
			newWikidataAgent.setPrefLabel(prefLabelMap);
			
			
			String [] sameAsArray=null;
			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataAgent.getSameAs_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty())	
			{
				sameAsArray = new String [jsonElement.size()];
				for(int i=0;i<jsonElement.size();i++)
				{
					sameAsArray[i]=jsonElement.get(i).get(0);
				}				
			}
			newWikidataAgent.setSameAs(sameAsArray);
			
			store(solrCore, newWikidataAgent, true);
		}
		else if (type.compareToIgnoreCase("place")==0)
		{
			
			WikidataPlace newWikidataPlace = new WikidataPlaceImpl ();
			
			Map<String,List<String>> altLabelMap = null;
			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataPlace.getAltLabel_jsonProp()); 
			if(jsonElement!=null && !jsonElement.isEmpty())
			{
				altLabelMap = convertListOfListOfStringToMapOfStringAndListOfString(jsonElement);
				
			}
			newWikidataPlace.setAltLabel(altLabelMap);
			
			String country = null;
			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataPlace.getCountry_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) 
			{
				country = "http://www.wikidata.org/entity/" + jsonElement.get(0).get(0);
			}
			newWikidataPlace.setCountry(country);
			
			Float latitude = null;
			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataPlace.getLatitude_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) 
			{
				latitude = Float.valueOf(jsonElement.get(0).get(0));
			}
			newWikidataPlace.setLatitude(latitude);

			
			Float longitude = null;
			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataPlace.getLongitude_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) 
			{
				longitude = Float.valueOf(jsonElement.get(0).get(0));
			}
			newWikidataPlace.setLongitude(longitude);

			String depiction = null;
			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataPlace.getDepiction_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) 
			{
				depiction = jsonElement.get(0).get(0);
			}
			newWikidataPlace.setDepiction(depiction);
		
			Map<String,List<String>> descriptionsMap = null;
			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataPlace.getDescription_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) 
			{
				descriptionsMap = convertListOfListOfStringToMapOfStringAndListOfString(jsonElement);
			}
			newWikidataPlace.setDescription(descriptionsMap);
			
			newWikidataPlace.setEntityId(wikidataURL);
			
			newWikidataPlace.setInternalType(type);
			
			String modificationDate = null;
			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataPlace.getModificationDate_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) 
			{
				modificationDate = jsonElement.get(0).get(0);
			}
			newWikidataPlace.setModificationDate(modificationDate);
		
			
			String logo = null;
			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataPlace.getLogo_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) 
			{
				logo = jsonElement.get(0).get(0);
			}
			newWikidataPlace.setLogo(logo);
		
			Map<String,List<String>> prefLabelMap = null;
			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataPlace.getPrefLabel_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty())
			{
				prefLabelMap = convertListOfListOfStringToMapOfStringAndListOfString(jsonElement);
			}
			newWikidataPlace.setPrefLabel(prefLabelMap);
			
			String [] sameAsArray = null;
			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataPlace.getSameAs_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) 
			{
				sameAsArray = new String [jsonElement.size()];
				for(int i=0;i<jsonElement.size();i++)
				{
					sameAsArray[i]=jsonElement.get(i).get(0);
				}		
			}
			newWikidataPlace.setSameAs(sameAsArray);

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

	@Override
	public String searchByWikidataURL(String wikidataURL) {
		
		log.debug("Search wikidata entity by its URL: " + wikidataURL);

		/**
		 * Construct a SolrQuery
		 */
		SolrQuery query = new SolrQuery();
		
		query.set("q", EntitySolrFields.ID+ ":\"" + wikidataURL + "\"");
		
	    QueryResponse rsp = null;
		try {
			rsp = solrBaseClientService.query(solrCore, query);
		} catch (SolrNamedEntityServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    SolrDocumentList docs = rsp.getResults();

	    if (docs.getNumFound() != 1) return null;
	    else
	    {
	    	Map<String,Collection<Object>> map = docs.get(0).getFieldValuesMap();
	    	
	    	String result = "{";
	    	
	    	//please note that all map operations are not supported on this solr returned "map" object
	    	Iterator<String> entries = map.keySet().iterator();
	    	
	    	while (entries.hasNext()) {
	    		
	    		String mapKey = entries.next();
    		
    			Collection<Object> valueCollection = map.get(mapKey);
    			
    			if(valueCollection != null && !valueCollection.isEmpty())
    			{
    				
    				if(entries.hasNext())
    				{
    	    			result += "\""+mapKey.toString()+"\"";
    	    			result += ":";
    	    			result += "[";    	    			

    	    			Iterator<Object> objIterator = valueCollection.iterator();
    	    			
    					while (objIterator.hasNext()) {
    						
    						Object nextObj = objIterator.next();
    						
    						if(objIterator.hasNext())
    						{
    							result += "\""+nextObj.toString()+"\"";
    							result += ",";
    						}
    						else
    						{
    							result += "\""+nextObj.toString()+"\"";
    						}
    					}
    					result += "]";
    					
    					result += ",";
    				}
    				else
    				{
    	    			result += "\""+mapKey.toString()+"\"";
    	    			result += ":";
    	    			result += "[";    	    			

    	    			Iterator<Object> objIterator = valueCollection.iterator();
    	    			
    					while (objIterator.hasNext()) {
    						
    						Object nextObj = objIterator.next();
    						
    						if(objIterator.hasNext())
    						{
    							result += "\""+nextObj.toString()+"\"";
    							result += ",";
    						}
    						else
    						{
    							result += "\""+nextObj.toString()+"\"";
    						}
    					}
    					
    					result += "]";
   
    				}
    			}
    			else
    			{
    				if(entries.hasNext())
    				{
    					result += "[],";
    				}
    				else
    				{
    					result += "[]";
    				}
    			}
	    		
	    	}
	    	
	    	result += "}";

	    	return result;
	    }
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public String searchByWikidataURL_usingJackson(String wikidataURL) {
		
		SolrQuery query = new SolrQuery();
		
		query.set("q", EntitySolrFields.ID+ ":\"" + wikidataURL + "\"");
		
	    QueryResponse rsp = null;
		try {
			rsp = solrBaseClientService.query(solrCore, query);
		} catch (SolrNamedEntityServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//ResultSet<T> resultSet = new ResultSet<>();		
		DocumentObjectBinder binder = new DocumentObjectBinder();
		SolrDocumentList docList = rsp.getResults();
		String type;
		
		
		if(docList.size() != 1)
		{
			log.error("There are !=1 Solr documents with the same wikidata URL! The number of documents is: " + String.valueOf(docList.size()));
			return null;
		}
		
		SolrDocument doc = docList.get(0);		
		type = (String) doc.get(EntitySolrFields.INTERNAL_TYPE);
		//entityClass = (Class<T>) EntityObjectFactory.getInstance().getClassForType(type);
		/*
		 * TODO: create a class of types as in the entity-api EntityTypes and check there for the 
		 * type of the class that needs to be serialized
		 */

		if(type.compareToIgnoreCase("agent")==0)
		{
			SolrWikidataAgentImpl entity;
			Class<SolrWikidataAgentImpl> entityClass = null;
			entityClass = SolrWikidataAgentImpl.class;
			entity = (SolrWikidataAgentImpl) binder.getBean(entityClass, doc);
						
	    	String serializedUserSetJsonLdStr=null;
	    	try {
				serializedUserSetJsonLdStr = wikidataEntitySerializer.serialize(entity);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	    	
	    	return serializedUserSetJsonLdStr;
		}
		else if(type.compareToIgnoreCase("place")==0) 
		{
			SolrWikidataPlaceImpl entity;
			Class<SolrWikidataPlaceImpl> entityClass = null;
			entityClass = SolrWikidataPlaceImpl.class;
			entity = (SolrWikidataPlaceImpl) binder.getBean(entityClass, doc);
					
	    	String serializedUserSetJsonLdStr=null;
	    	try {
				serializedUserSetJsonLdStr = wikidataEntitySerializer.serialize(entity);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	    	
	    	return serializedUserSetJsonLdStr;
		}
		else
		{
			log.error("The type of the Solr WikidataEntity is niether \"agent\" nor \"place\".");
			return null;
		}
	}
}
