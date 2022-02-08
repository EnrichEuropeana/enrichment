package eu.europeana.enrichment.model.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

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
	public String _id = new ObjectId().toString();
	
	
	private String topicID;
	private String identifier;
	private List<String> labels;
	// language based description
	private Map<String,String> descriptions;
	
	private List<TermEntity> terms;
	private List<TermEntity> keywords;
	
	@Transient
	private TopicModel model;
	
	private Date created;
	private Date modified;
	

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
		this.created = created;
		this.modified = modified;
	}

	@Override
	public String getTopicID() {
		// TODO Auto-generated method stub
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

}
