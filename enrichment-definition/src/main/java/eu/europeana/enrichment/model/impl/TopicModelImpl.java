package eu.europeana.enrichment.model.impl;

import java.util.Objects;

import eu.europeana.enrichment.exceptions.UnsupportedEntityTypeException;
import eu.europeana.enrichment.model.TopicModel;
import eu.europeana.enrichment.model.vocabulary.TopicConst;


//@Entity(value="TopicModelImpl")
public class TopicModelImpl implements TopicModel {
	
	private String url;
	// based on a naming convention 
	// e.g. LDA-K15-IT200-V1.0.0 (method-numOfTopics-maxIter-dataVersion)
	private String identifier;
	private String description;
	private String algorithm;

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
		TopicModelImpl other = (TopicModelImpl) obj;
		return  Objects.equals(identifier, other.identifier);
	}
	
	public TopicModelImpl()
	{
		
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
