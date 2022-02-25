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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.europeana.enrichment.common.commons.AppConfigConstants;
import eu.europeana.enrichment.common.commons.EnrichmentConfiguration;
import eu.europeana.enrichment.model.NamedEntity;
import eu.europeana.enrichment.model.PositionEntity;
import eu.europeana.enrichment.model.impl.NamedEntityImpl;
import eu.europeana.enrichment.model.impl.PositionEntityImpl;
import eu.europeana.enrichment.ner.enumeration.NERDBpediaClassification;
import eu.europeana.enrichment.ner.exception.NERAnnotateException;
import eu.europeana.enrichment.ner.service.NERService;
@Service(AppConfigConstants.BEAN_ENRICHMENT_NER_DBPEDIA_SPOTLIGHT_SERVICE)
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
		
		int entityOffset = -1;
		String entityName = null;
		String dbpediaUrl = null;

		try {
			entityOffset = entity.getInt(offsetKey);
			entityName = entity.getString(surfaceFormKey);
			dbpediaUrl = entity.getString(uriKey);
		}
		catch (JSONException e) {
			System.out.println("An exception: " + e.toString() + "is thrown during the json processing of the dbpedia NER.");
			e.printStackTrace();
		}
		
		//the name must exist
		if(entityName==null) {
			return;
		}
				
		List<NamedEntity> tmp;
		if(map.containsKey(entityType))
			tmp = map.get(entityType);
		else {
			tmp = new ArrayList<>();
			map.put(entityType, tmp);
		}
		
		NamedEntity alreadyExistNamedEntity = null;
		for(int index = 0; index < tmp.size(); index++) {
			if(entityName.equals(tmp.get(index).getLabel())) {
				alreadyExistNamedEntity = tmp.get(index);
				break;
			}
		}
		
		if(alreadyExistNamedEntity == null) {
			NamedEntity newNamedEntity = createNewNamedEntity(entityName, entityType, entityOffset, dbpediaUrl);
			tmp.add(newNamedEntity);
		}
		else {
			//update the dbpedia ids
			if(dbpediaUrl!=null) {
			    if(alreadyExistNamedEntity.getDBpediaIds()==null) {
					List<String> dbpediaIds = new ArrayList<String>();
					dbpediaIds.add(dbpediaUrl);
					alreadyExistNamedEntity.setDBpediaIds(dbpediaIds);
			    }
				else if(!alreadyExistNamedEntity.getDBpediaIds().contains(dbpediaUrl)) {
					alreadyExistNamedEntity.addDBpediaId(dbpediaUrl);
				}
			}
			//update the offset(position) of the entity
			if(entityOffset!=-1) {
				if(alreadyExistNamedEntity.getPositionEntities()==null) {
					List<Integer> offsetTranslatedText = new ArrayList<Integer>();
					offsetTranslatedText.add(Integer.valueOf(entityOffset));
					PositionEntity positionEntity = new PositionEntityImpl();
					// default: Offset position will be added to the translated
					positionEntity.setOffsetsTranslatedText(offsetTranslatedText);
					List<PositionEntity> positionEntities = new ArrayList<PositionEntity>();
					positionEntities.add(positionEntity);
					alreadyExistNamedEntity.setPositionEntities(positionEntities);
				}
				else if(!alreadyExistNamedEntity.getPositionEntities().get(0).getOffsetsTranslatedText().contains(entityOffset)){
					alreadyExistNamedEntity.getPositionEntities().get(0).addOfssetsTranslatedText(entityOffset);
				}
			}
		}
	}
	
	private NamedEntity createNewNamedEntity (String label, String type, int offset, String dbpediaUrl) {
		NamedEntity namedEntity = new NamedEntityImpl(label);
		
		namedEntity.setType(type);
		
		if(offset!=-1) {
			List<Integer> offsetTranslatedText = new ArrayList<Integer>();
			offsetTranslatedText.add(Integer.valueOf(offset));
			PositionEntity positionEntity = new PositionEntityImpl();
			// default: Offset position will be added to the translated
			positionEntity.setOffsetsTranslatedText(offsetTranslatedText);
			List<PositionEntity> positionEntities = new ArrayList<PositionEntity>();
			positionEntities.add(positionEntity);
			namedEntity.setPositionEntities(positionEntities);
		}

		if(dbpediaUrl!=null) {
			List<String> dbpediaIds = new ArrayList<String>();
			dbpediaIds.add(dbpediaUrl);
			namedEntity.setDBpediaIds(dbpediaIds);
		}

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
			logger.error("Exception raised during creating a DBPedia Spotlight query!" + ex.getMessage());
			return "";
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
