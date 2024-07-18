package eu.europeana.enrichment.web.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

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
import eu.europeana.enrichment.definitions.model.impl.Keyword;
import eu.europeana.enrichment.definitions.model.impl.KeywordStatus;
import eu.europeana.enrichment.mongo.dao.KeywordDaoImpl;
import eu.europeana.enrichment.web.model.KeywordUtils;
import eu.europeana.enrichment.web.model.KeywordView;
import eu.europeana.enrichment.web.repository.KeywordRepository;

@Service(EnrichmentConstants.BEAN_ENRICHMENT_KEYWORD_SERVICE)
public class EnrichmentKeywordServiceImpl {

    /*
     * Loading all translation services
     */

    @Autowired()
    //Data access object
    KeywordDaoImpl keywordDao;

    @Autowired
    //data tables repository
    KeywordRepository keywordRepository;

    Logger logger = LogManager.getLogger(getClass());

    public DataTablesOutput<KeywordView> getKeywords(DataTablesInput input) {

        DataTablesInput.SearchConfiguration searchConfiguration = new DataTablesInput.SearchConfiguration();      
        input.setSearchConfiguration(searchConfiguration);
        searchConfiguration.setSearchType("position", DataTablesInput.SearchType.Integer);
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
 
    public KeywordView approve(String objectId) throws HttpException {
        Keyword keyword = setApprovedAndSave(objectId, KeywordStatus.APPROVED, null);
        return KeywordUtils.createView(keyword);
    }

    public KeywordView approveAlternative(String objectId, @NotNull String wkdId) throws HttpException {
        Keyword keyword = setApprovedAndSave(objectId, KeywordStatus.APPROVED_ALTERNATIVE, wkdId);
        return KeywordUtils.createView(keyword);
    }
    
    public KeywordView approveBroadMatch(String objectId, @NotNull String wkdId) throws HttpException {
        Keyword keyword = setApprovedAndSave(objectId, KeywordStatus.APPROVED_BROAD_MATCH, wkdId);
        return KeywordUtils.createView(keyword);
    }
    
    private Keyword setApprovedAndSave(String objectId, String status, String alternativeWkdId) throws HttpException {
        Keyword keyword =  keywordDao.findByObjectId(objectId);
        if(keyword==null) {
            throw new HttpException("No keyword found with objectId: " + objectId, "KEYOWRD_NOT_FOUND", HttpStatus.NOT_FOUND);
        }
        markAsApproved(keyword, status, alternativeWkdId);
        return keywordDao.saveKeyword(keyword);
    }

    private void markAsApproved(@NotNull Keyword keyword, @NotNull String status, String alternativeWkdId) throws HttpException {
        if(alternativeWkdId == null) {
            keyword.setApprovedWikidataId(keyword.getPreferredWikidataId());
        } else {
            try {
                String wkdId = WikidataUtils.getWikidataEntityUri(alternativeWkdId);
                keyword.setApprovedWikidataId(wkdId);
            } catch (RuntimeException e) {
                String message = "Invalid alternative wikidata id: " + alternativeWkdId;
                throw new HttpException(message, message, HttpStatus.BAD_REQUEST);
            }   
        }
        keyword.setStatus(status);
        keyword.setModified(new Date());
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
