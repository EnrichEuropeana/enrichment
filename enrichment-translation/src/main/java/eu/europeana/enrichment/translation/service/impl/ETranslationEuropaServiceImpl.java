package eu.europeana.enrichment.translation.service.impl;

import eu.europeana.enrichment.translation.exception.TranslationException;
import eu.europeana.enrichment.translation.service.TranslationService;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public class ETranslationEuropaServiceImpl implements TranslationService {

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

	public ETranslationEuropaServiceImpl(String credentialFilePath, String domain,
			String requesterCallback, String errorCallback, String emailDestination) {
		readCredentialFile(credentialFilePath);
		this.domain = domain;
		this.requesterCallback = requesterCallback;
		this.errorCallback = errorCallback;
		this.emailDestination = emailDestination;
	}
	
	/**
	 * This method reads the necessary eTranslation credentials 
	 * 
	 * @param credentialFilePath		is the path to the credential file
	 */
	private void readCredentialFile(String credentialFilePath) {
				
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
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("Exception raised by reading eTranslation credentials!" + e.getMessage());
			System.err.println(e.getMessage());
		}
	}

	@Override
	public String translateText(List<String> textArray, String sourceLanguage, String targetLang) throws TranslationException {
		// TODO: check if credential != null
		targetLanguage=targetLang;
		String externalReference = String.valueOf((int)(Math.random() * 100000 + 1));
		//String externalReference = "123";
		createdRequests.put(externalReference, null);
		
		//TODO: handle textArray with more then one request
		String contentBody = createTranslationBodyForDirectCallback(textArray.get(0), sourceLanguage, externalReference);
		//String contentBody =  createTranslationBody (text, sourceLanguage);
		String reponseCode = createHttpRequest(contentBody);
		logger.info("Created and sent eTranslation request. Response code: " + reponseCode + ". External reference: " + externalReference);
		
		long maxWaitingTime = 2 * 60 * 1000;// in millisec.
		long waitingTime = 0;
		long sleepingTime = 500; //in millisec.
		while(createdRequests.get(externalReference) == null && waitingTime < maxWaitingTime)
		{
			try {
				Thread.sleep(sleepingTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			waitingTime += sleepingTime;
		}
		
		String response;
		
		if(waitingTime >= maxWaitingTime)
		{
			logger.info("Maximum waiting time of: " + String.valueOf(maxWaitingTime) + " for the eTranslation response has elapsed! No response obtained!");
			response = "";
		}
		else
		{
			logger.info("eTranslation response arrived and is successfully processed!");
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
	 */
	
	private String createTranslationBody(String text, String sourceLanguage) {
		String base64content = "";
		try {
			byte[] bytesEncoded = Base64.encodeBase64(text.getBytes("UTF-8"));
			base64content = new String(bytesEncoded);
		}catch(UnsupportedEncodingException ex) {
			logger.error("Exception by creating translation request body: " + ex.getMessage());
			System.out.println(ex.getMessage());
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
	 */
	private String createTranslationBodyForDirectCallback(String text, String sourceLanguage, String externalReference) {
		String base64content = "";
		try {
			byte[] bytesEncoded = Base64.encodeBase64(text.getBytes("UTF-8"));
			base64content = new String(bytesEncoded);
		}catch(UnsupportedEncodingException ex) {
			logger.error("Exception by creating translation request body: " + ex.getMessage());
			System.out.println(ex.getMessage());
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
				.put("documentToTranslateBase64", new JSONObject().put("format", fileFormat).put("content", base64content));
		//.put("textToTranslate", text)

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

			System.out.println("(eTranslation) Http reponse: " + responeString);
			return responeString;
		} catch (Exception ex) {
			//TODO: proper exception handling
			logger.error("Exception raised during the creation of eTranslation request: " + ex.getMessage());
			System.err.println(ex.getMessage());
			return "";
		}
	}
		
	@Override
	public void eTranslationResponse (String targetLanguage, String translatedText, String requestId, String externalReference)
	{
		logger.info("eTranslation response has been received with the following parameters: targetLanguage="+ targetLanguage + ", translatedText="+ translatedText + ", requestId=" + requestId + ", externalReference="+externalReference+" .");
		
		if(createdRequests.containsKey(externalReference))
		{	
			String URLDecodedTranslatedText = "";
			try {
				URLDecodedTranslatedText = URLDecoder.decode(translatedText, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			logger.info("eTranslation obtained translated text (url decoded): " + URLDecodedTranslatedText);
			createdRequests.put(externalReference, URLDecodedTranslatedText);
			
		}
	}

}
