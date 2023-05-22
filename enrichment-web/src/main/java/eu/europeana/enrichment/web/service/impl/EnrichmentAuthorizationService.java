package eu.europeana.enrichment.web.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.stereotype.Component;

import eu.europeana.api.commons.definitions.vocabulary.Role;
import eu.europeana.api.commons.nosql.service.ApiWriteLockService;
import eu.europeana.api.commons.oauth2.service.impl.EuropeanaClientDetailsService;
import eu.europeana.api.commons.service.authorization.BaseAuthorizationService;
import eu.europeana.enrichment.common.commons.EnrichmentConfiguration;
import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.web.auth.Roles;

@Component(EnrichmentConstants.BEAN_AUTHORIZATIOON_SERVICE)
public class EnrichmentAuthorizationService extends BaseAuthorizationService
    implements eu.europeana.api.commons.service.authorization.AuthorizationService {

  protected final Logger logger = LogManager.getLogger(getClass());

  private final EnrichmentConfiguration config;
  private final EuropeanaClientDetailsService clientDetailsService;

  @Autowired
  public EnrichmentAuthorizationService(
      EnrichmentConfiguration enrichmentConfig,
      EuropeanaClientDetailsService clientDetailsService) {
    this.config = enrichmentConfig;
    this.clientDetailsService = clientDetailsService;
  }

  @Override
  protected ClientDetailsService getClientDetailsService() {
    return clientDetailsService;
  }

  @Override
  protected String getSignatureKey() {
    return config.getApiKeyPublicKey();
  }

  @Override
  protected String getApiName() {
    return config.getAuthorizationApiName();
  }

  @Override
  protected Role getRoleByName(String name) {
    return Roles.getRoleByName(name);
  }

  @Override
  protected ApiWriteLockService getApiWriteLockService() {
	  // TODO Auto-generated method stub
	  return null;
  }

}
