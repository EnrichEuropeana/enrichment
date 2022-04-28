package eu.europeana.enrichment.ner.linking;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.common.commons.EnrichmentConfiguration;
import eu.europeana.enrichment.common.commons.HelperFunctions;
import eu.europeana.enrichment.model.WikidataAgent;
import eu.europeana.enrichment.model.WikidataEntity;
import eu.europeana.enrichment.model.WikidataPlace;
import eu.europeana.enrichment.model.impl.WikidataAgentImpl;
import eu.europeana.enrichment.model.impl.WikidataPlaceImpl;
import eu.europeana.enrichment.model.vocabulary.EntityTypes;

//import net.arnx.jsonic.JSONException;
@Service(EnrichmentConstants.BEAN_ENRICHMENT_WIKIDATA_SERVICE)
public class WikidataServiceImpl implements WikidataService {
	
	Logger logger = LogManager.getLogger(getClass());

	private static final String baseUrlSparql = "https://query.wikidata.org/bigdata/namespace/wdq/sparql"; // "https://query.wikidata.org/sparql";
//	private static final String baseUrlWikidataSearch = "https://wikidata.org/w/api.php";
	private static final String baseUrlWikidataSearch = "https://www.wikidata.org/w/index.php";
	private static final int KEEP_FIRST_N_WIKIDATA_IDS = 10;
	/*
	 * Defining Wikidata sparql query construct for Geonames ID and Label search
	 */
	private String geonamesIdQueryString = "SELECT ?item WHERE { ?item wdt:P1566 \"%s\" . "
			+ "SERVICE wikibase:label { bd:serviceParam wikibase:language \"en\"}}";
	
	private String labelQueryString = "SELECT ?item WHERE { ?item (rdfs:label) \"%s\"@%s . "
			+ "SERVICE wikibase:label { bd:serviceParam wikibase:language \"en\"}}"
			+ " GROUP BY ?item";
	
	private String labelAltLabelQueryString = "SELECT ?item WHERE { ?item (rdfs:label|skos:altLabel) \"%s\"@%s . "
			+ "SERVICE wikibase:label { bd:serviceParam wikibase:language \"en\"}}"
			+ " GROUP BY ?item";

	/*
	 * Wikidata place search
	 * Geographical object: Q618123
	 * Geographic location: Q2221906
	 * Location: Q17334923
	 */
	private String placeLabelQueryString = "SELECT ?item ?description ?type WHERE {\r\n"
			+ "   ?item rdfs:label \"%s\"@%s; \r\n p:P31/ps:P31/wdt:P279* ?type; \r\n"
			+ "      schema:description ?description.\r\n"
			+ "  FILTER(?type in (wd:Q82794,wd:Q2075301,wd:Q7444568,wd:Q18635222,wd:Q25345958,wd:Q56596860,wd:Q207326,wd:Q7444568)) \r\n"
			+ "  FILTER((LANG(?description)) = \"en\") }"
			+ " GROUP BY ?item ?description ?type";

	/*
	 * Defines multilingual Wikidata query including alternative label
	 */
	private String placeLabelAltLabelQueryString = "SELECT ?item ?description ?type WHERE {\r\n"
			+ "   ?item (rdfs:label|skos:altLabel) \"%s\"@%s; \r\n p:P31/ps:P31/wdt:P279* ?type; \r\n"
			+ "      schema:description ?description.\r\n"
			+ "  FILTER(?type in (wd:Q82794,wd:Q2075301,wd:Q7444568,wd:Q18635222,wd:Q25345958,wd:Q56596860,wd:Q207326,wd:Q7444568)) \r\n"
			+ "  FILTER((LANG(?description)) = \"en\") }"
			+ " GROUP BY ?item ?description ?type";
	
//	private String placeLabelAltLabelQueryString = "SELECT distinct ?item ?type ?rank WHERE {\r\n"
//			+ "  hint:Query hint:optimizer \"None\".\r\n" 
//			+ "  values ?labels {\"%s\"@%s \"%s\"@%s}\r\n"
//			+ "  ?item (rdfs:label|skos:altLabel) ?labels;\r\n"
//			+ "  p:P31/ps:P31/wdt:P279* ?type. #;\r\n"
//			+ "  FILTER(?type in (wd:Q82794,wd:Q2075301,wd:Q7444568,wd:Q18635222,wd:Q25345958,wd:Q56596860,wd:Q207326,wd:Q7444568))}";
	/*
	 * Wikidata agent search query
	 */
	private String agentlabelQueryString = "SELECT ?item ?description WHERE {\r\n" 
			+ "  ?item wdt:P31 wd:Q5;\r\n rdfs:label \"%s\"@%s;\r\n" 
			+ "      schema:description ?description.\r\n" 
			+ "  FILTER((LANG(?description)) = \"en\") }"
			+ " GROUP BY ?item ?description";
	
	private String agentlabelAltLabelQueryString = "SELECT ?item ?description WHERE {\r\n" 
			+ "  ?item wdt:P31 wd:Q5;\r\n (rdfs:label|skos:altLabel) \"%s\"@%s;\r\n" 
			+ "      schema:description ?description.\r\n" 
			+ "  FILTER((LANG(?description)) = \"en\") }"
			+ " GROUP BY ?item ?description";
	
	/*
	 * Wikidata keys for the response JSON
	 */
	private final String wikidataResultKey = "results";
	private final String wikidataBindingsKey = "bindings";
	private final String wikidataItemKey = "item";
	private final String wikidataDescriptionKey = "description";
	private final String wikidataValueKey = "value";
	
	
	private String wikidataDirectory;
	
	@Autowired
	public WikidataServiceImpl (EnrichmentConfiguration enrichmentConfiguration)
	{
		wikidataDirectory = enrichmentConfiguration.getEnrichWikidataDirectory();
	}
	
	
	public Logger getLogger() {
		return logger;
	}

	@Override
	public List<String> getWikidataId(String geonameId) {
		String query = String.format(geonamesIdQueryString, geonameId);
		return processWikidataSparqlResponse(createRequest(baseUrlSparql, Collections.singletonMap("query", query)));
	}
	
	@Override
	public List<String> getWikidataIdWithLabel(String label, String language) {
		String query = String.format(labelQueryString, label, language);
		List<String> wikidataIDs = processWikidataSparqlResponse(createRequest(baseUrlSparql, Collections.singletonMap("query", query)));
		if(wikidataIDs==null) {
			//get the wikidata ids using the wikidata search api
			return getWikidataIdWithWikidataSearch(label);
		}
		//returning the top n wikidata ids
		return wikidataIDs.stream().limit(KEEP_FIRST_N_WIKIDATA_IDS).collect(Collectors.toList());
	}

	@Override
	public List<String> getWikidataIdWithLabelAltLabel(String label, String language) {
		String query = String.format(labelAltLabelQueryString, label, language);
		List<String> wikidataIDs = processWikidataSparqlResponse(createRequest(baseUrlSparql, Collections.singletonMap("query", query)));
		if(wikidataIDs==null) {
			//get the wikidata ids using the wikidata search api
			return getWikidataIdWithWikidataSearch(label);
		}
		//returning the top n wikidata ids
		return wikidataIDs.stream().limit(KEEP_FIRST_N_WIKIDATA_IDS).collect(Collectors.toList());
	}
	
	private List<String> getWikidataIdWithWikidataSearch(String label) {
		
		Map<String, String> params = new HashMap<>();
		
		/*
		 * the params for the wikidata search with the exact label match over the wikidata api.
		 * The difference between this search and the sparql search is that this request will return the results
		 * even if the spelling of the label is not correct, e.g. Sajudis, instead of Sąjūdis
		 */
		params.put("action", "wbsearchentities");
		params.put("search", label);
		params.put("language", "en");
		params.put("format", "json");
		
		List<String> results = processWikidataSearchResponse(createRequest(baseUrlWikidataSearch, params));
		if(results != null) {
			return results;
		}

		/*
		 * the params for the direct request to the wikidata search results page, in which case
		 * the html as output would be received. An example: https://www.wikidata.org/w/index.php?search=Festung+Semendria&title=Special:Search&profile=advanced&fulltext=1&ns0=1
		 * This is an advanced search which recognizes the parts of the label, does some language adaptations, etc., e.g.
		 * when searching for "Festung Semendria", the result found is: Smederevo Fortress (Q2588696).
		 */
		params.clear();
		params.put("search", label);
		params.put("title", "Special:Search");
		params.put("profile", "advanced");
		params.put("fulltext", "1");
		params.put("ns0", "1");
		return processWikidataSearchHtml(createRequest(baseUrlWikidataSearch, params));

	}

	@Override
	public List<String> getWikidataPlaceIdWithLabel(String label, String language) {
		String query = String.format(placeLabelQueryString, label, language);
		List<String> result = processWikidataSparqlResponse(createRequest(baseUrlSparql, Collections.singletonMap("query", query)));
		if(result!=null)
			return result.stream().limit(KEEP_FIRST_N_WIKIDATA_IDS).collect(Collectors.toList());
		else 
			return null;
	}
	
	@Override
	public List<String> getWikidataPlaceIdWithLabelAltLabel(String label, String language) {
		String query = String.format(placeLabelAltLabelQueryString, label, language);
		List<String> result = processWikidataSparqlResponse(createRequest(baseUrlSparql, Collections.singletonMap("query", query)));
		if(result!=null)
			return result.stream().limit(KEEP_FIRST_N_WIKIDATA_IDS).collect(Collectors.toList());
		else 
			return null;
	}
	
	@Override
	public List<String> getWikidataAgentIdWithLabel(String label, String language){
		String query = String.format(agentlabelQueryString, label, language);
		List<String> result = processWikidataSparqlResponse(createRequest(baseUrlSparql, Collections.singletonMap("query", query)));
		if(result!=null)
			return result.stream().limit(KEEP_FIRST_N_WIKIDATA_IDS).collect(Collectors.toList());
		else 
			return null;
	}
	
	@Override
	public List<String> getWikidataAgentIdWithLabelAltLabel(String label, String language){
		String query = String.format(agentlabelAltLabelQueryString, label, language);
		List<String> result = processWikidataSparqlResponse(createRequest(baseUrlSparql, Collections.singletonMap("query", query)));
		if(result!=null)
			return result.stream().limit(KEEP_FIRST_N_WIKIDATA_IDS).collect(Collectors.toList());
		else 
			return null;
	}

	/*
	 * This method process the response of the Wikidata sparql query and returns a
	 * list of Wikidata entity urls or null
	 * 
	 * @param response is the response body of the Wikidata sparql query
	 * 
	 * @return a list of Wikidata entity
	 */
	private List<String> processWikidataSparqlResponse(String reponse) {
		// TODO: implement function and add type to distinguish between Place/Location
		// and Agent/Person
		if (reponse == null || reponse.isBlank() || !reponse.startsWith("{"))
		{
			return null;
		}				

		JSONObject responseJson = new JSONObject(reponse);
		if(!responseJson.has(wikidataResultKey))
		{
			return null;
		}
		
		JSONObject resultObj = responseJson.getJSONObject(wikidataResultKey);
		if(!resultObj.has(wikidataBindingsKey))
		{
			return null;
		}
		
		JSONArray bindingsArray = resultObj.getJSONArray(wikidataBindingsKey);
		List<String> retValue = new ArrayList<>();
		for(int index = 0; bindingsArray.length() > index; index++) {
			JSONObject bindingsObj = bindingsArray.getJSONObject(index);
			if(!bindingsObj.has(wikidataItemKey))
				continue;
			JSONObject cityObj = bindingsObj.getJSONObject(wikidataItemKey);
			String cityValue = cityObj.getString(wikidataValueKey);
			if(!retValue.contains(cityValue))
				retValue.add(cityValue);
		}
		
		if(retValue.size()>0) {
			return retValue;
		}
		else {
			return null;
		}
	}

	/*
	 * Wikidata search is applied to find the wikidata urls when the label to search for is not correctly spelled
	 * so that the wikidata sparql query cannot find it (e.g. the label Sajudis, should be Sąjūdis). When however searched
	 * with the wikidata search, the entity can be found
	 */
	private List<String> processWikidataSearchResponse(String reponse) {
		if (reponse == null || reponse.isBlank() || !reponse.startsWith("{"))
		{
			return null;
		}				

		JSONObject responseJson = new JSONObject(reponse);
		if(!responseJson.has("search"))
		{
			return null;
		}
		
		JSONArray searchArray = responseJson.getJSONArray("search");
		List<String> retValue = new ArrayList<>();
		for(int index = 0; (index<searchArray.length() && index<5); index++) {
			JSONObject searchObj = searchArray.getJSONObject(index);
			if(!searchObj.has("concepturi"))
				continue;
			Object concepturiObj = searchObj.get("concepturi");
			if(!retValue.contains(concepturiObj.toString()))
				retValue.add(concepturiObj.toString());
		}
		
		if(retValue.size()>0) {
			return retValue;
		}
		else {
			return null;
		}
	}
	
	private List<String> processWikidataSearchHtml(String response) {
		if(response==null || response.isBlank()) {
			return null;
		}
		Document html = Jsoup.parse(response);
		if(html==null) {
			return null;
		}
		Elements searchResultsHeadings = html.body().getElementsByClass("mw-search-result-heading");
		if(searchResultsHeadings==null) {
			return null;
		}
		List<String> retValue = new ArrayList<>();
		int numberElemToSelect = 5;
		for(Element el : searchResultsHeadings) {
			String wikidataId = el.getElementsByTag("a").get(0).attr("href");
			if(wikidataId==null || wikidataId.isBlank()) {
				continue;
			}
			retValue.add(EnrichmentConstants.WIKIDATA_ENTITY_BASE_URL + wikidataId.substring(wikidataId.lastIndexOf("/") + 1).trim());
			if(retValue.size()==numberElemToSelect) {
				break;
			}
		}
		
		if(retValue.size()>0) {
			return retValue;
		}
		else {
			return null;
		}
	}
	
	/*
	 * This method creates the Wikidata request, extracts the response body from the
	 * rest and returns the response body
	 * 
	 * @param baseUrl is the base URL to which the query is to be added
	 * 
	 * @param query is the Wikidata sparql Geonames ID or label search query
	 * 
	 * @return response body or null
	 */
	private String createRequest(String baseUrl, Map<String, String> params) {
		try {
			URIBuilder builder = new URIBuilder(baseUrl);
			//in case of calling a REST service for Wikidata JSON, the query parameter should be null/empty
			if(params!=null)
			{
				for(Map.Entry<String, String> httpParam : params.entrySet()) {
					builder.addParameter(httpParam.getKey(), httpParam.getValue());
				}				
			}

			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			HttpGet request = new HttpGet(builder.build());
			request.addHeader("content-type", "application/json");
			request.addHeader("accept", "application/json");
			HttpResponse result = httpClient.execute(request);
			String responeString = EntityUtils.toString(result.getEntity(), "UTF-8");
			
			// TODO: check status code
			return responeString;

		} catch (URISyntaxException | IOException e) {
			// TODO Auto-generated catch block
			logger.log(Level.ERROR, "Exception during the wikidata service call.", e);
			return null;
		}

	}

	@Override
	public String getWikidataJSONFromWikidataID(String WikidataID) {
		
		String WikidataJSONResponse = createRequest(WikidataID, null);
		return WikidataJSONResponse;
		
	}

	@Override
	public List<List<String>> getJSONFieldFromWikidataJSON(String WikidataJSON, String field) {
		
		List<List<String>> result = new ArrayList<List<String>>();
		JSONObject responseJson = new JSONObject(WikidataJSON);
		JSONObject responseJsonEntities = responseJson.getJSONObject("entities");
		Iterator<String> entitiesIterator = responseJsonEntities.keys();
		while(entitiesIterator.hasNext())
		{
			String entityKey = entitiesIterator.next();
			JSONObject entity = responseJsonEntities.getJSONObject(entityKey);
						
			analyseJSONFieldFromWikidataJSON(entity,field,result);
		}
		
		return result;
		
	}

	/**
	 * This function is a recursive implementation that enables taking a specific JSON field
	 * from the obtained Wikidata json response, containing both plain JSONObject but also 
	 * JSONArray json elements.
	 * 
	 * @param jsonElement
	 * @param field
	 * @param result
	 */
	
	private void analyseJSONFieldFromWikidataJSON (Object jsonElement, String field, List<List<String>> result)
	{
		String[] fieldParts = field.split("\\.");
		
		if(fieldParts.length==1)
		{
			if(jsonElement instanceof JSONObject)
			{
				JSONObject obj = (JSONObject) jsonElement;			
				if(fieldParts[0].compareTo("*")==0)
				{
					Iterator<String> allJsonElementsIterator = obj.keys();
					List<String> toAddList = new ArrayList<String>();
					while(allJsonElementsIterator.hasNext())
					{
						String jsonElementKey = allJsonElementsIterator.next();					
						//toAddList.add(obj.getString(jsonElementKey));
						toAddList.add(obj.get(jsonElementKey).toString());
					}
					result.add(toAddList);
				}
				else
				{
					List<String> toAddList = new ArrayList<String>();
					//toAddList.add(obj.getString(fieldParts[0]));
					toAddList.add(obj.get(fieldParts[0]).toString());
					result.add(toAddList);
				}
				
				return;
				
			}
			else if(jsonElement instanceof JSONArray)
			{
				JSONArray array = (JSONArray) jsonElement;
				for(int i=0;i<array.length();i++)
				{
					analyseJSONFieldFromWikidataJSON(array.get(i),field, result);
				}
			}	
			else
			{
				logger.error("The analysed Wikidata JSON element: " + fieldParts[0] + " in the JSON field: " + field  + " contains some element which is niether JSON object nor JSONArray!");
			}
			
		}
		
		
		if(jsonElement instanceof JSONObject)
		{
			JSONObject obj = (JSONObject) jsonElement;
			String [] newField = field.split("\\.",2);
			
			//take all elements of the given json element
			if(fieldParts[0].compareTo("*")==0)
			{
				Iterator<String> allJsonElementsIterator = obj.keys();
				while(allJsonElementsIterator.hasNext())
				{
					String jsonElementKey = allJsonElementsIterator.next();
					analyseJSONFieldFromWikidataJSON(obj.get(jsonElementKey),newField[1],result);
				}
			}
			else if(obj.has(fieldParts[0]))
			{				
				analyseJSONFieldFromWikidataJSON(obj.get(fieldParts[0]),newField[1], result);
			}
			else
			{
				logger.debug("The analysed Wikidata JSON response does not contain the required JSON object: " + 
						fieldParts[0] + " in the JSON field: " + field);
			}
			
		}
		else if(jsonElement instanceof JSONArray)
		{
			JSONArray array = (JSONArray) jsonElement;
			for(int i=0;i<array.length();i++)
			{
				analyseJSONFieldFromWikidataJSON(array.get(i),field, result);
			}
		}
		else
		{
			logger.error("The analysed Wikidata JSON element: " + fieldParts[0] + " in the JSON field: " + field  + " contains some element which is niether JSON object nor JSONArray!");
		}
			
	}
	
	@Override
	public WikidataEntity getWikidataEntityUsingLocalCache(String wikidataURL, String type) throws IOException {
		
		//trying to get the wikidata json from a local cache file, if does not exist fetch from wikidata and save into a cache file
		String WikidataJSON = HelperFunctions.getWikidataJsonFromLocalFileCache(wikidataDirectory, wikidataURL);
		if(WikidataJSON==null) 	
		{
			logger.debug("Wikidata entity does not exist in a local file cache!");
			WikidataJSON = getWikidataJSONFromWikidataID(wikidataURL);
			if(WikidataJSON==null || WikidataJSON.isEmpty()) return null;
			HelperFunctions.saveWikidataJsonToLocalFileCache(wikidataDirectory, wikidataURL, WikidataJSON);
			logger.debug("Wikidata entity is successfully saved to a local file cache!");			
		}
		
		WikidataEntity wikiEntity = null;
		/**
		 * TODO: introduce the EntityObjectFactory class that would do the automatic object creation
		 */
		if(EntityTypes.Agent.getEntityType().equalsIgnoreCase(type)) {
			wikiEntity = new WikidataAgentImpl();
		}
		else if(EntityTypes.Place.getEntityType().equalsIgnoreCase(type)) {
			wikiEntity = new WikidataPlaceImpl();
		}
		
//		List<List<String>> jsonTypeElement = getJSONFieldFromWikidataJSON(WikidataJSON,wikiEntity.getIdentification_jsonProp());
//		if(jsonTypeElement!=null && !jsonTypeElement.isEmpty())
//		{
//			return getWikidataEntity(wikidataURL,WikidataJSON,type);
//		}
		if(WikidataJSON.contains(wikiEntity.getIdentification_jsonProp_identifier())) {
			return getWikidataEntity(wikidataURL,WikidataJSON,type);
		}	
		
		return null;
		
	}
	
	@Override
	public WikidataEntity getWikidataEntity (String wikidataURL, String WikidataJSON, String type)
	{
		List<List<String>> jsonElement;
		
		if(type.equalsIgnoreCase("agent"))
		{
			WikidataAgent newWikidataAgent = new WikidataAgentImpl ();
			
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataAgent.getAltLabel_jsonProp());
			//converting the "jsonElement" to the appropriate object to be saved in Solr
			Map<String,List<String>> altLabelMap = null;
			if(jsonElement!=null && !jsonElement.isEmpty()) 
			{
				altLabelMap = HelperFunctions.convertListOfListOfStringToMapOfStringAndListOfString(jsonElement);
			}
//			/*this is added because jackson has problems with serializing null values (version 2.9.4 that we use)
//			 * TODO: find a better fix
//			 */			
//			if(altLabelMap==null)
//			{
//				altLabelMap = new HashMap<String, List<String>>();
//				List<String> altLabelMapList = new ArrayList<String>();
//				altLabelMapList.add("-");
//				altLabelMap.put("en", altLabelMapList);
//			}
			if(altLabelMap!=null) newWikidataAgent.setAltLabel(altLabelMap);

			String country = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataAgent.getCountry_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty())
			{
				country=EnrichmentConstants.WIKIDATA_ENTITY_BASE_URL + jsonElement.get(0).get(0);
			}
			if(country!=null) newWikidataAgent.setCountry(country);

			String [] dateBirthArray = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataAgent.getDateOfBirth_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) 
			{		
				dateBirthArray = new String [1];
				dateBirthArray[0]=jsonElement.get(0).get(0);				
			}
			if(dateBirthArray!=null) newWikidataAgent.setDateOfBirth(dateBirthArray);
			
			String [] dateDeathArray = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataAgent.getDateOfDeath_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty())
			{
				dateDeathArray = new String [jsonElement.size()];
				for(int i=0;i<jsonElement.size();i++)
				{
					dateDeathArray[i]=jsonElement.get(i).get(0);
				}				
			}
			if(dateDeathArray!=null) newWikidataAgent.setDateOfDeath(dateDeathArray);
			
			String depiction = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataAgent.getDepiction_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty())
			{
				depiction = "http://commons.wikimedia.org/wiki/Special:FilePath/" + jsonElement.get(0).get(0);
			}
			if(depiction!=null) newWikidataAgent.setDepiction(depiction);
			
			Map<String,List<String>> descriptionsMap = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataAgent.getDescription_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty())
			{
				descriptionsMap = HelperFunctions.convertListOfListOfStringToMapOfStringAndListOfString(jsonElement);
			}
			if(descriptionsMap!=null) newWikidataAgent.setDescription(descriptionsMap);
			
			newWikidataAgent.setEntityId(wikidataURL);			
			
			newWikidataAgent.setInternalType(type);
			
			
			String modificationDate = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataAgent.getModificationDate_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) 
			{
				modificationDate = jsonElement.get(0).get(0);
			}
			if(modificationDate!=null) newWikidataAgent.setModificationDate(modificationDate);
			
			
			String [] occupationArray = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataAgent.getProfessionOrOccupation_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) 
			{
				occupationArray = new String [jsonElement.size()];
				for(int i=0;i<jsonElement.size();i++)
				{
					occupationArray[i]=EnrichmentConstants.WIKIDATA_ENTITY_BASE_URL + jsonElement.get(i).get(0);
				}				
			}
			if(occupationArray!=null) newWikidataAgent.setProfessionOrOccupation(occupationArray);
			
			
			Map<String,List<String>> prefLabelMap = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataAgent.getPrefLabel_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty())
			{ 
				prefLabelMap = HelperFunctions.convertListOfListOfStringToMapOfStringAndListOfString(jsonElement);
			}
			if(prefLabelMap!=null) newWikidataAgent.setPrefLabel(prefLabelMap);
			
			
			String [] sameAsArray = null;		
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataAgent.getSameAs_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty())	
			{
				sameAsArray = new String [jsonElement.size()];
				for(int i=0;i<jsonElement.size();i++)
				{
					sameAsArray[i]=jsonElement.get(i).get(0);
				}				
			}
			if(sameAsArray!=null) newWikidataAgent.setSameAs(sameAsArray);
			
			return newWikidataAgent;

		}
		else
		{
			
			WikidataPlace newWikidataPlace = new WikidataPlaceImpl ();
			
			Map<String,List<String>> altLabelMap = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataPlace.getAltLabel_jsonProp()); 
			if(jsonElement!=null && !jsonElement.isEmpty())
			{
				altLabelMap = HelperFunctions.convertListOfListOfStringToMapOfStringAndListOfString(jsonElement);
				
			}
			if(altLabelMap!=null) newWikidataPlace.setAltLabel(altLabelMap);
			
			String country = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataPlace.getCountry_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) 
			{
				country = EnrichmentConstants.WIKIDATA_ENTITY_BASE_URL + jsonElement.get(0).get(0);
			}
			if(country!=null) newWikidataPlace.setCountry(country);
			
			Float latitude = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataPlace.getLatitude_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) 
			{
				latitude = Float.valueOf(jsonElement.get(0).get(0));
			}
			if(latitude!=null) newWikidataPlace.setLatitude(latitude);

			
			Float longitude = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataPlace.getLongitude_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) 
			{
				longitude = Float.valueOf(jsonElement.get(0).get(0));
			}
			if(longitude!=null) newWikidataPlace.setLongitude(longitude);

			String depiction = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataPlace.getDepiction_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) 
			{
				depiction = "http://commons.wikimedia.org/wiki/Special:FilePath/" + jsonElement.get(0).get(0);
			}
			if(depiction!=null) newWikidataPlace.setDepiction(depiction);
		
			Map<String,List<String>> descriptionsMap = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataPlace.getDescription_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) 
			{
				descriptionsMap = HelperFunctions.convertListOfListOfStringToMapOfStringAndListOfString(jsonElement);
			}
			if(descriptionsMap!=null) newWikidataPlace.setDescription(descriptionsMap);
			
			newWikidataPlace.setEntityId(wikidataURL);
			
			newWikidataPlace.setInternalType(type);
			
			String modificationDate = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataPlace.getModificationDate_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) 
			{
				modificationDate = jsonElement.get(0).get(0);
			}
			if(modificationDate!=null) newWikidataPlace.setModificationDate(modificationDate);
		
			
			String logo = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataPlace.getLogo_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) 
			{
				logo = jsonElement.get(0).get(0);
			}
			if(logo!=null) newWikidataPlace.setLogo(logo);
		
			Map<String,List<String>> prefLabelMap = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataPlace.getPrefLabel_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty())
			{
				prefLabelMap = HelperFunctions.convertListOfListOfStringToMapOfStringAndListOfString(jsonElement);
			}
			if(prefLabelMap!=null) newWikidataPlace.setPrefLabel(prefLabelMap);
			
			String [] sameAsArray = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataPlace.getSameAs_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) 
			{
				sameAsArray = new String [jsonElement.size()];
				for(int i=0;i<jsonElement.size();i++)
				{
					sameAsArray[i]=jsonElement.get(i).get(0);
				}		
			}
			if(sameAsArray!=null) newWikidataPlace.setSameAs(sameAsArray);

			return newWikidataPlace;
			
		}	
	}
}
