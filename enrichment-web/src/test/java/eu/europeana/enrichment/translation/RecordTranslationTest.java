package eu.europeana.enrichment.translation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.europeana.enrichment.EnrichmentApp;
import eu.europeana.enrichment.definitions.model.RecordTranslation;
import eu.europeana.enrichment.definitions.model.impl.EuropeanaRecordTranslationImpl;
import eu.europeana.enrichment.mongo.dao.RecordTranslationDao;
import eu.europeana.enrichment.translation.view.Description1418;
import eu.europeana.enrichment.web.service.RecordTranslationService;

@ComponentScan(basePackageClasses = EnrichmentApp.class)
@AutoConfigureMockMvc
@SpringBootTest
public class RecordTranslationTest {
    
    Logger logger = LogManager.getLogger(getClass());
    
    String[] supportedLanguages = new String[]{"fr", "de", "nl", "hr", "sr", "bs", "sl", "el", "ro", "pl", "pt"};

    @Autowired
    RecordTranslationService recordTranslationService;
    
    @Autowired
    RecordTranslationDao recordTranslationDao;  

    String rawDescriptionsFolder = "/app/enrich/data/1418-descriptions/raw-descriptions/";
    String rawDescriptionsLangFolder = "/app/enrich/data/1418-descriptions/raw-descriptions-lang/";
    
    String translationsFolder = "/app/enrich/data/1418-descriptions/translations/";

    @Test
    @Disabled
    public void recordTranslationTest() throws Exception {
        RecordTranslation recordTranslation = new EuropeanaRecordTranslationImpl();
        String testRecordId = "/test/recordId";
        recordTranslation.setRecordId(testRecordId);
        recordTranslation.setDescription(List.of("Das ist ein Text auf Deutsch!"));
        RecordTranslation translation = recordTranslationService.translate(recordTranslation, EuropeanaRecordTranslationImpl.class);
        assertEquals(RecordTranslation.TRANSLATION_STATUS_COMPLETE, translation.getTranslationStatus());
        assertNotNull(translation.getTranslation());
        assertFalse(translation.getTranslation().isEmpty());
        assertNotNull(translation.getLanguage());
        assertEquals("de", translation.getLanguage().get(0));
    }

    @Test
    @Disabled
    public void recordTranslation1418Test() throws Exception {

        String filename = "100000-nnnnl84.json";
        Description1418 description = getDescription(filename);
        assertNotNull(description.getId());
        assertEquals("100000-nnnnl84", description.getIdentifier());
        assertEquals(2, description.getDescriptions().size());

        RecordTranslation recordTranslation = buildRecordTranslation(description);
        RecordTranslation translation = recordTranslationService.translate(recordTranslation, EuropeanaRecordTranslationImpl.class);

        assertNotNull(translation);
        assertEquals(2, translation.getTranslation().size());
        assertEquals(2, translation.getLanguage().size());
        assertEquals("fr", translation.getLanguage().get(0));
        assertEquals("en", translation.getLanguage().get(1));
        assertEquals("Front", translation.getTranslation().get(1));

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(translation);
        File translatedFile = new File(translationsFolder, filename);
        FileUtils.write(translatedFile, json, StandardCharsets.UTF_8);

        EuropeanaRecordTranslationImpl savedTranslation = mapper.readValue(translatedFile,
                EuropeanaRecordTranslationImpl.class);
        assertEquals(savedTranslation.getDescription().size(), savedTranslation.getTranslation().size());
        assertEquals(savedTranslation.getDescription().size(), savedTranslation.getLanguage().size());
    }
    
    @Test
    @Disabled
    public void importMissingRecordTranslationsTest() throws Exception {

        String[] files =  new String[] {"197874-nnncZx9.json", "197883-nnncZxw.json"};
        String identifier;
        for (int i = 0; i < files.length; i++) {
            Description1418 description = getDescription(rawDescriptionsLangFolder, files[i]);
            //remove numeric sequence
            identifier = description.getIdentifier().split("-")[1];
            description.setIdentifier(identifier);
            RecordTranslation recordTranslation = buildRecordTranslation(description);
            RecordTranslation translation = recordTranslationService.translate(recordTranslation, EuropeanaRecordTranslationImpl.class);
            assertNotNull(translation);
        }
     }
    
//    @Test
//    @Disabled
//    public void fixTrippleBackSlashTest() throws Exception {
//        String identifier = "https___1914_1918_europeana_eu_contributions_19821";
//        RecordTranslation recordTranslation = recordTranslationService.getByRecordId(identifier);
//        System.out.println(recordTranslation.getTranslation().get(0));        
//        
//        assertTrue(recordTranslation.getTranslation().get(0).contains("\\\""));
//        
//    }
    
    @Test
    @Disabled
    public void updateDcLanguageTest() throws Exception {

        String filename = "198757-nnnncpS.json";
        Description1418 description = getDescription(rawDescriptionsLangFolder, filename);
        assertNotNull(description.getId());
        assertEquals("198757-nnnncpS", description.getIdentifier());
        assertEquals(2, description.getDescriptions().size());
        assertEquals(1, description.getLanguages().size());
        assertEquals("Slovenščina", description.getLanguages().get(0).getDcLanguage());
        

        RecordTranslation recordTranslation = buildRecordTranslation(description);
        RecordTranslation translation = recordTranslationService.updateDcLanguage(recordTranslation, false);
        assertEquals(recordTranslation.getRecordDclanguage().size(), translation.getRecordDclanguage().size() );
        assertEquals("Slovenščina", translation.getRecordDclanguage().get(0));
        
    }
    
    @Test
    @Disabled
    public void updateDcLanguageForAllRecordsTest() throws Exception {

        String[] fileNames = FileUtils.getFile(rawDescriptionsLangFolder).list();
        String filename;
        logger.info("Total files: " + fileNames.length);
        int cnt = 0;
        List<String> notFound = new ArrayList<String>() ;
        
        long start = System.currentTimeMillis();
        for (int i = 0; i < fileNames.length; i++) {
            filename = fileNames[i];
            logger.info("Count: " + i +  " Starting translation of file: " + filename);
            Description1418 description;
            try {
                description = getDescription(rawDescriptionsLangFolder, filename);
            }catch (Throwable e) {
                logger.error("Failed to parse file: " + filename);
                continue; 
            }
            
            if(description.getDescriptions() == null || description.getDescriptions().isEmpty()) {
                logger.info("The record has no descriptions, file: " + filename);
                continue;
            }
            
            RecordTranslation recordTranslation = buildRecordTranslation(description);
            assertNotNull(recordTranslation.getRecordDclanguage());
            RecordTranslation savedTranslation = recordTranslationService.updateDcLanguage(recordTranslation, false);
            if(savedTranslation == null) {
                notFound.add(filename);
                continue;
            }
            
            assertNotNull(savedTranslation);
            assertEquals(recordTranslation.getRecordDclanguage().size(), savedTranslation.getRecordDclanguage().size() );
            
            if(savedTranslation.getModified() != null && savedTranslation.getModified().getTime() > start) {
                cnt++;
            }
        } 
        logger.info("Total files: " + fileNames.length);
        logger.info("Updated: " + cnt);
        logger.info("NotFound: " + notFound);
    }
    

    @Test
    @Disabled
    public void allRecordTranslations1418Test() throws Exception {

        String[] fileNames = FileUtils.getFile(rawDescriptionsFolder).list();
        String filename;
        EuropeanaRecordTranslationImpl savedTranslation;
        logger.info("Total files: " + fileNames.length);
        for (int i = 0; i < fileNames.length; i++) {
            filename = fileNames[i];
            logger.info("Count: " + i +  " Starting translation of file: " + filename);
            Description1418 description;
            try {
                description = getDescription(filename);
            }catch (Throwable e) {
                logger.error("Failed to parse file: " + filename);
                continue; 
            }
            
            if(description.getDescriptions() == null || description.getDescriptions().isEmpty()) {
                logger.info("The record has no descriptions, file: " + filename);
                continue;
            }
            
            File translatedFile = getTranslationFile(filename);
            if(translatedFile.exists()) {
                logger.info("The record was already translated, skip translation for file: " + filename);
                continue;
            }
            savedTranslation = translateAndSerialize(filename, description);
            verifyTranslationComplete(savedTranslation);
        }        
    }
    
    @Test
//    @Disabled
    public void exportRecordTranslations1418Test() throws Exception {

        List<EuropeanaRecordTranslationImpl> recordTranslations= recordTranslationDao.getAllTranslationRecords(EuropeanaRecordTranslationImpl.class);
        EuropeanaRecordTranslationImpl serializedRecord;
        int cnt = 0;
        Set<String> includedTranslations = Set.of(supportedLanguages);
        for (EuropeanaRecordTranslationImpl recordTranslation : recordTranslations) {
            
            boolean serializeTranslation = includedTranslations.contains(recordTranslation.getLanguage().get(0));
            if(!serializeTranslation) {
                recordTranslation.setTranslation(null);
                recordTranslation.setTranslationStatus(null);
            }else {
                sanitizeTranslations(recordTranslation);
            }
            
            String fileName = recordTranslation.getIdentifier() + ".json";
            if(recordTranslation.getIdentifier().contains("-")) {
                String tmpIdentifier = recordTranslation.getIdentifier().split("-")[1];
                recordTranslation.setIdentifier(tmpIdentifier);
            }
            serializedRecord = serializeTranslation(recordTranslation, fileName);
            
            if(serializeTranslation) {
                verifyTranslationComplete(serializedRecord);
            }
            cnt++;
            if((cnt % 100) == 0) {
                logger.info("Serialization Count: " + cnt);
            }
        }
        
        logger.info("Serialization Count: " + cnt);
    }

    private void sanitizeTranslations(EuropeanaRecordTranslationImpl recordTranslation) {
        
        List<String> updatedTranslations = new ArrayList<>(recordTranslation.getTranslation().size());
        String fixedTranslation;
        for (String translation : recordTranslation.getTranslation()) {
            if(translation.contains("\\\"")) {
                fixedTranslation = translation.replaceAll("\\\\\"", "\"");                
            }else {
                if(translation.contains("\\")) {
                    System.out.println(translation);
                }
                fixedTranslation = translation; 
            }
            updatedTranslations.add(fixedTranslation);
        }
        recordTranslation.setTranslation(updatedTranslations);
    }

//    @Test
//    @Disabled
//    public void fixTrippleBackSlashInTranslations1418Test() throws Exception {
//
//        List<EuropeanaRecordTranslationImpl> recordTranslations= recordTranslationDao.getAllTranslationRecords(EuropeanaRecordTranslationImpl.class);
//        EuropeanaRecordTranslationImpl serializedRecord;
//        int cnt = 0;
//        for (EuropeanaRecordTranslationImpl recordTranslation : recordTranslations) {
//            //serializedRecord = serializeTranslation(recordTranslation, recordTranslation.getIdentifier() + ".json");
//            //verifyTranslationComplete(serializedRecord);
//            String fistTranslation = recordTranslation.getTranslation().get(0);
//            if(fistTranslation.contains("Opa Sciaroni was a hospice.")) {
//                System.out.println(fistTranslation);
//            }
//            cnt++;
//            if((cnt % 100) == 0) {
//                logger.info("Serialization Count: " + cnt);
//            }
//        }
//        
//        logger.info("Serialization Count: " + cnt);
//    }


    private void verifyTranslationComplete(EuropeanaRecordTranslationImpl savedTranslation) {
        assertEquals(savedTranslation.getDescription().size(), savedTranslation.getTranslation().size());
        assertEquals(savedTranslation.getDescription().size(), savedTranslation.getLanguage().size());
        assertNotNull(savedTranslation.getRecordId());
        assertNotNull(savedTranslation.getIdentifier());
        assertNotNull(savedTranslation.getTool());
    }

    private EuropeanaRecordTranslationImpl translateAndSerialize(String filename, Description1418 description)
            throws IOException, Exception, JsonProcessingException, JsonParseException, JsonMappingException {
        RecordTranslation recordTranslation = buildRecordTranslation(description);
        RecordTranslation translation = recordTranslationService.translate(recordTranslation, EuropeanaRecordTranslationImpl.class);
        return serializeTranslation(translation, filename);
    }

    private EuropeanaRecordTranslationImpl serializeTranslation(RecordTranslation translation, String filename)
            throws JsonProcessingException, IOException, JsonParseException, JsonMappingException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(translation);
        File translatedFile = getTranslationFile(filename);
        FileUtils.write(getTranslationFile(filename), json, StandardCharsets.UTF_8);
        return mapper.readValue(translatedFile, EuropeanaRecordTranslationImpl.class);
    }

    private File getTranslationFile(String filename) {
        File translatedFile = new File(translationsFolder, filename);
        return translatedFile;
    }

    private RecordTranslation buildRecordTranslation(Description1418 description) {
        RecordTranslation recordTranslation = new EuropeanaRecordTranslationImpl();
        recordTranslation.setRecordId(description.getId());
        recordTranslation.setIdentifier(description.getIdentifier());
        List<String> rawDescriptions = description.getDescriptions().stream().map(desc -> desc.getDescription())
                .collect(Collectors.toList());
        recordTranslation.setDescription(rawDescriptions);
        
        if(description.getLanguages() != null) {
            List<String> recordDcLanguage = description.getLanguages().stream().map(dcLanguage -> dcLanguage.getDcLanguage())
                .collect(Collectors.toList());
            recordTranslation.setRecordDclanguage(recordDcLanguage);
        }
        return recordTranslation;
    }

    private Description1418 getDescription(String filename) throws IOException {
//        String identifier = filename.substring(0, filename.length() - ".json".length());
//        File jsonFile = new File(rawDescriptionsFolder + filename);
//        ObjectMapper mapper = new ObjectMapper();
//        // JSON file to Java object
//        Description1418 res = mapper.readValue(jsonFile, Description1418.class);
//        res.setIdentifier(identifier);
//        return res;
        return getDescription(rawDescriptionsFolder, filename);
    }

    private Description1418 getDescription(String folder, String filename) throws IOException {
        String identifier = filename.substring(0, filename.length() - ".json".length());
        File jsonFile = new File(folder + filename);
        ObjectMapper mapper = new ObjectMapper();
        // JSON file to Java object
        Description1418 res = mapper.readValue(jsonFile, Description1418.class);
        res.setIdentifier(identifier);
        return res;
    }
}
