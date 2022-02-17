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
import eu.europeana.enrichment.web.model.EnrichmentTopicRequest;
import eu.europeana.enrichment.web.service.EnrichmentTopicService;

@Service(AppConfigConstants.BEAN_ENRICHMENT_TOPIC_SERVICE)
public class EnrichmentTopicServiceImpl implements EnrichmentTopicService{
	
	@Autowired
	PersistentTopicEntityService persistentTopicEntityService;
	@Autowired
	PersistentTopicModelService persistentTopicModelService;

	@Override
	public TopicEntity createTopic(EnrichmentTopicRequest topicRequest) throws HttpException, UnsupportedEntityTypeException {
		TopicModel dbtopicModel = persistentTopicModelService.findTopicModelByIdentifier(topicRequest.getModel().getIdentifier());
		if (dbtopicModel == null)
		{
			persistentTopicModelService.saveTopicModel(topicRequest.getModel());
			dbtopicModel = topicRequest.getModel();
		}
		
		TopicEntity dbtopicEntity = persistentTopicEntityService.findTopicEntityByIdentifier(topicRequest.getTopicIdentifier());
		if (dbtopicEntity != null)
		{
			
			return null;
		}
		
		TopicEntity topicEntity = new TopicEntityImpl(topicRequest.topicID, topicRequest.topicIdentifier, topicRequest.topicLabels, 
				topicRequest.descriptions, topicRequest.topicTerms, topicRequest.topicKeywords, dbtopicModel, topicRequest.created, topicRequest.modified);
				
		persistentTopicEntityService.saveTopicEntity(topicEntity);
		return topicEntity;
	}

}
