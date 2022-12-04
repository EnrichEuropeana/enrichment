package eu.europeana.enrichment.solr.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.europeana.enrichment.common.commons.EnrichmentConfiguration;
import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.common.commons.HelperFunctions;
import eu.europeana.enrichment.common.serializer.JsonLdSerializer;
import eu.europeana.enrichment.model.WikidataAgent;
import eu.europeana.enrichment.model.WikidataEntity;
import eu.europeana.enrichment.model.WikidataPlace;
import eu.europeana.enrichment.model.impl.NamedEntitySolrCollection;
import eu.europeana.enrichment.model.impl.WikidataEntityImpl;
import eu.europeana.enrichment.model.vocabulary.EntityTypes;
import eu.europeana.enrichment.ner.linking.WikidataService;
import eu.europeana.enrichment.solr.exception.SolrServiceException;
import eu.europeana.enrichment.solr.model.SolrWikidataAgentImpl;
import eu.europeana.enrichment.solr.model.SolrWikidataPlaceImpl;
import eu.europeana.enrichment.solr.model.vocabulary.EntitySolrFields;
import eu.europeana.enrichment.solr.service.SolrWikidataEntityService;

@Service(EnrichmentConstants.BEAN_ENRICHMENT_SOLR_WIKIDATA_ENTITY_SERVICE)
public class SolrWikidataEntityServiceImpl extends SolrBaseClientServiceImpl implements SolrWikidataEntityService {
	
	@Autowired
	JsonLdSerializer jsonLdSerializer; 	
	
	//@Resource(name = "wikidataService")
	@Autowired
	WikidataService wikidataService;	
	
	@Autowired
	EnrichmentConfiguration enrichmentConfiguration;

	private String solrCore = "wikidata";
	
	private final Logger logger = LogManager.getLogger(getClass());
	
	@Override
	public int storeWikidataFromURL(String wikidataURL, String type) throws SolrServiceException, IOException {
		
		WikidataEntity entity = wikidataService.getWikidataEntityUsingLocalCache(wikidataURL, type);

		if(entity!=null)
		{
			if(EntityTypes.Agent.getEntityType().equalsIgnoreCase(type)) {
				store(solrCore, new SolrWikidataAgentImpl((WikidataAgent)entity), true);
				return 1;
			}
			else if(EntityTypes.Place.getEntityType().equalsIgnoreCase(type)) {
				store(solrCore, new SolrWikidataPlaceImpl((WikidataPlace)entity), true);
				return 1;
			}
			return 0;			
		}
		
		return 0;
	}
	
	public boolean existWikidataURL(String wikidataURL) throws SolrServiceException {
		SolrQuery query = new SolrQuery();
		query.set("q", EntitySolrFields.ID+ ":\"" + wikidataURL + "\"");
		
	    QueryResponse rsp = null;
		try {
			rsp = query(solrCore, query);
		} catch (SolrServiceException e) {
			logger.log(Level.ERROR, "Exception during the Solr quering for the wikidata.", e);
			throw e;
		}
	    SolrDocumentList docs = rsp.getResults();
	    if (docs.getNumFound()>0) return true;
	    else return false;
	}
	
	@Override
	public String searchByWikidataURL(String wikidataURL) throws SolrServiceException {
		
		logger.debug("Search wikidata entity by its URL: " + wikidataURL);

		/**
		 * Construct a SolrQuery
		 */
		SolrQuery query = new SolrQuery();
		
		query.set("q", EntitySolrFields.ID+ ":\"" + wikidataURL + "\"");
		
	    QueryResponse rsp = null;
		try {
			rsp = query(solrCore, query);
		} catch (SolrServiceException e) {
			logger.log(Level.ERROR, "Exception during the Solr quering for the wikidata.", e);
			throw e;
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
	
	@Override
	public WikidataEntity getWikidataEntity (String wikidataURL, String type) throws SolrServiceException
	{
		SolrQuery query = new SolrQuery();
		String solrQueryString = "("+EntitySolrFields.ID+ ":\"" + wikidataURL + "\"" + " AND " + EntitySolrFields.INTERNAL_TYPE+ ":" + type + ")"; 
		query.setQuery(solrQueryString);
		
		SolrDocumentList docList;
		DocumentObjectBinder binder;
		
	    QueryResponse rsp = null;
		try {
			rsp = query(solrCore, query);
		} catch (SolrServiceException e) {
			// TODO Auto-generated catch block
			logger.log(Level.ERROR, "Exception during getting the wikidata from Solr.", e);
			throw e;
		}	
		binder = new DocumentObjectBinder();
		docList = rsp.getResults();
				
		if(docList.size() > 1)
		{
			logger.error("There are !=1 Solr documents with the same wikidata URL! The number of documents is: " + String.valueOf(docList.size()));
			return null;
		}
		else if(docList.size() == 0)
		{
			logger.error("There are neither Solr nor Wikidata documents that can be fetched from the web with this wikidata URL! ");
			return null;
		}
		
		SolrDocument doc = docList.get(0);		

		if(type.equalsIgnoreCase("agent"))
		{
			SolrWikidataAgentImpl entity;
			Class<SolrWikidataAgentImpl> entityClass = null;
			entityClass = SolrWikidataAgentImpl.class;
			entity = (SolrWikidataAgentImpl) binder.getBean(entityClass, doc);
				    	
	    	return entity;
		}
		else if(type.equalsIgnoreCase("place")) 
		{
			SolrWikidataPlaceImpl entity;
			Class<SolrWikidataPlaceImpl> entityClass = null;
			entityClass = SolrWikidataPlaceImpl.class;
			entity = (SolrWikidataPlaceImpl) binder.getBean(entityClass, doc);
	    	
	    	return entity;
		}
		else
		{
			logger.error("The type of the Solr WikidataEntity is niether \"agent\" nor \"place\".");
			return null;
		}
		
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public String searchByWikidataURL_usingJackson(String wikidataURL, String type) throws SolrServiceException, IOException {
		
		SolrQuery query = new SolrQuery();
		
		//query.set("q", EntitySolrFields.ID+ ":\"" + wikidataURL + "\"");
		String solrQueryString = "("+EntitySolrFields.ID+ ":\"" + wikidataURL + "\"" + " AND " + EntitySolrFields.INTERNAL_TYPE+ ":" + type + ")"; 
		query.setQuery(solrQueryString);
		//query.set("q", solrQueryString);
		//query.set("q", EntitySolrFields.ID+ ":\"" + wikidataURL + "\"");
		//query.set("q", EntitySolrFields.INTERNAL_TYPE+ ":" + type);		
		
		SolrDocumentList docList;
		DocumentObjectBinder binder;
		
		//try to fetch the wikidata entity if there is not one in solr
		while(true)
		{
		    QueryResponse rsp = null;
			try {
				rsp = query(solrCore, query);
			} catch (SolrServiceException e) {
				// TODO Auto-generated catch block
				logger.log(Level.ERROR, "Exception during the Solr search with the wikidata url.", e);
				throw e;
			}
	
			//ResultSet<T> resultSet = new ResultSet<>();		
			binder = new DocumentObjectBinder();
			docList = rsp.getResults();
			
			if(docList.size()==0)
			{
				if(storeWikidataFromURL(wikidataURL,type)==0) break;
			}
			else
			{
				break;
			}
		}

		
		if(docList.size() > 1)
		{
			logger.error("There are !=1 Solr documents with the same wikidata URL! The number of documents is: " + String.valueOf(docList.size()));
			return null;
		}
		else if(docList.size() == 0)
		{
			logger.error("There are neither Solr nor Wikidata documents that can be fetched from the web with this wikidata URL! ");
			return null;
		}
		
		SolrDocument doc = docList.get(0);		
		//String type = (String) doc.get(EntitySolrFields.INTERNAL_TYPE);
		
		
		//entityClass = (Class<T>) EntityObjectFactory.getInstance().getClassForType(type);
		/*
		 * TODO: create a class of types as in the entity-api EntityTypes and check there for the 
		 * type of the class that needs to be serialized
		 */

		if(type.equalsIgnoreCase("agent"))
		{
			SolrWikidataAgentImpl entity;
			Class<SolrWikidataAgentImpl> entityClass = null;
			entityClass = SolrWikidataAgentImpl.class;
			entity = (SolrWikidataAgentImpl) binder.getBean(entityClass, doc);
						
	    	String serializedUserSetJsonLdStr=null;
	    	try {
				serializedUserSetJsonLdStr = jsonLdSerializer.serializeObject(entity);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.log(Level.ERROR, "Exception during the serializion of the wikidata agent from Solr.", e);
				throw e;
			}
	    	
	    	return serializedUserSetJsonLdStr;
		}
		else if(type.equalsIgnoreCase("place")) 
		{
			SolrWikidataPlaceImpl entity;
			Class<SolrWikidataPlaceImpl> entityClass = null;
			entityClass = SolrWikidataPlaceImpl.class;
			entity = (SolrWikidataPlaceImpl) binder.getBean(entityClass, doc);
					
	    	String serializedUserSetJsonLdStr=null;
	    	try {
				serializedUserSetJsonLdStr = jsonLdSerializer.serializeObject(entity);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.log(Level.ERROR, "Exception during the serializion of the wikidata place from Solr.", e);
				throw e;
			}
	    	
	    	return serializedUserSetJsonLdStr;
		}
		else
		{
			logger.error("The type of the Solr WikidataEntity is niether \"agent\" nor \"place\".");
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String searchNamedEntities_usingJackson(String queryText, String entityType, String lang, String solrQuery, String solrSortText, String pageSize, String page) throws SolrServiceException, IOException {
		
		//forming required properties for the class to be serialized
		String URLPage;
		String URLWithoutPage;
		int totalResultsPerPage;
		int totalResultsAll;
		List<WikidataEntity> items = new ArrayList<WikidataEntity>();

		
		//forming solr queries to get the data from Solr
		logger.debug("Forming Solr queries to get the data from Solr.");
		
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
			
//			URLPage = "http://dsi-demo.ait.ac.at/enrichment-web/entity/search?wskey=" + wskey + "&query=" + queryText + "&type=" + entityType + "&lang="+ lang;
//			URLWithoutPage = "http://dsi-demo.ait.ac.at/enrichment-web/entity/search?wskey=" + wskey + "&query=" + queryText + "&type=" + entityType + "&lang="+ lang;
			URLPage = enrichmentConfiguration.getSolrWikidataBaseUrl() + "?query=" + queryText + "&type=" + entityType + "&lang="+ lang;
			URLWithoutPage = enrichmentConfiguration.getSolrWikidataBaseUrl() + "?query=" + queryText + "&type=" + entityType + "&lang="+ lang;
			
		}
		else
		{
			queryOnePage.set("q", EntitySolrFields.LABEL+ ":" + queryText);
			queryAllPages.set("q", EntitySolrFields.LABEL+ ":" + queryText);
			
			URLPage = enrichmentConfiguration.getSolrWikidataBaseUrl() + "?query=" + queryText + "&type=agent,place" + "&lang="+ lang;
			URLWithoutPage = enrichmentConfiguration.getSolrWikidataBaseUrl() + "?query=" + queryText + "&type=agent,place" + "&lang="+ lang;
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
	
		
		logger.debug("Calling Solr for executing queries.");
		
	    QueryResponse rspOnePage = null;
	    QueryResponse rspAllPages = null;
		try {
			rspOnePage = query(solrCore, queryOnePage);
			rspAllPages = query(solrCore, queryAllPages);
		} catch (SolrServiceException e) {
			// TODO Auto-generated catch block
			logger.log(Level.ERROR, "Exception during the search for the NamedEntity from Solr.", e);
			throw e;
		}

		logger.debug("Getting results from Solr in the form of SolrDocumentList.");
		
		//ResultSet<T> resultSet = new ResultSet<>();		
		DocumentObjectBinder binder = new DocumentObjectBinder();
		SolrDocumentList docListOnePage = rspOnePage.getResults();
		SolrDocumentList docListAllPages = rspAllPages.getResults();
			
		URLPage+="&page="+ page +"&pageSize=" + pageSize;
		totalResultsAll = docListAllPages.size();
		totalResultsPerPage = (totalResultsAll < Integer.valueOf(pageSize)) ? totalResultsAll : Integer.valueOf(pageSize);
		
		logger.debug("Analysing Solr data for NamedEntity types.");
		
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
			
			if(internalType.equalsIgnoreCase("agent"))
			{
				SolrWikidataAgentImpl entity;
				Class<SolrWikidataAgentImpl> entityClass = null;
				entityClass = SolrWikidataAgentImpl.class;
				entity = (SolrWikidataAgentImpl) binder.getBean(entityClass, doc);
				items.add(entity);
				wikidataEntity=entity;
								    	
			}
			else if(internalType.equalsIgnoreCase("place")) 
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
				logger.error("Solr document retrived is niether of type \"agent\" nor \"place\".");
				return null;
			}
			
			//adjust for languages, i.e. remove the fields for other not required languages
			if(wikidataEntity.getPrefLabel()!=null) HelperFunctions.removeDataForLanguages(wikidataEntity.getPrefLabel(),EnrichmentConstants.PREF_LABEL_DENORMALIZED, lang);
			if(wikidataEntity.getAltLabel()!=null) HelperFunctions.removeDataForLanguages(wikidataEntity.getAltLabel(),EnrichmentConstants.ALT_LABEL_DENORMALIZED,lang);
			if(wikidataEntity.getDescription()!=null) HelperFunctions.removeDataForLanguages(wikidataEntity.getDescription(),EnrichmentConstants.DC_DESCRIPTION_DENORMALIZED,lang);
		}
		
		logger.debug("Serializing Solr data using Jackson to JSON string.");
			
		
		NamedEntitySolrCollection neColl = new NamedEntitySolrCollection(items, URLPage, URLWithoutPage, totalResultsPerPage, totalResultsAll);
		
		String serializedNamedEntityCollection=null;
    	try {
    		serializedNamedEntityCollection = jsonLdSerializer.serializeObject(neColl);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.log(Level.ERROR, "Exception during the serializion of the NamedEntity from Solr.", e);
			throw e;
		}
    	
    	return serializedNamedEntityCollection;
	}
	
}
