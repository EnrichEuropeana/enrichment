package eu.europeana.enrichment.translation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import eu.europeana.enrichment.model.RecordTranslation;
import eu.europeana.enrichment.model.impl.EuropeanaRecordTranslationImpl;
import eu.europeana.enrichment.translation.view.Description1418;
import eu.europeana.enrichment.web.service.RecordTranslationService;

@ComponentScan(basePackageClasses = EnrichmentApp.class)
@AutoConfigureMockMvc
@SpringBootTest
public class RecordTranslationTest {
    
    Logger logger = LogManager.getLogger(getClass());

    @Autowired
    RecordTranslationService recordTranslationService;

    String rawDescriptionsFolder = "/app/enrich/data/1418-descriptions/raw-descriptions/";
    String translationsFolder = "/app/enrich/data/1418-descriptions/translations/";

//    @Test
    public void recordTranslationTest() throws Exception {
        RecordTranslation recordTranslation = new EuropeanaRecordTranslationImpl();
        String testRecordId = "/test/recordId";
        recordTranslation.setRecordId(testRecordId);
        recordTranslation.setDescription(List.of("Das ist ein Text auf Deutsch!"));
        RecordTranslation translation = recordTranslationService.translate(recordTranslation);
        assertEquals(RecordTranslation.TRANSLATION_STATUS_COMPLETE, translation.getTranslationStatus());
        assertNotNull(translation.getTranslation());
        assertFalse(translation.getTranslation().isEmpty());
        assertNotNull(translation.getLanguage());
        assertEquals("de", translation.getLanguage().get(0));
    }

    @Test
    public void recordTranslation1418Test() throws Exception {

        String filename = "100000-nnnnl84.json";
        Description1418 description = getDescription(filename);
        assertNotNull(description.getId());
        assertEquals("100000-nnnnl84", description.getIdentifier());
        assertEquals(2, description.getDescriptions().size());

        RecordTranslation recordTranslation = buildRecordTranslation(description);
        RecordTranslation translation = recordTranslationService.translate(recordTranslation);

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
    public void allRecordTranslations1418Test() throws Exception {

        String[] fileNames = FileUtils.getFile(rawDescriptionsFolder).list();
        String filename;
        EuropeanaRecordTranslationImpl savedTranslation;
        logger.info("Total files: " + fileNames.length);
        for (int i = 0; i < fileNames.length; i++) {
            filename = fileNames[i];
            logger.info("Count: " + i +  " Starting translation of file: " + filename);
            savedTranslation = translateAndSerialize(filename);
            verifyTranslationComplete(savedTranslation);
        }        
    }

    private void verifyTranslationComplete(EuropeanaRecordTranslationImpl savedTranslation) {
        assertEquals(savedTranslation.getDescription().size(), savedTranslation.getTranslation().size());
        assertEquals(savedTranslation.getDescription().size(), savedTranslation.getLanguage().size());
        assertNotNull(savedTranslation.getRecordId());
        assertNotNull(savedTranslation.getIdentifier());
        assertNotNull(savedTranslation.getTool());
    }

    private EuropeanaRecordTranslationImpl translateAndSerialize(String filename)
            throws IOException, Exception, JsonProcessingException, JsonParseException, JsonMappingException {
        Description1418 description = getDescription(filename);
        RecordTranslation recordTranslation = buildRecordTranslation(description);
        RecordTranslation translation = recordTranslationService.translate(recordTranslation);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(translation);
        File translatedFile = new File(translationsFolder, filename);
        FileUtils.write(translatedFile, json, StandardCharsets.UTF_8);

        return mapper.readValue(translatedFile, EuropeanaRecordTranslationImpl.class);
    }

    private RecordTranslation buildRecordTranslation(Description1418 description) {
        RecordTranslation recordTranslation = new EuropeanaRecordTranslationImpl();
        recordTranslation.setRecordId(description.getId());
        recordTranslation.setIdentifier(description.getIdentifier());
        List<String> rawDescriptions = description.getDescriptions().stream().map(desc -> desc.getDescription())
                .collect(Collectors.toList());
        recordTranslation.setDescription(rawDescriptions);
        return recordTranslation;
    }

    private Description1418 getDescription(String filename) throws IOException {
        String identifier = filename.substring(0, filename.length() - ".json".length());
        ObjectMapper mapper = new ObjectMapper();
        // JSON file to Java object
        Description1418 res = mapper.readValue(new File(rawDescriptionsFolder + filename), Description1418.class);
        res.setIdentifier(identifier);
        return res;
    }

}
