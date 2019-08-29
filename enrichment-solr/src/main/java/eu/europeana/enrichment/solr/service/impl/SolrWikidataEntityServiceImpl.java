package eu.europeana.enrichment.solr.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import eu.europeana.enrichment.common.commons.HelperFunctions;
import eu.europeana.enrichment.model.WikidataAgent;
import eu.europeana.enrichment.model.WikidataEntity;
import eu.europeana.enrichment.model.WikidataPlace;
import eu.europeana.enrichment.model.impl.NamedEntitySolrCollection;
import eu.europeana.enrichment.model.impl.WikidataEntityImpl;
import eu.europeana.enrichment.model.vocabulary.WikidataEntitySolrDenormalizationFields;
import eu.europeana.enrichment.ner.linking.WikidataService;
import eu.europeana.enrichment.solr.commons.JacksonSerializer;
import eu.europeana.enrichment.solr.exception.SolrNamedEntityServiceException;
import eu.europeana.enrichment.solr.model.SolrWikidataAgentImpl;
import eu.europeana.enrichment.solr.model.SolrWikidataPlaceImpl;
import eu.europeana.enrichment.solr.model.vocabulary.EntitySolrFields;
import eu.europeana.enrichment.solr.service.SolrBaseClientService;
import eu.europeana.enrichment.solr.service.SolrWikidataEntityService;

public class SolrWikidataEntityServiceImpl implements SolrWikidataEntityService {

	@Resource(name = "solrBaseClientService")
	SolrBaseClientService solrBaseClientService;
	
	@Resource(name = "jacksonSerializer")
	JacksonSerializer jacksonSerializer;
	
	
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
	public void storeWikidataFromURL(String wikidataURL, String type) throws SolrNamedEntityServiceException, IOException {
		
		WikidataEntity entity = wikidataService.getWikidataEntity(wikidataURL, type);

		store(solrCore, entity, true);
		
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
				serializedUserSetJsonLdStr = jacksonSerializer.serializeWikidataEntity(entity);
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
				serializedUserSetJsonLdStr = jacksonSerializer.serializeWikidataEntity(entity);
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
	
	@SuppressWarnings("unchecked")
	@Override
	public String searchNamedEntities_usingJackson(String wskey, String queryText, String entityType, String lang, String solrQuery, String solrSortText, String pageSize, String page) {
		
		//forming required properties for the class to be serialized
		String URLPage = "";
		String URLWithoutPage = "";
		int totalResultsPerPage;
		int totalResultsAll;
		List<WikidataEntity> items = new ArrayList<WikidataEntity>();

		
		//forming solr queries to get the data from Solr
		log.info("Forming Solr queries to get the data from Solr.");
		
		SolrQuery queryOnePage = new SolrQuery();
		SolrQuery queryAllPages = new SolrQuery();	
		
		if(lang==null || lang.isEmpty())
		{
			lang="en";
		}
		
		if(entityType!=null && !entityType.isEmpty())
		{
			String[] listTypes = entityType.split("\\s*,\\s*");
			String typeQueryText = "(";
			for(int i=0;i<listTypes.length;i++)
			{
				typeQueryText+=listTypes[i];
				if(i!=listTypes.length-1)
				{
					typeQueryText+="OR";
				}
			}
			typeQueryText+=")";
			
			queryOnePage.set("q", EntitySolrFields.LABEL+ ":" + queryText + " AND " + EntitySolrFields.INTERNAL_TYPE + ":" + typeQueryText);
			queryAllPages.set("q", EntitySolrFields.LABEL+ ":" + queryText + " AND " + EntitySolrFields.INTERNAL_TYPE + ":" + typeQueryText);
			
			URLPage += "http://dsi-demo.ait.ac.at/enrichment-web/entity/search?wskey=" + wskey + "&query=" + queryText + "&type=" + entityType + "&lang="+ lang;
			URLWithoutPage += "http://dsi-demo.ait.ac.at/enrichment-web/entity/search?wskey=" + wskey + "&query=" + queryText + "&type=" + entityType + "&lang="+ lang;
			
		}
		else
		{
			queryOnePage.set("q", EntitySolrFields.LABEL+ ":" + queryText);
			queryAllPages.set("q", EntitySolrFields.LABEL+ ":" + queryText);
			
			URLPage += "http://dsi-demo.ait.ac.at/enrichment-web/entity/search?wskey=" + wskey + "&query=" + queryText + "&type=agent,place" + "&lang="+ lang;
			URLWithoutPage += "http://dsi-demo.ait.ac.at/enrichment-web/entity/search?wskey=" + wskey + "&query=" + queryText + "&type=agent,place" + "&lang="+ lang;
		}
		
		if(solrSortText!=null && !solrSortText.isEmpty())
		{
			queryOnePage.set("sort", solrSortText);
		}
		
		if(solrQuery!=null && !solrQuery.isEmpty())
		{
			queryOnePage.set("fq", solrQuery);
			queryAllPages.set("fq", solrQuery);
		}
		
		if(pageSize==null || pageSize.isEmpty())
		{	
			pageSize="5";
		}
		
		if(page==null || page.isEmpty())
		{	
			page="0";
		}
		
		
		queryOnePage.set("start", Integer.valueOf(pageSize)*Integer.valueOf(page));
		
		queryOnePage.set("rows", Integer.valueOf(pageSize));
	
		
		log.info("Calling Solr for executing queries.");
		
	    QueryResponse rspOnePage = null;
	    QueryResponse rspAllPages = null;
		try {
			rspOnePage = solrBaseClientService.query(solrCore, queryOnePage);
			rspAllPages = solrBaseClientService.query(solrCore, queryAllPages);
		} catch (SolrNamedEntityServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		log.info("Getting results from Solr in the form of SolrDocumentList.");
		
		//ResultSet<T> resultSet = new ResultSet<>();		
		DocumentObjectBinder binder = new DocumentObjectBinder();
		SolrDocumentList docListOnePage = rspOnePage.getResults();
		SolrDocumentList docListAllPages = rspAllPages.getResults();
			
		URLPage+="&page="+ page +"&pageSize=" + pageSize;
		totalResultsAll = docListAllPages.size();
		totalResultsPerPage = (totalResultsAll < Integer.valueOf(pageSize)) ? totalResultsAll : Integer.valueOf(pageSize);
		
		log.info("Analysing Solr data for NamedEntity types.");
		
		for(int i=0;i<docListOnePage.size();i++)
		{
			SolrDocument doc = docListOnePage.get(i);		
			String internalType = (String) doc.get(EntitySolrFields.INTERNAL_TYPE);
			//entityClass = (Class<T>) EntityObjectFactory.getInstance().getClassForType(type);
			/*
			 * TODO: create a class of types as in the entity-api EntityTypes and check there for the 
			 * type of the class that needs to be serialized
			 */

			WikidataEntityImpl wikidataEntity=null;
			
			if(internalType.compareToIgnoreCase("agent")==0)
			{
				SolrWikidataAgentImpl entity;
				Class<SolrWikidataAgentImpl> entityClass = null;
				entityClass = SolrWikidataAgentImpl.class;
				entity = (SolrWikidataAgentImpl) binder.getBean(entityClass, doc);
				items.add(entity);
				wikidataEntity=entity;
								    	
			}
			else if(internalType.compareToIgnoreCase("place")==0) 
			{
				SolrWikidataPlaceImpl entity;
				Class<SolrWikidataPlaceImpl> entityClass = null;
				entityClass = SolrWikidataPlaceImpl.class;
				entity = (SolrWikidataPlaceImpl) binder.getBean(entityClass, doc);
				items.add(entity);
				wikidataEntity=entity;
			}
			else
			{
				log.error("Solr document retrived is niether of type \"agent\" nor \"place\".");
				return null;
			}
			
			//adjust for languages, i.e. remove the fields for other not required languages
			HelperFunctions.removeDataForLanguages(wikidataEntity.getPrefLabel(),WikidataEntitySolrDenormalizationFields.PREF_LABEL_DENORMALIZED, lang);
			HelperFunctions.removeDataForLanguages(wikidataEntity.getAltLabel(),WikidataEntitySolrDenormalizationFields.ALT_LABEL_DENORMALIZED,lang);
			HelperFunctions.removeDataForLanguages(wikidataEntity.getDescription(),WikidataEntitySolrDenormalizationFields.DC_DESCRIPTION_DENORMALIZED,lang);
		}
		
		log.info("Serializing Solr data using Jackson to JSON string.");
		
		NamedEntitySolrCollection neColl = new NamedEntitySolrCollection(items, URLPage, URLWithoutPage, totalResultsPerPage, totalResultsAll);
		
		String serializedNamedEntityCollection=null;
    	try {
    		serializedNamedEntityCollection = jacksonSerializer.serializeNamedEntitySolrCollection(neColl);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	
    	return serializedNamedEntityCollection;
	}
	
}
