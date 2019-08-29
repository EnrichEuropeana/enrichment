package eu.europeana.enrichment.common.commons;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HelperFunctions {

	public static String generateHashFromText(String text) throws NoSuchAlgorithmException {
		//generate key for the translation based on the original text
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
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


}
