package eu.europeana.enrichment.ner.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

import org.json.JSONArray;
import org.json.JSONObject;

public abstract class NERPythonBase {

	private static final String pythonPath = "C:\\Users\\katicd\\Documents\\Europeana\\Code\\Ait\\Code\\AIT\\venv\\Scripts\\python.exe";
	private static final String mainScriptPath = "C:\\Users\\katicd\\Documents\\Europeana\\Code\\Ait\\Code\\AIT\\Europeana\\scripts4java\\main.py";
	private String pythonCommand;
	
	public NERPythonBase() {
		StringBuilder cmdBuilder = new StringBuilder();
		cmdBuilder.append(pythonPath);
		cmdBuilder.append(" -u ");
		cmdBuilder.append(mainScriptPath);
		pythonCommand = cmdBuilder.toString();
	}
	
	public void runPythonScript(NERPythonCommunicationInterface<BufferedWriter, BufferedReader, BufferedReader> communicationInterface) {
		Process process;
		try {
			process = Runtime.getRuntime().exec(pythonCommand);

			OutputStream stdin = process.getOutputStream();
			InputStream stderr = process.getErrorStream();
			InputStream stdout = process.getInputStream();

			BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));
			BufferedReader error = new BufferedReader(new InputStreamReader(stderr));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stdin));

			communicationInterface.comInterface(writer, reader, error);

			writer.close();
			reader.close();
			// mProcess -> terminate or close
		} catch (Exception e) {
			System.out.println("Exception Raised" + e.toString());
		}
	}
	
	public TreeMap<String, TreeSet<String>> readJSON(String jsonString){
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
