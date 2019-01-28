package eu.europeana.enrichment.ner.service.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.TreeMap;
import java.util.TreeSet;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;

import eu.europeana.enrichment.ner.service.NERService;
import eu.europeana.enrichment.ner.exception.NERAnnotateException;

public class NERPythonServiceImpl implements NERService{

	private String pythonCommand;
	
	public NERPythonServiceImpl(String pythonPath, String scriptPath) {
		StringBuilder cmdBuilder = new StringBuilder();
		cmdBuilder.append(pythonPath);
		cmdBuilder.append(" -u ");
		cmdBuilder.append(scriptPath);
		pythonCommand = cmdBuilder.toString();
	}

	@Override
	public TreeMap<String, TreeSet<String>> identifyNER(String text) throws NERAnnotateException {
		TreeMap<String, TreeSet<String>> map = new TreeMap<>();
		Process process;
		try {
			process = Runtime.getRuntime().exec(pythonCommand);

			OutputStream stdin = process.getOutputStream();
			InputStream stderr = process.getErrorStream();
			InputStream stdout = process.getInputStream();

			BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));
			BufferedReader error = new BufferedReader(new InputStreamReader(stderr));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stdin));

			byte[] bytesEncoded = Base64.getEncoder().encode(text.getBytes("UTF-8"));
			String inputString = new String(bytesEncoded, "UTF-8");
			writer.write(inputString);
			writer.flush();
			writer.close();
			
			StringBuilder sb = new StringBuilder();
			String currentLine;
			while ((currentLine = reader.readLine()) != null) {
				sb.append(currentLine);
			}
			String pythonResponse = sb.toString();
			
			sb = new StringBuilder();
			while ((currentLine = error.readLine()) != null) {
				sb.append(currentLine);
			}
			String pythonErrorResponse = sb.toString();
			System.out.println("Python error: " + pythonErrorResponse);
			
			byte[] bytesDecoded = Base64.getDecoder().decode(pythonResponse.getBytes("UTF-8"));
			String pythonResponseText = new String(bytesDecoded, "UTF-8");
			//System.out.println("Python response: " + pythonResponse);
			
			map = readJSON(pythonResponseText);

			writer.close();
			reader.close();
			// mProcess -> terminate or close
		} catch (Exception e) {
			System.out.println("Exception Raised" + e.toString());
		}
		
		return map;
	}
	
	private TreeMap<String, TreeSet<String>> readJSON(String jsonString){
		TreeMap<String, TreeSet<String>> map = new TreeMap<String, TreeSet<String>>();
		JSONObject responseJson = new JSONObject(jsonString);
		//TODO: exception handling 
		
		Iterator<String> keys = responseJson.keys();
		while(keys.hasNext()) {
		    String key = keys.next();
		    map.put(key, getSet(responseJson.getJSONArray(key)));
		}
		return map;
	}
	
	private TreeSet<String> getSet(JSONArray jsonArray){
		ArrayList<String> tmp = new ArrayList<String>();
		for (int index = 0; index < jsonArray.length(); index++) {
			tmp.add(jsonArray.getString(index));
		}
		return new TreeSet<String>(tmp);
	}
}
