package eu.europeana.enrichment.web.commons;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.europeana.enrichment.model.TermEntity;


public class TopicTermsDeserializer extends JsonDeserializer<List<TermEntity>>{

	@Override
	public List<TermEntity> deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		
		return p.readValueAs(TYPE_REF);
	}
	
	private static final TypeReference<List<TermEntity>> TYPE_REF = 
            new TypeReference<List<TermEntity>>() {};

}
