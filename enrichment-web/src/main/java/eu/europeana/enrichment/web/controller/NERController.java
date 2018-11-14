package eu.europeana.enrichment.web.controller;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import eu.europeana.enrichment.web.config.swagger.SwaggerSelect;
import eu.europeana.enrichment.web.model.EnrichmentNERRequest;
import eu.europeana.enrichment.web.service.EnrichmentService;
import eu.europeana.enrichment.web.service.impl.EnrichmentServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


@RestController
@SwaggerSelect
@Api(tags = "NER annotation", description=" ")
public class NERController extends BaseRest {

	//@Resource
	//EnrichmentService enrichmentService;
	
	@Override
	protected void init() {
		//super.init();
		//Load all NER tools
		//enrichmentService = new EnrichmentServiceImpl();
		//enrichmentService.init();
	}
	
	@ApiOperation(value = "Annotate text", nickname = "getNERAnnotation")
	@RequestMapping(value = "/enrichment/annotation", method = {RequestMethod.POST},
			consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getNERAnnotation(@RequestBody EnrichmentNERRequest nerRequest) {

		//try {			
			//validateApiKey(wskey);
		
			String jsonLd = "";//enrichmentService.annotateText(nerRequest.text, nerRequest.tool);
			ResponseEntity<String> response = new ResponseEntity<String>(jsonLd, HttpStatus.OK);
			
			return response;
	
		//} catch (RuntimeException e) {
			//not found .. 
			//throw new InternalServerException(e);
		//} catch (Exception e) {
			//throw new InternalServerException(e);
		//}				
	}
	
}
