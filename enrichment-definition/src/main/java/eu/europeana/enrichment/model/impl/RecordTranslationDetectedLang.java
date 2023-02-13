package eu.europeana.enrichment.model.impl;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import dev.morphia.annotations.Embedded;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
@Embedded
public class RecordTranslationDetectedLang {
	
	private String primary;
	private Map<String,Double> detected;

	public RecordTranslationDetectedLang()
	{	
	}	
	
	public String getPrimary() {
		return primary;
	}

	public void setPrimary(String primary) {
		this.primary = primary;
	}

	public Map<String, Double> getDetected() {
		return detected;
	}

	public void setDetected(Map<String, Double> detected) {
		this.detected = detected;
	}
	
}
