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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import eu.europeana.enrichment.model.NamedEntity;
import eu.europeana.enrichment.ner.linking.model.StanfordNerRequest;
import eu.europeana.enrichment.ner.service.NERService;
import eu.europeana.enrichment.ner.service.model.StanfordNamedEntityImpl;


public class NERStanfordServiceImpl implements NERService{

	private String endpoint;
	
	/*
	 * This class constructor loads a model for the Stanford named
	 * entity recognition and classification
	 */
	public NERStanfordServiceImpl(String url) {
		endpoint = url;
	}
		
	@Override
	public TreeMap<String, List<NamedEntity>> identifyNER(String text) {
		TreeMap<String, List<NamedEntity>> map = null;
		String serializedRequest = null;
		try {
			serializedRequest = new ObjectMapper().writeValueAsString(new StanfordNerRequest(text));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(serializedRequest == null)
			return null;
		String response = createRequest(serializedRequest);
		
		ObjectMapper mapper = new ObjectMapper();
		TypeReference<TreeMap<String, List<StanfordNamedEntityImpl>>> typeRef = new TypeReference<TreeMap<String, List<StanfordNamedEntityImpl>>>() {};
		try {
			map = mapper.readValue(response, typeRef);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
		//return processClassifiedResult(classify);
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
			return "";
		}
	}
	
}
