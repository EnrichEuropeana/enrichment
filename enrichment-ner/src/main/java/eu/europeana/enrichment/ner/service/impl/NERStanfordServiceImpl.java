package eu.europeana.enrichment.ner.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.europeana.enrichment.common.commons.AppConfigConstants;
import eu.europeana.enrichment.common.commons.EnrichmentConfiguration;
import eu.europeana.enrichment.model.NamedEntity;
import eu.europeana.enrichment.ner.linking.model.StanfordNerRequest;
import eu.europeana.enrichment.ner.service.NERService;
import eu.europeana.enrichment.ner.service.model.StanfordNamedEntityImpl;

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
	public TreeMap<String, List<NamedEntity>> identifyNER(String text) throws IOException {
		TreeMap<String, List<StanfordNamedEntityImpl>> map = null;
		String serializedRequest = null;
		try {
			serializedRequest = new ObjectMapper().writeValueAsString(new StanfordNerRequest(text));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
		if(serializedRequest == null)
			return null;
		String response = createRequest(serializedRequest);
		
		ObjectMapper mapper = new ObjectMapper();
		TypeReference<TreeMap<String, List<StanfordNamedEntityImpl>>> typeRef = new TypeReference<TreeMap<String, List<StanfordNamedEntityImpl>>>() {};
		try {
			logger.info("\n The response from the StanfordNER service is: " + response + "\n");
			map = mapper.readValue(response, typeRef);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
		
		/*
		 * convert the response from TreeMap<String, List<StanfordNamedEntityImpl>> to TreeMap<String, List<NamedEntity>>
		 * TODO: improve the code so that we do not need such a conversion
		 */
		TreeMap<String, List<NamedEntity>> result = null;
		if (map.size()>0) result =  new TreeMap<String, List<NamedEntity>>();
		else return null;
		for(Map.Entry<String, List<StanfordNamedEntityImpl>> entry : map.entrySet()) {
			List<NamedEntity> resultValue =  new ArrayList<NamedEntity>();
			for(StanfordNamedEntityImpl entryValueElem : entry.getValue()) {
				resultValue.add(entryValueElem);
			}
			result.put(entry.getKey(), resultValue);
		}
		
		return result;
		
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

	@Override
	public String getEnpoint() {
		return endpoint;
	}

	@Override
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
	
}
