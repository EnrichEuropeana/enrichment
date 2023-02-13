package eu.europeana.enrichment.common.commons;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.safety.Whitelist;

public class HelperFunctions {
	
	static Logger logger = LogManager.getLogger(HelperFunctions.class);
	
	/**
	 * This method creates a Http request.
	 */
	
	public static String createHttpRequest(String content, String baseUrl) {
		try {
//			CredentialsProvider credsProvider = new BasicCredentialsProvider();
//		    credsProvider.setCredentials(AuthScope.ANY,
//		      new UsernamePasswordCredentials(credentialUsername, credentialPwd));
//			CloseableHttpClient httpClient = HttpClientBuilder.create()
//					.setDefaultCredentialsProvider(credsProvider).build();

			CloseableHttpClient httpClient = HttpClientBuilder.create().build();

			HttpResponse result;
			
			if(content!=null && !content.isEmpty())
			{
				HttpPost request = new HttpPost(baseUrl);
				request.addHeader("content-type", "application/json");
				StringEntity params = new StringEntity(content, "UTF-8");
				request.setEntity(params);
				result = httpClient.execute(request);
			}
			else
			{
				HttpGet request = new HttpGet(baseUrl);
				result = httpClient.execute(request);
			}

			String responeString = EntityUtils.toString(result.getEntity(), "UTF-8");

			return responeString;
		} catch (Exception ex) {
			//TODO: proper exception handling
			logger.log(Level.ERROR, "Exception raised during the creation of the Http request to: " + baseUrl, ex);
			return "";
		}
	}


	public static String generateHashFromText(String text) throws NoSuchAlgorithmException {
		//generate key for the translation based on the original text
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e1) {
			throw e1;
		}
		String textWithoutWithespace = text.replaceAll("\\s+","");
		byte[] hash = digest.digest(textWithoutWithespace.getBytes(StandardCharsets.UTF_8));
		// bytes to hex
	    StringBuilder sb = new StringBuilder();
	    for (byte b : hash) {
	        sb.append(String.format("%02x", b));
	    } 
	    
	    return sb.toString();
	}
	
	public static void removeDataForLanguages (Map<String,List<String>> data, String stringForDenormalization, String languages)
	{
		
		if (data==null)
		{
			data = new HashMap<String, List<String>>();
			List<String> addEmptyElem = new ArrayList<String>();
			addEmptyElem.add("-");
			if(stringForDenormalization!=null && !stringForDenormalization.isEmpty())
			{
				data.put(stringForDenormalization+".en", addEmptyElem);
			}
			else
			{
				data.put("en", addEmptyElem);
			}
			return;
		}
		
		List<String> keysToRemove = new ArrayList<String>();
		
		for (String key : data.keySet())
		{
			String denormalizedString = null;
			if(stringForDenormalization!=null && !stringForDenormalization.isEmpty())
			{
				denormalizedString = key.substring(stringForDenormalization.length()+1);
			}
			else
			{
				denormalizedString = key;
			}
			if(!languages.contains(denormalizedString)) keysToRemove.add(key);
		}
		
		for (String key : keysToRemove)
		{
			data.remove(key);
		}
		
		
		//this is added to avoid NoSuchElementException in case we remove all fields from the data and NullPointerException
		//TODO: find another better solution
		if(data.isEmpty())
		{
			List<String> addEmptyElem = new ArrayList<String>();
			addEmptyElem.add("-");
			if(stringForDenormalization!=null && !stringForDenormalization.isEmpty())
			{
				data.put(stringForDenormalization+".en", addEmptyElem);
			}
			else
			{
				data.put("en", addEmptyElem);
			}			
		}
	}
	
	public static void saveWikidataJsonToLocalFileCache (String directory, String wikidataURL, String content) throws IOException
	{
		String fileName = wikidataURL.substring(wikidataURL.lastIndexOf("/") + 1);
		String pathName = directory + "/" + "wikidata-" + "entity-" + fileName + ".json";
		
	    try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(pathName))))
	    {			
	    	
			bw.write(content);
			
		    logger.debug("Wikidata JSON File is written successfully!");
			    
		} catch (IOException ioe) 
	    {
			throw ioe;
	    }	    
	}

	
	public static String getWikidataJsonFromLocalFileCache (String directory, String wikidataURL) throws IOException
	{
		String fileName = wikidataURL.substring(wikidataURL.lastIndexOf("/") + 1);
		String pathName = null;

    	//Specify the file name and path here
    	pathName = directory + "/" + "wikidata-" + "entity-" + fileName + ".json";
    	File file = new File(pathName);

    	/* This logic will make sure that the file 
		 * gets created if it is not present at the
		 * specified location
	    */
		if (!file.exists()) {
			return null;
		}
		else
		{
			//String path = pathName.replace("/", "\\\\");
			String contentJsonFile = null;

			try {
	            
				contentJsonFile = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
	            
	        } catch (IOException e) {
				throw e;
	        }

			return contentJsonFile;
		}

	    
	}
	
	public static boolean checkWikidataJSONFileExistance (String directory, String wikidataURL) throws IOException
	{
		String fileName = wikidataURL.substring(wikidataURL.lastIndexOf("/") + 1);
		String pathName = null;

    	//Specify the file name and path here
    	pathName = directory + "/" + "wikidata-" + "entity-" + fileName + ".json";
    	File file = new File(pathName);

    	/* This logic will make sure that the file 
		 * gets created if it is not present at the
		 * specified location
	    */
		if (file.exists()) {
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public static Map<String, List<String>> convertListOfListOfStringToMapOfStringAndListOfString (List<List<String>> jsonElement)
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
	
	/**
	 * Splitting a string into an array. A null separator splits on whitespace.
	 * @param concatenatedStrings
	 * @param separator
	 * @return
	 */
    public static String[] toArray(String concatenatedStrings, String separator) {
		if (StringUtils.isEmpty(concatenatedStrings))
		    return null;
		String[] array = StringUtils.splitByWholeSeparator(concatenatedStrings, separator);
		return StringUtils.stripAll(array);
    }	

	/**
	 * This method removes unnecessary prefixes from the fields in format Map<String, String> languageMap
	 * e.g. "skos_prefLabel" in solr
	 * @param fieldNamePrefix e.g. ConceptSolrFields.PREF_LABEL
	 * @param languageMap e.g. prefLabel
	 * @return normalized content in format Map<String, String>  
	 */
	public static Map<String, String> normalizeStringMap(String fieldNamePrefix, Map<String, String> languageMap) {
		if(languageMap==null) return null;
		Map<String, String> res;
		int prefixLen = fieldNamePrefix.length() + 1;
		if (languageMap.keySet().iterator().next().startsWith(fieldNamePrefix)) {
			res = languageMap.entrySet().stream().collect(Collectors.toMap(
					entry -> entry.getKey().substring(prefixLen), 
					entry -> entry.getValue())
			);	
		} else {
			res = languageMap;
		}
		return res;
	}
	
	/**
	 * This method adds prefixes to the fields in format Map<String, Object> (useful for solr store)
	 * @param fieldNamePrefix e.g. ConceptSolrFields.PREF_LABEL
	 * @param languageMap e.g. prefLabel
	 * @return normalized content in format Map<String, Object>  
	 */
    public static <T> Map<String, T> normalizeStringMapByAddingPrefix(String fieldNamePrefix,
	    Map<String, T> languageMap) {
    if(languageMap==null) return null;
	Map<String, T> res;
	if (!languageMap.keySet().iterator().next().contains(fieldNamePrefix)) {
	    res = languageMap.entrySet().stream()
		    .collect(Collectors.toMap(entry -> fieldNamePrefix + entry.getKey(), entry -> entry.getValue()));
	} else {
	    res = languageMap;
	}
	return res;
    }
    
    /**
	 * This method adds prefixes to the fields in format Map<String, List<String>> languageMap (useful for solr store)
	 * e.g. "skos_prefLabel"
	 * @param fieldNamePrefix e.g. ConceptSolrFields.PREF_LABEL
	 * @param languageMap e.g. prefLabel
	 * @return normalized content in format Map<String, List<String>>  
	 */
    public static Map<String, List<String>> normalizeStringListMapByAddingPrefix(String fieldNamePrefix,
	    Map<String, List<String>> languageMap) {
    if(languageMap==null) return null;
	Map<String, List<String>> res;
	if (!languageMap.keySet().iterator().next().contains(fieldNamePrefix)) {
	    res = languageMap.entrySet().stream()
		    .collect(Collectors.toMap(entry -> fieldNamePrefix + entry.getKey(), entry -> entry.getValue()));
	} else {
	    res = languageMap;
	}
	return res;
    }
	
	/**
	 * This method removes unnecessary prefixes from the fields in format Map<String, List<String>> 
	 * e.g. "skos_altLabel" (useful for solr store)
	 * @param fieldNamePrefix e.g. ConceptSolrFields.ALT_LABEL
	 * @param languageMap e.g. altLabel
	 * @return normalized content in format Map<String, List<String>>  
	 */
	public static Map<String, List<String>> normalizeStringListMap(String fieldNamePrefix, Map<String, List<String>> languageMap){
		Map<String, List<String>> res;
		int prefixLen = fieldNamePrefix.length() + 1;
		if (languageMap.keySet().iterator().next().startsWith(fieldNamePrefix)) {
			res = languageMap.entrySet().stream().collect(Collectors.toMap(
					entry -> entry.getKey().substring(prefixLen), 
					entry -> entry.getValue())
			);	
		} else {
			res = languageMap;
		}
		return res;
	}
	
	/**
	 * This method removes unnecessary prefixes from the fields in format Map<String, List<String>> 
	 * e.g. "skos_altLabel" (useful for solr store)
	 * @param fieldNamePrefix e.g. OrganizationSolrFields.DC_DESCRIPTION
	 * @param languageMap e.g. dcDescription
	 * @return normalized content in format Map<String, String>  
	 */
	public static Map<String, String> normalizeToStringMap(String fieldNamePrefix, Map<String, List<String>> languageMap){
		Map<String, String> res;
		int prefixLen = fieldNamePrefix.length() + 1;
        	boolean hasPrefix = languageMap.keySet().iterator().next().startsWith(fieldNamePrefix);
        	// substring prefixes if needed and re-map first value
        	res = languageMap.entrySet().stream()
        		.collect(Collectors.toMap(
        			entry -> hasPrefix ? entry.getKey().substring(prefixLen) : entry.getKey(),
        			entry -> entry.getValue().get(0)));
        
        	return res;
	}
	
	
	/**
	 * This method removes unnecessary prefixes from the fields in format List<String> itemList
	 * e.g. "rdagr2_dateOfBirth" (useful for solr store)
	 * @param fieldNamePrefix e.g. AgentSolrFields.DATE_OF_BIRTH
	 * @param itemList e.g. dateOfBirth
	 * @return normalized content in formatString[]  
	 */
	public static String[] normalizeStringList(String fieldNamePrefix, List<String> itemList) {
		if(itemList==null) return null;
		List<String> res;
		int prefixLen = fieldNamePrefix.length() + 1;
		if (itemList.iterator().next().startsWith(fieldNamePrefix)) {
			res = itemList.stream() 
                     .map(entry -> entry.substring(prefixLen)) 
                     .collect(Collectors.toList()			
			);	
		} else {
			res = itemList;
		}
		return convertListToArray(res);
	}
	
	/**
	 * This method converts a string list to an array
	 * @param itemList
	 * @return string array
	 */
	public static String[] convertListToArray(List<String> itemList) {
		String[] itemArr = new String[itemList.size()];
		itemArr = itemList.toArray(itemArr);
		return itemArr;
	}
	
	public static String parseHTMLWithJsoup (String htmlText)
	{
//		StringBuilder response = new StringBuilder ();

		//https://stackoverflow.com/questions/5640334/how-do-i-preserve-line-breaks-when-using-jsoup-to-convert-html-to-plain-text
		String response;
		Document doc = Jsoup.parse(htmlText);		
		doc.outputSettings(new Document.OutputSettings().prettyPrint(false));//makes html() preserve linebreaks and spacing
	    doc.select("br").append("\\n");
	    doc.select("p").prepend("\\n\\n");
	    String s = doc.html().replaceAll("\\\\n", "\n");
	    /*
	     * By passing it Whitelist.none() we make sure that all HTML is removed.
	     * By passsing new OutputSettings().prettyPrint(false) we make sure that the output is not reformatted and line breaks are preserved.
	     */
	    String whole = Jsoup.clean(s, "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));

	    /*
	     * These are used to escape characters that are markup sensitive in certain contexts:
		 *	&amp; → & (ampersand, U+0026)
		 *	&lt; → < (less-than sign, U+003C)
		 *	&gt; → > (greater-than sign, U+003E)
		 *	&quot; → " (quotation mark, U+0022)
		 *	&apos; → ' (apostrophe, U+0027)
		 *  &nbsp;  → " " (space)
	     */
	    response = Parser.unescapeEntities(whole, false);
	    //logger.debug(response);
	    //logger.debug(response);
	    return response;

//	    Elements allParagraphs = doc.getElementsByTag("p");
//		allParagraphs.forEach(paragraph -> response.append(paragraph.text()));
//		logger.debug(whole);
//		logger.debug(whole);
	}
	

}
