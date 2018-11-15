package eu.europeana.enrichment.ner.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public abstract class NERPythonCommunicationInterface<TArg1, TArg2, TArg3> {

	/*
	 * this function represents the callback function for python scripts
	 * 
	 */
	public abstract void comInterface(BufferedWriter writer, BufferedReader reader, BufferedReader error) throws IOException;
	
}
