package eu.europeana.enrichment.solr.service.impl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.europeana.enrichment.solr.commons.HTTPParameterStringBuilder;

public class SolrHTTPRequest {
	
	private final Logger log = LogManager.getLogger(getClass());
	
	public String sendSolrHTTPRequest (String solrCoreName, String requestHandler, Map<String, String> parameters) throws IOException
	{
		String urlString = "http://localhost:8983/solr/" + solrCoreName + "/" + requestHandler + "?";
		URL url = new URL(urlString);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		
		/*
		 * adding parameters to the request
		 */
		 
		con.setDoOutput(true);
		DataOutputStream out = new DataOutputStream(con.getOutputStream());
		out.writeBytes(HTTPParameterStringBuilder.getParamsString(parameters));
		out.flush();
		out.close();
		
		/*
		 * execute the request
		 */
		
		int status = con.getResponseCode();
		log.debug("Solr request output code: " + String.valueOf(status));
		
		/*
		 * read a response and put it in the "content" string 
		 */
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer content = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
		    content.append(inputLine);
		}
		in.close();
		
		log.debug("Solr response: " + content.toString());
		return content.toString();
	}

}
