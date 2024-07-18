//package eu.europeana.enrichment.utils;
//
//import static org.junit.Assert.assertTrue;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//
//import javax.annotation.Resource;
//
//import org.apache.commons.io.FileUtils;
//import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
//import org.apache.solr.client.solrj.util.ClientUtils;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import eu.europeana.enrichment.model.WikidataAgent;
//import eu.europeana.enrichment.model.WikidataEntity;
//import eu.europeana.enrichment.model.WikidataPlace;
//import eu.europeana.enrichment.ner.linking.WikidataService;
//import eu.europeana.enrichment.solr.model.SolrWikidataAgentImpl;
//import eu.europeana.enrichment.solr.model.SolrWikidataPlaceImpl;
//import eu.europeana.enrichment.solr.service.SolrWikidataEntityService;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = "classpath:test-ner-config-book-dumitru.xml")
//
///**
// * 
// * @author StevaneticS
// *
// * Importing stories and items to the mongo db from a json file
// */
//
//public class ImportPlacesFromCSVToSolr {
//	
//  Logger logger = LogManager.getLogger(getClass());
//
//	@Resource(name = "wikidataService")
//	WikidataService wikidataService;
//	
//	@Resource
//	SolrWikidataEntityService solrWikidataEntityService;
//	
//	private String solrCore = "wikidata";
//	private String wikidataDirectory = "C:/wikidata";
//	
//	@Test
//	public void test() throws Exception {		
//
//		File directory=new File(wikidataDirectory);
//		File [] listFiles = directory.listFiles();
//	    int fileCount=listFiles.length;
//	    
//	    int solrDocumentsInOneFile = 300;
//	    int solrFileNumber = 1;
//	    int solrMaxDocumentsReached = solrFileNumber*solrDocumentsInOneFile;
//	    
//	    String fileNameSchema = "C:/wikidata_solr_xmls/wikidata_solr_";
//	    	    
//	    //write analyzed wikidata ids to this file in case an analysis needs to continue
//	    String analysedWikidata = "C:/wikidata_solr_xmls/analysed_wikidata.txt";
//	    File analysedWikidataFile=new File(analysedWikidata);
//		//read from this file
//		String contentAnalysedWikidata = null;
//
//		contentAnalysedWikidata = FileUtils.readFileToString(analysedWikidataFile, StandardCharsets.UTF_8);
//            
//
//		
//		BufferedWriter bwAnalysedWikidata = new BufferedWriter(new FileWriter(new File(analysedWikidata), true));
//
//		/*
//		 * write not found wikidata entities where there is som eocntent in the .json file but that content
//		 * is not the actual json but the info message that the data has not been found 
//		 */
//		String notFoundWikidata = "C:/wikidata_solr_xmls/not_found_wikidata.txt";
//	    BufferedWriter bwNotFoundWikidata = new BufferedWriter(new FileWriter(new File(notFoundWikidata), true));
//		
//	    //write Solr xml documents to this file
//		String newOutpuFileName = fileNameSchema + String.valueOf("0") + "_" + String.valueOf(solrMaxDocumentsReached-1) + ".xml";		
//		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(newOutpuFileName), true));
//		bw.append("<add>" + "\n");
//
//	  
//		for(int i=0; i<fileCount; i++)
//		{
//			
//			if(!contentAnalysedWikidata.contains(listFiles[i].getName()))
//			{
//					
//				logger.debug("Current analyzed file is: " + listFiles[i].getName() + " .\n");
//				
//				//read from this file
//				String contentJsonFile = null;
//				contentJsonFile = FileUtils.readFileToString(listFiles[i], StandardCharsets.UTF_8);
//
//				//adding file name to the list of analyzed files
//
//				bwAnalysedWikidata.append(listFiles[i].getName() + ",");
//				logger.debug("Current wikidata entity is successfully added to the list of analyzed files. \n");
//	
//				
//				//this file contains no real wikidata, but the message that the wikidata id is not found
//				if(!contentJsonFile.substring(0, 1).equalsIgnoreCase("{"))
//				{
//
//					bwNotFoundWikidata.append(listFiles[i].getName() + ",");
//					logger.debug("Current wikidata entity contains no json. \n");
//						
//					continue;
//				}
//				
//				
//				DocumentObjectBinder binder = new DocumentObjectBinder();
//				
//				String fileName=listFiles[i].getName();
//				
//				String wikidataIdentifierWithJsonExtension = fileName.substring(fileName.lastIndexOf("-") + 1);
//				
//				String wikidataURL = "http://www.wikidata.org/entity/"+wikidataIdentifierWithJsonExtension.substring(0,wikidataIdentifierWithJsonExtension.length()-5);
//				
//				WikidataEntity wikiEntity = wikidataService.getWikidataEntity(wikidataURL, contentJsonFile, "place");
//				
//				logger.debug("Current wikidata entity is successfully created. \n");
//				
//				String xml = null;
//				if(wikiEntity instanceof WikidataAgent)
//				{
//					WikidataAgent agentLocal = (WikidataAgent) wikiEntity;
//					SolrWikidataAgentImpl solrWikidataAgent = null;
//					
//					if(agentLocal instanceof SolrWikidataAgentImpl) {
//						solrWikidataAgent=(SolrWikidataAgentImpl) agentLocal;
//					}
//					else {
//						solrWikidataAgent=new SolrWikidataAgentImpl(agentLocal);
//					}
//					
//					xml = ClientUtils.toXML(binder.toSolrInputDocument(solrWikidataAgent));
//					
//				}
//				else if (wikiEntity instanceof WikidataPlace)
//				{
//					WikidataPlace placeLocal = (WikidataPlace) wikiEntity;
//					SolrWikidataPlaceImpl solrWikidataPlace = null;		
//					
//					if(placeLocal instanceof SolrWikidataPlaceImpl) {
//						solrWikidataPlace=(SolrWikidataPlaceImpl) placeLocal;
//					}
//					else {
//						solrWikidataPlace=new SolrWikidataPlaceImpl(placeLocal);
//					}
//					
//					xml = ClientUtils.toXML(binder.toSolrInputDocument(solrWikidataPlace));
//				
//				}
//				
//				if(i>=solrMaxDocumentsReached)
//				{
//					
//					int moduo = i % solrDocumentsInOneFile;
//					solrMaxDocumentsReached = i - moduo;
//					
//					
//					//append </add> and close old file and write to a new file
//					bw.append("</add>" + "\n");
//					bw.close();
//					
//					newOutpuFileName = fileNameSchema + String.valueOf(solrMaxDocumentsReached) + "_" + String.valueOf(solrMaxDocumentsReached+solrDocumentsInOneFile-1) + ".xml";
//					bw = new BufferedWriter(new FileWriter(new File(newOutpuFileName), true));
//					
//					solrMaxDocumentsReached = solrMaxDocumentsReached + solrDocumentsInOneFile;
//
//					bw.append("<add>" + "\n");
//					bw.append(xml + "\n");
//						
//					logger.debug("Current wikidata entity is successfully added to the new solr docs file. \n");
//					
//				}
//				else
//				{
//					
//					bw.append(xml + "\n");
//						
//					logger.debug("Current wikidata entity is successfully added to the existing solr docs file. \n");
//					
//				}
//				
//				
//				
//			}
//		
//			
//		}
//		
//		bwNotFoundWikidata.close();
//		bwAnalysedWikidata.close();
//		bw.close();
//		assertTrue(true);
//		
//	}
//
//}
