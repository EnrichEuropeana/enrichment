package eu.europeana.enrichment.solr.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;

import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.WikidataEntity;
import eu.europeana.enrichment.solr.commons.GoogleTranslator;
import eu.europeana.enrichment.solr.commons.JavaJSONParser;
import eu.europeana.enrichment.solr.commons.LevenschteinDistance;
import eu.europeana.enrichment.solr.exception.SolrNamedEntityServiceException;
import eu.europeana.enrichment.solr.model.SolrStoryEntityImpl;
import eu.europeana.enrichment.solr.model.SolrWikidataAgentImpl;
import eu.europeana.enrichment.solr.model.SolrWikidataPlaceImpl;
import eu.europeana.enrichment.solr.service.SolrBaseClientService;
import eu.europeana.enrichment.translation.service.TranslationService;

public class SolrBaseClientServiceImpl implements SolrBaseClientService {

	@Resource
	SolrClient solrServer;
	
	private final Logger log = LogManager.getLogger(getClass());

	//private Jyandex clientJyandex;

	public void setSolrServer(SolrClient solrServer) {
		this.solrServer = solrServer;
	}

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
			log.info("store response: " + rsp.toString());
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
			
			log.info("store response: " + rsp.toString());
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

		log.info("search StoryEntity by term: " + term);

		/**
		 * Construct a SolrQuery
		 */
		SolrQuery query = new SolrQuery(term);
		log.info("query: " + query.toString());

		/**
		 * Query the server
		 */
		try {
			QueryResponse rsp = solrServer.query(solrCollection, query);
			log.info("query response: " + rsp.toString());
			
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
			log.info("Solr deleteByQuery call: " + query);
			UpdateResponse rsp = solrServer.deleteByQuery(solrCollection, query);
			log.info("Solr deleteByQuery response: " + rsp.toString());
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
			log.info("query response: " + rsp.toString());
			
		} catch (IOException | SolrServerException e) {
			throw new SolrNamedEntityServiceException("Unexpected exception occured when sending a query to the Solr server: " + query.toString(),
					e);
		}
		return rsp;
		
	}

}
