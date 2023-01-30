package eu.europeana.enrichment.web.service.impl;

import eu.europeana.enrichment.common.commons.EnrichmentConstants;

public class WikidataUtils {

    public static String getWikidataEntityUri(String wikidataEntity) {
        if(wikidataEntity.startsWith(EnrichmentConstants.WIKIDATA_ENTITY_BASE_URL)) {
            return wikidataEntity;
        }else  if(wikidataEntity.startsWith("Q")) {
             return EnrichmentConstants.WIKIDATA_ENTITY_BASE_URL + wikidataEntity;
         } else {
            throw new IllegalArgumentException("The argument must be a wikidata entity URI or a wikidata entity ID (e.g. Q1234)");
         }
    }

}
