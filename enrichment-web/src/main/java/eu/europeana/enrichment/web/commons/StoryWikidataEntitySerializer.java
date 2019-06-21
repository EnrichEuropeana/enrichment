package eu.europeana.enrichment.web.commons;

import java.io.IOException;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.europeana.enrichment.model.impl.NamedEntityAnnotationCollection;
import eu.europeana.enrichment.model.impl.NamedEntityAnnotationImpl;
import ioinformarics.oss.jackson.module.jsonld.JsonldModule;
import ioinformarics.oss.jackson.module.jsonld.JsonldResource;
import ioinformarics.oss.jackson.module.jsonld.JsonldResourceBuilder;

public class StoryWikidataEntitySerializer {
	public static final String SET_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	public final static String CONTEXT = "http://www.w3.org/ns/anno.jsonld";
	ObjectMapper mapper = new ObjectMapper(); 
		
	public StoryWikidataEntitySerializer() {
		SimpleDateFormat df = new SimpleDateFormat(SET_DATE_FORMAT);
		mapper.setDateFormat(df);
	}
	
	/**
	 * This method provides full serialization of all NamedEntities of a story using the NamedEntityAnnotationCollection class
	 * @param NEACollection parameter is of type NamedEntityAnnotationCollection
	 * @return json representation of NamedEntityAnnotationCollection
	 * @throws IOException
	 */
	
	public String serializeCollection(NamedEntityAnnotationCollection NEACollection) throws IOException {		
		mapper.registerModule(new JsonldModule(() -> mapper.createObjectNode())); 
		JsonldResourceBuilder<NamedEntityAnnotationCollection> jsonResourceBuilder = JsonldResource.Builder.create();
		jsonResourceBuilder.context(CONTEXT);
		String jsonString = mapper.writer().writeValueAsString(jsonResourceBuilder.build(NEACollection));
		return jsonString;
	}
	
	/**
	 * This method provides full serialization of a single NamedEntities of a story using the NamedEntityAnnotation class
	 * @param namedEntityAnno parameter is of type NamedEntityAnnotation
	 * @return json representation of NamedEntityAnnotation
	 * @throws IOException
	 */
	
	public String serialize(NamedEntityAnnotationImpl namedEntityAnno) throws IOException {		
		mapper.registerModule(new JsonldModule(() -> mapper.createObjectNode())); 
		JsonldResourceBuilder<NamedEntityAnnotationImpl> jsonResourceBuilder = JsonldResource.Builder.create();
		jsonResourceBuilder.context(CONTEXT);
		String jsonString = mapper.writer().writeValueAsString(jsonResourceBuilder.build(namedEntityAnno));
		return jsonString;
	}
}
