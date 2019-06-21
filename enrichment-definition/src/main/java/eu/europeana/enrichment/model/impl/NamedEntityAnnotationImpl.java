package eu.europeana.enrichment.model.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import eu.europeana.enrichment.model.NamedEntityAnnotation;

@JsonPropertyOrder({ "id", "type", "motivation","body","target"})
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class NamedEntityAnnotationImpl implements NamedEntityAnnotation {

	private final String idBase = "http://dsi-demo.ait.ac.at/enrichment-web/enrichment/annotation/";
	protected String id;
	protected String source;
	protected String target;
	protected String type;
	protected String motivation;
	protected Map<String,String> body;

	public NamedEntityAnnotationImpl (String storyId, String wikidataId, String storySource) {

		source = wikidataId;
		target = storySource;		
		id = idBase + storyId + "/" + wikidataId.substring(wikidataId.lastIndexOf("/")+1);
		type = "Annotation";
		motivation = "tagging";
		body = new HashMap<String, String>();
		body.put("type", "SpecificResource");
		body.put("source", source);
		body.put("purpose", "tagging");
	}
	
	@Override
	@JsonIgnore
	public String getSource() {
		return source;
	}

	@Override
	public void setSource(String sourceParam) {
		source = sourceParam;	
	}

	@Override
	@JsonProperty("target")
	public String getTarget() {
		return target;
	}

	@Override
	public void setTarget(String targetParam) {
		target = targetParam;
		
	}

	@Override
	@JsonProperty("id")
	public String getId() {
		
		return id;
	}

	@Override
	public void setId(String idParam) {
		id = idParam; 
		
	}

	@Override
	@JsonProperty("type")
	public String getType() {
		
		return type;
	}

	@Override
	public void setType(String typeParam) {
		type = typeParam;
		
	}

	@Override
	@JsonProperty("motivation")
	public String getMotivation() {
		return motivation;
	}

	@Override
	public void setMotivation(String motivationParam) {
		motivation = motivationParam;
		
	}

	@Override
	@JsonProperty("body")
	public Map<String, String> getBody() {
		
		return body;
	}

	@Override
	public void setBody(Map<String, String> bodyParam) {
		body = bodyParam;
		
	}

}
