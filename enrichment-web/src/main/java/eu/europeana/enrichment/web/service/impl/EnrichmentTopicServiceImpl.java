package eu.europeana.enrichment.web.service.impl;

import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.enrichment.common.commons.AppConfigConstants;
import eu.europeana.enrichment.exceptions.UnsupportedEntityTypeException;
import eu.europeana.enrichment.model.Topic;
import eu.europeana.enrichment.mongo.service.PersistentTopicService;
import eu.europeana.enrichment.solr.exception.SolrNamedEntityServiceException;
import eu.europeana.enrichment.solr.model.SolrTopicEntityImpl;
import eu.europeana.enrichment.solr.model.vocabulary.TopicSolrFields;
import eu.europeana.enrichment.solr.service.SolrBaseClientService;
import eu.europeana.enrichment.web.service.EnrichmentTopicService;

@Service(AppConfigConstants.BEAN_ENRICHMENT_TOPIC_SERVICE)
public class EnrichmentTopicServiceImpl implements EnrichmentTopicService{
	
	@Autowired
	PersistentTopicService persistentTopicService;
	
	@Autowired
	SolrBaseClientService solrService;

	@Override
	public Topic createTopic(Topic topic) throws HttpException, UnsupportedEntityTypeException {
		Topic dbtopicEntity = persistentTopicService.getByIdentifier(topic.getIdentifier());
		if (dbtopicEntity != null)
		{
			
			return null;
		}
		
		if (topic.getCreated() == null)
			topic.setCreated(new Date());
		persistentTopicService.save(topic);
		try {
			solrService.storeTopic(TopicSolrFields.SOLR_CORE, new SolrTopicEntityImpl(topic), true);
		} catch (SolrNamedEntityServiceException e) {
			e.printStackTrace();
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
			if (topic.getCreated() != null)
				dbtopicEntity.setCreated(topic.getCreated());
			
			// we set modified to current date
			dbtopicEntity.setModified(new Date());
			persistentTopicService.save(dbtopicEntity);
			try {
				solrService.updateTopic(TopicSolrFields.SOLR_CORE, new SolrTopicEntityImpl(dbtopicEntity), true);
			} catch (SolrNamedEntityServiceException e) {
				e.printStackTrace();
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
			solrService.deleteTopic(TopicSolrFields.SOLR_CORE, new SolrTopicEntityImpl(dbtopicEntity));
		} catch (SolrNamedEntityServiceException e) {
			e.printStackTrace();
		}
		return dbtopicEntity;
	}

}
