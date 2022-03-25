package eu.europeana.enrichment.model.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Transient;
import eu.europeana.enrichment.model.Term;
import eu.europeana.enrichment.model.Topic;
import eu.europeana.enrichment.model.TopicModel;


@Entity(value="TopicEntity")
public class TopicImpl implements Topic {
	
	//id will be used for storing MongoDB _id
	@Id
	@JsonIgnore
	public ObjectId _id;	
	private TopicModel model;
	
	// <{baseurl}/topic/1>
	private String topicID;
	
	// "LDA_EXP1-K15-IT200#TOPIC1"
	private String identifier;
	
	private List<String> labels;
	// language based description
	private Map<String,String> descriptions;
	
	
	private List<Term> terms;
	
	private List<Term> keywords;
	
	private String modelId;
	
	private Date created;
	private Date modified;
	
	
	public TopicImpl()
	{
		
	}

	@Override
	public String getTopicID() {
		return this.topicID;
	}

	@Override
	public void setTopicID(String id) {
		this.topicID = id;

	}

	@Override
	public String getIdentifier() {
		return this.identifier;
	}

	@Override
	public void setIdentifier(String identifier) {
		this.identifier = identifier;

	}

	@Override
	public List<String> getLabel() {
		return this.labels;
	}

	@Override
	public void setLabel(List<String> label) {
		this.labels = label;
	}

	@Override
	public Map<String, String> getDescription() {
		return this.descriptions;
	}

	@Override
	public void setDescription(Map<String, String> descr) {
		this.descriptions = descr;
	}

	@Override
	public List<Term> getTopicTerms() {
		return this.terms;
	}

	@Override
	public void setTopicTerms(List<Term> terms) {
		this.terms = terms;
	}

	@Override
	public List<Term> getTopicKeywords() {
		return this.keywords;
	}

	@Override
	public void setTopicKeywords(List<Term> keywords) {
		this.keywords = keywords;
	}

	@Override
	public TopicModel getTopicModel() {
		return this.model;
	}

	@Override
	public void setTopicModel(TopicModel model) {
		this.model = model;

	}

	@Override
	public Date getCreatedDate() {
		return this.created;
	}

	@Override
	public Date getModifiedDate() {
		return this.modified;
	}

	@Override
	public void setCreatedDate(Date date) {
		this.created = date;
	}

	@Override
	public void setModifiedDate(Date date) {
		this.modified = date;
	}
	
	@Override
	public ObjectId getObjectId() {
		return _id;
	}

	

	@Override
	public String getModelId() {
		return modelId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(identifier);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TopicImpl other = (TopicImpl) obj;
		return  Objects.equals(identifier, other.identifier);
	}

	@Override
	public void setModelId(String modelId) {
		if (model != null)
			this.modelId = this.model.getIdentifier();
		else
			this.modelId = modelId;
		
	}

}
