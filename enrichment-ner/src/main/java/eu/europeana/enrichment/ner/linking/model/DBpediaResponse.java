package eu.europeana.enrichment.ner.linking.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class DBpediaResponse {

	@JsonIgnore
	private final String WIKIDATA_PREFIX = "http://www.wikidata.org/entity";
	
	@JacksonXmlProperty(localName = "sameAs")
	List<RdfResource> resources;
	
	@JsonIgnore
	public List<String> getWikidataUrls(){
		List<String> wikidataUrls = new ArrayList<>();
		for(RdfResource res : resources) {
			if(res.getResourceUrl() != null && res.getResourceUrl().startsWith(WIKIDATA_PREFIX))
				wikidataUrls.add(res.getResourceUrl());
		}
		return wikidataUrls;
	}
}
