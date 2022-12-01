package eu.europeana.enrichment.solr.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections4.MapUtils;
import org.apache.solr.client.solrj.beans.Field;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import eu.europeana.enrichment.common.commons.HelperFunctions;
import eu.europeana.enrichment.model.Term;
import eu.europeana.enrichment.model.Topic;
import eu.europeana.enrichment.model.impl.TermImpl;
import eu.europeana.enrichment.model.impl.TopicImpl;
import eu.europeana.enrichment.solr.model.vocabulary.EntitySolrFields;
import eu.europeana.enrichment.solr.model.vocabulary.TopicSolrFields;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class SolrTopicEntityImpl extends TopicImpl implements Topic {
	
	public SolrTopicEntityImpl() {
		super();
	}
	
	@JsonProperty("keywords")
	private List<String> solrKeywords;
	
	public SolrTopicEntityImpl(Topic copy)
	{
		this.setIdentifier(copy.getIdentifier());
		this.setTopicID(copy.getTopicID());
		if(copy.getLabels()!=null) {
			this.setLabels(new ArrayList<>(copy.getLabels()));
		}
		if(copy.getTerms()!=null) {
			this.setTerms(copy.getTerms());
		}
		if(copy.getKeywords()!=null) {
			this.setKeywords(copy.getKeywords());
		}
		this.setModelId(copy.getModel().getIdentifier());
		this.setModel(copy.getModel());
		if(copy.getDescriptions()!=null) {
			this.setDescriptions(new HashMap<String, String>(copy.getDescriptions()));
		}
		this.setCreated(copy.getCreated());
		this.setModified(copy.getModified());
	}	

	@Override
	@Field(TopicSolrFields.TOPIC_ID)
	public void setTopicID(String id) {
		super.setTopicID(id);
	}	

	@Override
	@Field(TopicSolrFields.IDENTIFIER)
	public void setIdentifier(String identifier)
	{
		super.setIdentifier(identifier);
	}
	
	@Override
	@Field(TopicSolrFields.LABELS)
	public void setLabels(List<String> label) {
		super.setLabels(label);
	}
	
	@Override
	@Field(TopicSolrFields.DESCRIPTION_ALL)
	public void setDescriptions(Map<String,String> descr) {
		if (MapUtils.isNotEmpty(descr)) {
			super.setDescriptions(new HashMap<>(
				HelperFunctions.normalizeStringMapByAddingPrefix(
						TopicSolrFields.DESCRIPTION + EntitySolrFields.DYNAMIC_FIELD_SEPARATOR,
						descr)));
	    }
	}
	
	@Field(TopicSolrFields.TERMS)
	public void setTermsFromSolr(List<String> termsSolr) {
		Map<String, Long> termsCount = termsSolr.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
		List<Term> terms = new ArrayList<Term>();
		List<String> uniqueTerms = termsSolr.stream().distinct().collect(Collectors.toList());
		int rank=1;
		for(String term : uniqueTerms) {
			TermImpl newTerm = new TermImpl(term, rank, termsCount.get(term).intValue());
			rank++;
			terms.add(newTerm);
		}
		super.setTerms(terms);
	}

	@Field(TopicSolrFields.KEYWORDS)
	public void setKeywordsFromSolr(List<String> keywordsSolr) {
		LinkedHashMap<String, Long> keywordsCount = new LinkedHashMap<>(keywordsSolr.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting())));
		List<Term> keywords = new ArrayList<Term>();
		List<String> uniqueKeywords = keywordsSolr.stream().distinct().collect(Collectors.toList());
		int rank=1;
		for(String keyword : uniqueKeywords) {
			TermImpl newTerm = new TermImpl(keyword, rank, keywordsCount.get(keyword).intValue());
			rank++;
			keywords.add(newTerm);
		}
		super.setKeywords(keywords);
	}

	@Override
	@Field(TopicSolrFields.MODEL_ID)
	public void setModelId(String modelId) {
		super.setModelId(modelId);
	}
	
}
