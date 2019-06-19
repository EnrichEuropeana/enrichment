package eu.europeana.enrichment.web.commons;

import java.io.IOException;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.europeana.enrichment.model.impl.NamedEntityAnnotationCollection;
import ioinformarics.oss.jackson.module.jsonld.JsonldModule;
import ioinformarics.oss.jackson.module.jsonld.JsonldResource;
import ioinformarics.oss.jackson.module.jsonld.JsonldResourceBuilder;

public class StoryEntitySerializer {
	public static final String SET_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	public final static String CONTEXT = "http://www.w3.org/ns/anno.jsonld";
	ObjectMapper mapper = new ObjectMapper(); 
		
	public StoryEntitySerializer() {
		SimpleDateFormat df = new SimpleDateFormat(SET_DATE_FORMAT);
		mapper.setDateFormat(df);
	}
	
	/**
	 * This method provides full serialization of a story using the NamedEntityAnnotationCollection class
	 * @param NEACollection parameter is of type NamedEntityAnnotationCollection
	 * @return json representation of NamedEntityAnnotationCollection
	 * @throws IOException
	 */
	
	public String serialize(NamedEntityAnnotationCollection NEACollection) throws IOException {		
		mapper.registerModule(new JsonldModule(() -> mapper.createObjectNode())); 
		JsonldResourceBuilder<NamedEntityAnnotationCollection> jsonResourceBuilder = JsonldResource.Builder.create();
		jsonResourceBuilder.context(CONTEXT);
		String jsonString = mapper.writer().writeValueAsString(jsonResourceBuilder.build(NEACollection));
		return jsonString;
	}
}
