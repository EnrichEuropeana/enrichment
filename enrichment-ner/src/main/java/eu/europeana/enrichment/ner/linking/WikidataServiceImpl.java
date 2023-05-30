package eu.europeana.enrichment.ner.linking;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import eu.europeana.enrichment.common.commons.EnrichmentConfiguration;
import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.common.commons.HelperFunctions;
import eu.europeana.enrichment.common.exceptions.FunctionalRuntimeException;
import eu.europeana.enrichment.definitions.model.WikidataAgent;
import eu.europeana.enrichment.definitions.model.WikidataEntity;
import eu.europeana.enrichment.definitions.model.WikidataOrganization;
import eu.europeana.enrichment.definitions.model.WikidataPlace;
import eu.europeana.enrichment.definitions.model.impl.NamedEntityImpl;
import eu.europeana.enrichment.definitions.model.impl.PositionEntityImpl;
import eu.europeana.enrichment.definitions.model.impl.WikidataAgentImpl;
import eu.europeana.enrichment.definitions.model.impl.WikidataOrganizationImpl;
import eu.europeana.enrichment.definitions.model.impl.WikidataPlaceImpl;
import eu.europeana.enrichment.definitions.model.vocabulary.NERClassification;
import eu.europeana.enrichment.definitions.model.vocabulary.NerTools;
import eu.europeana.enrichment.solr.exception.SolrServiceException;
import eu.europeana.enrichment.solr.service.SolrWikidataEntityService;

//import net.arnx.jsonic.JSONException;
@Service(EnrichmentConstants.BEAN_ENRICHMENT_WIKIDATA_SERVICE)
public class WikidataServiceImpl implements WikidataService {
	
	@Autowired
	@Qualifier(EnrichmentConstants.BEAN_ENRICHMENT_CONFIGURATION)
	EnrichmentConfiguration configuration;
	
	@Autowired
	SolrWikidataEntityService solrWikidataEntityService;
	
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
	private final String wikidataValueKey = "value";
		
	private String wikidataDirectory;
	/*
	 * Saving the wikidata identifiers for all wikidata subclasses related to the place and agent entities.
	 * This is used to check the wikidata type during the linking process in the named entity recognition analysis.
	 */
	private Set<String> wikidataSubclassesForPlace;
	private Set<String> wikidataSubclassesForAgent;
	private Set<String> wikidataSubclassesForOrganization;
	
	@Autowired
	public WikidataServiceImpl (EnrichmentConfiguration enrichmentConfiguration) throws IOException
	{
		wikidataDirectory = enrichmentConfiguration.getEnrichWikidataDirectory();
		//reading the files for the wikidata subclasses
		Set<String> wikidataSubclassesForPlaceAll = new HashSet<String>(readWikidataIdsFromQueryServiceOutput(enrichmentConfiguration.getWikidataSubclassesGeographicLocation()));
		Set<String> wikidataSubclassesForPlaceRemove = new HashSet<String>(readWikidataIdsFromQueryServiceOutput(enrichmentConfiguration.getWikidataSubclassesGeographicLocationRemove()));
		wikidataSubclassesForPlace = new HashSet<>(wikidataSubclassesForPlaceAll);
		wikidataSubclassesForPlace.removeAll(wikidataSubclassesForPlaceRemove);
		wikidataSubclassesForAgent = new HashSet<String>(readWikidataIdsFromQueryServiceOutput(enrichmentConfiguration.getWikidataSubclassesNaturalPerson()));
		wikidataSubclassesForOrganization = new HashSet<String>(readWikidataIdsFromQueryServiceOutput(enrichmentConfiguration.getWikidataSubclassesJuridicalPerson()));
	}
	
	
	public Logger getLogger() {
		return logger;
	}

	@Override
	public List<String> getWikidataId(String geonameId) throws Exception {
		String query = String.format(geonamesIdQueryString, geonameId);
		return processWikidataSparqlResponse(createRequest(baseUrlSparql, Collections.singletonMap("query", query)));
	}
	
	@Override
	public List<String> getWikidataIdWithLabel(String label, String language) throws Exception {
		String query = String.format(labelQueryString, label, language);
		List<String> wikidataIDs = processWikidataSparqlResponse(createRequest(baseUrlSparql, Collections.singletonMap("query", query)));
		if(wikidataIDs!=null) {
			for(String wikidataIdEach : wikidataIDs) {
				String wikidataJSONResponse = getWikidataJSONFromRemote(wikidataIdEach);
				if(validWikidataPage(wikidataJSONResponse)) {
					return wikidataIDs;					
				}
			}
		}
		return null;
	}

	@Override
	public List<String> getWikidataIdWithLabelAltLabel(String label, String language) throws Exception {
		String query = String.format(labelAltLabelQueryString, label, language);
		List<String> wikidataIDs = processWikidataSparqlResponse(createRequest(baseUrlSparql, Collections.singletonMap("query", query)));
		if(wikidataIDs!=null) {
			for(String wikidataIdEach : wikidataIDs) {
				String wikidataJSONResponse = getWikidataJSONFromRemote(wikidataIdEach);
				if(validWikidataPage(wikidataJSONResponse)) {
					return wikidataIDs;					
				}
			}
		}		
		return null;
	}
	
	@Override
	public List<String> getWikidataIdWithWikidataSearch(String label) throws Exception {
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
	public List<String> getWikidataPlaceIdWithLabel(String label, String language) throws Exception {
		String query = String.format(placeLabelQueryString, label, language);
		List<String> result = processWikidataSparqlResponse(createRequest(baseUrlSparql, Collections.singletonMap("query", query)));
		if(result!=null)
			return result;
		else 
			return null;
	}
	
	@Override
	public List<String> getWikidataPlaceIdWithLabelAltLabel(String label, String language) throws Exception {
		String query = String.format(placeLabelAltLabelQueryString, label, language);
		List<String> result = processWikidataSparqlResponse(createRequest(baseUrlSparql, Collections.singletonMap("query", query)));
		if(result!=null)
			return result;
		else 
			return null;
	}
	
	@Override
	public List<String> getWikidataAgentIdWithLabel(String label, String language) throws Exception{
		String query = String.format(agentlabelQueryString, label, language);
		List<String> result = processWikidataSparqlResponse(createRequest(baseUrlSparql, Collections.singletonMap("query", query)));
		if(result!=null)
			return result;
		else 
			return null;
	}
	
	@Override
	public List<String> getWikidataAgentIdWithLabelAltLabel(String label, String language) throws Exception{
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
	
	private String createRequestWikiId(String wikidataId, int... retry) throws Exception {
		int retryArgs[] = retry;
		try {
			String Q_identifier = wikidataId.substring(wikidataId.lastIndexOf("/") + 1);			
			URIBuilder builder = new URIBuilder(configuration.getEnrichWikidataJsonBaseUrl() + Q_identifier + ".json");

			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			HttpGet request = new HttpGet(builder.build());
			request.addHeader("content-type", "application/json");
			request.addHeader("accept", "application/json");
			HttpResponse result = httpClient.execute(request);
			if(HttpStatus.SC_OK==result.getStatusLine().getStatusCode()) {
				String responeString = EntityUtils.toString(result.getEntity(), "UTF-8");
				if(responeString.contains("entities")) {
					return responeString;
				}
				else {
					throw new FunctionalRuntimeException("Wikidata response json is invalid (does not contain \"entities\").");
				}
			}
			else {
				throw new FunctionalRuntimeException("Wikidata response for the wikidata id: " + wikidataId + " failed, and did not return the 200 status code.");
			}
			
		} catch (URISyntaxException | IOException e) {
			// TODO Auto-generated catch block
			logger.log(Level.ERROR, "Exception during the wikidata service call for wikidata id: " + wikidataId, e);
			//retry
			if(retryArgs.length==0) {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e1) {
				}
				return createRequestWikiId(wikidataId, 1);
			}
			else if(retryArgs.length==1 && retryArgs[0]<3) {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e1) {
				}
				return createRequestWikiId(wikidataId, retryArgs[0]+1);				
			}
			else {
				logger.log(Level.ERROR, "Data could not be fetched from wikidata service after a couple of tries for wikidata id: " + wikidataId, e);
				throw e;
			}
		}

	}

	private String createRequest(String baseUrl, Map<String, String> params, int... retry) throws Exception {
		int retryArgs[] = retry;
		try {
			URIBuilder builder = new URIBuilder(baseUrl);
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
			logger.log(Level.ERROR, "Exception during the wikidata service call with base url: " + baseUrl, e);
			//retry
			if(retryArgs.length==0) {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e1) {
				}
				return createRequest(baseUrl, params, 1);
			}
			else if(retryArgs.length==1 && retryArgs[0]<3) {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e1) {
				}
				return createRequest(baseUrl, params, retryArgs[0]+1);				
			}
			else {
				logger.log(Level.ERROR, "Data could not be fetched from wikidata service after a couple of tries for base url: " + baseUrl, e);
				throw e;
			}
		}

	}
	
	@Override
	public String getWikidataJSONFromRemote(String WikidataID) throws Exception {
		return createRequestWikiId(WikidataID);
	}

	@Override
	public List<List<String>> getJSONFieldFromWikidataJSON(String WikidataJSON, String field) {
		List<List<String>> result = new ArrayList<List<String>>();
		try {
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
			return result;
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
					//toAddList.add(obj.getString(fieldParts[0]));
					if(obj.has(fieldParts[0])) {
						List<String> toAddList = new ArrayList<String>();
						toAddList.add(obj.get(fieldParts[0]).toString());
						result.add(toAddList);
					}
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
		else {
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
	}
	
	@Override
	public WikidataEntity getWikidataEntityAndSaveToLocalCache(String wikidataURL, String type, boolean matchType) throws Exception {
		
		//trying to get the wikidata json from a local cache file, if does not exist fetch from wikidata and save into a cache file
		String wikidataJSONLocalCache = HelperFunctions.getWikidataJsonFromLocalFileCache(wikidataDirectory, wikidataURL);
		String wikidataJSON=wikidataJSONLocalCache;
		if(StringUtils.isBlank(wikidataJSON)) 	
		{
			logger.debug("Wikidata entity does not exist in a local file cache!");
			wikidataJSON = getWikidataJSONFromRemote(wikidataURL);
		}

		if(matchType) {
			if(matchInstanceOfProperty(wikidataJSON, type)) {
				if(wikidataJSONLocalCache==null) {
					HelperFunctions.saveWikidataJsonToLocalFileCache(wikidataDirectory, wikidataURL, wikidataJSON);
					logger.debug("Wikidata entity is successfully saved to a local file cache!");
				}
				return getWikidataEntity(wikidataURL,wikidataJSON,type);
			}
			else {
				return null;
			}
		}
		else {
			if(wikidataJSONLocalCache==null) {
				HelperFunctions.saveWikidataJsonToLocalFileCache(wikidataDirectory, wikidataURL, wikidataJSON);
				logger.debug("Wikidata entity is successfully saved to a local file cache!");
			}
			return getWikidataEntity(wikidataURL,wikidataJSON,type);
		}
	}
	
	@Override
	public WikidataEntity getWikidataEntity (String wikidataURL, String WikidataJSON, String type)
	{
		List<List<String>> jsonElement;
		
		if(type.equalsIgnoreCase(NERClassification.AGENT.toString()))
		{
			WikidataAgent newWikidataAgent = new WikidataAgentImpl ();
			
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON, EnrichmentConstants.ALTLABEL_JSONPROP);
			//converting the "jsonElement" to the appropriate object to be saved in Solr
			Map<String,List<String>> altLabelMap = null;
			if(!jsonElement.isEmpty()) 
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
			if(!jsonElement.isEmpty())
			{
				country=EnrichmentConstants.WIKIDATA_ENTITY_BASE_URL + jsonElement.get(0).get(0);
			}
			if(country!=null) newWikidataAgent.setCountry(country);

			String [] dateBirthArray = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,EnrichmentConstants.DATEOFBIRTH_JSONPROP);
			if(!jsonElement.isEmpty()) 
			{		
				dateBirthArray = new String [1];
				dateBirthArray[0]=jsonElement.get(0).get(0);				
			}
			if(dateBirthArray!=null) newWikidataAgent.setDateOfBirth(dateBirthArray);
			
			String [] dateDeathArray = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,EnrichmentConstants.DATEOFDEATH_JSONPROP);
			if(!jsonElement.isEmpty())
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
			if(!jsonElement.isEmpty())
			{
				depiction = "http://commons.wikimedia.org/wiki/Special:FilePath/" + jsonElement.get(0).get(0);
			}
			if(depiction!=null) newWikidataAgent.setDepiction(depiction);
			
			Map<String,List<String>> descriptionsMap = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,EnrichmentConstants.DESCRIPTION_JSONPROP);
			if(!jsonElement.isEmpty())
			{
				descriptionsMap = HelperFunctions.convertListOfListOfStringToMapOfStringAndListOfString(jsonElement);
			}
			if(descriptionsMap!=null) newWikidataAgent.setDescription(descriptionsMap);
			
			newWikidataAgent.setEntityId(wikidataURL);			
			
			newWikidataAgent.setInternalType(type);
			
			
			String modificationDate = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,EnrichmentConstants.MODIFICATIONDATE_JSONPROP);
			if(!jsonElement.isEmpty()) 
			{
				modificationDate = jsonElement.get(0).get(0);
			}
			if(modificationDate!=null) newWikidataAgent.setModificationDate(modificationDate);
			
			
			String [] occupationArray = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,EnrichmentConstants.PROFESSIONOROCCUPATION_JSONPROP);
			if(!jsonElement.isEmpty()) 
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
			if(!jsonElement.isEmpty())
			{ 
				prefLabelMap = HelperFunctions.convertListOfListOfStringToMapOfStringAndListOfString(jsonElement);
			}
			if(prefLabelMap!=null) newWikidataAgent.setPrefLabel(prefLabelMap);
			
			
			String [] sameAsArray = null;		
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,EnrichmentConstants.SAMEAS_JSONPROP);
			if(!jsonElement.isEmpty())	
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
		else if(type.equalsIgnoreCase(NERClassification.PLACE.toString()))
		{
			
			WikidataPlace newWikidataPlace = new WikidataPlaceImpl ();
			
			Map<String,List<String>> altLabelMap = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,EnrichmentConstants.ALTLABEL_JSONPROP); 
			if(!jsonElement.isEmpty())
			{
				altLabelMap = HelperFunctions.convertListOfListOfStringToMapOfStringAndListOfString(jsonElement);
				
			}
			if(altLabelMap!=null) newWikidataPlace.setAltLabel(altLabelMap);
			
			String country = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,EnrichmentConstants.PLACE_COUNTRY_JSONPROP);
			if(!jsonElement.isEmpty()) 
			{
				country = EnrichmentConstants.WIKIDATA_ENTITY_BASE_URL + jsonElement.get(0).get(0);
			}
			if(country!=null) newWikidataPlace.setCountry(country);
			
			Float latitude = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,EnrichmentConstants.LATITUDE_JSONPROP);
			if(!jsonElement.isEmpty()) 
			{
				latitude = Float.valueOf(jsonElement.get(0).get(0));
			}
			if(latitude!=null) newWikidataPlace.setLatitude(latitude);

			
			Float longitude = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,EnrichmentConstants.LONGITUDE_JSONPROP);
			if(!jsonElement.isEmpty()) 
			{
				longitude = Float.valueOf(jsonElement.get(0).get(0));
			}
			if(longitude!=null) newWikidataPlace.setLongitude(longitude);

			String depiction = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,EnrichmentConstants.DEPICTION_JSONPROP);
			if(!jsonElement.isEmpty()) 
			{
				depiction = "http://commons.wikimedia.org/wiki/Special:FilePath/" + jsonElement.get(0).get(0);
			}
			if(depiction!=null) newWikidataPlace.setDepiction(depiction);
		
			Map<String,List<String>> descriptionsMap = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,EnrichmentConstants.DESCRIPTION_JSONPROP);
			if(!jsonElement.isEmpty()) 
			{
				descriptionsMap = HelperFunctions.convertListOfListOfStringToMapOfStringAndListOfString(jsonElement);
			}
			if(descriptionsMap!=null) newWikidataPlace.setDescription(descriptionsMap);
			
			newWikidataPlace.setEntityId(wikidataURL);
			
			newWikidataPlace.setInternalType(type);
			
			String modificationDate = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,EnrichmentConstants.MODIFICATIONDATE_JSONPROP);
			if(!jsonElement.isEmpty()) 
			{
				modificationDate = jsonElement.get(0).get(0);
			}
			if(modificationDate!=null) newWikidataPlace.setModificationDate(modificationDate);
		
			
			String logo = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,EnrichmentConstants.LOGO_JSONPROP);
			if(!jsonElement.isEmpty()) 
			{
				logo = jsonElement.get(0).get(0);
			}
			if(logo!=null) newWikidataPlace.setLogo(logo);
		
			Map<String,List<String>> prefLabelMap = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,EnrichmentConstants.PREFLABEL_JSONPROP);
			if(!jsonElement.isEmpty())
			{
				prefLabelMap = HelperFunctions.convertListOfListOfStringToMapOfStringAndListOfString(jsonElement);
			}
			if(prefLabelMap!=null) newWikidataPlace.setPrefLabel(prefLabelMap);
			
			String [] sameAsArray = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,EnrichmentConstants.SAMEAS_JSONPROP);
			if(!jsonElement.isEmpty()) 
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
		else if(type.equalsIgnoreCase(NERClassification.ORGANIZATION.toString())) {
			
			WikidataOrganization newWikidataOrganization = new WikidataOrganizationImpl();
			
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON, EnrichmentConstants.ALTLABEL_JSONPROP);
			//converting the "jsonElement" to the appropriate object to be saved in Solr
			Map<String,List<String>> altLabelMap = null;
			if(!jsonElement.isEmpty()) 
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
			if(altLabelMap!=null) newWikidataOrganization.setAltLabel(altLabelMap);

			String country = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,EnrichmentConstants.PLACE_COUNTRY_JSONPROP);
			if(!jsonElement.isEmpty())
			{
				country=EnrichmentConstants.WIKIDATA_ENTITY_BASE_URL + jsonElement.get(0).get(0);
			}
			if(country!=null) newWikidataOrganization.setCountry(country);
						
			String depiction = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,EnrichmentConstants.DEPICTION_JSONPROP);
			if(!jsonElement.isEmpty())
			{
				depiction = "http://commons.wikimedia.org/wiki/Special:FilePath/" + jsonElement.get(0).get(0);
			}
			if(depiction!=null) newWikidataOrganization.setDepiction(depiction);
			
			Map<String,List<String>> descriptionsMap = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,EnrichmentConstants.DESCRIPTION_JSONPROP);
			if(!jsonElement.isEmpty())
			{
				descriptionsMap = HelperFunctions.convertListOfListOfStringToMapOfStringAndListOfString(jsonElement);
			}
			if(descriptionsMap!=null) newWikidataOrganization.setDescription(descriptionsMap);
			
			newWikidataOrganization.setEntityId(wikidataURL);			
			
			newWikidataOrganization.setInternalType(type);
			
			String modificationDate = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,EnrichmentConstants.MODIFICATIONDATE_JSONPROP);
			if(!jsonElement.isEmpty()) 
			{
				modificationDate = jsonElement.get(0).get(0);
			}
			if(modificationDate!=null) newWikidataOrganization.setModificationDate(modificationDate);			
			
			Map<String,List<String>> prefLabelMap = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,EnrichmentConstants.PREFLABEL_JSONPROP);
			if(!jsonElement.isEmpty())
			{ 
				prefLabelMap = HelperFunctions.convertListOfListOfStringToMapOfStringAndListOfString(jsonElement);
			}
			if(prefLabelMap!=null) newWikidataOrganization.setPrefLabel(prefLabelMap);
			
			
			String [] sameAsArray = null;		
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,EnrichmentConstants.SAMEAS_JSONPROP);
			if(!jsonElement.isEmpty())	
			{
				sameAsArray = new String [jsonElement.size()];
				for(int i=0;i<jsonElement.size();i++)
				{
					sameAsArray[i]=jsonElement.get(i).get(0);
				}				
			}
			if(sameAsArray!=null) newWikidataOrganization.setSameAs(sameAsArray);
			
			String [] officialWebsite = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,EnrichmentConstants.OFFICIAL_WEBSITE_JSONPROP);
			if(!jsonElement.isEmpty()) 
			{
				officialWebsite = new String [jsonElement.size()];
				for(int i=0;i<jsonElement.size();i++)
				{
					officialWebsite[i] = jsonElement.get(i).get(0);
				}				
			}
			if(officialWebsite!=null) newWikidataOrganization.setOfficialWebsite(officialWebsite);
			
			String VIAF_ID = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,EnrichmentConstants.VIAF_ID_JSONPROP);
			if(!jsonElement.isEmpty())
			{
				VIAF_ID=jsonElement.get(0).get(0);
			}
			if(VIAF_ID!=null) newWikidataOrganization.setVIAF_ID(VIAF_ID);

			String ISNI = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,EnrichmentConstants.ISNI_JSONPROP);
			if(!jsonElement.isEmpty())
			{
				ISNI=jsonElement.get(0).get(0);
			}
			if(ISNI!=null) newWikidataOrganization.setISNI(ISNI);

			String logo = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,EnrichmentConstants.LOGO_JSONPROP);
			if(!jsonElement.isEmpty()) 
			{
				logo = jsonElement.get(0).get(0);
			}
			if(logo!=null) newWikidataOrganization.setLogo(logo);

			String inception = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,EnrichmentConstants.INCEPTION_JSONPROP);
			if(!jsonElement.isEmpty()) 
			{
				inception = jsonElement.get(0).get(0);
			}
			if(inception!=null) newWikidataOrganization.setInception(inception);

			String headquartersLoc = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,EnrichmentConstants.HEADQUARTERS_LOC_JSONPROP);
			if(!jsonElement.isEmpty()) 
			{
				headquartersLoc = EnrichmentConstants.WIKIDATA_ENTITY_BASE_URL + jsonElement.get(0).get(0);
			}
			if(headquartersLoc!=null) newWikidataOrganization.setHeadquartersLoc(headquartersLoc);

			String headquartersPostalCode = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,EnrichmentConstants.HEADQUARTERS_POSTAL_CODE_JSONPROP);
			if(!jsonElement.isEmpty()) 
			{
				headquartersPostalCode = jsonElement.get(0).get(0);
			}
			if(headquartersPostalCode!=null) newWikidataOrganization.setHeadquartersPostalCode(headquartersPostalCode);

			String headquartersStreetAddress = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,EnrichmentConstants.HEADQUARTERS_STREET_ADDRESS_JSONPROP);
			if(!jsonElement.isEmpty()) 
			{
				headquartersStreetAddress = jsonElement.get(0).get(0);
			}
			if(headquartersStreetAddress!=null) newWikidataOrganization.setHeadquartersStreetAddress(headquartersStreetAddress);

			Float headquartersLatitude = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,EnrichmentConstants.HEADQUARTERS_LATITUDE_JSONPROP);
			if(!jsonElement.isEmpty()) 
			{
				headquartersLatitude = Float.valueOf(jsonElement.get(0).get(0));
			}
			if(headquartersLatitude!=null) newWikidataOrganization.setHeadquartersLatitude(headquartersLatitude);

			Float headquartersLongitude = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,EnrichmentConstants.HEADQUARTERS_LONGITUDE_JSONPROP);
			if(!jsonElement.isEmpty()) 
			{
				headquartersLongitude = Float.valueOf(jsonElement.get(0).get(0));
			}
			if(headquartersLongitude!=null) newWikidataOrganization.setHeadquartersLongitude(headquartersLongitude);

			String [] industry = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,EnrichmentConstants.INDUSTRY_JSONPROP);
			if(!jsonElement.isEmpty()) 
			{
				industry = new String [jsonElement.size()];
				for(int i=0;i<jsonElement.size();i++)
				{
					industry[i] = EnrichmentConstants.WIKIDATA_ENTITY_BASE_URL + jsonElement.get(i).get(0);
				}				
			}
			if(industry!=null) newWikidataOrganization.setIndustry(industry);
			
			String [] phone = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,EnrichmentConstants.PHONE_JSONPROP);
			if(!jsonElement.isEmpty()) 
			{
				phone = new String [jsonElement.size()];
				for(int i=0;i<jsonElement.size();i++)
				{
					phone[i] = jsonElement.get(i).get(0);
				}				
			}
			if(phone!=null) newWikidataOrganization.setPhone(phone);
			
			return newWikidataOrganization;
		}
		else {
			return null;
		}
	}
	
	@Override
	public String getDescriptionEnFromWikidataJson(String wikidataJson) {		
		Map<String,List<String>> descriptionsMap = null;
		List<List<String>> jsonElement = getJSONFieldFromWikidataJSON(wikidataJson,"descriptions.*.*");
		if(!jsonElement.isEmpty())
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
		if(!jsonElement.isEmpty())
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
//					String prefLabelSingleUTF8LowerCase = prefLabelSingleUTF8.toLowerCase();
//					List<String> prefLabelSingleSplitted = new ArrayList<>(Arrays.asList(prefLabelSingleUTF8LowerCase.split("\\s+")));
					String nameUTF8 = new String(name.getBytes(), StandardCharsets.UTF_8);
//					String nameUTF8LowerCase = nameUTF8.toLowerCase();
//					List<String> nameSplitted = new ArrayList<>(Arrays.asList(nameUTF8LowerCase.split("\\s+")));
//					if(prefLabelSingleSplitted.containsAll(nameSplitted) 
//						&& prefLabelSingleSplitted.size()==nameSplitted.size()) {
//						return true;
//					}
					if(prefLabelSingleUTF8.equalsIgnoreCase(nameUTF8)) {
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
		if(!jsonElement.isEmpty()) 
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
//					String altLabelSingleUTF8LowerCase = altLabelSingleUTF8.toLowerCase();
//					List<String> altLabelSingleSplitted = new ArrayList<>(Arrays.asList(altLabelSingleUTF8LowerCase.split("\\s+")));
					String nameUTF8 = new String(name.getBytes(), StandardCharsets.UTF_8);
//					String nameUTF8LowerCase = nameUTF8.toLowerCase();
//					List<String> nameSplitted = new ArrayList<>(Arrays.asList(nameUTF8LowerCase.split("\\s+")));
//					if(altLabelSingleSplitted.containsAll(nameSplitted) 
//						&& altLabelSingleSplitted.size()==nameSplitted.size()) {
//						return true;
//					}
					if(altLabelSingleUTF8.equalsIgnoreCase(nameUTF8)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean matchInstanceOfProperty(String wikidataJSONResponse, String type) {
		List<List<String>> jsonElement = getJSONFieldFromWikidataJSON(wikidataJSONResponse,EnrichmentConstants.INSTANCE_OF_JSONPROP);
		if(!jsonElement.isEmpty())	
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
				else if(type.equalsIgnoreCase(NERClassification.ORGANIZATION.toString())) {
					if(wikidataSubclassesForOrganization.contains(jsonElement.get(i).get(0))) {
						return true;
					}
				}
			}				
		}
		return false;
	}
	
	private String matchPrefLabelForPreferredWikidataId(NamedEntityImpl namedEntity, String wikidataId, String wikidataJSONLocalCache, String wikidataJSON,
			boolean matchType, List<String> savedWikidataIds, List<String> savedWikidataJsons, List<Boolean> isWikidataFromLocalCache) throws IOException, SolrServiceException {
		if(matchType) {
			if(matchInstanceOfProperty(wikidataJSON, namedEntity.getType())) {
				if(matchPrefLabelInWikidata(wikidataJSON, namedEntity.getLabel())) {
					if(wikidataJSONLocalCache==null) {
						//save to local cache and solr
						HelperFunctions.saveWikidataJsonToLocalFileCache(wikidataDirectory, wikidataId, wikidataJSON);
						solrWikidataEntityService.storeWikidataEntity(getWikidataEntity(wikidataId,wikidataJSON,namedEntity.getType()), namedEntity.getType());
					}
					else if(! solrWikidataEntityService.existWikidataURL(wikidataId)) {
						solrWikidataEntityService.storeWikidataEntity(getWikidataEntity(wikidataId,wikidataJSONLocalCache,namedEntity.getType()), namedEntity.getType());
					}
					return wikidataId;
				}
				//save to do the rest of the checks below
				else {
					savedWikidataIds.add(wikidataId);
					savedWikidataJsons.add(wikidataJSON);
					if(wikidataJSONLocalCache==null) {
						isWikidataFromLocalCache.add(false);
					}
					else {
						isWikidataFromLocalCache.add(true);
					}
				}
			}
		}
		else {
			if(matchPrefLabelInWikidata(wikidataJSON, namedEntity.getLabel())) {
				if(wikidataJSONLocalCache==null) {
					//save to local cache and solr
					HelperFunctions.saveWikidataJsonToLocalFileCache(wikidataDirectory, wikidataId, wikidataJSON);
					solrWikidataEntityService.storeWikidataEntity(getWikidataEntity(wikidataId,wikidataJSON,namedEntity.getType()), namedEntity.getType());
				}
				else if(! solrWikidataEntityService.existWikidataURL(wikidataId)) {
					solrWikidataEntityService.storeWikidataEntity(getWikidataEntity(wikidataId,wikidataJSONLocalCache,namedEntity.getType()), namedEntity.getType());
				}
				return wikidataId;
			}
			//save to do the rest of the checks below
			else {
				savedWikidataIds.add(wikidataId);
				savedWikidataJsons.add(wikidataJSON);
				if(wikidataJSONLocalCache==null) {
					isWikidataFromLocalCache.add(false);
				}
				else {
					isWikidataFromLocalCache.add(true);
				}
			}
		}
		return null;
	}
	
	private String checkPreferredWikiIdPrefLabelMatch(List<String> wikiIds, NamedEntityImpl namedEntity, boolean matchType, List<String> savedWikidataIds, List<String> savedWikidataJsons, List<Boolean> isWikidataFromLocalCache) throws Exception {
		if(wikiIds==null) {
			return null;
		}
		for(String wikidataId : wikiIds) {
			String wikidataJSONLocalCache = HelperFunctions.getWikidataJsonFromLocalFileCache(wikidataDirectory, wikidataId);
			String wikidataJSON=null;
			if(! StringUtils.isBlank(wikidataJSONLocalCache)) {
				wikidataJSON=wikidataJSONLocalCache;
			}
			else {
				wikidataJSON=getWikidataJSONFromRemote(wikidataId);
			}

			if(validWikidataPage(wikidataJSON)) {		
				String foundPreferredId = matchPrefLabelForPreferredWikidataId(namedEntity, wikidataId, wikidataJSONLocalCache, wikidataJSON, matchType, savedWikidataIds, savedWikidataJsons, isWikidataFromLocalCache);
				if(foundPreferredId!=null) {
					return foundPreferredId;
				}
			}
		}
		return null;
	}
	
	private String checkPreferredWikiIdAltLabelMatch(NamedEntityImpl namedEntity, List<String> savedWikidataIds, List<String> savedWikidataJsons, List<Boolean> isWikidataFromLocalCache) throws Exception {
		for(int i=0;i<savedWikidataJsons.size();i++) {
			if(matchAltLabelInWikidata(savedWikidataJsons.get(i), namedEntity.getLabel())) {
				if(! isWikidataFromLocalCache.get(i)) {
					//save to local cache and solr
					HelperFunctions.saveWikidataJsonToLocalFileCache(wikidataDirectory, savedWikidataIds.get(i), savedWikidataJsons.get(i));
					solrWikidataEntityService.storeWikidataEntity(getWikidataEntity(savedWikidataIds.get(i),savedWikidataJsons.get(i),namedEntity.getType()), namedEntity.getType());
				}					
				else if(! solrWikidataEntityService.existWikidataURL(savedWikidataIds.get(i))) {
					solrWikidataEntityService.storeWikidataEntity(getWikidataEntity(savedWikidataIds.get(i),savedWikidataJsons.get(i),namedEntity.getType()), namedEntity.getType());
				}
				return savedWikidataIds.get(i);
			}
		}
		return null;
	}

	/*
	 * For each position entity (defined for a triple {storyId,ItemId,fieldUsedForNER}), check the offsetsTranslatedText map values,
	 * and if there is a value with both ner tools compute the prefWikiIdAll, if not and there is a value with only dbpedia tool, compute
	 * the prefWikiIdDbpedia, and if that also not but there is a value with only stanford tool, compute the prefWikiIdStanford. This means that
	 * for generating the annotations, if the named entity is found by both tools, the socre will be the highest, then found by dbpedia, and at the end
	 * found by stanford. This function needs to be called when all named entities for all analyzed stories are computed.
	 */
	public boolean computePreferredWikidataIds(NamedEntityImpl namedEntity, List<PositionEntityImpl> positions, boolean matchType) throws Exception { 
		boolean updated=false;
			
		boolean prefWikiIdAll_computed=(namedEntity.getPrefWikiIdBothStanfordAndDbpedia()!=null) ? true : false;
		boolean prefWikiIdDbpedia_computed=(namedEntity.getPrefWikiIdOnlyDbpedia()!=null) ? true : false;
		boolean prefWikiIdStanford_computed=(namedEntity.getPrefWikiIdOnlyStanford()!=null) ? true : false;
		if(prefWikiIdAll_computed && prefWikiIdDbpedia_computed && prefWikiIdStanford_computed) {
			return updated;
		}

		for(PositionEntityImpl pe : positions) {
			//find the position which is found by both stanford and dbpedia tools
			Optional<Integer> optNerToolsBoth = pe.getOffsetsTranslatedText().entrySet().stream()
				.filter(e -> (e.getValue().contains(NerTools.Stanford.getStringValue()) && e.getValue().contains(NerTools.Dbpedia.getStringValue())))
				.map(Map.Entry::getKey)
				.findFirst();
			//find the position which is found only by stanford tool
			Optional<Integer> optNerToolsStanford = pe.getOffsetsTranslatedText().entrySet().stream()
					.filter(e -> e.getValue().contains(NerTools.Stanford.getStringValue()))
					.map(Map.Entry::getKey)
					.findFirst();
			//find the position which is found only by dbpedia tool
			Optional<Integer> optNerToolsDbpedia = pe.getOffsetsTranslatedText().entrySet().stream()
					.filter(e -> e.getValue().contains(NerTools.Dbpedia.getStringValue()))
					.map(Map.Entry::getKey)
					.findFirst();
						
			List<String> savedWikidataIds=new ArrayList<>();
			List<String> savedWikidataJsons=new ArrayList<>();
			List<Boolean> isWikidataFromLocalCache=new ArrayList<>();
			String preferredWikiId=null;
			//compute the crossvalidated pref wiki id
			if(optNerToolsBoth.isPresent()) {
				if(! prefWikiIdAll_computed) {
					prefWikiIdAll_computed=true;
					List<String> crossValidatedIds = new ArrayList<>();
					if(namedEntity.getDbpediaWikidataIds()!=null && namedEntity.getWikidataSearchIds()!=null) {
						crossValidatedIds.addAll(namedEntity.getDbpediaWikidataIds().stream().filter(namedEntity.getWikidataSearchIds()::contains).collect(Collectors.toList()));
					}
					if(! crossValidatedIds.isEmpty()) {
						//check pref label match
						preferredWikiId=checkPreferredWikiIdPrefLabelMatch(crossValidatedIds, namedEntity, matchType, savedWikidataIds, savedWikidataJsons, isWikidataFromLocalCache);
						if(preferredWikiId!=null) {
							updated=true;
							namedEntity.setPrefWikiIdBothStanfordAndDbpedia(preferredWikiId);
							namedEntity.setPrefWikiIdBothStanfordAndDbpedia_status(EnrichmentConstants.PREF_WIKI_ID_STATUS_CROSSVALID_PREF_LABEL);
						}
						else {
							//then check alt label match
							preferredWikiId=checkPreferredWikiIdAltLabelMatch(namedEntity, savedWikidataIds, savedWikidataJsons, isWikidataFromLocalCache);
							if(preferredWikiId!=null) {
								updated=true;
								namedEntity.setPrefWikiIdBothStanfordAndDbpedia(preferredWikiId);
								namedEntity.setPrefWikiIdBothStanfordAndDbpedia_status(EnrichmentConstants.PREF_WIKI_ID_STATUS_CROSSVALID_ALT_LABEL);
							}
						}
					}		
					
					if(preferredWikiId==null) {
						prefWikiIdStanford_computed=true;
						//check matches in the wikidata search ids
						savedWikidataIds.clear();
						savedWikidataJsons.clear();
						isWikidataFromLocalCache.clear();
						preferredWikiId=checkPreferredWikiIdPrefLabelMatch(namedEntity.getWikidataSearchIds(), namedEntity, matchType, savedWikidataIds, savedWikidataJsons, isWikidataFromLocalCache);
						if(preferredWikiId!=null) {
							updated=true;
							namedEntity.setPrefWikiIdBothStanfordAndDbpedia(preferredWikiId);
							namedEntity.setPrefWikiIdBothStanfordAndDbpedia_status(EnrichmentConstants.PREF_WIKI_ID_STATUS_STANFORD_VALID_PREF_LABEL);
	
							//set also the prefWikiIdStanford
							namedEntity.setPrefWikiIdOnlyStanford(preferredWikiId);
							namedEntity.setPrefWikiIdOnlyStanford_status(EnrichmentConstants.PREF_WIKI_ID_STATUS_STANFORD_VALID_PREF_LABEL);
							
						}
						else {
							//then check alt label match
							preferredWikiId=checkPreferredWikiIdAltLabelMatch(namedEntity, savedWikidataIds, savedWikidataJsons, isWikidataFromLocalCache);
							if(preferredWikiId!=null) {
								updated=true;
								namedEntity.setPrefWikiIdBothStanfordAndDbpedia(preferredWikiId);
								namedEntity.setPrefWikiIdBothStanfordAndDbpedia_status(EnrichmentConstants.PREF_WIKI_ID_STATUS_STANFORD_VALID_ALT_LABEL);
	
								//set also the prefWikiIdStanford
								namedEntity.setPrefWikiIdOnlyStanford(preferredWikiId);
								namedEntity.setPrefWikiIdOnlyStanford_status(EnrichmentConstants.PREF_WIKI_ID_STATUS_STANFORD_VALID_ALT_LABEL);
							}
						}
					}
					
					//in case no preferred id is found either by cross-validation or only in with the wiki search (this is probably the case which will never happen in praxis)
					if(preferredWikiId==null) {
						prefWikiIdDbpedia_computed=true;
						
						//check matches in the dbpedia wiki ids
						preferredWikiId=checkPreferredWikiIdPrefLabelMatch(namedEntity.getDbpediaWikidataIds(), namedEntity, matchType, savedWikidataIds, savedWikidataJsons, isWikidataFromLocalCache);
						if(preferredWikiId!=null) {
							updated=true;
							namedEntity.setPrefWikiIdBothStanfordAndDbpedia(preferredWikiId);
							namedEntity.setPrefWikiIdBothStanfordAndDbpedia_status(EnrichmentConstants.PREF_WIKI_ID_STATUS_DBP_VALID_PREF_LABEL);
	
							//set also the prefWikiIdOnlyDbpedia
							namedEntity.setPrefWikiIdOnlyDbpedia(preferredWikiId);
							namedEntity.setPrefWikiIdOnlyDbpedia_status(EnrichmentConstants.PREF_WIKI_ID_STATUS_DBP_VALID_PREF_LABEL);
						}
						else {
							//then check alt label match
							preferredWikiId=checkPreferredWikiIdAltLabelMatch(namedEntity, savedWikidataIds, savedWikidataJsons, isWikidataFromLocalCache);
							if(preferredWikiId!=null) {
								updated=true;
								namedEntity.setPrefWikiIdBothStanfordAndDbpedia(preferredWikiId);
								namedEntity.setPrefWikiIdBothStanfordAndDbpedia_status(EnrichmentConstants.PREF_WIKI_ID_STATUS_DBP_VALID_ALT_LABEL);
	
								//set also the prefWikiIdOnlyDbpedia
								namedEntity.setPrefWikiIdOnlyDbpedia(preferredWikiId);
								namedEntity.setPrefWikiIdOnlyDbpedia_status(EnrichmentConstants.PREF_WIKI_ID_STATUS_DBP_VALID_ALT_LABEL);
							}	
						}
					}
				}
			}	
			//compute the pref wiki id for dbpedia
			else if(optNerToolsDbpedia.isPresent()) {
				if(! prefWikiIdDbpedia_computed) {
					prefWikiIdDbpedia_computed=true;
					
					//check matches in the dbpedia wiki ids
					preferredWikiId=checkPreferredWikiIdPrefLabelMatch(namedEntity.getDbpediaWikidataIds(), namedEntity, matchType, savedWikidataIds, savedWikidataJsons, isWikidataFromLocalCache);
					if(preferredWikiId!=null) {
						updated=true;
						namedEntity.setPrefWikiIdOnlyDbpedia(preferredWikiId);
						namedEntity.setPrefWikiIdOnlyDbpedia_status(EnrichmentConstants.PREF_WIKI_ID_STATUS_DBP_VALID_PREF_LABEL);
					}
					else {
						//then check alt label match
						preferredWikiId=checkPreferredWikiIdAltLabelMatch(namedEntity, savedWikidataIds, savedWikidataJsons, isWikidataFromLocalCache);
						if(preferredWikiId!=null) {
							updated=true;
							namedEntity.setPrefWikiIdOnlyDbpedia(preferredWikiId);
							namedEntity.setPrefWikiIdOnlyDbpedia_status(EnrichmentConstants.PREF_WIKI_ID_STATUS_DBP_VALID_ALT_LABEL);
						}	
					}
				}
			}
			//compute the pref wiki id for stanford
			else if(optNerToolsStanford.isPresent()) {	
				if(! prefWikiIdStanford_computed) {
					prefWikiIdStanford_computed=true;
					
					//check matches in the wiki search ids
					preferredWikiId=checkPreferredWikiIdPrefLabelMatch(namedEntity.getWikidataSearchIds(), namedEntity, matchType, savedWikidataIds, savedWikidataJsons, isWikidataFromLocalCache);
					if(preferredWikiId!=null) {
						updated=true;
						namedEntity.setPrefWikiIdOnlyStanford(preferredWikiId);
						namedEntity.setPrefWikiIdOnlyStanford_status(EnrichmentConstants.PREF_WIKI_ID_STATUS_STANFORD_VALID_PREF_LABEL);
					}
					else {
						//then check alt label match
						preferredWikiId=checkPreferredWikiIdAltLabelMatch(namedEntity, savedWikidataIds, savedWikidataJsons, isWikidataFromLocalCache);
						if(preferredWikiId!=null) {
							updated=true;
							namedEntity.setPrefWikiIdOnlyStanford(preferredWikiId);
							namedEntity.setPrefWikiIdOnlyStanford_status(EnrichmentConstants.PREF_WIKI_ID_STATUS_STANFORD_VALID_ALT_LABEL);
						}	
					}
				}
			}
			
			if(prefWikiIdAll_computed && prefWikiIdDbpedia_computed && prefWikiIdStanford_computed) {
				break;
			}
		}
		return updated;
	}

	public Set<String> readWikidataIdsFromQueryServiceOutput(String path) throws IOException {
		Set<String> wikidataIdentifiers = new HashSet<String>();
		String subclassesPlaceString = HelperFunctions.readFileFromResources(path);
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

	public Set<String> getWikidataSubclassesForOrganization() {
		return wikidataSubclassesForOrganization;
	}

	@Override
	@Async
	public CompletableFuture<String> saveWikidataJSONFromRemoteParallel(String wikidataId) throws Exception {
		String response = createRequestWikiId(wikidataId);
		HelperFunctions.saveWikidataJsonToLocalFileCache(wikidataDirectory, wikidataId, response);
		return CompletableFuture.completedFuture(response);
	}
	
}
