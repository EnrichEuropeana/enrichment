package eu.europeana.enrichment.solr.commons;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.BreakIterator;
import java.util.Locale;

import org.json.JSONArray;

public class GoogleTranslator {

	 public static void main(String[] args) throws Exception 
	 {
		 
		 Locale currentLocale2 = new Locale ("ro","RO");
		 BreakIterator iterator = BreakIterator.getSentenceInstance(currentLocale2);
		 String source = "de la infanterie. 7. Smyrna (azi Izmir) este un vechi oraș";
		 iterator.setText(source);
		 int start = iterator.first();
		 for (int end = iterator.next();
		   end != BreakIterator.DONE;
		   start = end, end = iterator.next()) {
		  System.out.println(source.substring(start,end));
		 }
		 
		 

	  GoogleTranslator http = new GoogleTranslator();
	  String word = http.callUrlAndParseResult("ro", "en", "Iară eu, sărmanul de mine, de acestea de toate am fost închis și părăsit, am fost silit în loc de acestea de toate să mă mulțămesc cu sbierătele sălbatece ale Arabilor și a Chinezilor, ce auzindu-le te înfiora până la oase, aceasta era muzica mea și a consoldaților mei de pe vapor");
	  
	  String testExample = "And I, the poor of me, of all this, I have been shut up and left, I have been forced instead of all of this to please the savages of the Arabs and the Chinese, who heard them burying you to the bones, that was my music and my ship consorts";
	 
	  LevenschteinDistance ld = new LevenschteinDistance();
	  
	  int ldValue= ld.calculateLevenshteinDistance(word, testExample);
			  
	  System.out.println("Levenschteins distance is: " + String.valueOf(1-1.0*ldValue/(word.length()+testExample.length())));
	  
	  System.out.println(word);
	 }
	 
	 
	 public String callUrlAndParseResult(String langFrom, String langTo,
	                                             String word) throws Exception 
	 {

	  String url = "https://translate.googleapis.com/translate_a/single?"+
	    "client=gtx&"+
	    "sl=" + langFrom + 
	    "&tl=" + langTo + 
	    "&dt=t&q=" + URLEncoder.encode(word, "UTF-8");    
	  
	  URL obj = new URL(url);
	  HttpURLConnection con = (HttpURLConnection) obj.openConnection(); 
	  con.setRequestProperty("User-Agent", "Mozilla/5.0");
	 
	  BufferedReader in = new BufferedReader(
	    new InputStreamReader(con.getInputStream()));
	  String inputLine;
	  StringBuffer response = new StringBuffer();
	 
	  while ((inputLine = in.readLine()) != null) {
	   response.append(inputLine);
	  }
	  in.close();
	 
	  return parseResult(response.toString());
	 }
	 
	 private String parseResult(String inputJson) throws Exception
	 {
	  /*
	   * inputJson for word 'hello' translated to language Hindi from English-
	   * [[["नमस्ते","hello",,,1]],,"en"]
	   * We have to get 'नमस्ते ' from this json.
	   */
	  
	  JSONArray jsonArray = new JSONArray(inputJson);
	  JSONArray jsonArray2 = (JSONArray) jsonArray.get(0);
	  JSONArray jsonArray3 = (JSONArray) jsonArray2.get(0);
	  
	  return jsonArray3.get(0).toString();
	 }
	}