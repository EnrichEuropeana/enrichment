package eu.europeana.enrichment.ner.service.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.TreeMap;
import java.util.TreeSet;

import eu.europeana.enrichment.ner.service.NERPythonCommunicationInterface;
import eu.europeana.enrichment.ner.service.NERService;
import eu.europeana.enrichment.ner.exception.NERAnnotateException;
import eu.europeana.enrichment.ner.service.NERPythonBase;

public class NERPythonServiceImpl extends NERPythonBase implements NERService{

	TreeMap<String, TreeSet<String>> map;
	
	@Override
	public void init() {
		// TODO Auto-generated method stub
	}

	@Override
	public void init(String model) {
		// TODO Auto-generated method stub
	}

	@Override
	public TreeMap<String, TreeSet<String>> identifyNER(String text) throws NERAnnotateException {
		// TODO Auto-generated method stub
		
		super.runPythonScript(new NERPythonCommunicationInterface<BufferedWriter, BufferedReader, BufferedReader>() {
			@Override
			public void comInterface(BufferedWriter writer, BufferedReader reader, BufferedReader error) throws IOException{
				writer.write(text);
				writer.flush();
				writer.close();
				
				StringBuilder sb = new StringBuilder();
				String currentLine;
				while ((currentLine = reader.readLine()) != null) {
					sb.append(currentLine);
				}
				String pythonResponse = sb.toString();
				
				sb = new StringBuilder();
				while ((currentLine = reader.readLine()) != null) {
					sb.append(currentLine);
				}
				String pythonErrorResponse = sb.toString();
				//System.out.println("Python response: " + pythonResponse);
				
				map = readJSON(pythonResponse);
			}
		});
		
		return map;
	}
	
}
