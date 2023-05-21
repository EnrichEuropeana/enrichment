package eu.europeana.enrichment.definitions.model.impl;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Property;
import eu.europeana.enrichment.common.commons.EnrichmentConstants;

@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
@Embedded
public class Processing {

	//this field is a concatenated string of ner tools, first being dbpedia, e.g. DBpedia_Spotlight,Stanford_NER or only Stanford_NER
	@Property(EnrichmentConstants.FOUND_BY_NER_TOOLS)
	private List<String> foundByNerTools;
	//this field has the same format as foundByNerTools
	@Property(EnrichmentConstants.LINKED_BY_NER_TOOLS)
	private List<String> linkedByNerTools;
	
	private String matching;
	private double score;

	public String getMatching() {
		return matching;
	}
	public void setMatching(String matching) {
		this.matching = matching;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}	
	public List<String> getFoundByNerTools() {
		return foundByNerTools;
	}
	public void setFoundByNerTools(List<String> foundByNerTools) {
		this.foundByNerTools = foundByNerTools;
	}
	public List<String> getLinkedByNerTools() {
		return linkedByNerTools;
	}
	public void setLinkedByNerTools(List<String> linkedByNerTools) {
		this.linkedByNerTools = linkedByNerTools;
	}	
}
