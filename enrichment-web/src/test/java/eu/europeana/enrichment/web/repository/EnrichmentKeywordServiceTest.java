package eu.europeana.enrichment.web.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import eu.europeana.enrichment.EnrichmentApp;
import eu.europeana.enrichment.web.model.KeywordItemView;
import eu.europeana.enrichment.web.service.impl.EnrichmentKeywordServiceImpl;
import eu.europeana.enrichment.web.service.impl.EnrichmentTpKeywordServiceImpl;

@ComponentScan(basePackageClasses = EnrichmentApp.class)
@AutoConfigureMockMvc
@SpringBootTest
public class EnrichmentKeywordServiceTest {

    Logger logger = LogManager.getLogger(getClass());

    @Autowired(required = false)
    KeywordItemRepository keywordItemRepository;
    
    @Autowired
    EnrichmentKeywordServiceImpl enrichmentKeywordService;
    
    @Autowired
    EnrichmentTpKeywordServiceImpl enrichmentTpKeywordService; 

    @Test
    @Disabled // only manual execution
    public void keywordRetrievalTest() throws Exception {

        long TEST_KEYWORD_ID = 112; // count 27
        long TEST_KEYWORD_COUNT = 27;

        long keywordCount = keywordItemRepository.countByKeywordId(TEST_KEYWORD_ID);
        assertEquals(TEST_KEYWORD_COUNT, keywordCount);

        List<KeywordItemView> keywordItems = keywordItemRepository.retrieveByKeywordId(TEST_KEYWORD_ID);
        assertEquals(TEST_KEYWORD_COUNT, keywordItems.size());

        Pageable pr = PageRequest.of(0, 5);
        Page<KeywordItemView> res = keywordItemRepository.retrieveByKeywordId(TEST_KEYWORD_ID, pr);
        assertNotNull(res);
        assertEquals(5, res.getSize());
        assertEquals(TEST_KEYWORD_COUNT, res.getTotalElements());
//        

    }

    @Test
//    @Disabled // only manual execution
    public void updateWithUnreferencedStatus() throws Exception {
        int res = enrichmentTpKeywordService.updateUnreferencedStatus();
        System.out.println(res);
    }

    @Test
//    @Disabled // only manual execution
    public void updateWithNotLinkedStatus() throws Exception {
        int res = enrichmentTpKeywordService.updateNotLinkedStatus();
        System.out.println(res);
    }

    @Test
//    @Disabled // only manual execution 
    public void updateItemIds() throws Exception {
        int res = enrichmentTpKeywordService.updateItemIds();
        System.out.println(res);
    }

}
