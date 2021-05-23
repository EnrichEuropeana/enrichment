package eu.europeana.enrichment.solr;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;

import eu.europeana.enrichment.common.commons.EnrichmentConfiguration;

@Configuration
@EnableSolrRepositories
public class SolrConfig {

	@Autowired
	public SolrConfig(EnrichmentConfiguration enrichmentConfiguration) {
		this.solrURL = enrichmentConfiguration.getSolrEntityPositionsUrl();
	}

	String solrURL;
	
	@Bean
	public SolrClient solrClient() {
		return new HttpSolrClient.Builder(solrURL).build();
	}

	@Bean
	public SolrTemplate solrTemplate(SolrClient client) throws Exception {
		return new SolrTemplate(client);
	}

}
