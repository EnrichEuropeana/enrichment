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
import eu.europeana.enrichment.model.TermEntity;
import eu.europeana.enrichment.model.TopicEntity;
import eu.europeana.enrichment.model.TopicModel;
import eu.europeana.enrichment.model.impl.TopicEntityImpl;
import eu.europeana.enrichment.mongo.service.PersistentTopicEntityService;
import eu.europeana.enrichment.mongo.service.PersistentTopicModelService;
import eu.europeana.enrichment.solr.exception.SolrNamedEntityServiceException;
import eu.europeana.enrichment.solr.model.SolrTopicEntityImpl;
import eu.europeana.enrichment.solr.model.vocabulary.TopicEntitySolrFields;
import eu.europeana.enrichment.solr.service.SolrBaseClientService;
import eu.europeana.enrichment.web.model.EnrichmentTopicRequest;
import eu.europeana.enrichment.web.service.EnrichmentTopicService;

@Service(AppConfigConstants.BEAN_ENRICHMENT_TOPIC_SERVICE)
public class EnrichmentTopicServiceImpl implements EnrichmentTopicService{
	
	@Autowired
	PersistentTopicEntityService persistentTopicEntityService;
	@Autowired
	PersistentTopicModelService persistentTopicModelService;
	@Autowired
	SolrBaseClientService solrService;

	@Override
	public TopicEntity createTopic(EnrichmentTopicRequest topicRequest) throws HttpException, UnsupportedEntityTypeException {
		TopicModel dbtopicModel = persistentTopicModelService.findTopicModelByIdentifier(topicRequest.getModel().getIdentifier());
		if (dbtopicModel == null)
		{
			persistentTopicModelService.saveTopicModel(topicRequest.getModel());
			dbtopicModel = topicRequest.getModel();
		}
		
		TopicEntity dbtopicEntity = persistentTopicEntityService.findById(topicRequest.getTopicIdentifier());
		if (dbtopicEntity != null)
		{
			
			return null;
		}
		
		TopicEntity topicEntity = new TopicEntityImpl();
		topicEntity.setTopicID(topicRequest.topicID);
		topicEntity.setIdentifier(topicRequest.topicIdentifier);
		topicEntity.setLabel(topicRequest.topicLabels);
		topicEntity.setTopicTerms(topicRequest.topicTerms);
		topicEntity.setDescription(topicRequest.descriptions);
		topicEntity.setTopicKeywords(topicRequest.topicKeywords);
		topicEntity.setModelId(topicRequest.getModel().getIdentifier());
		topicEntity.setTopicModel(dbtopicModel);
		topicEntity.setCreatedDate(topicRequest.created);
		topicEntity.setModifiedDate(topicRequest.modified);
				
				
		persistentTopicEntityService.save(topicEntity);
		try {
			solrService.storeTopicEntity(TopicEntitySolrFields.SOLR_CORE, new SolrTopicEntityImpl(topicEntity), true);
		} catch (SolrNamedEntityServiceException e) {
			e.printStackTrace();
		}
		return topicEntity;
	}

	@Override
	public TopicEntity updateTopic(EnrichmentTopicRequest request) {
		TopicEntity dbtopicEntity = persistentTopicEntityService.findById(request.getTopicIdentifier());
		if (dbtopicEntity != null)
		{
			if (request.topicTerms != null)
				dbtopicEntity.setTopicTerms(request.topicTerms);
			if (request.topicKeywords != null)
				dbtopicEntity.setTopicKeywords(request.topicKeywords);
			if (request.descriptions != null)
				dbtopicEntity.setDescription(request.descriptions);
			if (request.topicID != null)
				dbtopicEntity.setTopicID(request.topicID);
			if (request.topicLabels != null)
				dbtopicEntity.setLabel(request.topicLabels);
			if (request.getCreated() != null)
				dbtopicEntity.setCreatedDate(request.created);
			
			// we set modified to current date
			dbtopicEntity.setModifiedDate(new Date());
			persistentTopicEntityService.update(dbtopicEntity);
			try {
				solrService.updateTopicEntity(TopicEntitySolrFields.SOLR_CORE, new SolrTopicEntityImpl(dbtopicEntity));
			} catch (SolrNamedEntityServiceException e) {
				e.printStackTrace();
			}
			return dbtopicEntity;
		}
		return null;
	}

	@Override
	public TopicEntity deleteTopic(String topicIdentifier) {
		TopicEntity dbtopiEntity = persistentTopicEntityService.findById(topicIdentifier);
		if (dbtopiEntity == null)
			return null;
		
		List<TopicEntity> otherTopics = persistentTopicEntityService.findByModelId(dbtopiEntity.getModelId());
		otherTopics.remove(dbtopiEntity);
		if (otherTopics.isEmpty())
			persistentTopicModelService.deleteTopicModel(persistentTopicModelService.findTopicModelByIdentifier(dbtopiEntity.getModelId()));
		persistentTopicEntityService.delete(dbtopiEntity);
		try {
			solrService.deleteTopicEntity(TopicEntitySolrFields.SOLR_CORE, new SolrTopicEntityImpl(dbtopiEntity));
		} catch (SolrNamedEntityServiceException e) {
			e.printStackTrace();
		}
		return dbtopiEntity;
	}

}
