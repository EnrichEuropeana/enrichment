package eu.europeana.enrichment.model.impl;

import java.util.List;
import java.util.Map;

import dev.morphia.annotations.Embedded;

@Embedded
public class KeywordPosition {
	private Map<Integer,String> offsetsTranslatedText;
	private List<String> linkedByNerTools;	
	public Map<Integer,String> getOffsetsTranslatedText() {
		return offsetsTranslatedText;
	}
	public void setOffsetsTranslatedText(Map<Integer,String> offsetsTranslatedText) {
		this.offsetsTranslatedText = offsetsTranslatedText;
	}
	public List<String> getLinkedNerTools() {
		return linkedByNerTools;
	}
	public void setLinkedNerTools(List<String> linkedByNerTools) {
		this.linkedByNerTools=linkedByNerTools;
	}
}
