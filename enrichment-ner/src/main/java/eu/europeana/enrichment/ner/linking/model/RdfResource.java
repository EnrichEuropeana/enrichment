package eu.europeana.enrichment.ner.linking.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class RdfResource {

	@JacksonXmlProperty(localName = "resource", isAttribute = true)
	String resourceUrl;

	@JsonIgnore
	public String getResourceUrl() {
		return resourceUrl;
	}
	
}
