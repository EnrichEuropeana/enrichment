package eu.europeana.enrichment.model.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import eu.europeana.enrichment.model.Term;
import eu.europeana.enrichment.model.Topic;
import eu.europeana.enrichment.model.TopicModel;
import eu.europeana.enrichment.model.utils.*;

@Entity(value="TopicEntity")
public class TopicImpl implements Topic {
	
	//id will be used for storing MongoDB _id
	@Id
	@JsonIgnore
	public ObjectId _id;	
	
	
	@JsonProperty("model")
	private TopicModel model;
	
	// <{baseurl}/topic/1>
	@JsonProperty("topicID")
	private String topicID;
	
	// "LDA_EXP1-K15-IT200#TOPIC1"
	@JsonProperty("identifier")
	private String identifier;
	
	@JsonProperty("labels")
	private List<String> labels;
	
	// language based description
	@JsonProperty("descriptions")
	private Map<String,String> descriptions;
	
	@JsonDeserialize(using=TopicTermsDeserializer.class)
	@JsonProperty("terms")
	private List<Term> terms;
	
	@JsonDeserialize(using=TopicTermsDeserializer.class)
	@JsonProperty("keywords")
	private List<Term> keywords;
	
	@JsonIgnore
	private String modelId;
	
	
	@JsonProperty("created")
	private Date created;
	
	@JsonProperty("modified")
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
	public List<String> getLabels() {
		return this.labels;
	}

	@Override
	public void setLabels(List<String> label) {
		this.labels = label;
	}

	@Override
	public Map<String, String> getDescriptions() {
		return this.descriptions;
	}

	@Override
	public void setDescriptions(Map<String, String> descr) {
		this.descriptions = descr;
	}

	@Override
	public List<Term> getTerms() {
		return this.terms;
	}

	@Override
	public void setTerms(List<Term> terms) {
		this.terms = terms;
	}

	@Override
	public List<Term> getKeywords() {
		return this.keywords;
	}

	@Override
	public void setKeywords(List<Term> keywords) {
		this.keywords = keywords;
	}

	@Override
	public TopicModel getModel() {
		return this.model;
	}

	@Override
	public void setModel(TopicModel model) {
		this.model = model;

	}

	@Override
	public Date getCreated() {
		return this.created;
	}

	@Override
	public Date getModified() {
		return this.modified;
	}

	@Override
	public void setCreated(Date date) {
		this.created = date;
	}

	@Override
	public void setModified(Date date) {
		this.modified = date;
	}
	

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
