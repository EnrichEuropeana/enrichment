package eu.europeana.enrichment.solr.commons;

import java.io.IOException;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.europeana.enrichment.model.WikidataEntity;
import ioinformarics.oss.jackson.module.jsonld.JsonldModule;
import ioinformarics.oss.jackson.module.jsonld.JsonldResource;
import ioinformarics.oss.jackson.module.jsonld.JsonldResourceBuilder;

public class WikidataEntitySerializer {

	public static final String SET_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	public final static String CONTEXT = "http://www.europeana.eu/schemas/context/collection.jsonld";
	ObjectMapper mapper = new ObjectMapper(); 
		
	public WikidataEntitySerializer() {
		SimpleDateFormat df = new SimpleDateFormat(SET_DATE_FORMAT);
		mapper.setDateFormat(df);
	}
	
	/**
	 * This method provides full serialization of a WikidataEntity
	 * @param wikidataEntity
	 * @return json representation of WikidataEntity
	 * @throws IOException
	 */
	
	public String serialize(WikidataEntity wikidataEntity) throws IOException {		
		mapper.registerModule(new JsonldModule(() -> mapper.createObjectNode())); 
		JsonldResourceBuilder<WikidataEntity> jsonResourceBuilder = JsonldResource.Builder.create();
		jsonResourceBuilder.context(CONTEXT);
		String jsonString = mapper.writer().writeValueAsString(jsonResourceBuilder.build(wikidataEntity));
		return jsonString;
	}

}
