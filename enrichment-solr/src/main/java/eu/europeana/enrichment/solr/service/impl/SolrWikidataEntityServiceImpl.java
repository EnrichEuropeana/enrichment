package eu.europeana.enrichment.solr.service.impl;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.WikidataEntity;
import eu.europeana.enrichment.model.impl.WikidataEntityImpl;
import eu.europeana.enrichment.ner.linking.WikidataService;
import eu.europeana.enrichment.solr.exception.SolrNamedEntityServiceException;
import eu.europeana.enrichment.solr.model.SolrStoryEntityImpl;
import eu.europeana.enrichment.solr.model.SolrWikidataEntityImpl;
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
		
		SolrWikidataEntityImpl solrWikidataEntity = null;
		if(wikidataEntity instanceof SolrWikidataEntityImpl) {
			solrWikidataEntity=(SolrWikidataEntityImpl) wikidataEntity;
		}
		else {
			solrWikidataEntity=new SolrWikidataEntityImpl(wikidataEntity);
		}
		
		solrBaseClientService.store(solrCollection, solrWikidataEntity, doCommit);
		
	}

	@Override
	public void storeWikidataFromURL(String wikidataURL) {
		
		String WikidataJSON = wikidataService.getWikidataJSONFromWikidataID(wikidataURL);
		
		WikidataEntity newWikidataEntity = new WikidataEntityImpl ();
		/*
		 * TODO: add implementation for taking all fields if * is specified, e.g.
		 * aliases.*.value should take all fields in the field aliases and then for each of them the field "value"
		 */
		
		//newWikidataEntity.setAltLabel(wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON, "aliases"));
		
		wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON, "claims.P106.mainsnak.datavalue.value.id");

		
		
	}
}
