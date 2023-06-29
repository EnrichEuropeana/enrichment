package eu.europeana.enrichment.definitions.model.utils;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import eu.europeana.enrichment.definitions.model.impl.TermImpl;


public class TopicTermsDeserializer extends JsonDeserializer<List<TermImpl>>{

	@Override
	public List<TermImpl> deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		
		return p.readValueAs(TYPE_REF);
	}
	
	private static final TypeReference<List<TermImpl>> TYPE_REF = 
            new TypeReference<List<TermImpl>>() {};

}
