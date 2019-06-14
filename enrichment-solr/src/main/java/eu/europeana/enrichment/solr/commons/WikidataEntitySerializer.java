package eu.europeana.enrichment.solr.commons;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.europeana.enrichment.model.WikidataAgent;
import eu.europeana.enrichment.model.WikidataPlace;
import ioinformarics.oss.jackson.module.jsonld.JsonldModule;
import ioinformarics.oss.jackson.module.jsonld.JsonldResource;
import ioinformarics.oss.jackson.module.jsonld.JsonldResourceBuilder;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class WikidataEntitySerializer {

	public static final String SET_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	public final static String CONTEXT = "http://www.europeana.eu/schemas/context/collection.jsonld";
	ObjectMapper mapper = new ObjectMapper(); 
		
	public WikidataEntitySerializer() {
		SimpleDateFormat df = new SimpleDateFormat(SET_DATE_FORMAT);
		mapper.setDateFormat(df);
	}
	
	/**
	 * This method provides full serialization of a WikidataAgent
	 * @param WikidataAgent
	 * @return full WikidataAgent view
	 * @throws IOException
	 */
	
	public String serialize(WikidataAgent wikidataAgent) throws IOException {
		
		mapper.registerModule(new JsonldModule(() -> mapper.createObjectNode())); 
		JsonldResourceBuilder<WikidataAgent> jsonResourceBuilder = JsonldResource.Builder.create();
		jsonResourceBuilder.context(CONTEXT);
		String jsonString = mapper.writer().writeValueAsString(jsonResourceBuilder.build(wikidataAgent));
		return jsonString;
	}

	/**
	 * This method provides full serialization of a WikidataPlace
	 * @param WikidataPlace
	 * @return full WikidataPlace view
	 * @throws IOException
	 */
	
	public String serialize(WikidataPlace wikidataPlace) throws IOException {		
		mapper.registerModule(new JsonldModule(() -> mapper.createObjectNode())); 
		JsonldResourceBuilder<WikidataPlace> jsonResourceBuilder = JsonldResource.Builder.create();
		String jsonString = mapper.writer().writeValueAsString(jsonResourceBuilder.build(wikidataPlace));
		return jsonString;
	}

}
