package eu.europeana.enrichment.ner.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.TreeMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.europeana.enrichment.common.commons.AppConfigConstants;
import eu.europeana.enrichment.common.commons.EnrichmentConfiguration;
import eu.europeana.enrichment.model.impl.NamedEntityImpl;
import eu.europeana.enrichment.ner.linking.model.StanfordNerRequest;
import eu.europeana.enrichment.ner.service.NERService;

@Service(AppConfigConstants.BEAN_ENRICHMENT_NER_STANFORD_SERVICE)
public class NERStanfordServiceImpl implements NERService{

	private String endpoint;
	
	Logger logger = LogManager.getLogger(getClass());
	
	/*
	 * This class constructor loads a model for the Stanford named
	 * entity recognition and classification
	 */
	@Autowired
	public NERStanfordServiceImpl(EnrichmentConfiguration enrichmentConfiguration) {
		endpoint = enrichmentConfiguration.getNerStanfordUrl();
	}
		
	@Override
	public TreeMap<String, List<NamedEntityImpl>> identifyNER(String text) throws IOException {
		TreeMap<String, List<NamedEntityImpl>> result = null;
		String serializedRequest = null;
		try {
			serializedRequest = new ObjectMapper().writeValueAsString(new StanfordNerRequest(text));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			logger.log(Level.ERROR, "Exception during the staford NER request serialization.", e);
			throw e;
		}
		if(serializedRequest == null)
			return null;
		String response = createRequest(serializedRequest);
		if(response==null) return null;
		
		ObjectMapper mapper = new ObjectMapper();
		TypeReference<TreeMap<String, List<NamedEntityImpl>>> typeRef = new TypeReference<TreeMap<String, List<NamedEntityImpl>>>() {};
		try {
			logger.debug("\n The response from the StanfordNER service is: " + response + "\n");
			result = mapper.readValue(response, typeRef);
		} catch (IOException e) {
			logger.log(Level.ERROR, "Exception during the staford NER response deserialization.", e);
			throw e;
		}
		
		return result;
	}
	
	private String createRequest(String requestJson) {
		try {
			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpPost request = new HttpPost(endpoint);
			StringEntity body = new StringEntity(requestJson, ContentType.APPLICATION_JSON);
			request.setEntity(body);
			HttpResponse result = httpClient.execute(request);
			String responeString = EntityUtils.toString(result.getEntity(), "UTF-8");
			return responeString;

		} catch (Exception ex) {
			System.err.println(ex.getMessage());
			return null;
		}
	}

	@Override
	public String getEnpoint() {
		return endpoint;
	}

	@Override
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
	
}
