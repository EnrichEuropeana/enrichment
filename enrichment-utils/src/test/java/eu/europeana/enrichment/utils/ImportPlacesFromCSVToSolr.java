package eu.europeana.enrichment.utils;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.europeana.enrichment.common.commons.HelperFunctions;
import eu.europeana.enrichment.model.WikidataEntity;
import eu.europeana.enrichment.ner.linking.WikidataService;
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
	  
		for(int i=0; i<fileCount; i++)
		{
			
			String contentJsonFile = null;

			try {
	            
				contentJsonFile = FileUtils.readFileToString(listFiles[i], StandardCharsets.UTF_8);
	            
	        } catch (IOException e) {
	        	
				e.printStackTrace();
	        }
			
			String fileName=listFiles[i].getName();
			
			String wikidataIdentifierWithJsonExtension = fileName.substring(fileName.lastIndexOf("-") + 1);
			
			String wikidataURL = "http://www.wikidata.org/entity/"+wikidataIdentifierWithJsonExtension.substring(0,wikidataIdentifierWithJsonExtension.length()-5);
			
			WikidataEntity wikiEntity = wikidataService.getWikidataEntity(wikidataURL, contentJsonFile, "place");
			
			solrWikidataEntityService.store(solrCore, wikiEntity, true);

		}
		assertTrue(true);
		
	}

}
