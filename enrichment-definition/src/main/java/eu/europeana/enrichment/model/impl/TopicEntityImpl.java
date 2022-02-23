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
import eu.europeana.enrichment.model.TermEntity;
import eu.europeana.enrichment.model.TopicEntity;
import eu.europeana.enrichment.model.TopicModel;


@Entity(value="TopicEntityImpl")
public class TopicEntityImpl implements TopicEntity {
	
	//id will be used for storing MongoDB _id
	@Id
	@JsonIgnore
	public String _id = new ObjectId().toString();
	
	@Transient
	private TopicModel model;
	
	// <{baseurl}/topic/1>
	private String topicID;
	
	// "LDA_EXP1-K15-IT200#TOPIC1"
	private String identifier;
	
	private List<String> labels;
	// language based description
	private Map<String,String> descriptions;
	
	
	private List<TermEntity> terms;
	
	private List<TermEntity> keywords;
	
	private String modelId;
	
	private Date created;
	private Date modified;
	
	
	public TopicEntityImpl()
	{
		
	}

	public TopicEntityImpl(String id, String identifier, List<String> labels, Map<String, String> descriptions,
			List<TermEntity> terms, List<TermEntity> keywords, TopicModel model, Date created, Date modified) {
		super();
		this.topicID = id;
		this.identifier = identifier;
		this.labels = labels;
		this.descriptions = descriptions;
		this.terms = terms;
		this.keywords = keywords;
		this.model = model;
		this.modelId = model.getIdentifier();
		this.created = created;
		if (modified == null)
			this.modified = new Date();
		else
			this.modified = modified;
	}

	public TopicEntityImpl(TopicEntity topicEntity) {
		this.topicID = topicEntity.getTopicID();
		this.identifier = topicEntity.getIdentifier();
		this.labels = topicEntity.getLabel();
		this.descriptions = topicEntity.getDescription();
		this.terms = topicEntity.getTopicTerms();
		this.keywords = topicEntity.getTopicKeywords();
		this.model = topicEntity.getTopicModel();
		this.modelId = topicEntity.getModelId();
		this.created = topicEntity.getCreatedDate();
		if (topicEntity.getModifiedDate()==null)
			this.modified = new Date();
		else
			this.modified = topicEntity.getModifiedDate();
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
	public List<TermEntity> getTopicTerms() {
		return this.terms;
	}

	@Override
	public void setTopicTerms(List<TermEntity> terms) {
		this.terms = terms;
	}

	@Override
	public List<TermEntity> getTopicKeywords() {
		return this.keywords;
	}

	@Override
	public void setTopicKeywords(List<TermEntity> keywords) {
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
	public String getId() {
		return _id;
	}

	@Override
	public String toJSON() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		
		return mapper.writeValueAsString(this);
	}

	@Override
	public String getModelId() {
		return modelId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(_id, created, descriptions, identifier, keywords, labels, model, modelId, modified, terms,
				topicID);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TopicEntityImpl other = (TopicEntityImpl) obj;
		return Objects.equals(_id, other._id) && Objects.equals(created, other.created)
				&& Objects.equals(descriptions, other.descriptions) && Objects.equals(identifier, other.identifier)
				&& Objects.equals(keywords, other.keywords) && Objects.equals(labels, other.labels)
				&& Objects.equals(modelId, other.modelId)
				&& Objects.equals(modified, other.modified) && Objects.equals(terms, other.terms)
				&& Objects.equals(topicID, other.topicID);
	}

	@Override
	public void setModelId(String modelId) {
		if (model != null)
			this.modelId = this.model.getIdentifier();
		else
			this.modelId = modelId;
		
	}

}
