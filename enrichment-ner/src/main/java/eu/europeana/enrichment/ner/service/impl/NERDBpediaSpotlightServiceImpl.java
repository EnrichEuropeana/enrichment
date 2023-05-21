package eu.europeana.enrichment.ner.service.impl;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.europeana.enrichment.common.commons.EnrichmentConfiguration;
import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.definitions.model.impl.NamedEntityImpl;
import eu.europeana.enrichment.definitions.model.impl.PositionEntityImpl;
import eu.europeana.enrichment.definitions.model.vocabulary.NerTools;
import eu.europeana.enrichment.ner.enumeration.NERDBpediaClassification;
import eu.europeana.enrichment.ner.service.NERService;
@Service(EnrichmentConstants.BEAN_ENRICHMENT_NER_DBPEDIA_SPOTLIGHT_SERVICE)
public class NERDBpediaSpotlightServiceImpl implements NERService{

	private String baseUrl;
	private final Logger logger = LogManager.getLogger(getClass());
	
	/*
	 * DBpedia spotlight JSON response keys 
	 */
	private static final String nameKey = "@name";
	private static final String finalScoreKey = "@finalScore";
	private static final String labelKey = "@label";
	private static final String typeKey = "@types";
	private static final String resourceKey = "Resources";
	private static final String surfaceFormKey = "@surfaceForm";
	private static final String annotationKey = "annotation";
	private static final String offsetKey = "@offset";
	private static final String uriKey = "@URI";
	
	@Autowired
	public NERDBpediaSpotlightServiceImpl(EnrichmentConfiguration enrichmentConfiguration) {
		this.baseUrl = enrichmentConfiguration.getNerDbpediaBaseUrl();
	}

	@Override
	public TreeMap<String, List<NamedEntityImpl>> identifyNER(String text) throws Exception {
		String response = createRequest(text);
		if (StringUtils.isBlank(response)) return null;
		
		TreeMap<String, List<NamedEntityImpl>> map = readJSON(response);
		return map;
	}
	
	/*
	 * This method parses the DBpedia spotlight JSON response into a TreeMap
	 * which is separated by classification types
	 * 
	 * @param jsonString			is the JSON string response of the previous request
	 * @return						a TreeMap of named entities which are separated
	 * 								based on their classification type
	 */
	private TreeMap<String, List<NamedEntityImpl>> readJSON(String jsonString) {
		JSONObject responseJson = new JSONObject(jsonString);

		//TODO: exception handling 
		if(!responseJson.has(resourceKey))
			return null;		
		JSONArray resourcesList = responseJson.getJSONArray(resourceKey);
		
		TreeMap<String, List<NamedEntityImpl>> map = new TreeMap<>();
		processFindings(resourcesList, map);

		return map;
	}
	
	/*
	 * This method process the specific JSON area of the response into a TreeMap
	 * separated by classification type (e.g. agent, place, ..)
	 * 
	 * @param findings				is the JSON response of the DBpedia spotlight request
	 * @param map					is the TreeMap which could already have some named entities
	 * return						the map parameter will be extended by all findings of
	 * 								the JSON response
	 */
	private void processFindings(JSONArray findings, TreeMap<String, List<NamedEntityImpl>> map){
		for (int index = 0; index < findings.length(); index++) {
			JSONObject entity = findings.getJSONObject(index);
			String entityTypes = entity.getString(typeKey);
			
			//TODO:check if type contains more then one type (like person and location for one entity)
			boolean classificationFound = false;
			if(NERDBpediaClassification.isAgent(entityTypes)) {
				classificationFound = true;
				setEntityToMap(NERDBpediaClassification.AGENT.toString(), map, entity);
			}
			if(NERDBpediaClassification.isPlace(entityTypes)) {
				classificationFound = true;
				setEntityToMap(NERDBpediaClassification.PLACE.toString(), map, entity);
			}
			if(NERDBpediaClassification.isOrganization(entityTypes)) {
				classificationFound = true;
				setEntityToMap(NERDBpediaClassification.ORGANIZATION.toString(), map, entity);
			}
			if(!classificationFound) {
				setEntityToMap(NERDBpediaClassification.MISC.toString(), map, entity);
			}
		}
	}
	
	/*
	 * This method adds new DBpedia spotlight named entities into the TreeMap which 
	 * is separated by classification type (e.g. agent, place, ..)
	 * 
	 * @param entityType			is the classification type of the new DBpedia spotlight named entity
	 * 								(e.g. place for Vienna)
	 * @param entityName			represents the label of the new DBpedia spotlight named entity
	 * 
	 * @param entityOffset			represents the offset (starting position in the text) of the new DBpedia spotlight named entity 
	 * 
	 * @param map					is a TreeMap which already contains some named entities and this
	 * 								TreeMap will be extended by the new DBpedia spotlight named entity
	 * return						the map parameter will be changed through this function
	 */
	private void setEntityToMap(String entityType, TreeMap<String, List<NamedEntityImpl>> map, JSONObject entity) {
		
		int entityOffset = -1;
		String entityName = null;
		String dbpediaUrl = null;

		try {
			entityOffset = entity.getInt(offsetKey);
			entityName = entity.getString(surfaceFormKey);
			dbpediaUrl = entity.getString(uriKey);
		}
		catch (JSONException e) {
			logger.log(Level.ERROR, "An Exception during the the json rocessing of the dbpedia NER.", e);
		}
		
		if(entityName==null || entityOffset==-1 || dbpediaUrl==null) {
			return;
		}
				
		List<NamedEntityImpl> tmp;
		if(map.containsKey(entityType))
			tmp = map.get(entityType);
		else {
			tmp = new ArrayList<>();
			map.put(entityType, tmp);
		}
		
		NamedEntityImpl alreadyExistNamedEntityImpl = null;
		for(int index = 0; index < tmp.size(); index++) {
			if(existingNamedEntity(tmp.get(index), dbpediaUrl)) {
				alreadyExistNamedEntityImpl = tmp.get(index);
				break;
			}
		}
		
		if(alreadyExistNamedEntityImpl == null) {
			NamedEntityImpl newNamedEntityImpl = createNewNamedEntityImpl(entityName, entityType, entityOffset, dbpediaUrl);
			tmp.add(newNamedEntityImpl);
		}
		else {
			//update the offset(position) of the entity
			if(! alreadyExistNamedEntityImpl.getPositionEntity().getOffsetsTranslatedText().containsKey(entityOffset)) {
				alreadyExistNamedEntityImpl.getPositionEntity().getOffsetsTranslatedText().put(entityOffset, NerTools.Dbpedia.getStringValue());
			}
		}
	}
	
	/*
	 * Please note that the rules for checking the existence of the same named entity for the dbpedia
	 * NER tool must be consistent through the whole application. 
	 */
	private boolean existingNamedEntity(NamedEntityImpl neToCheckAgainst, String dbpediaUrl) {
		if(neToCheckAgainst.getDBpediaId().equals(dbpediaUrl)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	private NamedEntityImpl createNewNamedEntityImpl (String label, String type, int offset, String dbpediaUrl) {
		NamedEntityImpl namedEntity = new NamedEntityImpl(label);
		
		namedEntity.setType(type);

		Map<Integer, String> offsetTranslatedText = new HashMap<>();
		offsetTranslatedText.put(offset, NerTools.Dbpedia.getStringValue());
		PositionEntityImpl positionEntity = new PositionEntityImpl();
		// default: Offset position will be added to the translated
		positionEntity.setOffsetsTranslatedText(offsetTranslatedText);
		namedEntity.setPositionEntity(positionEntity);
		namedEntity.setDBpediaId(dbpediaUrl);

		return namedEntity;
	}
	
	/*
	 * This method creates the DBpedia spotlight query and extracts the response body for 
	 * further steps
	 * 
	 * @param text					is the translated text which is send to the DBpedia spotlight 
	 * 								for named entity recognition and classification
	 * @return						response body which should be a JSON or empty string
	 */
	private String createRequest(String text) throws Exception {
		try {
			CloseableHttpClient httpClient = HttpClients.createDefault();
			
			HttpPost request = new HttpPost(baseUrl);
			String requestString = String.format("text=%s", URLEncoder.encode(text, "UTF-8"));
			StringEntity params = new StringEntity(requestString, "UTF-8");
			request.addHeader("Accept", "application/json");
			request.addHeader("Content-Type", "application/x-www-form-urlencoded");
			request.setEntity(params);
			HttpResponse result = httpClient.execute(request);
			String responeString = EntityUtils.toString(result.getEntity(), "UTF-8");
			return responeString;

		} catch (Exception ex) {
			logger.log(Level.ERROR, "Exception raised during creating a DBPedia Spotlight query!" + ex.getMessage(), ex);
			throw ex;
		}
	}

	@Override
	public String getEnpoint() {
		return baseUrl;
	}

	@Override
	public void setEndpoint(String endpoint) {
		this.baseUrl = endpoint;
	}

}
