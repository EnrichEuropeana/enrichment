package eu.europeana.enrichment.web.service.impl;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.definitions.model.impl.Keyword;
import eu.europeana.enrichment.definitions.model.impl.KeywordStatus;
import eu.europeana.enrichment.mongo.dao.KeywordDaoImpl;
import eu.europeana.enrichment.web.model.KeywordItemView;
import eu.europeana.enrichment.web.repository.KeywordItemRepository;
import eu.europeana.enrichment.web.repository.KeywordRepository;

@Service(EnrichmentConstants.BEAN_ENRICHMENT_TP_KEYWORD_SERVICE)
public class EnrichmentTpKeywordServiceImpl {

    /*
     * Loading all translation services
     */

    @Autowired()
//	@Qualifier(EnrichmentConstants.BEAN_ENRICHMENT_KEYWORD_DAO)
    KeywordDaoImpl keywordDao;

    @Autowired
    KeywordRepository keywordRepository;

    @Autowired
    private KeywordItemRepository keywordItemRepository;

    Logger logger = LogManager.getLogger(getClass());


    public int updateNotLinkedStatus() {

        List<Keyword> keywords = keywordDao.findAllKeywords();
        int updated = 0;
        int processed = 0;
        for (Keyword keyword : keywords) {
            boolean toUpdate = !KeywordStatus.UNREFERENCED.equals(keyword.getStatus())
                    && !KeywordStatus.NOT_LINKED.equals(keyword.getStatus())
                    && StringUtils.isBlank(keyword.getPreferredWikidataId());
            // do d
            if (toUpdate) {
                keyword.setStatus(KeywordStatus.NOT_LINKED);
                keyword.setModified(new Date());
                keywordDao.saveKeyword(keyword);
                // increment counter
                updated++;
            }

            processed++;
            processingProgressLogging(updated, processed);
        }

        logProcessingProgress(updated, processed);
        return updated;
    }

    private void processingProgressLogging(int updated, int processed) {
        if (processed % 100 == 0) {
            logProcessingProgress(updated, processed);
        }
    }

    private void logProcessingProgress(int updated, int processed) {
        logger.debug("Processed keyword records: " + processed);
        logger.debug("Updated keyword records: " + updated);
    }

    public int updateUnreferencedStatus() {

        List<Keyword> keywords = keywordDao.findAllKeywords();
        int updated = 0;
        int processed = 0;
        long count;
        for (Keyword keyword : keywords) {
            // do d
            count = keywordItemRepository.countByKeywordId(Long.valueOf(keyword.getPropertyId()));
            boolean notReferenced = !(count > 0);
            boolean toUpdate = notReferenced && !KeywordStatus.UNREFERENCED.equals(keyword.getStatus());
            if (toUpdate) {
                keyword.setStatus(KeywordStatus.UNREFERENCED);
                keyword.setModified(new Date());
                keywordDao.saveKeyword(keyword);
                // increment counter
                updated++;
            }
            processed++;
            processingProgressLogging(updated, processed);
        }

        logProcessingProgress(updated, processed);
        return updated;
    }

    public int updateItemIds() {

        List<Keyword> keywords = keywordDao.findAllKeywords();
        int updated = 0;
        int processed = 0;
        List<KeywordItemView> keywordItemViews;
        List<Long> itemIds;
        for (Keyword keyword : keywords) {
            // do d
            keywordItemViews = keywordItemRepository.retrieveByKeywordId(Long.valueOf(keyword.getPropertyId()));
            if (keywordItemViews == null || keywordItemViews.isEmpty()) {
                processed++;
                continue;
            }
            itemIds = keywordItemViews.stream().map(entry -> entry.getItemId()).collect(Collectors.toList());

            keyword.setTpItemIds(itemIds);
//            keyword.setModified(new Date());
            keywordDao.saveKeyword(keyword);
            // increment counter
            updated++;
            processed++;
            processingProgressLogging(updated, processed);

        }
        logProcessingProgress(updated, processed);
        return updated;
    }   
}
