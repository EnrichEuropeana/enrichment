package eu.europeana.enrichment.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.datatables.DataTablesInput;
import org.springframework.data.mongodb.datatables.DataTablesOutput;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import eu.europeana.enrichment.model.impl.Keyword;
import eu.europeana.enrichment.web.model.KeywordUtils;
import eu.europeana.enrichment.web.model.KeywordView;
import eu.europeana.enrichment.web.repository.KeywordRepository;
import io.swagger.annotations.Api;

@RestController
@Api(tags = "Keyword Validation Service", description=" ")
@RequestMapping("/keyword")
public class KeywordController extends BaseRest {

    @Autowired
    private KeywordRepository keywordRepository;
    
    @GetMapping(value = "/datatable")
    @ResponseBody
    @JsonView(DataTablesOutput.View.class)
    public DataTablesOutput<KeywordView> getKeywords(@Valid DataTablesInput input) {

        DataTablesInput.SearchConfiguration searchConfiguration = new DataTablesInput.SearchConfiguration();
        input.setSearchConfiguration(searchConfiguration);
        searchConfiguration.setSearchType("position", DataTablesInput.SearchType.Integer);
//        searchConfiguration.setExcludedColumns(List.of("country", "geoCoordinates"));
        
//        searchConfiguration.setSearchType("isEnabled", DataTablesInput.SearchType.Boolean);

//        searchConfiguration.getExcludedColumns().add("user");

//        List<String> productRefColumns = new ArrayList<>();
//        productRefColumns.add("label");
//        productRefColumns.add("isEnabled");
//        productRefColumns.add("createdAt");
//        searchConfiguration.addRefConfiguration("product", "product", productRefColumns, "label");

//        Criteria additionalCriteria = new Criteria();
        input.setSearch(new DataTablesInput.Search(null, false));
        DataTablesOutput<Keyword> keywords = keywordRepository.findAll(input); 

        DataTablesOutput<KeywordView> res = new DataTablesOutput<KeywordView>();
        res.setError(keywords.getError());
        res.setDraw(keywords.getDraw());
        res.setException(keywords.getException());
        res.setRecordsFiltered(keywords.getRecordsFiltered());  
        res.setRecordsTotal(keywords.getRecordsTotal());
        List<KeywordView> results = new ArrayList<KeywordView>();
        KeywordView view;
        for (Keyword keyword : keywords.getData()) {
            view = KeywordUtils.createView(keyword);  
            results.add(view);
        }
        res.setData(results);
        return res;
    }
	
}