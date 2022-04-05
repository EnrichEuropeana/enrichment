package eu.europeana.enrichment.solr.service.impl;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.europeana.enrichment.common.commons.AppConfigConstants;
import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.Topic;
import eu.europeana.enrichment.model.WikidataEntity;
import eu.europeana.enrichment.solr.exception.SolrNamedEntityServiceException;
import eu.europeana.enrichment.solr.service.SolrBaseClientService;

@Service(AppConfigConstants.BEAN_ENRICHMENT_SOLR_BASE_CLIENT_SERVICE)
public class SolrBaseClientServiceImpl implements SolrBaseClientService {

	@Autowired
	SolrClient solrServer;
	
	private final Logger log = LogManager.getLogger(getClass());

	//private Jyandex clientJyandex;

	@Override
	public List<Integer> searchByEntityName(String solrCollection, String entityName) throws SolrNamedEntityServiceException {
		return null;
	}
	

	@Override
	public boolean store(String solrCollection, Object solrObject) throws SolrNamedEntityServiceException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void storeStoryEntity(String solrCollection, StoryEntity solrObject, boolean doCommit) throws SolrNamedEntityServiceException {
		try {
			
			log.debug("store: " + solrObject.toString());
				
			UpdateResponse rsp = solrServer.addBean(solrCollection, solrObject);
			log.debug("store response: " + rsp.toString());
			if(doCommit)
				solrServer.commit(solrCollection);
		} catch (SolrServerException ex) {
			throw new SolrNamedEntityServiceException(
					"Unexpected Solr server exception occured when storing StoryEntity: " + solrObject.toString(),
					ex);
		} catch (IOException ex) {
			throw new SolrNamedEntityServiceException(
					"Unexpected IO exception occured when storing StoryEntity: " + solrObject.toString() + "in Solr.", ex);
		}
		
	}
	
	@Override
	public void storeWikidataEntity(String solrCollection, WikidataEntity solrObject, boolean doCommit) throws SolrNamedEntityServiceException {
		try {
			
			log.debug("store: " + solrObject.toString());
				
			UpdateResponse rsp = solrServer.addBean(solrCollection, solrObject);
//			if(solrObject instanceof SolrWikidataAgentImpl) 
//			{
//				rsp = solrServer.addBean(solrCollection, (SolrWikidataAgentImpl)solrObject);
//			}
//			else
//			{
//				rsp = solrServer.addBean(solrCollection, (SolrWikidataPlaceImpl)solrObject);
//			}
			
			log.debug("store response: " + rsp.toString());
			if(doCommit)
				solrServer.commit(solrCollection);
		} catch (SolrServerException ex) {
			throw new SolrNamedEntityServiceException(
					"Unexpected Solr server exception occured when storing WikidataEntity: " + solrObject.toString(),
					ex);
		} catch (IOException ex) {
			throw new SolrNamedEntityServiceException(
					"Unexpected IO exception occured when storing WikidataEntity: " + solrObject.toString() + "in Solr.", ex);
		}
		
	}

	
	@Override
	public void search (String solrCollection, String term) throws SolrNamedEntityServiceException {

		log.debug("search StoryEntity by term: " + term);

		/**
		 * Construct a SolrQuery
		 */
		SolrQuery query = new SolrQuery(term);
		log.debug("query: " + query.toString());

		/**
		 * Query the server
		 */
		try {
			QueryResponse rsp = solrServer.query(solrCollection, query);
			log.debug("query response: " + rsp.toString());
			
		} catch (IOException | SolrServerException e) {
			throw new SolrNamedEntityServiceException("Unexpected exception occured when searching StoryEntity in Solr for the term: " + term,
					e);
		}

	}

	@Override
	public void update(String solrCollection, StoryEntity stryEntity) throws SolrNamedEntityServiceException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteByQuery(String solrCollection, String query) throws SolrNamedEntityServiceException {
		try {
			log.debug("Solr deleteByQuery call: " + query);
			UpdateResponse rsp = solrServer.deleteByQuery(solrCollection, query);
			log.debug("Solr deleteByQuery response: " + rsp.toString());
			solrServer.commit(solrCollection);
		} catch (IOException | SolrServerException ex) {
			throw new SolrNamedEntityServiceException(
					"Unexpected solr server or IO exception occured when deleting StoryEntity with query: " + query, ex);
		}		
	}

	@Override
	public QueryResponse query(String solrCollection, SolrQuery query) throws SolrNamedEntityServiceException {
		
		/**
		 * Query the server
		 */
		QueryResponse rsp = null;
		try {
			rsp = solrServer.query(solrCollection, query);
			log.debug("query response: " + rsp.toString());
			
		} catch (IOException | SolrServerException e) {
			throw new SolrNamedEntityServiceException("Unexpected exception occured when sending a query to the Solr server: " + query.toString(),
					e);
		}
		return rsp;
		
	}


	@Override
	public void storeTopic(String solrCollection, Topic solrObject, boolean doCommit)
			throws SolrNamedEntityServiceException {
		try {
			
			log.debug("store: " + solrObject.toString());
				
			UpdateResponse rsp = solrServer.addBean(solrCollection, solrObject);
			log.info("store response: " + rsp.toString());
			if(doCommit)
				solrServer.commit(solrCollection);
		} catch (SolrServerException ex) {
			throw new SolrNamedEntityServiceException(
					"Unexpected Solr server exception occured when storing Topic: " + solrObject.toString(),
					ex);
		} catch (IOException ex) {
			throw new SolrNamedEntityServiceException(
					"Unexpected IO exception occured when storing Topic: " + solrObject.toString() + "in Solr.", ex);
		}
		
	}


	@Override
	public void updateTopic(String solrCore, Topic solrObject, boolean doCommit) throws SolrNamedEntityServiceException {
		log.debug("update: " + solrObject.toString());
		deleteTopic(solrCore, solrObject);
		storeTopic(solrCore, solrObject, doCommit);
	}


	@Override
	public void deleteTopic(String solrCore, Topic solrObject) throws SolrNamedEntityServiceException {
		log.debug("delete: " + solrObject.toString());
		try {
			solrServer.deleteById(solrCore,solrObject.getIdentifier());
		} catch (SolrServerException | IOException e) {
			throw new SolrNamedEntityServiceException(
					"Unexpected Solr server exception occured when deleting Topic: " + solrObject.toString(),
					e);
		}
		
	}

}
