package eu.europeana.enrichment.definitions.model.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexed;
import dev.morphia.annotations.Transient;
import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.definitions.model.Term;
import eu.europeana.enrichment.definitions.model.Topic;
import eu.europeana.enrichment.definitions.model.TopicModel;
import eu.europeana.enrichment.definitions.model.utils.TopicTermsDeserializer;

@JsonPropertyOrder({
	EnrichmentConstants.TOPIC_ID, 
	EnrichmentConstants.TOPIC_IDENTIFIER, 
	EnrichmentConstants.TOPIC_SCORE, 
	EnrichmentConstants.TOPIC_LABELS, 
	EnrichmentConstants.TOPIC_DESCRIPTIONS, 
	EnrichmentConstants.TOPIC_TERMS, 
	EnrichmentConstants.TOPIC_KEYWORDS, 
	EnrichmentConstants.TOPIC_MODEL, 
	EnrichmentConstants.TOPIC_CREATED, 
	EnrichmentConstants.TOPIC_MODIFIED 
})
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
@Entity(value="TopicEntity")
public class TopicImpl implements Topic {
	
	//id will be used for storing MongoDB _id
	@Id
	@JsonIgnore
	public ObjectId _id;
	
	@JsonIgnore
	@Transient
	private String modelId;	
	
	@JsonProperty(EnrichmentConstants.TOPIC_MODEL)
	private TopicModel model;
	
	// <{baseurl}/topic/1>
	@JsonIgnore
	private long id;
	
	@Transient
	@JsonProperty(EnrichmentConstants.TOPIC_ID)
	private String urlId;

	// "LDA_EXP1-K15-IT200#TOPIC1"
	@JsonProperty(EnrichmentConstants.TOPIC_IDENTIFIER)
	@Indexed(options = @IndexOptions(unique = true))
	private String identifier;
	
	@JsonProperty(EnrichmentConstants.TOPIC_LABELS)
	private List<String> labels;
	
	// language based description
	@JsonProperty(EnrichmentConstants.TOPIC_DESCRIPTIONS)
	private Map<String,String> descriptions;
	
	@JsonDeserialize(using=TopicTermsDeserializer.class)
	@JsonProperty(EnrichmentConstants.TOPIC_TERMS)
	private List<Term> terms;
	
	@JsonDeserialize(using=TopicTermsDeserializer.class)
	@JsonProperty(EnrichmentConstants.TOPIC_KEYWORDS)
	private List<Term> keywords;
	
	@JsonProperty(EnrichmentConstants.TOPIC_CREATED)
	private Date created;
	
	@JsonProperty(EnrichmentConstants.TOPIC_MODIFIED)
	private Date modified;
	
	@JsonProperty(EnrichmentConstants.TOPIC_SCORE)
	private Float score;
		
	public TopicImpl()
	{
		
	}

	@Override
	public long getId() {
		return this.id;
	}

	@Override
	public void setId(long id) {
		this.id = id;

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
	
	public Float getScore() {
		return score;
	}

	public void setScore(Float score) {
		this.score = score;
	}

	public String getUrlId() {
		return urlId;
	}

	public void setUrlId(String urlId) {
		this.urlId = urlId;
	}	
}
