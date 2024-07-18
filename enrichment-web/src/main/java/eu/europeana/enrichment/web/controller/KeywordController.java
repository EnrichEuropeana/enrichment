package eu.europeana.enrichment.web.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.datatables.DataTablesInput;
import org.springframework.data.mongodb.datatables.DataTablesOutput;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.enrichment.web.model.KeywordView;
import eu.europeana.enrichment.web.service.impl.EnrichmentKeywordServiceImpl;
import io.swagger.annotations.Api;

@RestController
@Api(tags = "Keyword Validation Service", description=" ")
@RequestMapping("/keyword")
public class KeywordController extends BaseRest {

    @Autowired
    private EnrichmentKeywordServiceImpl enrichmenKeywordService;
    
    
    @GetMapping(value = "/datatable")
    @ResponseBody
    @JsonView(DataTablesOutput.View.class)
    public DataTablesOutput<KeywordView> getKeywords(@Valid DataTablesInput input) {

        return enrichmenKeywordService.getKeywords(input);
    }
	
    @GetMapping(value = "/approve")
    @ResponseBody
    public KeywordView approve(@RequestParam(value = "objectId", required = true) String objectId) throws HttpException {
        return enrichmenKeywordService.approve(objectId);
    }

    @GetMapping(value = "/approve/alternative")
    @ResponseBody
    public KeywordView approveAlternative(
            @RequestParam(value = "objectId", required = true) String objectId,
            @RequestParam(value = "wkdId", required = true) String wkdId
            ) throws HttpException {
        
        return enrichmenKeywordService.approveAlternative(objectId, wkdId);
    }

    @GetMapping(value = "/approve/broad")
    @ResponseBody
    public KeywordView approveBroad(
            @RequestParam(value = "objectId", required = true) String objectId,
            @RequestParam(value = "wkdId", required = true) String wkdId
            ) throws HttpException {
        return enrichmenKeywordService.approveBroadMatch(objectId, wkdId);
    }

    
    @GetMapping(value = "/reject")
    @ResponseBody
    public KeywordView reject(@RequestParam(value = "objectId", required = true) String objectId) throws HttpException {
        return enrichmenKeywordService.reject(objectId);
    }

}