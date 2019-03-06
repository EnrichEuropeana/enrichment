package eu.europeana.enrichment.solr.service.impl;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;


import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.solr.exception.SolrNamedEntityServiceException;
import eu.europeana.enrichment.solr.model.SolrItemEntityImpl;
import eu.europeana.enrichment.solr.service.SolrEntityPositionsService;


public class SolrEntityPositionsServiceImpl implements SolrEntityPositionsService{

	@Resource
	SolrClient solrServer;
	
	private final Logger log = LogManager.getLogger(getClass());

	public void setSolrServer(SolrClient solrServer) {
		this.solrServer = solrServer;
	}

	@Override
	public List<Integer> searchByEntityName(String entityName) throws SolrNamedEntityServiceException {


		return null;
	}
	

	@Override
	public boolean store(ItemEntity ItemEntity) throws SolrNamedEntityServiceException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void store(List<? extends ItemEntity> storyItemEntities) throws SolrNamedEntityServiceException {
		
		for(ItemEntity ent : storyItemEntities) {
			store(ent, false);
		}
		
		try {
			solrServer.commit();
		} catch (SolrServerException ex) {
			throw new SolrNamedEntityServiceException(
					"Unexpected Solr server exception occured when storing a list of ItemEntity.", ex);			
		} catch (IOException ex) {
			throw new SolrNamedEntityServiceException(
					"Unexpected IO exception occured when storing a list of storyItemEntit", ex);
		}
		
	}

	@Override
	public void store(ItemEntity ItemEntity, boolean doCommit) throws SolrNamedEntityServiceException {
		try {
			
			log.debug("store: " + ItemEntity.toString());
			
			SolrItemEntityImpl solrStoryItem = null;
			if(ItemEntity instanceof SolrItemEntityImpl) {
				solrStoryItem=(SolrItemEntityImpl) ItemEntity;
			}
			else {
				solrStoryItem=new SolrItemEntityImpl(ItemEntity);
			}
			
			UpdateResponse rsp = solrServer.addBean(solrStoryItem);
			log.info("store response: " + rsp.toString());
			if(doCommit)
				solrServer.commit();
		} catch (SolrServerException ex) {
			throw new SolrNamedEntityServiceException(
					"Unexpected Solr server exception occured when storing ItemEntity with storyItemId: " + ItemEntity.getStoryItemId(),
					ex);
		} catch (IOException ex) {
			throw new SolrNamedEntityServiceException(
					"Unexpected IO exception occured when storing ItemEntity with storyItemId: " + ItemEntity.getStoryItemId(), ex);
		}
		
	}
	
	@Override
	public void search (String term) throws SolrNamedEntityServiceException {

		log.info("search Annotation by term: " + term);

		/**
		 * Construct a SolrQuery
		 */
		SolrQuery query = new SolrQuery(term);
		log.info("query: " + query.toString());

		/**
		 * Query the server
		 */
		try {
			QueryResponse rsp = solrServer.query(query);
			log.info("query response: " + rsp.toString());
			
		} catch (IOException | SolrServerException e) {
			throw new SolrNamedEntityServiceException("Unexpected exception occured when searching annotations for: " + term,
					e);
		}

	}


	@Override
	public void update(ItemEntity ItemEntity) throws SolrNamedEntityServiceException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(String storyItemID) throws SolrNamedEntityServiceException {
		// TODO Auto-generated method stub
		
	}


}
