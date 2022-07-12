package eu.europeana.enrichment.model.impl;

import java.util.List;

import dev.morphia.annotations.Embedded;

@Embedded
public class KeywordNamedEntityNew {
	private List<Integer> offsetsTranslatedText;
	private List<String> nerTools;	
	public List<Integer> getOffsetsTranslatedText() {
		return offsetsTranslatedText;
	}
	public void setOffsetsTranslatedText(List<Integer> offsetsTranslatedText) {
		this.offsetsTranslatedText = offsetsTranslatedText;
	}
	public List<String> getNerTools() {
		return nerTools;
	}
	public void setNerTools(List<String> nerTools) {
		this.nerTools = nerTools;
	}
}
