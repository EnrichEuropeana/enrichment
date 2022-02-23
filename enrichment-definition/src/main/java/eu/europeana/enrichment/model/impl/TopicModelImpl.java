package eu.europeana.enrichment.model.impl;

import java.util.Objects;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonIgnore;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import eu.europeana.enrichment.exceptions.UnsupportedEntityTypeException;
import eu.europeana.enrichment.model.TopicModel;
import eu.europeana.enrichment.model.vocabulary.TopicConst;


@Entity(value="TopicModelImpl")
public class TopicModelImpl implements TopicModel {
	
	private String url;
	// based on a naming convention 
	// e.g. LDA-K15-IT200-V1.0.0 (method-numOfTopics-maxIter-dataVersion)
	private String identifier;
	private String description;
	private String algorithm;
	
	
	@Id
	@JsonIgnore
    private String _id = new ObjectId().toString();
	

	@Override
	public int hashCode() {
		return Objects.hash(_id, algorithm, description, identifier, url);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TopicModelImpl other = (TopicModelImpl) obj;
		return Objects.equals(_id, other._id) && Objects.equals(algorithm, other.algorithm)
				&& Objects.equals(description, other.description) && Objects.equals(identifier, other.identifier)
				&& Objects.equals(url, other.url);
	}


	@Override
	public String getId() {
		return _id;
	}

	
	public TopicModelImpl()
	{
		
	}
	public TopicModelImpl(String url, String id, String description, String algorithm) {
		this.url = url;
		this.identifier = id;
		this.description = description;
		this.algorithm = algorithm;
	}

	public TopicModelImpl(TopicModel tm) {
		this._id = tm.getId();
		this.url = tm.getURL();
		this.identifier = tm.getIdentifier();
		this.description = tm.getDescription();
		this.algorithm = tm.getAlgorithm();
	}

	@Override
	public String getURL() {
		return this.url;
	}

	@Override
	public void setURL(String url) {
		this.url = url;
	}

	@Override
	public String getIdentifier() {
		return  this.identifier;
	}

	@Override
	public void setIdentifier(String id) {
		this.identifier = id;
	}

	@Override
	public String getDescription() {
		return this.description;
	}

	@Override
	public void setDescription(String descr) {
		this.description = descr;
	}

	@Override
	public String getAlgorithm() {
		return this.algorithm;
	}

	@Override
	public void setAlgorithm(String alg) throws UnsupportedEntityTypeException {
		if (!(alg.equals(TopicConst.LDA) || alg.equals(TopicConst.LDA2Vec) ))
			throw new UnsupportedEntityTypeException("Algorithm must be one of: LDA or LDA2Vec");
		this.algorithm = alg;
	}



}
