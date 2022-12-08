package eu.europeana.enrichment.web.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.datatables.DataTablesInput;
import org.springframework.data.mongodb.datatables.DataTablesOutput;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.model.impl.Keyword;
import eu.europeana.enrichment.model.impl.KeywordStatus;
import eu.europeana.enrichment.mongo.dao.KeywordDaoImpl;
import eu.europeana.enrichment.web.model.KeywordItemView;
import eu.europeana.enrichment.web.model.KeywordUtils;
import eu.europeana.enrichment.web.model.KeywordView;
import eu.europeana.enrichment.web.repository.KeywordItemRepository;
import eu.europeana.enrichment.web.repository.KeywordRepository;

@Service(EnrichmentConstants.BEAN_ENRICHMENT_KEYWORD_SERVICE)
public class EnrichmentKeywordServiceImpl {

    /*
     * Loading all translation services
     */

    @Autowired()
//	@Qualifier(EnrichmentConstants.BEAN_ENRICHMENT_KEYWORD_DAO)
    KeywordDaoImpl keywordDao;

    @Autowired
    KeywordRepository keywordRepository;

    @Autowired
    KeywordItemRepository keywordItemRepository;

    Logger logger = LogManager.getLogger(getClass());

    public DataTablesOutput<KeywordView> getKeywords(DataTablesInput input) {

        DataTablesInput.SearchConfiguration searchConfiguration = new DataTablesInput.SearchConfiguration();
        input.setSearchConfiguration(searchConfiguration);
        // TODO: use constants and enable sort in the table
//        input.addSorting(null);
//        input.getColumn("Id").get().setName("propertyId");
        input.AddSorting("propertyId", DataTablesInput.Order.Direction.asc);
        searchConfiguration.setSearchType("position", DataTablesInput.SearchType.Integer);
//	        searchConfiguration.setExcludedColumns(List.of("country", "geoCoordinates"));

//	        searchConfiguration.setSearchType("isEnabled", DataTablesInput.SearchType.Boolean);

//	        searchConfiguration.getExcludedColumns().add("user");

//	        List<String> productRefColumns = new ArrayList<>();
//	        productRefColumns.add("label");
//	        productRefColumns.add("isEnabled");
//	        productRefColumns.add("createdAt");
//	        searchConfiguration.addRefConfiguration("product", "product", productRefColumns, "label");

//	        Criteria additionalCriteria = new Criteria();
        input.setSearch(new DataTablesInput.Search(null, false));
        // TODO: use constants
        Criteria filter = new Criteria("status");
        filter.nin(List.of(KeywordStatus.UNREFERENCED, KeywordStatus.NOT_LINKED));
        DataTablesOutput<Keyword> keywords = keywordRepository.findAll(input, filter);

        DataTablesOutput<KeywordView> res = new DataTablesOutput<KeywordView>();
        res.setError(keywords.getError());
        res.setDraw(keywords.getDraw());
        res.setException(keywords.getException());
        res.setRecordsFiltered(keywords.getRecordsFiltered());
        res.setRecordsTotal(keywords.getRecordsTotal());
        List<KeywordView> results = new ArrayList<KeywordView>();
        KeywordView view;
        
        // https://europeana.transcribathon.eu/documents/story/item/?item=451984
        for (Keyword keyword : keywords.getData()) {
            view = KeywordUtils.createView(keyword);
            results.add(view);
        }
        res.setData(results);
        return res;
    }

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

    public KeywordView approve(String objectId) throws HttpException {
        Keyword keyword =  keywordDao.findByObjectId(objectId);
        if(keyword==null) {
            throw new HttpException("No keyword found with objetId: " + objectId, "KEYOWRD_NOT_FOUND", HttpStatus.NOT_FOUND);
        }
        keyword.setApprovedWikidataId(keyword.getPreferredWikidataId());
        keyword.setStatus(KeywordStatus.APPROVED);
        keyword.setModified(new Date());
        keyword = keywordDao.saveKeyword(keyword);
        return KeywordUtils.createView(keyword);
    }
    
    public KeywordView reject(String objectId) throws HttpException {
        Keyword keyword =  keywordDao.findByObjectId(objectId);
        if(keyword==null) {
            throw new HttpException("No keyword found with objetId: " + objectId, "KEYOWRD_NOT_FOUND", HttpStatus.NOT_FOUND);
        }
        keyword.setApprovedWikidataId(null);
        keyword.setStatus(KeywordStatus.INVALID);
        keyword.setModified(new Date());
        keyword = keywordDao.saveKeyword(keyword);
        return KeywordUtils.createView(keyword);
    }
}
