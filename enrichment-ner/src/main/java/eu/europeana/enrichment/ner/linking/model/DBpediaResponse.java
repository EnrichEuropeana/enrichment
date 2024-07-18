package eu.europeana.enrichment.ner.linking.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import eu.europeana.enrichment.common.commons.EnrichmentConstants;

//@JsonIgnoreProperties(ignoreUnknown=true)
//@XmlRootElement(namespace = XmlConstants.NAMESPACE_RDF, name = XmlConstants.XML_DESCRIPTION)
//@XmlAccessorType(XmlAccessType.FIELD)
//@JacksonXmlRootElement(namespace = "rdf", localName = XmlConstants.XML_SAME_DESCRIPTION)
public class DBpediaResponse {
	
//	@JacksonXmlProperty(localName = "sameAs")
//	List<RdfResource> resources;
	
	@XmlElement(namespace = XmlConstants.NAMESPACE_OWL, name = XmlConstants.XML_SAME_AS)
	private List<RdfResource> resources = new ArrayList<>();
	
	@XmlElement(namespace = XmlConstants.NAMESPACE_DBO, name = XmlConstants.XML_WIKIPAGE_REDIRECTS)
	private RdfResource wikipageRedirect;		
	
	//@JacksonXmlProperty(namespace = "owl", localName = XmlConstants.XML_SAME_AS)
	public List<RdfResource> getResources() {
		return resources;
	}

	//@JsonIgnore
	public List<String> getWikidataUrls(){
			
		if(resources==null || resources.isEmpty()) return Collections.emptyList();
		
		List<String> wikidataUrls = new ArrayList<>();
		for(RdfResource res : resources) {
			if(res.getResourceUrl() != null && res.getResourceUrl().startsWith(EnrichmentConstants.WIKIDATA_ENTITY_BASE_URL))
				wikidataUrls.add(res.getResourceUrl());
		}
		return wikidataUrls;
	}
	
	public RdfResource getWikipageRedirect() {
		return wikipageRedirect;
	}
	
}
