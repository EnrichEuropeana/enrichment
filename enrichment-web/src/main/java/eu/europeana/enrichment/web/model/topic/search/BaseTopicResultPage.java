package eu.europeana.enrichment.web.model.topic.search;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import eu.europeana.api.commons.definitions.search.FacetFieldView;
import eu.europeana.api.commons.definitions.search.result.impl.ResultsPageImpl;
import eu.europeana.api.commons.definitions.vocabulary.CommonLdConstants;
import eu.europeana.enrichment.common.commons.EnrichmentConstants;

@JsonPropertyOrder({ EnrichmentConstants.ID, EnrichmentConstants.TYPE, EnrichmentConstants.PART_OF,
	EnrichmentConstants.START_INDEX, EnrichmentConstants.TOTAL, EnrichmentConstants.ITEMS, EnrichmentConstants.PREV, EnrichmentConstants.NEXT, EnrichmentConstants.FACETS })
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class BaseTopicResultPage<T> extends ResultsPageImpl<T>{

    CollectionOverview partOf;
    String type = CommonLdConstants.RESULT_PAGE;

    @JsonProperty(EnrichmentConstants.TYPE)
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty(EnrichmentConstants.PART_OF)
    public CollectionOverview getPartOf() {
        return partOf;
    }

    public void setPartOf(CollectionOverview partOf) {
        this.partOf = partOf;
    }

    @Override
    @JsonProperty(EnrichmentConstants.TOTAL)
    public long getTotalInPage() {
        return super.getTotalInPage();
    }

    @Override
    @JsonIgnore
    public long getTotalInCollection() {
        return super.getTotalInCollection();
    }

    @Override
    @JsonIgnore
    public int getCurrentPage() {
        return super.getCurrentPage();
    }

    @Override
    @JsonIgnore
    public String getResultCollectionUri() {
        return super.getResultCollectionUri();
    }

    @Override
    @JsonIgnore
    public String getCollectionUri() {
        return super.getCollectionUri();
    }

    @Override
    @JsonProperty(EnrichmentConstants.ID)
    public String getCurrentPageUri() {
        return super.getCurrentPageUri();
    }

    @Override
    @JsonProperty(EnrichmentConstants.NEXT)
    public String getNextPageUri() {
        return super.getNextPageUri();
    }

    @Override
    @JsonProperty(EnrichmentConstants.PREV)
    public String getPrevPageUri() {
        return super.getPrevPageUri();
    }

    @Override
    @JsonProperty(EnrichmentConstants.FACETS)
    public List<FacetFieldView> getFacetFields() {
        return super.getFacetFields();
    }

}