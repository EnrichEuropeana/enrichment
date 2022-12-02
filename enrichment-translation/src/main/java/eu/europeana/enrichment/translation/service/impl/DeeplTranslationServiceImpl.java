package eu.europeana.enrichment.translation.service.impl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.europeana.enrichment.common.commons.EnrichmentConfiguration;
import eu.europeana.enrichment.common.commons.HelperFunctions;

@Service
public class DeeplTranslationServiceImpl {

	Logger logger = LogManager.getLogger(getClass());
	private String authenticationKey;
	private String deeplFreeBaseUrl;

	@Autowired
	public DeeplTranslationServiceImpl(EnrichmentConfiguration enrichmentConfiguration) throws Exception {
		readCredentialFile(enrichmentConfiguration.getTranslationDeeplFreeAuthenticationKey());
		this.deeplFreeBaseUrl = enrichmentConfiguration.getTranslationDeeplFreeBaseUrl();
	}
	
	/**
	 * This method reads the necessary Deepl credentials.
	 * 
	 * @param credentialFilePath
	 * @throws IOException
	 */
	private void readCredentialFile(String credentialFilePath) throws IOException  {
		try (BufferedReader br = new BufferedReader(new FileReader(credentialFilePath))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] splitString = HelperFunctions.toArray(line,"=");
				if (splitString[0].equals("AuthenticationKey")) {
					authenticationKey = splitString[1];
					break;
				}
			}
		} catch (FileNotFoundException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} 
	}

	public String translateText(String text, String targetLang) throws ClientProtocolException, IOException {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(deeplFreeBaseUrl);

		String authHeader = "DeepL-Auth-Key " + new String(authenticationKey);
		httpPost.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
	    params.add(new BasicNameValuePair("text", text));
	    params.add(new BasicNameValuePair("target_lang", targetLang.toUpperCase()));
	    httpPost.setEntity(new UrlEncodedFormEntity(params));

	    CloseableHttpResponse response = client.execute(httpPost);
	    HttpEntity entity = response.getEntity();
	    if(entity!=null) {
	    	String result = EntityUtils.toString(response.getEntity());
	    	return processDeeplResponse(result);
	    }
	    else {
	    	return null;
	    }
		
	}

	public String processDeeplResponse(String reponse) {
		JSONObject responseJson = null;
		try {
			responseJson = new JSONObject(reponse);
		}
		catch (JSONException e) {
			logger.debug("Exception during parsing the Deepl translation responce json.");
			return null;
		}
		
		if(!responseJson.has("translations"))
		{
			return null;
		}
		
		JSONArray translationsArray = responseJson.getJSONArray("translations");
		if(translationsArray==null) {
			return null;
		}
		JSONObject translationObj = translationsArray.getJSONObject(0);
		if(translationObj==null || !translationObj.has("text")) {
			return null;
		}
		String translatedText = translationObj.getString("text");
		return translatedText;
	}


}
