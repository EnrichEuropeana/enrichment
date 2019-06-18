package eu.europeana.enrichment.solr.commons;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.europeana.enrichment.model.WikidataAgent;
import eu.europeana.enrichment.model.WikidataEntity;
import eu.europeana.enrichment.model.WikidataPlace;
import eu.europeana.enrichment.solr.model.SolrWikidataAgentImpl;
import eu.europeana.enrichment.solr.model.SolrWikidataPlaceImpl;
import eu.europeana.enrichment.solr.model.vocabulary.EntitySolrFields;
import ioinformarics.oss.jackson.module.jsonld.JsonldModule;
import ioinformarics.oss.jackson.module.jsonld.JsonldResource;
import ioinformarics.oss.jackson.module.jsonld.JsonldResourceBuilder;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class WikidataEntitySerializer {

	public static final String SET_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	public final static String CONTEXT = "http://www.europeana.eu/schemas/context/collection.jsonld";
	ObjectMapper mapper = new ObjectMapper(); 
		
	public WikidataEntitySerializer() {
		SimpleDateFormat df = new SimpleDateFormat(SET_DATE_FORMAT);
		mapper.setDateFormat(df);
	}
	
	/**
	 * This method provides full serialization of a SolrWikidataAgentImpl
	 * @param solrWikidataAgent
	 * @return json representation of SolrWikidataAgentImpl
	 * @throws IOException
	 */
	
//	public String serializeSolrAgent(SolrWikidataAgentImpl solrWikidataAgent) throws IOException {
//		
//		mapper.registerModule(new JsonldModule(() -> mapper.createObjectNode())); 
//		JsonldResourceBuilder<SolrWikidataAgentImpl> jsonResourceBuilder = JsonldResource.Builder.create();
//		jsonResourceBuilder.context(CONTEXT);
//		String jsonString = mapper.writer().writeValueAsString(jsonResourceBuilder.build(solrWikidataAgent));
//		return jsonString;
//	}

	/**
	 * This method provides full serialization of a SolrWikidataPlaceImpl
	 * @param solrWikidataPlace
	 * @return json representation of SolrWikidataPlaceImpl
	 * @throws IOException
	 */
	
//	public String serializeSolrPlace(SolrWikidataPlaceImpl solrWikidataPlace) throws IOException {		
//		mapper.registerModule(new JsonldModule(() -> mapper.createObjectNode())); 
//		JsonldResourceBuilder<SolrWikidataPlaceImpl> jsonResourceBuilder = JsonldResource.Builder.create();
//		String jsonString = mapper.writer().writeValueAsString(jsonResourceBuilder.build(solrWikidataPlace));
//		return jsonString;
//	}
	
	/**
	 * This method provides full serialization of a WikidataEntity
	 * @param wikidataEntity
	 * @return json representation of WikidataEntity
	 * @throws IOException
	 */
	
	public String serialize(WikidataEntity wikidataEntity) throws IOException {		
		mapper.registerModule(new JsonldModule(() -> mapper.createObjectNode())); 
		JsonldResourceBuilder<WikidataEntity> jsonResourceBuilder = JsonldResource.Builder.create();
		String jsonString = mapper.writer().writeValueAsString(jsonResourceBuilder.build(wikidataEntity));
		return jsonString;
	}

}
