package eu.europeana.enrichment.ner.service.impl;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import eu.europeana.enrichment.ner.enumeration.NERDBpediaClassification;
import eu.europeana.enrichment.ner.exception.NERAnnotateException;
import eu.europeana.enrichment.ner.service.NERService;

public class NERDBpediaSpotlightServiceImpl implements NERService{

	private String baseUrl;
	
	/*
	 * DBpedia spotlight JSON response keys 
	 */
	private static final String nameKey = "@name";
	private static final String finalScoreKey = "@finalScore";
	private static final String labelKey = "@label";
	private static final String typeKey = "@types";
	private static final String resourceKey = "resource";
	private static final String surfaceFormKey = "surfaceForm";
	private static final String annotationKey = "annotation";
	private static final String offsetKey = "@offset";
	
	public NERDBpediaSpotlightServiceImpl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	@Override
	public TreeMap<String, List<List<String>>> identifyNER(String text) throws NERAnnotateException {
		
		TreeMap<String, List<List<String>>> map = readJSON(createRequest(text));
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
	private TreeMap<String, List<List<String>>> readJSON(String jsonString){
		TreeMap<String, List<List<String>>> map = new TreeMap<String, List<List<String>>>();
		JSONObject responseJson = new JSONObject(jsonString);
		//TODO: exception handling 
		JSONObject annotationObject = responseJson.getJSONObject(annotationKey);
		
		if(annotationObject.isNull(surfaceFormKey))
			return map;
		
		Object findingsObj = annotationObject.get(surfaceFormKey);
		JSONArray findings = new JSONArray();
		if(findingsObj instanceof JSONArray)
			findings = (JSONArray) findingsObj;
		else {
			findings.put((JSONObject) findingsObj);
		}
		
		processFindings(findings, map);

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
	private void processFindings(JSONArray findings, TreeMap<String, List<List<String>>> map){
		for (int index = 0; index < findings.length(); index++) {
			JSONObject entity = findings.getJSONObject(index);
			String entityName = entity.getString(nameKey);
			int entityOffset = entity.getInt(offsetKey);
			
			JSONObject resourceObject = entity.getJSONObject(resourceKey);
			String entityTypes = resourceObject.getString(typeKey);
			String entityLabel = resourceObject.getString(labelKey);
			Float finalScore = resourceObject.getFloat(finalScoreKey);
			
			String correctType = NERDBpediaClassification.MISC.toString();
			//TODO:check if type contains more then one type (like person and location for one entity)
			boolean classificationFound = false;
			if(NERDBpediaClassification.isAgent(entityTypes)) {
				classificationFound = true;
				setEntityToMap(NERDBpediaClassification.AGENT.toString(), entityName, entityOffset, map);
			}
			if(NERDBpediaClassification.isPlace(entityTypes)) {
				classificationFound = true;
				setEntityToMap(NERDBpediaClassification.PLACE.toString(), entityName, entityOffset, map);
			}
			if(NERDBpediaClassification.isOrganization(entityTypes)) {
				classificationFound = true;
				setEntityToMap(NERDBpediaClassification.ORGANIZATION.toString(), entityName, entityOffset, map);
			}
			if(!classificationFound) {
				setEntityToMap(NERDBpediaClassification.MISC.toString(), entityName, entityOffset, map);
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
	private void setEntityToMap(String entityType, String entityName, int entityOffset, TreeMap<String, List<List<String>>> map) {
		
		List<String> wordWithPosition = new ArrayList<String>();
		wordWithPosition.add(entityName);
		wordWithPosition.add(String.valueOf(entityOffset));
		
		if(map.containsKey(entityType)) {
			map.get(entityType).add(wordWithPosition);
		}
		else {
			List<List<String>> temp = new ArrayList<List<String>>();					
			temp.add(wordWithPosition);
			map.put(entityType, temp);

		}
	}
	
	/*
	 * This method creates the DBpedia spotlight query and extracts the response body for 
	 * further steps
	 * 
	 * @param text					is the translated text which is send to the DBpedia spotlight 
	 * 								for named entity recognition and classification
	 * @return						response body which should be a JSON or empty string
	 */
	private String createRequest(String text) {
		try {
			CloseableHttpClient httpClient = HttpClients.createDefault();
			
			URIBuilder builder = new URIBuilder(baseUrl);
			//builder.setParameter("text", text);
			
			HttpPost request = new HttpPost(baseUrl);
			String requestString = String.format("text=%s", URLEncoder.encode(text, "UTF-8"));
			StringEntity params = new StringEntity(requestString, "UTF-8");
			request.addHeader("Accept", "application/json");
			request.addHeader("Content-Type", "application/x-www-form-urlencoded");
			request.setEntity(params);
			HttpResponse result = httpClient.execute(request);
			String responeString = EntityUtils.toString(result.getEntity(), "UTF-8");

			//System.out.println("(DBpedia spotlight) Http reponse: " + responeString);
			return responeString;

		} catch (Exception ex) {
			System.err.println(ex.getMessage());
			return "";
		}
	}

}
