package eu.europeana.enrichment.web.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import eu.europeana.enrichment.model.TermEntity;
import eu.europeana.enrichment.model.TopicModel;
import eu.europeana.enrichment.web.commons.TopicTermsDeserializer;

/**
 * This class represents the Rest Post body structure for
 * end point /enrichment/topic. The request body will
 * be parsed into this class. An example of the request body:
 * {"id" : "<{baseurl}/topic/{numeric_sequence}>",
	"identifier": "LDA_EXP1-K15-IT200#TOPIC1",
	"label": ["first world war", "eastern front"],
	"description": {
		"en": "LDA topic model learned on 1914-1918 collection",
		"de": "LDA topic model generiert von 1914-1918 Kollektion"
	},
	"topicTerm": [
		{"term" : "war",
	 	"score" : 80,
	 	"rank" : 1
	},
	{"term" : "front",
	 "score" : 72,
	 "rank" : 2
	},
	....],
	"keyword":[
	{"term" : "letters from the front",
	 "score" : 70,
	 "rank": 1
	},
	{"term" : "war diary",
	 "score" : 50,
	 "rank" : 2
	},
	{"term" : "prisoners of war",
	 "score" : 20
	 "rank": 3
	}	
	....],
	"model":{
	"id":"<url>"
	"identifier": "LDA_EXP1-K15-IT200",
	"description": "First experiment using LDA on 1914-1918 collection, K=15, Iterations=200 ",
	"algorithm": "LDA" },
	created:”01-10-2021”,
	modified:”05-10-2021”}

 * @author AndreselM
 *
 */
public class EnrichmentTopicRequest {
	
	public String topicID;
	public String topicIdentifier;
	public List<String> topicLabels;
	public Map<String,String> descriptions;
	@JsonDeserialize(using=TopicTermsDeserializer.class)
	public List<TermEntity> topicTerms;
	
	@JsonDeserialize(using=TopicTermsDeserializer.class)
	public List<TermEntity> topicKeywords;
	public TopicModel model;
	public Date created;
	public Date modified;
	
	public String getTopicID() {
		return topicID;
	}
	public void setTopicID(String topicID) {
		this.topicID = topicID;
	}
	public String getTopicIdentifier() {
		return topicIdentifier;
	}
	public void setTopicIdentifier(String topicIdentifier) {
		this.topicIdentifier = topicIdentifier;
	}
	public List<String> getTopicLabels() {
		return topicLabels;
	}
	public void setTopicLabels(List<String> topicLabels) {
		this.topicLabels = topicLabels;
	}
	public Map<String, String> getDescriptions() {
		return descriptions;
	}
	public void setDescriptions(Map<String, String> descriptions) {
		this.descriptions = descriptions;
	}
	public List<TermEntity> getTopicTerms() {
		return topicTerms;
	}
	public void setTopicTerms(List<TermEntity> topicTerms) {
		this.topicTerms = topicTerms;
	}
	public List<TermEntity> getTopicKeywords() {
		return topicKeywords;
	}
	public void setTopicKeywords(List<TermEntity> topicKeywords) {
		this.topicKeywords = topicKeywords;
	}
	public TopicModel getModel() {
		return model;
	}
	public void setModel(TopicModel model) {
		this.model = model;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public Date getModified() {
		return modified;
	}
	public void setModified(Date modified) {
		this.modified = modified;
	}

}
