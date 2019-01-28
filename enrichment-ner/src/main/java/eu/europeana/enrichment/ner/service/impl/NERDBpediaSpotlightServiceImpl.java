package eu.europeana.enrichment.ner.service.impl;

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

	// docker command: docker run --name spotlight -p 2222:80 -it dbpedia/spotlight-english spotlight.sh
	
	//TODO: config file  ("http://api.dbpedia-spotlight.org/en")
	private static final String baseUrl = "http://localhost:2222/rest/candidates";
	
	// Json response keys
	private static final String nameKey = "@name";
	private static final String finalScoreKey = "@finalScore";
	private static final String labelKey = "@label";
	private static final String typeKey = "@types";
	private static final String resourceKey = "resource";
	private static final String surfaceFormKey = "surfaceForm";
	private static final String annotationKey = "annotation";
	private static final String offsetKey = "@offset";
	

	@Override
	public TreeMap<String, TreeSet<String>> identifyNER(String text) throws NERAnnotateException {
		
		TreeMap<String, TreeSet<String>> map = readJSON(createRequest(text));
		return map;
	}
	
	private TreeMap<String, TreeSet<String>> readJSON(String jsonString){
		TreeMap<String, TreeSet<String>> map = new TreeMap<String, TreeSet<String>>();
		JSONObject responseJson = new JSONObject(jsonString);
		//TODO: exception handling 
		JSONObject annotationObject = responseJson.getJSONObject(annotationKey);
		JSONArray findings = annotationObject.getJSONArray(surfaceFormKey);
		
		processFindings(findings, map);

		return map;
	}
	
	private void processFindings(JSONArray findings, TreeMap<String, TreeSet<String>> map){
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
			if(NERDBpediaClassification.isPerson(entityTypes)) {
				classificationFound = true;
				setEntityToMap(NERDBpediaClassification.PERSON.toString(), entityName, map);
			}
			if(NERDBpediaClassification.isLocation(entityTypes)) {
				classificationFound = true;
				setEntityToMap(NERDBpediaClassification.LOCATION.toString(), entityName, map);
			}
			if(NERDBpediaClassification.isOrganization(entityTypes)) {
				classificationFound = true;
				setEntityToMap(NERDBpediaClassification.ORGANIZATION.toString(), entityName, map);
			}
			if(!classificationFound) {
				setEntityToMap(NERDBpediaClassification.MISC.toString(), entityName, map);
			}
		}
	}
	
	private void setEntityToMap(String entityType, String entityName, TreeMap<String, TreeSet<String>> map) {
		if(map.containsKey(entityType)) {
			map.get(entityType).add(entityName);
		}
		else {
			TreeSet<String> entitySet = new TreeSet<String>();
			entitySet.add(entityName);
			map.put(entityType, entitySet);
		}
	}
	
	private String createRequest(String text) {
		try {
			CloseableHttpClient httpClient = HttpClients.createDefault();
			
			URIBuilder builder = new URIBuilder(baseUrl);
			//builder.setParameter("text", text);
			
			HttpPost request = new HttpPost(baseUrl);
			String requestString = String.format("text=%s", text);
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
