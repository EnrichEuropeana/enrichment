//package eu.europeana.enrichment.utils;
//
//import static org.junit.Assert.assertTrue;
//
//import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileReader;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//import javax.annotation.Resource;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import eu.europeana.enrichment.common.commons.HelperFunctions;
//import eu.europeana.enrichment.model.WikidataEntity;
//import eu.europeana.enrichment.ner.linking.WikidataService;
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
//public class ImportPlacesFromCSV {
//	
//	@Resource(name = "wikidataService")
//	WikidataService wikidataService;
//	
//	@Resource
//	SolrWikidataEntityService solrWikidataEntityService;
//	
//	private String wikidataDirectory = "C:/wikidata";
//	
//	@Test
//	public void test() throws Exception {		
//
//		List<String> wikidataIDs = new ArrayList<String>();
//		
//		String [] pathName = new String [2];
//		pathName [0] = "C:/java/places_data_wikidata_unique.csv";
//		pathName [1] = "C:/java/places_data_wikidata_unique_with_label_description_v3.csv";
//		
//		String pathFileNotFoundEntities = "C:/java/not_found_wikidata_places.txt";
//		
//		for(int i=0; i<pathName.length;i++)
//		{
//			try (BufferedReader br = new BufferedReader(new FileReader(new File(pathName[i])))) {
//			    
//				String line;
//				List<String> labels = new ArrayList<String>();
//			    
//				while ((line = br.readLine()) != null) {
//			        
//					String[] values = line.split("\\s*,\\s*");	
//					
//					if(line.contains("\""))
//					{
//						for(int m=2;m<values.length;m++)
//						{
//							if(values[m].contains("www.wikidata.org")) 
//							{ 
//								wikidataIDs.add(values[m]);
//								break;
//							}
//						}
//						
//						int start_pos = line.indexOf("\"") + 1;
//						int end_pos = line.indexOf("\"", start_pos);
//						labels.add(line.substring(start_pos, end_pos));
//						
//					}
//					else
//					{
//						if(values[2].contains("www.wikidata.org"))
//						{
//							wikidataIDs.add(values[2]);
//							labels.add(values[1]);
//						}
//						
//					}
//					
//			    }
//				
//				for(int j=0;j<wikidataIDs.size();j++)
//				{
//					
//					boolean fileexists = HelperFunctions.checkWikidataJSONFileExistance(wikidataDirectory, wikidataIDs.get(j));
//					
//					if(fileexists==false) 	
//					{			
//						System.out.print("Current wikidata id is: " + wikidataIDs.get(j) + ". Current index is: " + j + " .\n");
//						String WikidataJSON = wikidataService.getWikidataJSONFromWikidataID(wikidataIDs.get(j));
//						if(WikidataJSON==null || WikidataJSON.isEmpty()) 
//						{
//							try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(pathFileNotFoundEntities), true)))
//						    {					    	
//								bw.append(wikidataIDs.get(j)+ "," + labels.get(j));
//								    
//							} catch (IOException ioe) 
//						    {
//								ioe.printStackTrace();
//						    }
//
//						}
//						else
//						{
//							HelperFunctions.saveWikidataJsonToLocalFileCache(wikidataDirectory, wikidataIDs.get(j), WikidataJSON);
//							System.out.print("Analyzed wikidata entity number: " + j + " .\n");
//						}
//						
//					}
//
//					
//				}
//			}		
//		}
//		assertTrue(true);
//		
//	}
//
//}
