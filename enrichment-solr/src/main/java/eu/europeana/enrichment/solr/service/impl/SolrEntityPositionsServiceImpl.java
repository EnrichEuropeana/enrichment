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


import eu.europeana.enrichment.model.StoryItemEntity;
import eu.europeana.enrichment.solr.exception.SolrNamedEntityServiceException;
import eu.europeana.enrichment.solr.model.SolrStoryItemEntityImpl;
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
	public boolean store(StoryItemEntity storyItemEntity) throws SolrNamedEntityServiceException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void store(List<? extends StoryItemEntity> storyItemEntities) throws SolrNamedEntityServiceException {
		
		for(StoryItemEntity ent : storyItemEntities) {
			store(ent, false);
		}
		
		try {
			solrServer.commit();
		} catch (SolrServerException ex) {
			throw new SolrNamedEntityServiceException(
					"Unexpected Solr server exception occured when storing a list of storyItemEntity.", ex);			
		} catch (IOException ex) {
			throw new SolrNamedEntityServiceException(
					"Unexpected IO exception occured when storing a list of storyItemEntit", ex);
		}
		
	}

	@Override
	public void store(StoryItemEntity storyItemEntity, boolean doCommit) throws SolrNamedEntityServiceException {
		try {
			
			log.debug("store: " + storyItemEntity.toString());
			
			SolrStoryItemEntityImpl solrStoryItem = null;
			if(storyItemEntity instanceof SolrStoryItemEntityImpl) {
				solrStoryItem=(SolrStoryItemEntityImpl) storyItemEntity;
			}
			else {
				solrStoryItem=new SolrStoryItemEntityImpl(storyItemEntity);
			}
			
			UpdateResponse rsp = solrServer.addBean(solrStoryItem);
			log.info("store response: " + rsp.toString());
			if(doCommit)
				solrServer.commit();
		} catch (SolrServerException ex) {
			throw new SolrNamedEntityServiceException(
					"Unexpected Solr server exception occured when storing storyItemEntity with storyItemId: " + storyItemEntity.getStoryItemId(),
					ex);
		} catch (IOException ex) {
			throw new SolrNamedEntityServiceException(
					"Unexpected IO exception occured when storing storyItemEntity with storyItemId: " + storyItemEntity.getStoryItemId(), ex);
		}
		
	}

	@Override
	public void update(StoryItemEntity storyItemEntity) throws SolrNamedEntityServiceException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(String storyItemID) throws SolrNamedEntityServiceException {
		// TODO Auto-generated method stub
		
	}


}
