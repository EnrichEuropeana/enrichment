package eu.europeana.enrichment.solr.commons;

import java.io.IOException;
import java.text.SimpleDateFormat;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.europeana.enrichment.common.commons.AppConfigConstants;
import eu.europeana.enrichment.model.WikidataEntity;
import eu.europeana.enrichment.model.impl.NamedEntitySolrCollection;
import ioinformarics.oss.jackson.module.jsonld.JsonldModule;
import ioinformarics.oss.jackson.module.jsonld.JsonldResource;
import ioinformarics.oss.jackson.module.jsonld.JsonldResourceBuilder;

@Component(AppConfigConstants.BEAN_ENRICHMENT_JACKSON_SERIALIZER)
public class JacksonSerializer {

	public static final String SET_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	public final static String CONTEXT = "http://www.europeana.eu/schemas/context/entity.jsonld";
	ObjectMapper mapper = new ObjectMapper(); 
		
	public JacksonSerializer() {
		SimpleDateFormat df = new SimpleDateFormat(SET_DATE_FORMAT);
		mapper.setDateFormat(df);
	}
	
	/**
	 * This method provides full serialization of a WikidataEntity
	 * @param wikidataEntity
	 * @return json representation of WikidataEntity
	 * @throws IOException
	 */
	
	public String serializeWikidataEntity(WikidataEntity wikidataEntity) throws IOException {		
		mapper.registerModule(new JsonldModule(() -> mapper.createObjectNode())); 
		JsonldResourceBuilder<WikidataEntity> jsonResourceBuilder = JsonldResource.Builder.create();
		jsonResourceBuilder.context(CONTEXT);
		String jsonString = mapper.writer().writeValueAsString(jsonResourceBuilder.build(wikidataEntity));
		return jsonString;
	}
	
	/**
	 * This method provides full serialization of a collection of NamedEntity from Solr 
	 * @param NamedEntitySolrCollection
	 * @return json representation of NamedEntitySolrCollection
	 * @throws IOException
	 */
	
	public String serializeNamedEntitySolrCollection(NamedEntitySolrCollection namedEntityCollection) throws IOException {		
		mapper.registerModule(new JsonldModule(() -> mapper.createObjectNode())); 
		JsonldResourceBuilder<NamedEntitySolrCollection> jsonResourceBuilder = JsonldResource.Builder.create();
		jsonResourceBuilder.context(CONTEXT);
		String jsonString = mapper.writer().writeValueAsString(jsonResourceBuilder.build(namedEntityCollection));
		return jsonString;
	}


}
