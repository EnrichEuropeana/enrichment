package eu.europeana.enrichment.ner.service.impl;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import eu.europeana.enrichment.model.NamedEntity;
import eu.europeana.enrichment.model.PositionEntity;
import eu.europeana.enrichment.model.impl.NamedEntityImpl;
import eu.europeana.enrichment.model.impl.PositionEntityImpl;
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
	private static final String resourceKey = "Resources";
	private static final String surfaceFormKey = "@surfaceForm";
	private static final String annotationKey = "annotation";
	private static final String offsetKey = "@offset";
	private static final String uriKey = "@URI";
	
	public NERDBpediaSpotlightServiceImpl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	@Override
	public TreeMap<String, List<NamedEntity>> identifyNER(String text) throws NERAnnotateException {
		
		TreeMap<String, List<NamedEntity>> map = readJSON(createRequest(text));
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
	private TreeMap<String, List<NamedEntity>> readJSON(String jsonString){
		TreeMap<String, List<NamedEntity>> map = new TreeMap<>();
		JSONObject responseJson = new JSONObject(jsonString);
		//TODO: exception handling 
		if(!responseJson.has(resourceKey))
			return null;
		
		JSONArray resourcesList = responseJson.getJSONArray(resourceKey);

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
	private void processFindings(JSONArray findings, TreeMap<String, List<NamedEntity>> map){
		for (int index = 0; index < findings.length(); index++) {
			JSONObject entity = findings.getJSONObject(index);
			String entityTypes = entity.getString(typeKey);
			
			
			String correctType = NERDBpediaClassification.MISC.toString();
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
	private void setEntityToMap(String entityType, TreeMap<String, List<NamedEntity>> map, JSONObject entity) {
		int entityOffset = entity.getInt(offsetKey);
		String entityName = entity.getString(surfaceFormKey);
		String dbpediaUrl = entity.getString(uriKey);
		
		NamedEntity namedEntity = new NamedEntityImpl(entityName);
		namedEntity.setType(entityType);
		PositionEntity positionEntity = new PositionEntityImpl();
		// default: Offset position will be added to the translated 
		positionEntity.addOfssetsTranslatedText(entityOffset);
		namedEntity.addPositionEntity(positionEntity);
		namedEntity.addDBpediaId(dbpediaUrl);
		
		List<NamedEntity> tmp;
		if(map.containsKey(entityType))
			tmp = map.get(entityType);
		else {
			tmp = new ArrayList<>();
			map.put(entityType, tmp);
		}
		
		NamedEntity alreadyExistNamedEntity = null;
		for(int index = 0; index < tmp.size(); index++) {
			if(tmp.get(index).getKey().equals(namedEntity.getKey())) {
				alreadyExistNamedEntity = tmp.get(index);
				break;
			}
		}
		if(alreadyExistNamedEntity == null)
			tmp.add(namedEntity);
		else {
			for(int dbpediaIndex = 0; dbpediaIndex < namedEntity.getDBpediaIds().size(); dbpediaIndex++) {
				int tmpIndex = dbpediaIndex;
				boolean found = alreadyExistNamedEntity.getDBpediaIds().stream().anyMatch(x -> x.equals(namedEntity.getDBpediaIds().get(tmpIndex)));
				if(!found){
					alreadyExistNamedEntity.addDBpediaId(namedEntity.getDBpediaIds().get(tmpIndex));
				}
			}
			alreadyExistNamedEntity.getPositionEntities().get(0).addOfssetsTranslatedText(entityOffset);
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
