package eu.europeana.enrichment.ner.linking;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import eu.europeana.enrichment.common.commons.EnrichmentConfiguration;
import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.common.commons.HelperFunctions;
import eu.europeana.enrichment.model.WikidataAgent;
import eu.europeana.enrichment.model.WikidataEntity;
import eu.europeana.enrichment.model.WikidataPlace;
import eu.europeana.enrichment.model.impl.NamedEntityImpl;
import eu.europeana.enrichment.model.impl.WikidataAgentImpl;
import eu.europeana.enrichment.model.impl.WikidataPlaceImpl;
import eu.europeana.enrichment.model.vocabulary.EntityTypes;
import eu.europeana.enrichment.ner.enumeration.NERClassification;

//import net.arnx.jsonic.JSONException;
@Service(EnrichmentConstants.BEAN_ENRICHMENT_WIKIDATA_SERVICE)
public class WikidataServiceImpl implements WikidataService {
	
	@Autowired
	@Qualifier(EnrichmentConstants.BEAN_ENRICHMENT_CONFIGURATION)
	EnrichmentConfiguration configuration;
	
	Logger logger = LogManager.getLogger(getClass());

	private static final String baseUrlSparql = "https://query.wikidata.org/bigdata/namespace/wdq/sparql"; // "https://query.wikidata.org/sparql";
//	private static final String baseUrlWikidataSearch = "https://wikidata.org/w/api.php";
	private static final String baseUrlWikidataSearch = "https://www.wikidata.org/w/index.php";
	private static final int KEEP_FIRST_N_WIKIDATA_IDS = 20;
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
	/*
	 * Saving the wikidata identifiers for all wikidata subclasses related to the place and agent entities.
	 * This is used to check the wikidata type during the linking process in the named entity recognition analysis.
	 */
	private Set<String> wikidataSubclassesForPlace;
	private Set<String> wikidataSubclassesForAgent;
	
	@Autowired
	public WikidataServiceImpl (EnrichmentConfiguration enrichmentConfiguration) throws IOException
	{
		wikidataDirectory = enrichmentConfiguration.getEnrichWikidataDirectory();
		//reading the files for the wikidata subclasses
		wikidataSubclassesForPlace = new HashSet<String>(readWikidataSubclasses(enrichmentConfiguration.getWikidataSubclassesGeographicLocation()));
		wikidataSubclassesForAgent = new HashSet<String>(readWikidataSubclasses(enrichmentConfiguration.getWikidataSubclassesHuman()));
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
		if(wikidataIDs!=null) {
			for(String wikidataIdEach : wikidataIDs) {
				String wikidataJSONResponse = getWikidataJSONFromWikidataID(wikidataIdEach);
				if(validWikidataPage(wikidataJSONResponse)) {
					return wikidataIDs;					
				}
			}
		}
		return null;
	}

	@Override
	public List<String> getWikidataIdWithLabelAltLabel(String label, String language) {
		String query = String.format(labelAltLabelQueryString, label, language);
		List<String> wikidataIDs = processWikidataSparqlResponse(createRequest(baseUrlSparql, Collections.singletonMap("query", query)));
		if(wikidataIDs!=null) {
			for(String wikidataIdEach : wikidataIDs) {
				String wikidataJSONResponse = getWikidataJSONFromWikidataID(wikidataIdEach);
				if(validWikidataPage(wikidataJSONResponse)) {
					return wikidataIDs;					
				}
			}
		}		
		return null;
	}
	
	@Override
	public List<String> getWikidataIdWithWikidataSearch(String label) {
		/*
		 * the params for the direct request to the wikidata search results page, in which case
		 * the html as output would be received. An example: https://www.wikidata.org/w/index.php?search=Festung+Semendria&title=Special:Search&profile=advanced&fulltext=1&ns0=1
		 * This is an advanced search which recognizes the parts of the label, does some language adaptations, etc., e.g.
		 * when searching for "Festung Semendria", the result found is: Smederevo Fortress (Q2588696).
		 */
		Map<String, String> params = new HashMap<>();
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
			return result;
		else 
			return null;
	}
	
	@Override
	public List<String> getWikidataPlaceIdWithLabelAltLabel(String label, String language) {
		String query = String.format(placeLabelAltLabelQueryString, label, language);
		List<String> result = processWikidataSparqlResponse(createRequest(baseUrlSparql, Collections.singletonMap("query", query)));
		if(result!=null)
			return result;
		else 
			return null;
	}
	
	@Override
	public List<String> getWikidataAgentIdWithLabel(String label, String language){
		String query = String.format(agentlabelQueryString, label, language);
		List<String> result = processWikidataSparqlResponse(createRequest(baseUrlSparql, Collections.singletonMap("query", query)));
		if(result!=null)
			return result;
		else 
			return null;
	}
	
	@Override
	public List<String> getWikidataAgentIdWithLabelAltLabel(String label, String language){
		String query = String.format(agentlabelAltLabelQueryString, label, language);
		List<String> result = processWikidataSparqlResponse(createRequest(baseUrlSparql, Collections.singletonMap("query", query)));
		if(result!=null)
			return result;
		else 
			return null;
	}

	/*
	 * This function ensure that the wikidata id is valid, and so in the way that
	 * it checks that it is not a wikidata disambiguation page
	 */
	@Override
	public boolean validWikidataPage (String wikidataJSONResponse) {
		if(wikidataJSONResponse==null) {
			return false;
		}
		String descriptionEn = getDescriptionEnFromWikidataJson(wikidataJSONResponse);
		if(descriptionEn!=null && descriptionEn.contains("disambiguation page")) {
			return false;
		}		
		else {
			return true;
		}
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
			return retValue.stream().limit(KEEP_FIRST_N_WIKIDATA_IDS).collect(Collectors.toList());
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
			return retValue.stream().limit(KEEP_FIRST_N_WIKIDATA_IDS).collect(Collectors.toList());
		}
		else {
			return null;
		}
	}
	
	private List<String> processWikidataSearchHtml(String response) {
		if(response==null || response.isBlank()) {
			return Collections.<String>emptyList();
		}
		Document html = Jsoup.parse(response);
		if(html==null) {
			return Collections.<String>emptyList();
		}
		Elements searchResultsHeadings = html.body().getElementsByClass("mw-search-result-heading");
		if(searchResultsHeadings==null) {
			return Collections.<String>emptyList();
		}
		List<String> retValue = new ArrayList<String>();
		int numberElemToSelect = KEEP_FIRST_N_WIKIDATA_IDS;
		for(Element el : searchResultsHeadings) {
			Elements aTags = el.getElementsByTag("a");
			if(aTags.isEmpty()) {
				continue;
			}
			
			String wikidataId = aTags.get(0).attr("href");
			if(wikidataId==null || wikidataId.isBlank()) {
				continue;
			}
			retValue.add(EnrichmentConstants.WIKIDATA_ENTITY_BASE_URL + wikidataId.substring(wikidataId.lastIndexOf("/") + 1).trim());
			if(retValue.size()==numberElemToSelect) {
				break;
			}
		}

		return retValue;
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
		try {
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
		catch (JSONException ex) {
			return null;
		}
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
			if(!configuration.getWikidataSaveJsonToLocalCache()) { 
				return null;
			}
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
			if(WikidataJSON.contains(EnrichmentConstants.AGENT_IDENTIFICATION_JSONPROP_IDENTIFIER)) {
				return getWikidataEntity(wikidataURL,WikidataJSON,type);
			}	

		}
		else if(EntityTypes.Place.getEntityType().equalsIgnoreCase(type)) {
			wikiEntity = new WikidataPlaceImpl();
			if(WikidataJSON.contains(EnrichmentConstants.PLACE_IDENTIFICATION_JSONPROP_IDENTIFIER)) {
				return getWikidataEntity(wikidataURL,WikidataJSON,type);
			}				
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
			
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON, EnrichmentConstants.ALTLABEL_JSONPROP);
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
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,EnrichmentConstants.AGENT_COUNTRY_JSONPROP);
			if(jsonElement!=null && !jsonElement.isEmpty())
			{
				country=EnrichmentConstants.WIKIDATA_ENTITY_BASE_URL + jsonElement.get(0).get(0);
			}
			if(country!=null) newWikidataAgent.setCountry(country);

			String [] dateBirthArray = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,EnrichmentConstants.DATEOFBIRTH_JSONPROP);
			if(jsonElement!=null && !jsonElement.isEmpty()) 
			{		
				dateBirthArray = new String [1];
				dateBirthArray[0]=jsonElement.get(0).get(0);				
			}
			if(dateBirthArray!=null) newWikidataAgent.setDateOfBirth(dateBirthArray);
			
			String [] dateDeathArray = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,EnrichmentConstants.DATEOFDEATH_JSONPROP);
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
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,EnrichmentConstants.DEPICTION_JSONPROP);
			if(jsonElement!=null && !jsonElement.isEmpty())
			{
				depiction = "http://commons.wikimedia.org/wiki/Special:FilePath/" + jsonElement.get(0).get(0);
			}
			if(depiction!=null) newWikidataAgent.setDepiction(depiction);
			
			Map<String,List<String>> descriptionsMap = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,EnrichmentConstants.DESCRIPTION_JSONPROP);
			if(jsonElement!=null && !jsonElement.isEmpty())
			{
				descriptionsMap = HelperFunctions.convertListOfListOfStringToMapOfStringAndListOfString(jsonElement);
			}
			if(descriptionsMap!=null) newWikidataAgent.setDescription(descriptionsMap);
			
			newWikidataAgent.setEntityId(wikidataURL);			
			
			newWikidataAgent.setInternalType(type);
			
			
			String modificationDate = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,EnrichmentConstants.MODIFICATIONDATE_JSONPROP);
			if(jsonElement!=null && !jsonElement.isEmpty()) 
			{
				modificationDate = jsonElement.get(0).get(0);
			}
			if(modificationDate!=null) newWikidataAgent.setModificationDate(modificationDate);
			
			
			String [] occupationArray = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,EnrichmentConstants.PROFESSIONOROCCUPATION_JSONPROP);
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
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,EnrichmentConstants.PREFLABEL_JSONPROP);
			if(jsonElement!=null && !jsonElement.isEmpty())
			{ 
				prefLabelMap = HelperFunctions.convertListOfListOfStringToMapOfStringAndListOfString(jsonElement);
			}
			if(prefLabelMap!=null) newWikidataAgent.setPrefLabel(prefLabelMap);
			
			
			String [] sameAsArray = null;		
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,EnrichmentConstants.SAMEAS_JSONPROP);
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
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,EnrichmentConstants.ALTLABEL_JSONPROP); 
			if(jsonElement!=null && !jsonElement.isEmpty())
			{
				altLabelMap = HelperFunctions.convertListOfListOfStringToMapOfStringAndListOfString(jsonElement);
				
			}
			if(altLabelMap!=null) newWikidataPlace.setAltLabel(altLabelMap);
			
			String country = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,EnrichmentConstants.PLACE_COUNTRY_JSONPROP);
			if(jsonElement!=null && !jsonElement.isEmpty()) 
			{
				country = EnrichmentConstants.WIKIDATA_ENTITY_BASE_URL + jsonElement.get(0).get(0);
			}
			if(country!=null) newWikidataPlace.setCountry(country);
			
			Float latitude = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,EnrichmentConstants.LATITUDE_JSONPROP);
			if(jsonElement!=null && !jsonElement.isEmpty()) 
			{
				latitude = Float.valueOf(jsonElement.get(0).get(0));
			}
			if(latitude!=null) newWikidataPlace.setLatitude(latitude);

			
			Float longitude = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,EnrichmentConstants.LONGITUDE_JSONPROP);
			if(jsonElement!=null && !jsonElement.isEmpty()) 
			{
				longitude = Float.valueOf(jsonElement.get(0).get(0));
			}
			if(longitude!=null) newWikidataPlace.setLongitude(longitude);

			String depiction = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,EnrichmentConstants.DEPICTION_JSONPROP);
			if(jsonElement!=null && !jsonElement.isEmpty()) 
			{
				depiction = "http://commons.wikimedia.org/wiki/Special:FilePath/" + jsonElement.get(0).get(0);
			}
			if(depiction!=null) newWikidataPlace.setDepiction(depiction);
		
			Map<String,List<String>> descriptionsMap = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,EnrichmentConstants.DESCRIPTION_JSONPROP);
			if(jsonElement!=null && !jsonElement.isEmpty()) 
			{
				descriptionsMap = HelperFunctions.convertListOfListOfStringToMapOfStringAndListOfString(jsonElement);
			}
			if(descriptionsMap!=null) newWikidataPlace.setDescription(descriptionsMap);
			
			newWikidataPlace.setEntityId(wikidataURL);
			
			newWikidataPlace.setInternalType(type);
			
			String modificationDate = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,EnrichmentConstants.MODIFICATIONDATE_JSONPROP);
			if(jsonElement!=null && !jsonElement.isEmpty()) 
			{
				modificationDate = jsonElement.get(0).get(0);
			}
			if(modificationDate!=null) newWikidataPlace.setModificationDate(modificationDate);
		
			
			String logo = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,EnrichmentConstants.LOGO_JSONPROP);
			if(jsonElement!=null && !jsonElement.isEmpty()) 
			{
				logo = jsonElement.get(0).get(0);
			}
			if(logo!=null) newWikidataPlace.setLogo(logo);
		
			Map<String,List<String>> prefLabelMap = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,EnrichmentConstants.PREFLABEL_JSONPROP);
			if(jsonElement!=null && !jsonElement.isEmpty())
			{
				prefLabelMap = HelperFunctions.convertListOfListOfStringToMapOfStringAndListOfString(jsonElement);
			}
			if(prefLabelMap!=null) newWikidataPlace.setPrefLabel(prefLabelMap);
			
			String [] sameAsArray = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,EnrichmentConstants.SAMEAS_JSONPROP);
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
	
	@Override
	public String getDescriptionEnFromWikidataJson(String wikidataJson) {		
		Map<String,List<String>> descriptionsMap = null;
		List<List<String>> jsonElement = getJSONFieldFromWikidataJSON(wikidataJson,"descriptions.*.*");
		if(jsonElement!=null && !jsonElement.isEmpty())
		{ 
			descriptionsMap = HelperFunctions.convertListOfListOfStringToMapOfStringAndListOfString(jsonElement);
			List<String> descriptionEn = descriptionsMap.get("en");
			if(descriptionEn!=null && descriptionEn.size()>0) {
				return descriptionEn.get(0);
			}
			else
				return null;
		}
		else
			return null;
	}
	
	private boolean matchPrefLabelInWikidata(String wikidataJSONResponse, String name) {
		//search through the prefLabel
		Map<String,List<String>> prefLabelMap = null;
		List<List<String>> jsonElement = getJSONFieldFromWikidataJSON(wikidataJSONResponse,EnrichmentConstants.PREFLABEL_JSONPROP);
		if(jsonElement!=null && !jsonElement.isEmpty())
		{ 
			prefLabelMap = HelperFunctions.convertListOfListOfStringToMapOfStringAndListOfString(jsonElement);
		}
		if(prefLabelMap!=null) {
			for(List<String> prefLabelValues : prefLabelMap.values()) {
				for(String prefLabelSingle : prefLabelValues) {
					/* 
					 * check for the order of words that does not have to be the same, e.g.
					 * for the name: Moore Lane, the found wikidata entity is Lane Moore,
					 * which is correct.
					 */
					String prefLabelSingleUTF8 = new String(prefLabelSingle.getBytes(), StandardCharsets.UTF_8);
					String prefLabelSingleUTF8LowerCase = prefLabelSingleUTF8.toLowerCase();
					List<String> prefLabelSingleSplitted = Arrays.asList(prefLabelSingleUTF8LowerCase.split("\\s+"));
					String nameUTF8 = new String(name.getBytes(), StandardCharsets.UTF_8);
					String nameUTF8LowerCase = nameUTF8.toLowerCase();
					List<String> nameSplitted = Arrays.asList(nameUTF8LowerCase.split("\\s+"));
					if(prefLabelSingleSplitted.containsAll(nameSplitted) 
						&& prefLabelSingleSplitted.size()==nameSplitted.size()) {
						return true;
					}
				}
			}
		}
		return false;

	}
	
	private boolean matchAltLabelInWikidata(String wikidataJSONResponse, String name) {
		List<List<String>> jsonElement = getJSONFieldFromWikidataJSON(wikidataJSONResponse,EnrichmentConstants.ALTLABEL_JSONPROP);
		Map<String,List<String>> altLabelMap = null;
		if(jsonElement!=null && !jsonElement.isEmpty()) 
		{
			altLabelMap = HelperFunctions.convertListOfListOfStringToMapOfStringAndListOfString(jsonElement);
		}
		if(altLabelMap!=null) {
			for(List<String> altLabelValues : altLabelMap.values()) {
				for(String altLabelSingle : altLabelValues) {
					/* 
					 * check for the order of words that does not have to be the same, e.g.
					 * for the name: Moore Lane, the found wikidata entity is Lane Moore,
					 * which is correct.
					 */
					String altLabelSingleUTF8 = new String(altLabelSingle.getBytes(), StandardCharsets.UTF_8);
					String altLabelSingleUTF8LowerCase = altLabelSingleUTF8.toLowerCase();
					List<String> altLabelSingleSplitted = Arrays.asList(altLabelSingleUTF8LowerCase.split("\\s+"));
					String nameUTF8 = new String(name.getBytes(), StandardCharsets.UTF_8);
					String nameUTF8LowerCase = nameUTF8.toLowerCase();
					List<String> nameSplitted = Arrays.asList(nameUTF8LowerCase.split("\\s+"));
					if(altLabelSingleSplitted.containsAll(nameSplitted) 
						&& altLabelSingleSplitted.size()==nameSplitted.size()) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private boolean matchTypeInWikidata(String wikidataJSONResponse, String type) {
		List<List<String>> jsonElement = getJSONFieldFromWikidataJSON(wikidataJSONResponse,EnrichmentConstants.INSTANCE_OF_JSONPROP);
		if(jsonElement!=null)	
		{
			for(int i=0;i<jsonElement.size();i++)
			{
				if(type.equalsIgnoreCase(NERClassification.AGENT.toString())) {
					if(wikidataSubclassesForAgent.contains(jsonElement.get(i).get(0))) {
						return true;
					}
				}
				else if(type.equalsIgnoreCase(NERClassification.PLACE.toString())) {
					if(wikidataSubclassesForPlace.contains(jsonElement.get(i).get(0))) {
						return true;
					}
				}
			}				
		}
		return false;

	}
	
	public String computePreferedWikidataId(NamedEntityImpl namedEntity, boolean matchType) { 
		List<String> savedWikidataIds=new ArrayList<String>();
		List<String> savedWikidataJsons=new ArrayList<String>();
		if(namedEntity.getDbpediaWikidataIds()!=null) {
			//first check if any of the ids match the prefLabel
			for(String wikidataId : namedEntity.getDbpediaWikidataIds()) {
				String wikidataJSONResponse = getWikidataJSONFromWikidataID(wikidataId);
				if(validWikidataPage(wikidataJSONResponse)) {
					if(matchType && matchTypeInWikidata(wikidataJSONResponse, namedEntity.getType())) {
						savedWikidataIds.add(wikidataId);
						savedWikidataJsons.add(wikidataJSONResponse);
						if(matchPrefLabelInWikidata(wikidataJSONResponse, namedEntity.getLabel())) {
							return wikidataId;
						}
					}
					else {
						savedWikidataIds.add(wikidataId);
						savedWikidataJsons.add(wikidataJSONResponse);
						if(matchPrefLabelInWikidata(wikidataJSONResponse, namedEntity.getLabel())) {
							return wikidataId;
						}
						
					}
				}
			}
			//then check if any of the ids match the altLabel
			for(int i=0;i<savedWikidataJsons.size();i++) {
				if(matchAltLabelInWikidata(savedWikidataJsons.get(i), namedEntity.getLabel())) {
					return savedWikidataIds.get(i);
				}
			}
			return getTheLowestWikidataId(namedEntity.getDbpediaWikidataIds());
		}		
		else if(namedEntity.getWikidataLabelAltLabelAndTypeMatchIds()!=null) {
			//first check if any of the ids match the prefLabel
			for(String wikidataId : namedEntity.getWikidataLabelAltLabelAndTypeMatchIds()) {
				String wikidataJSONResponse = getWikidataJSONFromWikidataID(wikidataId);
				if(validWikidataPage(wikidataJSONResponse)) {
					if(matchType && matchTypeInWikidata(wikidataJSONResponse, namedEntity.getType())) {
						savedWikidataIds.add(wikidataId);
						savedWikidataJsons.add(wikidataJSONResponse);
						if(matchPrefLabelInWikidata(wikidataJSONResponse, namedEntity.getLabel())) {
							return wikidataId;
						}
					}
					else {
						savedWikidataIds.add(wikidataId);
						savedWikidataJsons.add(wikidataJSONResponse);
						if(matchPrefLabelInWikidata(wikidataJSONResponse, namedEntity.getLabel())) {
							return wikidataId;
						}
					}
				}
			}
			//then check if any of the ids match the altLabel
			for(int i=0;i<savedWikidataJsons.size();i++) {
				if(matchAltLabelInWikidata(savedWikidataJsons.get(i), namedEntity.getLabel())) {
					return savedWikidataIds.get(i);
				}
			}
		}

		return null;
	}

	private String getTheLowestWikidataId(List<String> wikidataIds) {
		if(wikidataIds.size()==0) {
			return null;
		}
		int lowestQId=Integer.valueOf(wikidataIds.get(0).substring(wikidataIds.get(0).lastIndexOf("/") + 2).trim());
		for(String id : wikidataIds) {
			int wikidataIdInt = Integer.valueOf(id.substring(id.lastIndexOf("/") + 2).trim());
			if(wikidataIdInt<lowestQId) {
				lowestQId=wikidataIdInt;
			}
		}
		return EnrichmentConstants.WIKIDATA_ENTITY_BASE_URL + "Q" + lowestQId;
	}
	
	private Set<String> readWikidataSubclasses(String path) throws IOException {
		Set<String> wikidataIdentifiers = new HashSet<String>();
		Path subclassesPlacePath = Path.of(path);
		String subclassesPlaceString = Files.readString(subclassesPlacePath);
		JSONArray subclassesPlaceJson = new JSONArray(subclassesPlaceString);
		for(int index = 0; subclassesPlaceJson.length() > index; index++) {
			JSONObject item = subclassesPlaceJson.getJSONObject(index);
			String identifierUrl = item.getString("s");
			//extracts the Q identifier, e.g. "Q64"
			String identifier = identifierUrl.substring(identifierUrl.lastIndexOf("/") + 1);
			wikidataIdentifiers.add(identifier);
		}
		return wikidataIdentifiers;
	}
	
	public Set<String> getWikidataSubclassesForPlace() {
		return wikidataSubclassesForPlace;
	}

	public Set<String> getWikidataSubclassesForAgent() {
		return wikidataSubclassesForAgent;
	}
}
