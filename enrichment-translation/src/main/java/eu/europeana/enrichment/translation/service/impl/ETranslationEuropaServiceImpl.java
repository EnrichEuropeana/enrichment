package eu.europeana.enrichment.translation.service.impl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.europeana.enrichment.common.commons.AppConfigConstants;
import eu.europeana.enrichment.common.commons.EnrichmentConfiguration;
import eu.europeana.enrichment.translation.exception.TranslationException;

@Service(AppConfigConstants.BEAN_ENRICHMENT_E_TRANSLATION_EUROPA_SERVICE)
//public class ETranslationEuropaServiceImpl implements TranslationService {
public class ETranslationEuropaServiceImpl {

	private String baseUrl = "https://webgate.ec.europa.eu/etranslation/si/translate";
	private String domain;
	private String requesterCallback = "http://dsi-demo.ait.ac.at/enrichment-web/administration/eTranslation";
	private String errorCallback;
	private String emailDestination;
	private String fileFormat = "txt";
	private String targetLanguage = "en";
	
	Logger logger = LogManager.getLogger(getClass());
	
	/**
	 * This is a Map that represents the created requests for translation.
	 * For each request, the key is an "external-reference" (see eTranslation documentation)
	 * and the value is translated text. It is used to check if the translation has been 
	 * completed in which case the map should contain the corresponding element
	 */
	
	private static Map<String, String> createdRequests = new HashMap<String, String>();
	
	public String getTargetLanguage() {
		return targetLanguage;
	}

	public void setTargetLanguage(String targetLanguage) {
		this.targetLanguage = targetLanguage;
	}

	private String credentialUsername;
	private String credentialPwd;

	@Autowired
	public ETranslationEuropaServiceImpl(EnrichmentConfiguration enrichmentConfiguration) throws Exception {
		readCredentialFile(enrichmentConfiguration.getTranslationETranslationCredentials());
		this.domain = enrichmentConfiguration.getTranslationETranslationDomain();
		//this.requesterCallback = requesterCallback;
		this.errorCallback = enrichmentConfiguration.getTranslationETranslationErrorCallback();
		this.emailDestination = enrichmentConfiguration.getTranslationETranslationEmailDestination();
	}
	
	/**
	 * This method reads the necessary eTranslation credentials 
	 * 
	 * @param credentialFilePath		is the path to the credential file
	 * @throws IOException 
	 * @throws Exception 
	 */
	private void readCredentialFile(String credentialFilePath) throws IOException  {
				
		//try (BufferedReader br = new BufferedReader(new FileReader(credentialFilePath))) {
		try (BufferedReader br = new BufferedReader(new FileReader(credentialFilePath))) {
			
			String line;
			while ((line = br.readLine()) != null) {
				String[] splitString = line.split("=");
				if (splitString[0].equals("user"))
					credentialUsername = splitString[1];
				else if (splitString[0].equals("pwd"))
					credentialPwd = splitString[1];
			}
		} catch (FileNotFoundException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} 
	}

	public String translateText(String text, String sourceLanguage, String targetLang) throws TranslationException, InterruptedException, UnsupportedEncodingException {
		// TODO: check if credential != null
		targetLanguage=targetLang;
		String externalReference = String.valueOf((int)(Math.random() * 100000 + 1));
		//String externalReference = "123";
		createdRequests.put(externalReference, null);
		
		//TODO: handle textArray with more then one request
		String contentBody = createTranslationBodyForDirectCallback(text, sourceLanguage, externalReference);
		//String contentBody =  createTranslationBody (textArray.get(0), sourceLanguage);
		
		
		String reponseCode = createHttpRequest(contentBody);
		logger.debug("Created and sent eTranslation request. Response code: " + reponseCode + ". External reference: " + externalReference);
		
		long maxWaitingTime = 2 * 60 * 1000;// in millisec.
		long waitingTime = 0;
		long sleepingTime = 500; //in millisec.
		while(createdRequests.get(externalReference) == null && waitingTime < maxWaitingTime)
		{
			try {
				Thread.sleep(sleepingTime);
			} catch (InterruptedException e) {
				throw e;
			}
			waitingTime += sleepingTime;
		}
		
		String response = null;
		
		if(waitingTime >= maxWaitingTime)
		{
			logger.debug("Maximum waiting time of: " + String.valueOf(maxWaitingTime) + " for the eTranslation response has elapsed! No response obtained!");
		}
		else
		{
			logger.debug("eTranslation response arrived and is successfully processed!");
			response = createdRequests.get(externalReference);
		}
		
		createdRequests.remove(externalReference);

		return response;
		
		
	}

	/**
	 * This method creates the translation request body including all information
	 * and the base64 encoded text (appropriate when the translated text is sent to the email)
	 * 
	 * @param text 						this is the transcribed text
	 * @param sourceLanguage			is the original language of transcribed text
	 * @return							a stringified JSON including the transcribed
	 * 									text as a base64 string
	 * @throws UnsupportedEncodingException 
	 */
	
	private String createTranslationBody(String text, String sourceLanguage) throws UnsupportedEncodingException {
		String base64content;
		try {
			byte[] bytesEncoded = Base64.encodeBase64(text.getBytes("UTF-8"));
			base64content = new String(bytesEncoded);
		}catch(UnsupportedEncodingException ex) {

			throw ex;
		}

		// .put("externalReference", "123")
		JSONObject jsonBody = new JSONObject().put("priority", 0)
				.put("callerInformation", new JSONObject().put("application", credentialUsername).put("username", credentialUsername))
				.put("sourceLanguage", sourceLanguage.toUpperCase())
				.put("targetLanguages", new JSONArray().put(0, targetLanguage.toUpperCase()))
				.put("domain", domain)
				.put("destinations",
						new JSONObject().put("emailDestinations", new JSONArray().put(0, emailDestination)))
				.put("documentToTranslateBase64",
						new JSONObject().put("format", fileFormat).put("content", base64content));

		return jsonBody.toString();
	}
	

	/**
	 * This method creates the translation request body where the response is sent back
	 * to the application over a specified URL (REST service). The request includes all information
	 * and the base64 encoded text
	 * 
	 * @param text 						this is the transcribed text
	 * @param sourceLanguage			is the original language of transcribed text
	 * @param externalReference			a number sent to indicate the request (a kind of request id)
	 * @return							a stringified JSON including the transcribed
	 * 									text as a base64 string
	 * @throws UnsupportedEncodingException 
	 */
	private String createTranslationBodyForDirectCallback(String text, String sourceLanguage, String externalReference) throws UnsupportedEncodingException {
		String base64content;
		try {
			byte[] bytesEncoded = Base64.encodeBase64(text.getBytes("UTF-8"));
			base64content = new String(bytesEncoded);
		}catch(UnsupportedEncodingException ex) {

			throw ex;
		}

		// .put("externalReference", "123")
		
		JSONObject jsonBody = new JSONObject().put("priority", 0)
				.put("requesterCallback", requesterCallback)
				.put("externalReference", externalReference)
				.put("callerInformation", new JSONObject().put("application", credentialUsername).put("username", credentialUsername))
				.put("sourceLanguage", sourceLanguage.toUpperCase())
				.put("targetLanguages", new JSONArray().put(0, targetLanguage.toUpperCase()))
				.put("domain", domain)
				.put("destinations",
						new JSONObject().put("httpDestinations", new JSONArray().put(0, "http://dsi-demo.ait.ac.at/enrichment-web")))
				//.put("documentToTranslateBase64", new JSONObject().put("format", fileFormat).put("content", base64content));
		        .put("textToTranslate", text);

		return jsonBody.toString();
	}

	/**
	 * This method creates a request to the eTranslation server including
	 * the transcribed text which should be translated.
	 * 
	 * @param content 					is the base64 content which contains 
	 * 									the transcribed text and other information
	 * @return							response
	 */
	private String createHttpRequest(String content) {
		try {
			CredentialsProvider credsProvider = new BasicCredentialsProvider();
		    credsProvider.setCredentials(AuthScope.ANY,
		      new UsernamePasswordCredentials(credentialUsername, credentialPwd));
			CloseableHttpClient httpClient = HttpClientBuilder.create()
					.setDefaultCredentialsProvider(credsProvider).build();
			
			HttpPost request = new HttpPost(baseUrl);
			StringEntity params = new StringEntity(content, "UTF-8");
			request.addHeader("content-type", "application/json");
			request.setEntity(params);
			HttpResponse result = httpClient.execute(request);
			String responeString = EntityUtils.toString(result.getEntity(), "UTF-8");
			return responeString;
		} catch (Exception ex) {
			//TODO: proper exception handling
			logger.log(Level.ERROR, "Exception during the creation of eTranslation request.", ex);
			return null;
		}
	}
		
	public void eTranslationResponse (String targetLanguage, String translatedText, String requestId, String externalReference, String body) throws UnsupportedEncodingException
	{
		logger.debug("eTranslation response has been received with the following parameters: targetLanguage="+ targetLanguage + ", translatedText="+ translatedText + ", requestId=" + requestId + ", externalReference="+externalReference+" ." + ", body="+body+" .");
		
		if(translatedText==null)
		{
			logger.debug("eTranslation obtained translated text: null");
			createdRequests.put(externalReference, "-");
		}
		
		if(createdRequests.containsKey(externalReference))
		{	
			logger.debug("eTranslation obtained translated text original: " + translatedText);
			
//			byte[] bytesEncoded = Base64.decodeBase64(translatedText);
//			String base64DecodedContent = new String(bytesEncoded);
//			
//			logger.debug("eTranslation obtained translated text (base64 decoded): " + base64DecodedContent);
			
//			String URLDecodedTranslatedText = "";
//			try {
//				URLDecodedTranslatedText = URLDecoder.decode(translatedText, "UTF-8");
//			} catch (UnsupportedEncodingException e) {
//
//				throw e;
//			}
//			logger.debug("eTranslation obtained translated text (url decoded): " + URLDecodedTranslatedText);
			
			createdRequests.put(externalReference, translatedText);
			
		}
	}

}
