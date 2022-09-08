package eu.europeana.enrichment.web.service.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.web.repository.KeywordRepository;

@Service(EnrichmentConstants.BEAN_ENRICHMENT_UI_SERVICE)
public class UIServiceImpl {

    @Autowired
    KeywordRepository keywordRepository;
}
