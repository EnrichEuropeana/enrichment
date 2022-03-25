package eu.europeana.enrichment.web.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.enrichment.common.commons.AppConfigConstants;
import eu.europeana.enrichment.exceptions.UnsupportedEntityTypeException;
import eu.europeana.enrichment.model.Topic;
import eu.europeana.enrichment.model.TopicModel;
import eu.europeana.enrichment.model.impl.TopicImpl;
import eu.europeana.enrichment.mongo.service.PersistentTopicService;
import eu.europeana.enrichment.solr.exception.SolrNamedEntityServiceException;
import eu.europeana.enrichment.solr.model.SolrTopicEntityImpl;
import eu.europeana.enrichment.solr.model.vocabulary.TopicEntitySolrFields;
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
		
		persistentTopicService.save(topic);
		try {
			solrService.storeTopic(TopicEntitySolrFields.SOLR_CORE, new SolrTopicEntityImpl(topic), true);
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
			if (topic.getTopicTerms() != null)
				dbtopicEntity.setTopicTerms(topic.getTopicTerms());
			if (topic.getTopicKeywords() != null)
				dbtopicEntity.setTopicKeywords(topic.getTopicKeywords());
			if (topic.getDescription() != null)
				dbtopicEntity.setDescription(topic.getDescription());
			if (topic.getTopicID() != null)
				dbtopicEntity.setTopicID(topic.getTopicID());
			if (topic.getLabel() != null)
				dbtopicEntity.setLabel(topic.getLabel());
			if (topic.getCreatedDate() != null)
				dbtopicEntity.setCreatedDate(topic.getCreatedDate());
			
			// we set modified to current date
			dbtopicEntity.setModifiedDate(new Date());
			persistentTopicService.save(dbtopicEntity);
			try {
				solrService.updateTopic(TopicEntitySolrFields.SOLR_CORE, new SolrTopicEntityImpl(dbtopicEntity));
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
			solrService.deleteTopic(TopicEntitySolrFields.SOLR_CORE, new SolrTopicEntityImpl(dbtopicEntity));
		} catch (SolrNamedEntityServiceException e) {
			e.printStackTrace();
		}
		return dbtopicEntity;
	}

}
