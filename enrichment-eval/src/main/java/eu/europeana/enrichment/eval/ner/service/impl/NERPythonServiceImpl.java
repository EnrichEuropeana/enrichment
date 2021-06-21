package eu.europeana.enrichment.eval.ner.service.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import eu.europeana.enrichment.common.commons.EnrichmentConfiguration;
import eu.europeana.enrichment.model.NamedEntity;
import eu.europeana.enrichment.ner.service.NERService;

@Service
public class NERPythonServiceImpl implements NERService{

	private String pythonCommand;
	private final Logger logger = LogManager.getLogger(getClass());
	
	/*
	 * This class constructor is used to create the required python command
	 */
	@Autowired
	public NERPythonServiceImpl(EnrichmentConfiguration enrichmentConfiguraion) throws IOException {
		File resource = new ClassPathResource(enrichmentConfiguraion.getNerPythonScript()).getFile();
		StringBuilder cmdBuilder = new StringBuilder();
		cmdBuilder.append(enrichmentConfiguraion.getNerPythonPath());
		cmdBuilder.append(" -u ");
		cmdBuilder.append(resource.getAbsolutePath());
		cmdBuilder.append(" --spaCy \"");
		cmdBuilder.append(enrichmentConfiguraion.getNerPythonSpacyModel());
		cmdBuilder.append("\"");
		pythonCommand = cmdBuilder.toString();
	}
	
//	public NERPythonServiceImpl(String pythonPath, String scriptPath, String spaCyModel) throws IOException {
//		File resource = new ClassPathResource(scriptPath).getFile();
//		StringBuilder cmdBuilder = new StringBuilder();
//		cmdBuilder.append(pythonPath);
//		cmdBuilder.append(" -u ");
//		cmdBuilder.append(resource.getAbsolutePath());
//		cmdBuilder.append(" --spaCy \"");
//		cmdBuilder.append(spaCyModel);
//		cmdBuilder.append("\"");
//		pythonCommand = cmdBuilder.toString();
//	}

	/*
	 * In addition to the inheritance method documentation, this specific method
	 * starts a python process including Streams for stdin, stderr and stdout.
	 * All communications is base64 encoded, because of named entities with 
	 * special character, and runs over Streams.
	 * 
	 * @see eu.europeana.enrichment.ner.service.NERService#identifyNER(java.lang.String)
	 */
	@Override
	public TreeMap<String, List<NamedEntity>> identifyNER(String text) throws IOException {
		TreeMap<String, List<List<String>>> map = new TreeMap<>();
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
			logger.error("Python error: " + pythonErrorResponse);
			System.out.println("Python error: " + pythonErrorResponse);
			
			byte[] bytesDecoded = Base64.getDecoder().decode(pythonResponse.getBytes("UTF-8"));
			String pythonResponseText = new String(bytesDecoded, "UTF-8");
			//System.out.println("Python response: " + pythonResponse);
			
			map = readJSON(pythonResponseText);

			writer.close();
			reader.close();
			// mProcess -> terminate or close
		} catch (IOException e) {
			throw e;
		}
		
		return null;
	}
	
	/*
	 * This method parses the python response JSON into a TreeMap
	 * which are separated by classification types (e.g. agent, place, ..)
	 * 
	 * @param jsonString				is the response JSON which are generated by 
	 * 									python NER tools
	 * @return							a TreeMap including all python named entities
	 * 									separated by classification type
	 */
	private TreeMap<String, List<List<String>>> readJSON(String jsonString){
		TreeMap<String, List<List<String>>> map = new TreeMap<String, List<List<String>>>();
		JSONObject responseJson = new JSONObject(jsonString);
		//TODO: exception handling 
		
		Iterator<String> keys = responseJson.keys();
		while(keys.hasNext()) {
		    String key = keys.next();
		    map.put(key, getList(responseJson.getJSONArray(key)));
		}
		return map;
	}
	
	/*
	 * This method parses all named entities of a classification type
	 * from JSON format to an TreeSet format. TreeSet is used, because 
	 * of uniqueness.
	 * 
	 * @param jsonArray					contains all python named entities of
	 * 									a classification type
	 * @return 							a unique TreeSet of named entities
	 */
	private List<List<String>> getList(JSONArray jsonArray){
		ArrayList<List<String>> tmp = new ArrayList<List<String>>();
		for (int index = 0; index < jsonArray.length(); index++) {
			//tmp.add(jsonArray.getString(index));
			String[] wordWithPosition = new String[2];
			wordWithPosition[0]=jsonArray.getString(index);
			wordWithPosition[1]="-1";
			tmp.add(Arrays.asList(wordWithPosition));

		}
		return tmp;
	}

	@Override
	public String getEnpoint() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setEndpoint(String endpoint) {
		// TODO Auto-generated method stub
		
	}
}
