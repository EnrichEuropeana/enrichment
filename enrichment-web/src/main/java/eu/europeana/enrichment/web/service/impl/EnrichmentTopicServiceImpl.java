package eu.europeana.enrichment.web.service.impl;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.common.serializer.JsonLdSerializer;
import eu.europeana.enrichment.exceptions.UnsupportedEntityTypeException;
import eu.europeana.enrichment.model.Topic;
import eu.europeana.enrichment.model.impl.TopicImpl;
import eu.europeana.enrichment.mongo.service.PersistentTopicService;
import eu.europeana.enrichment.solr.exception.SolrServiceException;
import eu.europeana.enrichment.solr.model.SolrTopicEntityImpl;
import eu.europeana.enrichment.solr.model.vocabulary.TopicSolrFields;
import eu.europeana.enrichment.solr.service.impl.SolrTopicServiceImpl;
import eu.europeana.enrichment.web.service.EnrichmentTopicService;

@Service(EnrichmentConstants.BEAN_ENRICHMENT_TOPIC_SERVICE)
public class EnrichmentTopicServiceImpl implements EnrichmentTopicService{
	
	Logger logger = LogManager.getLogger(getClass());
	
	@Autowired
	PersistentTopicService persistentTopicService;
	
	@Autowired
	@Qualifier(EnrichmentConstants.BEAN_ENRICHMENT_SOLR_TOPIC_SERVICE)
	SolrTopicServiceImpl solrTopicService;
	
	@Autowired
	JsonLdSerializer jsonLdSerializer;

	@Override
	public Topic createTopic(Topic topic) throws HttpException, UnsupportedEntityTypeException {
		Topic dbtopicEntity = persistentTopicService.getByIdentifier(topic.getIdentifier());
		if (dbtopicEntity != null)
			return dbtopicEntity;
				
		if (topic.getCreated() == null)
			topic.setCreated(new Date());

		persistentTopicService.save(topic);
		
		try {
			solrTopicService.store(TopicSolrFields.SOLR_CORE, new SolrTopicEntityImpl(topic), true);
		} catch (SolrServiceException e) {
			logger.log(Level.ERROR, "Exception is thrown during saving of the topic to Solr.", e);
		}
		return topic;
	}

	@Override
	public Topic updateTopic(Topic topic) {
		Topic dbtopicEntity = persistentTopicService.getByIdentifier(topic.getIdentifier());
		if (dbtopicEntity != null)
		{
			if (topic.getTerms() != null)
				dbtopicEntity.setTerms(topic.getTerms());
			if (topic.getKeywords() != null)
				dbtopicEntity.setKeywords(topic.getKeywords());
			if (topic.getDescriptions() != null)
				dbtopicEntity.setDescriptions(topic.getDescriptions());
			if (topic.getTopicID() != null)
				dbtopicEntity.setTopicID(topic.getTopicID());
			if (topic.getLabels() != null)
				dbtopicEntity.setLabels(topic.getLabels());
			
			dbtopicEntity.setModified(new Date());
			persistentTopicService.save(dbtopicEntity);
			try {
				solrTopicService.store(TopicSolrFields.SOLR_CORE, new SolrTopicEntityImpl(dbtopicEntity), true);
			} catch (SolrServiceException e) {
				logger.log(Level.ERROR, "Exception is thrown during saving of the topic to Solr.", e);
			}
			return dbtopicEntity;
		}
		return null;
	}

	@Override
	public Topic deleteTopic(String topicIdentifier) {
		Topic dbtopicEntity = persistentTopicService.getByIdentifier(topicIdentifier);
		if (dbtopicEntity == null)
			return null;
		
		persistentTopicService.delete(dbtopicEntity);
		
		try {
			solrTopicService.deleteById(TopicSolrFields.SOLR_CORE, dbtopicEntity.getIdentifier());
		} catch (SolrServiceException e) {
			logger.log(Level.ERROR, "Exception is thrown during the deletion of the topic from Solr.", e);
		}
		return dbtopicEntity;
	}

	@Override
	public String searchTopics(String query, String fq, String fl, String facets, String sort, int page, int pageSize) throws SolrServiceException {
		List<TopicImpl> topics = solrTopicService.searchTopics(query, fq, fl, facets, sort, page, pageSize);
		if(topics==null) {
			return "[]";
		}
		String serializedJsonLdStr=null;
    	try {
    		serializedJsonLdStr = jsonLdSerializer.serializeObject(topics);
		} catch (IOException e) {
			throw new SolrServiceException("Exception during the json serialization of the solr topics.", e);
		}
    	
    	return serializedJsonLdStr;

	}

}
