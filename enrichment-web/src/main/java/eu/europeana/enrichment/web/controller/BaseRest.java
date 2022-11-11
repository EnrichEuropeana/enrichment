package eu.europeana.enrichment.web.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;

import eu.europeana.api.commons.web.controller.BaseRestController;
import eu.europeana.api.commons.web.exception.ApplicationAuthenticationException;
import eu.europeana.enrichment.common.commons.EnrichmentConfiguration;
import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.web.service.impl.EnrichmentAuthorizationService;

public abstract class BaseRest  extends BaseRestController{

	@Autowired
	@Qualifier(EnrichmentConstants.BEAN_ENRICHMENT_CONFIGURATION)
	EnrichmentConfiguration config;
	
	@Autowired private EnrichmentAuthorizationService enrichmentAuthorizationService;

	public BaseRest() {
		super();
	}
		
	/**
     * This method generates etag for response header.
     * 
     * @param timestamp The date of the last modification
     * @param format    The MIME format
     * 
     * @return etag value
     */
    public String generateETag(Date timestamp, String format) {
	// add timestamp, format and version to an etag
	Integer hashCode = (timestamp + format).hashCode();
	return hashCode.toString();
    }
    
    @Override
    public Authentication verifyWriteAccess(String operation, HttpServletRequest request)
        throws ApplicationAuthenticationException {
      if (config.isAuthWriteEnabled()) {
        return super.verifyWriteAccess(operation, request);
      }
      return null;
    }

    @Override
    public Authentication verifyReadAccess(HttpServletRequest request)
        throws ApplicationAuthenticationException {
      if (config.isAuthReadEnabled()) {
        return super.verifyReadAccess(request);
      }
      return null;
    }

    protected EnrichmentAuthorizationService getAuthorizationService() {
    	return enrichmentAuthorizationService;
    }

}
