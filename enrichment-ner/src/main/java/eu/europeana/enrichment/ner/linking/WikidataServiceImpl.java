package eu.europeana.enrichment.ner.linking;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import eu.europeana.enrichment.common.commons.HelperFunctions;
import eu.europeana.enrichment.model.WikidataAgent;
import eu.europeana.enrichment.model.WikidataEntity;
import eu.europeana.enrichment.model.WikidataPlace;
import eu.europeana.enrichment.model.impl.WikidataAgentImpl;
import eu.europeana.enrichment.model.impl.WikidataEntityImpl;
import eu.europeana.enrichment.model.impl.WikidataPlaceImpl;

//import net.arnx.jsonic.JSONException;

public class WikidataServiceImpl implements WikidataService {
	
	Logger logger = LogManager.getLogger(getClass());

	private static final String baseUrlSparql = "https://query.wikidata.org/bigdata/namespace/wdq/sparql"; // "https://query.wikidata.org/sparql";
	
	/*
	 * Defining Wikidata sparql query construct for Geonames ID and Label search
	 */
	private String geonamesIdQueryString = "SELECT ?item WHERE { ?item wdt:P1566 \"%s\" . "
			+ "SERVICE wikibase:label { bd:serviceParam wikibase:language \"e\"}}";
	private String labelQueryString = "SELECT ?item WHERE { ?item rdfs:label \"%s\"@%s . "
			+ "SERVICE wikibase:label { bd:serviceParam wikibase:language \"e\"}}";

	/*
	 * Wikidata place search
	 * Geographical object: Q618123
	 * Geographic location: Q2221906
	 * Location: Q17334923
	 */
	private String placeLabelQueryString = "SELECT ?item ?description ?type WHERE {\r\n"
			+ "   ?item rdfs:label \"%s\"@%s; \r\n p:P31/ps:P31/wdt:P279* ?type; \r\n"
			+ "      schema:description ?description.\r\n"
			+ "  FILTER(?type in (wd:Q82794,wd:Q2075301,wd:Q7444568,wd:Q12371824,wd:Q18635222,wd:Q25345958,wd:Q56596860,wd:Q207326,wd:Q7444568)) \r\n"
			+ "  FILTER((LANG(?description)) = \"en\") }";

	/*
	 * Defines multilingual Wikidata query including alternative label and 
	 */
	private String placeLabelAltLabelQueryString = "SELECT distinct ?item ?type ?rank WHERE {\r\n"
			+ "  hint:Query hint:optimizer \"None\".\r\n" 
			+ "  values ?labels {\"%s\"@%s \"%s\"@%s}\r\n"
			+ "  ?item (rdfs:label|skos:altLabel) ?labels;\r\n"
			+ "  p:P31/ps:P31/wdt:P279* ?type. #;\r\n"
			+ "  FILTER(?type in (wd:Q82794,wd:Q2075301,wd:Q7444568,wd:Q12371824,wd:Q18635222,wd:Q25345958,wd:Q56596860,wd:Q207326,wd:Q7444568))}";
	/*
	 * Wikidata agent search query
	 */
	private String agentlabelQueryString = "SELECT ?item ?description WHERE {\r\n" 
			+ "  ?item wdt:P31 wd:Q5;\r\n rdfs:label \"%s\"@%s;\r\n" 
			+ "      schema:description ?description.\r\n" 
			+ "  FILTER((LANG(?description)) = \"en\") }";
	
	/*
	 * Wikidata keys for the response JSON
	 */
	private final String wikidataResultKey = "results";
	private final String wikidataBindingsKey = "bindings";
	private final String wikidataItemKey = "item";
	private final String wikidataDescriptionKey = "description";
	private final String wikidataValueKey = "value";
	
	
	private String wikidataDirectory;
	
	public WikidataServiceImpl (String wikidataPath)
	{
		wikidataDirectory = wikidataPath;
	}
	
	
	public Logger getLogger() {
		return logger;
	}

	@Override
	public List<String> getWikidataId(String geonameId) {
		String query = String.format(geonamesIdQueryString, geonameId);
		return processResponse(createRequest(baseUrlSparql, query));
	}

	@Override
	public List<String> getWikidataIdWithLabel(String label, String language) {
		String query = String.format(labelQueryString, label, language);
		return processResponse(createRequest(baseUrlSparql, query));
	}

	@Override
	public List<String> getWikidataPlaceIdWithLabel(String label, String language) {
		String query = String.format(placeLabelQueryString, label, language);
		return processResponse(createRequest(baseUrlSparql, query));
	}
	
	@Override
	public List<String> getWikidataPlaceIdWithLabelAltLabel(String label, String language) {
		String query = String.format(placeLabelAltLabelQueryString, label, language, label, "en");
		return processResponse(createRequest(baseUrlSparql, query));
	}
	
	@Override
	public List<String> getWikidataAgentIdWithLabel(String label, String language){
		String query = String.format(agentlabelQueryString, label, language);
		return processResponse(createRequest(baseUrlSparql, query));
	}

	/*
	 * This method process the response of the Wikidata sparql query and returns a
	 * list of Wikidata entity urls or null
	 * 
	 * @param response is the response body of the Wikidata sparql query
	 * 
	 * @return a list of Wikidata entity entity or empty list
	 */
	private List<String> processResponse(String reponse) {
		// TODO: implement function and add type to distinguish between Place/Location
		// and Agent/Person
		List<String> retValue = new ArrayList<>();
		if (reponse == null || reponse.equals(""))
		{
			logger.info("\n" + this.getClass().getSimpleName() + "The response to the Wikidata request is: null" + "\n");
			return retValue;
		}
				

		JSONObject responseJson = new JSONObject(reponse);
		if(!responseJson.has(wikidataResultKey))
		{
			logger.info("\n" + this.getClass().getSimpleName() + "The response to the Wikidata request is: " + retValue.toString() + "\n");
			return retValue;
		}
		JSONObject resultObj = responseJson.getJSONObject(wikidataResultKey);
		if(!resultObj.has(wikidataBindingsKey))
		{
			logger.info("\n" + this.getClass().getSimpleName() + "The response to the Wikidata request is: " + retValue.toString() + "\n");
			return retValue;
		}
		JSONArray bindingsArray = resultObj.getJSONArray(wikidataBindingsKey);
		for(int index = 0; bindingsArray.length() > index; index++) {
			JSONObject bindingsObj = bindingsArray.getJSONObject(index);
			if(!bindingsObj.has(wikidataItemKey))
				continue;
			JSONObject cityObj = bindingsObj.getJSONObject(wikidataItemKey);
			String cityValue = cityObj.getString(wikidataValueKey);
			if(!retValue.contains(cityValue))
				retValue.add(cityValue);
		}
		
		logger.info("\n" + this.getClass().getSimpleName() + "The response to the Wikidata request is: " + retValue.toString() + "\n");
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
	private String createRequest(String baseUrl, String query) {
		try {
			URIBuilder builder = new URIBuilder(baseUrl);
			//in case of calling a REST service for Wikidata JSON, the query parameter should be null/empty
			if(query!=null && !query.isEmpty())
			{
				builder.addParameter("query", query);
			}

			logger.info(this.getClass().getSimpleName() + ": " + query);
			logger.info(this.getClass().getSimpleName() + builder.toString());

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
			e.printStackTrace();
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
				logger.error("The analysed Wikidata JSON response does not contain the required JSON object: " + 
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
	
	
	private Map<String, List<String>> convertListOfListOfStringToMapOfStringAndListOfString (List<List<String>> jsonElement)
	{
		Map<String,List<String>> altLabelMap = new HashMap<String,List<String>>();
		for (List<String> altLabelElem : jsonElement)
		{
			if(altLabelMap.containsKey(altLabelElem.get(0)))
			{
				List<String> altLabelMapValue = altLabelMap.get(altLabelElem.get(0));
				altLabelMapValue.add(altLabelElem.get(1));
				altLabelMap.put(altLabelElem.get(0), altLabelMapValue);
			}
			else
			{
				List<String> newaltLabelMapValue = new ArrayList<String>();
				newaltLabelMapValue.add(altLabelElem.get(1));
				altLabelMap.put(altLabelElem.get(0), newaltLabelMapValue);
			}
			
		}
		return altLabelMap;
	}

	
	@Override
	public WikidataEntity getWikidataEntityUsingLocalCache(String wikidataURL, String type) throws IOException {
		
		//trying to get the wikidata json from a local cache file, if does not exist fetch from wikidata and save into a cache file
		String WikidataJSON = HelperFunctions.getWikidataJsonFromLocalFileCache(wikidataDirectory, wikidataURL);
		if(WikidataJSON==null) 	
		{
			logger.info("Wikidata entity does not exist in a local file cache!");
			WikidataJSON = getWikidataJSONFromWikidataID(wikidataURL);
			if(WikidataJSON==null || WikidataJSON.isEmpty()) return null;
			HelperFunctions.saveWikidataJsonToLocalFileCache(wikidataDirectory, wikidataURL, WikidataJSON);
			logger.info("Wikidata entity is successfully saved to a local file cache!");			
		}
		
		//check if the local cache wikidata entity is of the given type
		WikidataEntity wikiEntity = new WikidataEntityImpl();
		List<List<String>> jsonTypeElement = getJSONFieldFromWikidataJSON(WikidataJSON,wikiEntity.getInternalType_jsonProp());
		if(jsonTypeElement!=null && !jsonTypeElement.isEmpty())
		{
			if((jsonTypeElement.get(0).get(0).contains("place") && type.compareToIgnoreCase("place")==0) ||
					(!jsonTypeElement.get(0).get(0).contains("place") && type.compareToIgnoreCase("agent")==0))	
			{
				return getWikidataEntity(wikidataURL,WikidataJSON,type);
			}
		}
		
		return null;
		
	}
	
	@Override
	public WikidataEntity getWikidataEntity (String wikidataURL, String WikidataJSON, String type)
	{
		List<List<String>> jsonElement;
		
		if(type.compareToIgnoreCase("agent")==0)
		{
			WikidataAgent newWikidataAgent = new WikidataAgentImpl ();
			
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataAgent.getAltLabel_jsonProp());
			//converting the "jsonElement" to the appropriate object to be saved in Solr
			Map<String,List<String>> altLabelMap = null;
			if(jsonElement!=null && !jsonElement.isEmpty()) 
			{
				altLabelMap = convertListOfListOfStringToMapOfStringAndListOfString(jsonElement);
			}
			/*this is added because jackson has problems with serializing null values (version 2.9.4 that we use)
			 * TODO: find a better fix
			 */			
			if(altLabelMap==null)
			{
				altLabelMap = new HashMap<String, List<String>>();
				List<String> altLabelMapList = new ArrayList<String>();
				altLabelMapList.add("-");
				altLabelMap.put("en", altLabelMapList);
			}
			newWikidataAgent.setAltLabel(altLabelMap);

			String country = "-";
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataAgent.getCountry_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty())
			{
				country="http://www.wikidata.org/entity/" + jsonElement.get(0).get(0);
			}
			newWikidataAgent.setCountry(country);

			String [] dateBirthArray = new String [1];
			dateBirthArray[0] = "-";
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataAgent.getDateOfBirth_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) 
			{				
				dateBirthArray[0]=jsonElement.get(0).get(0);				
			}
			newWikidataAgent.setDateOfBirth(dateBirthArray);
			
			String [] dateDeathArray = new String [1];
			dateDeathArray[0] = "-";
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataAgent.getDateOfDeath_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty())
			{
				dateDeathArray = new String [jsonElement.size()];
				for(int i=0;i<jsonElement.size();i++)
				{
					dateDeathArray[i]=jsonElement.get(i).get(0);
				}				
			}
			newWikidataAgent.setDateOfDeath(dateDeathArray);
			
			String depiction = "-";
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataAgent.getDepiction_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty())
			{
				depiction = "http://commons.wikimedia.org/wiki/Special:FilePath/" + jsonElement.get(0).get(0);
			}
			newWikidataAgent.setDepiction(depiction);
			
			Map<String,List<String>> descriptionsMap = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataAgent.getDescription_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty())
			{
				descriptionsMap = convertListOfListOfStringToMapOfStringAndListOfString(jsonElement);
			}
			if(descriptionsMap==null)
			{
				descriptionsMap = new HashMap<String, List<String>>();
				List<String> descriptionsMapList = new ArrayList<String>();
				descriptionsMapList.add("-");
				altLabelMap.put("en", descriptionsMapList);
			}
			newWikidataAgent.setDescription(descriptionsMap);
			
			newWikidataAgent.setEntityId(wikidataURL);			
			
			newWikidataAgent.setInternalType(type);
			
			
			String modificationDate = "-";
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataAgent.getModificationDate_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) 
			{
				modificationDate = jsonElement.get(0).get(0);
			}
			newWikidataAgent.setModificationDate(modificationDate);
			
			
			String [] occupationArray = new String [1];
			occupationArray[0] = "-";
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataAgent.getProfessionOrOccupation_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) 
			{
				occupationArray = new String [jsonElement.size()];
				for(int i=0;i<jsonElement.size();i++)
				{
					occupationArray[i]="http://www.wikidata.org/entity/" + jsonElement.get(i).get(0);
				}				
			}
			newWikidataAgent.setProfessionOrOccupation(occupationArray);
			
			
			Map<String,List<String>> prefLabelMap = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataAgent.getPrefLabel_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty())
			{ 
				prefLabelMap = convertListOfListOfStringToMapOfStringAndListOfString(jsonElement);
			}
			if(prefLabelMap==null)
			{
				prefLabelMap = new HashMap<String, List<String>>();
				List<String> prefLabelMapList = new ArrayList<String>();
				prefLabelMapList.add("-");
				altLabelMap.put("en", prefLabelMapList);
			}
			newWikidataAgent.setPrefLabel(prefLabelMap);
			
			
			String [] sameAsArray=new String [1];
			sameAsArray[0]="-";			
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataAgent.getSameAs_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty())	
			{
				sameAsArray = new String [jsonElement.size()];
				for(int i=0;i<jsonElement.size();i++)
				{
					sameAsArray[i]=jsonElement.get(i).get(0);
				}				
			}
			newWikidataAgent.setSameAs(sameAsArray);
			
			return newWikidataAgent;

		}
		else
		{
			
			WikidataPlace newWikidataPlace = new WikidataPlaceImpl ();
			
			Map<String,List<String>> altLabelMap = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataPlace.getAltLabel_jsonProp()); 
			if(jsonElement!=null && !jsonElement.isEmpty())
			{
				altLabelMap = convertListOfListOfStringToMapOfStringAndListOfString(jsonElement);
				
			}
			if(altLabelMap==null)
			{
				altLabelMap = new HashMap<String, List<String>>();
				List<String> altLabelMapList = new ArrayList<String>();
				altLabelMapList.add("-");
				altLabelMap.put("en", altLabelMapList);
			}
			newWikidataPlace.setAltLabel(altLabelMap);
			
			String country = "-";
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataPlace.getCountry_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) 
			{
				country = "http://www.wikidata.org/entity/" + jsonElement.get(0).get(0);
			}
			newWikidataPlace.setCountry(country);
			
			Float latitude = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataPlace.getLatitude_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) 
			{
				latitude = Float.valueOf(jsonElement.get(0).get(0));
			}
			newWikidataPlace.setLatitude(latitude);

			
			Float longitude = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataPlace.getLongitude_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) 
			{
				longitude = Float.valueOf(jsonElement.get(0).get(0));
			}
			newWikidataPlace.setLongitude(longitude);

			String depiction = "-";
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataPlace.getDepiction_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) 
			{
				depiction = "http://commons.wikimedia.org/wiki/Special:FilePath/" + jsonElement.get(0).get(0);
			}
			newWikidataPlace.setDepiction(depiction);
		
			Map<String,List<String>> descriptionsMap = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataPlace.getDescription_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) 
			{
				descriptionsMap = convertListOfListOfStringToMapOfStringAndListOfString(jsonElement);
			}
			if(descriptionsMap==null)
			{
				descriptionsMap = new HashMap<String, List<String>>();
				List<String> descriptionsMapList = new ArrayList<String>();
				descriptionsMapList.add("-");
				altLabelMap.put("en", descriptionsMapList);
			}
			newWikidataPlace.setDescription(descriptionsMap);
			
			newWikidataPlace.setEntityId(wikidataURL);
			
			newWikidataPlace.setInternalType(type);
			
			String modificationDate = "-";
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataPlace.getModificationDate_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) 
			{
				modificationDate = jsonElement.get(0).get(0);
			}
			newWikidataPlace.setModificationDate(modificationDate);
		
			
			String logo = "-";
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataPlace.getLogo_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) 
			{
				logo = jsonElement.get(0).get(0);
			}
			newWikidataPlace.setLogo(logo);
		
			Map<String,List<String>> prefLabelMap = null;
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataPlace.getPrefLabel_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty())
			{
				prefLabelMap = convertListOfListOfStringToMapOfStringAndListOfString(jsonElement);
			}
			if(prefLabelMap==null)
			{
				prefLabelMap = new HashMap<String, List<String>>();
				List<String> prefLabelMapList = new ArrayList<String>();
				prefLabelMapList.add("-");
				altLabelMap.put("en", prefLabelMapList);
			}
			newWikidataPlace.setPrefLabel(prefLabelMap);
			
			String [] sameAsArray = new String [1];
			sameAsArray[0]="-";
			jsonElement = getJSONFieldFromWikidataJSON(WikidataJSON,newWikidataPlace.getSameAs_jsonProp());
			if(jsonElement!=null && !jsonElement.isEmpty()) 
			{
				sameAsArray = new String [jsonElement.size()];
				for(int i=0;i<jsonElement.size();i++)
				{
					sameAsArray[i]=jsonElement.get(i).get(0);
				}		
			}
			newWikidataPlace.setSameAs(sameAsArray);

			return newWikidataPlace;
			
		}	
	}
}
