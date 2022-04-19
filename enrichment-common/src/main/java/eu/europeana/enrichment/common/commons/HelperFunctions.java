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

import org.apache.commons.io.FileUtils;
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

}
