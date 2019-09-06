package eu.europeana.enrichment.utils;

import static org.junit.Assert.assertTrue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.europeana.enrichment.model.WikidataAgent;
import eu.europeana.enrichment.model.WikidataEntity;
import eu.europeana.enrichment.model.WikidataPlace;
import eu.europeana.enrichment.ner.linking.WikidataService;
import eu.europeana.enrichment.solr.model.SolrWikidataAgentImpl;
import eu.europeana.enrichment.solr.model.SolrWikidataPlaceImpl;
import eu.europeana.enrichment.solr.service.SolrWikidataEntityService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-ner-config-book-dumitru.xml")

/**
 * 
 * @author StevaneticS
 *
 * Importing stories and items to the mongo db from a json file
 */

public class ImportPlacesFromCSVToSolr {
	
	@Resource(name = "wikidataService")
	WikidataService wikidataService;
	
	@Resource
	SolrWikidataEntityService solrWikidataEntityService;
	
	private String solrCore = "wikidata";
	private String wikidataDirectory = "C:/wikidata";
	
	@Test
	public void test() throws Exception {		

		File directory=new File(wikidataDirectory);
		File [] listFiles = directory.listFiles();
	    int fileCount=listFiles.length;
	    
	    int solrDocumentsInOneFile = 300;
	    int solrFileNumber = 1;
	    int solrMaxDocumentsReached = solrFileNumber*solrDocumentsInOneFile;
	    
	    String fileNameSchema = "C:/wikidata_solr_xmls/wikidata_solr_";
	    
		
	    //write to this file
		String newOutpuFileName = fileNameSchema + String.valueOf("0") + "_" + String.valueOf(solrMaxDocumentsReached-1) + ".xml";		
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(newOutpuFileName), true));
	  
		for(int i=0; i<fileCount; i++)
		{
			

			
			//read from this file
			String contentJsonFile = null;
			try {
	            
				contentJsonFile = FileUtils.readFileToString(listFiles[i], StandardCharsets.UTF_8);
	            
	        } catch (IOException e) {
	        	
				e.printStackTrace();
	        }
			
			
			DocumentObjectBinder binder = new DocumentObjectBinder();
			
			String fileName=listFiles[i].getName();
			
			String wikidataIdentifierWithJsonExtension = fileName.substring(fileName.lastIndexOf("-") + 1);
			
			String wikidataURL = "http://www.wikidata.org/entity/"+wikidataIdentifierWithJsonExtension.substring(0,wikidataIdentifierWithJsonExtension.length()-5);
			
			WikidataEntity wikiEntity = wikidataService.getWikidataEntity(wikidataURL, contentJsonFile, "place");
			
			String xml = null;
			if(wikiEntity instanceof WikidataAgent)
			{
				WikidataAgent agentLocal = (WikidataAgent) wikiEntity;
				SolrWikidataAgentImpl solrWikidataAgent = null;
				
				if(agentLocal instanceof SolrWikidataAgentImpl) {
					solrWikidataAgent=(SolrWikidataAgentImpl) agentLocal;
				}
				else {
					solrWikidataAgent=new SolrWikidataAgentImpl(agentLocal);
				}
				
				xml = ClientUtils.toXML(binder.toSolrInputDocument(solrWikidataAgent));
				
			}
			else if (wikiEntity instanceof WikidataPlace)
			{
				WikidataPlace placeLocal = (WikidataPlace) wikiEntity;
				SolrWikidataPlaceImpl solrWikidataPlace = null;		
				
				if(placeLocal instanceof SolrWikidataPlaceImpl) {
					solrWikidataPlace=(SolrWikidataPlaceImpl) placeLocal;
				}
				else {
					solrWikidataPlace=new SolrWikidataPlaceImpl(placeLocal);
				}
				
				xml = ClientUtils.toXML(binder.toSolrInputDocument(solrWikidataPlace));
			
			}
			
			if(i>=solrMaxDocumentsReached)
			{
				
				//close old file and write to a new file
				bw.close();
				
				newOutpuFileName = fileNameSchema + String.valueOf(solrMaxDocumentsReached) + "_" + String.valueOf(solrMaxDocumentsReached+solrDocumentsInOneFile-1) + ".xml";
				bw = new BufferedWriter(new FileWriter(new File(newOutpuFileName), true));
				solrFileNumber+=1;
				solrMaxDocumentsReached = solrFileNumber*solrDocumentsInOneFile;
			    
				try {	    	
					
					bw.append(xml + "\n");
					
				} catch (IOException ioe) 
			    {
					ioe.printStackTrace();
			    }
			}
			else
			{
				try {	    	
					
					bw.append(xml + "\n");
					
				} catch (IOException ioe) 
			    {
					ioe.printStackTrace();
			    }
			}
			
			
			
			
			
		}
		
		assertTrue(true);
		
	}

}
