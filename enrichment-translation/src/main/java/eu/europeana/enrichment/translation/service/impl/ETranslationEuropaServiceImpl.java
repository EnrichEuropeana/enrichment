package eu.europeana.enrichment.translation.service.impl;

import eu.europeana.enrichment.translation.exception.TranslationException;
import eu.europeana.enrichment.translation.service.TranslationService;

import java.io.BufferedReader;
import java.io.FileReader;

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
import org.json.JSONArray;
import org.json.JSONObject;

public class ETranslationEuropaServiceImpl implements TranslationService {

	private static final String baseUrl = "https://webgate.ec.europa.eu/etranslation/si/translate";
	private static final String domain = "SPD";
	private static final String application = "Europeana Enrichment";
	private static final String username = "denis.katic@ait.ac.at";
	private static final String requesterCallback = null;
	private static final String errorCallback = null;
	private static final String emailDestination = "denis.katic@ait.ac.at";
	private static final String fileFormat = "txt";
	private static final String targetLanguage = "en";
	private String credentialUsername;
	private String credentialPwd;

	public ETranslationEuropaServiceImpl(String filePath) {
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
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
			System.err.println(e.getMessage());
		}
	}

	@Override
	public String translateText(String text, String sourceLanguage) throws TranslationException {
		// TODO: check if credential != null
		String contentBody = createTranslationBody(text, sourceLanguage);
		createHttpRequest(contentBody);
		return null;
	}

	/*
	 * This method creates the translation request body including all information
	 * and the base64 encoded text
	 * 
	 * @param text this is the transcribed text
	 * 
	 * @param sourceLanguage
	 * 
	 * @return a stringified json including the base64 string
	 */
	private String createTranslationBody(String text, String sourceLanguage) {

		byte[] bytesEncoded = Base64.encodeBase64(text.getBytes());
		String base64content = new String(bytesEncoded);

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

	/*
	 * This method creates the request to eTranslation server
	 * 
	 * @param content is the base64 content which contains transcribed text and other information
	 * @return
	 */
	private void createHttpRequest(String content) {
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

		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}
	}

}
