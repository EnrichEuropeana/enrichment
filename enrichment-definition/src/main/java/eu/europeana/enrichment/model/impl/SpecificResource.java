package eu.europeana.enrichment.model.impl;

import com.fasterxml.jackson.annotation.JsonInclude;

import dev.morphia.annotations.Embedded;

@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
@Embedded
public class SpecificResource {

	private String id;
	private String source;
	private String scope;
	private String format;
	private String purpose;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}	
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}	
	public String getPurpose() {
		return purpose;
	}
	public void setPurpose(String role) {
		this.purpose = role;
	}		
}
