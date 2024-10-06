package eu.europeana.enrichment.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import eu.europeana.api.commons.definitions.vocabulary.CommonApiConstants;
import eu.europeana.api.commons.web.model.vocabulary.Operations;
import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.definitions.exceptions.EntityRetrievalException;
import eu.europeana.enrichment.definitions.model.impl.ItemEntityImpl;
import eu.europeana.enrichment.definitions.model.impl.StoryEntityImpl;
import eu.europeana.enrichment.definitions.model.impl.TranslationEntityImpl;
import eu.europeana.enrichment.mongo.service.PersistentItemEntityService;
import eu.europeana.enrichment.mongo.service.PersistentStoryEntityService;
import eu.europeana.enrichment.web.common.config.I18nConstants;
import eu.europeana.enrichment.web.exception.ParamValidationException;
import eu.europeana.enrichment.web.exception.ResourceNotFoundException;
import eu.europeana.enrichment.web.model.TranslationUpdateRequest;
import eu.europeana.enrichment.web.service.EnrichmentStoryAndItemStorageService;
import eu.europeana.enrichment.web.service.EnrichmentTranslationService;
import eu.europeana.enrichment.web.service.impl.EnrichmentUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@EnableCaching
@Api(tags = "Translation service", description = " ")
public class TranslationController extends BaseRest {

    @Autowired
    EnrichmentTranslationService enrichmentTranslationService;

    Logger logger = LogManager.getLogger(getClass());

    /**
     * This method represents the /enrichment/translation/{story} end point, where a
     * translation request will be processed. All requests on this end point are
     * processed here.
     * 
     * @param storyId
     * @param translationTool
     * @param property
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "Translate text (Google, eTranslation, DeepL) for Stories", nickname = "postTranslationStory", notes = "This method translates the textual information of transcribathon documents. \"storyId\" represents the identifier of the document in Transcribathon platform.\n"
            + " The \"property\" parameter indicates which textual information will be translated, supported values: \"summary\" or \"description\".\n"
            + "The \"translationTool\" parameter indicates which machine translation tool will be used for performing the translation, supported value: \"Google\", \"eTranslation\", \"DeepL\".")
    @RequestMapping(value = "/enrichment/translation/{storyId}", method = {
            RequestMethod.POST }, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> postTranslationStory(@PathVariable("storyId") String storyId,
            @RequestParam(value = "translationTool", required = true, defaultValue = "Google") String translationTool,
            @RequestParam(value = "property", required = false, defaultValue = "description") String property,
            HttpServletRequest request) throws Exception {

        verifyWriteAccess(Operations.CREATE, request);

        validateTranslationParams(storyId, null, translationTool, property, false);

        String result = null;
        StoryEntityImpl story = persistentStoryEntityService.findStoryEntity(storyId);
        if (story != null) {
            result = enrichmentTranslationService.translateStory(story, property, translationTool, true);
        }
        ResponseEntity<String> response = new ResponseEntity<String>(result, HttpStatus.OK);
        return response;
    }

    @ApiOperation(value = "Get translated text (Google, eTranslation, DeepL) for Stories", nickname = "getTranslationStory", notes = "This method retrieves the translated story elements. \"storyId\" represents the identifier of the document in Transcribathon platform.\n"
            + " The \"property\" parameter indicates which textual information has been translated, supported values: \"summary\" or \"description\".\n"
            + "The \"translationTool\" parameter indicates which machine translation tool has been used for performing the translation, supported value: \"Google\", \"eTranslation\", \"DeepL\".")
    @RequestMapping(value = "/enrichment/translation/{storyId}", method = {
            RequestMethod.GET }, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getTranslationStory(@PathVariable("storyId") String storyId,
            @RequestParam(value = "translationTool", required = true, defaultValue = "Google") String translationTool,
            @RequestParam(value = "property", required = false, defaultValue = "description") String property,
            @RequestParam(value = CommonApiConstants.PARAM_WSKEY) String wskey, HttpServletRequest request)
            throws Exception {

        verifyReadAccess(request);

        validateTranslationParams(storyId, null, translationTool, property, false);

        String result = null;
        StoryEntityImpl story = persistentStoryEntityService.findStoryEntity(storyId);
        if (story != null) {
            result = enrichmentTranslationService.translateStory(story, property, translationTool, false);
        }
        ResponseEntity<String> response = new ResponseEntity<String>(result, HttpStatus.OK);
        return response;
    }

    /**
     * This method represents the /enrichment/translation/{storyId}/{itemId} end
     * point, where a translation request for an item will be processed. All
     * requests on this end point are processed here.
     * 
     * @param storyId
     * @param itemId
     * @param translationTool
     * @param property
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "Translate text (Google, eTranslation, DeepL) for Items", nickname = "postTranslationItem", notes = "This method translates the textual information of transcribathon documents. \"storyId\" represents the identifier of the document in Transcribathon platform.\n"
            + " The parameter \"itemId\" further enables considering only specific story item. "
            + " The \"property\" parameter indicates which textual information will be translated, supported values:  \"description\" or \"transcription\".\n"
            + "The \"translationTool\" parameter indicates which machine translation tool will be used for performing the translation, supported value: \"Google\", \"eTranslation\", \"DeepL\".")
    @RequestMapping(value = "/enrichment/translation/{storyId}/{itemId}", method = {
            RequestMethod.POST }, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> postTranslationItem(@PathVariable("storyId") String storyId,
            @PathVariable("itemId") String itemId,
            @RequestParam(value = "translationTool", required = true, defaultValue = "Google") String translationTool,
            @RequestParam(value = "property", required = false, defaultValue = "transcription") String property,
            HttpServletRequest request) throws Exception {

        verifyWriteAccess(Operations.CREATE, request);

        validateTranslationParams(storyId, itemId, translationTool, property, true);

        String result = null;
        ItemEntityImpl item = retrieveOrFetchItem(storyId, itemId, false);
        if(item==null) {
            throw new ResourceNotFoundException("item", EnrichmentUtils.buildResourcePath(storyId, itemId));
        } 

        //remove previously computed enrichments
        //remove also translation
        //TODO revisit to remove redundant calls to remove enrichments 
        enrichmentStoryAndItemStorageService.removeItemEnrichments(item, property, true);
        //compute and save translation into DB 
        //TODO change implementation to update instead of delete and recreate
        result = enrichmentTranslationService.translateItem(item, property, translationTool, true);

        return new ResponseEntity<String>(result, HttpStatus.OK);
    }

    /**
     * This method represents the /enrichment/translation/{storyId}/{itemId} end
     * point, where a translation request for an item will be processed. All
     * requests on this end point are processed here.
     * 
     * @param storyId  TP story id
     * @param itemId   TP item id
     * @param property indicating the property which holds the original text (e.g.
     *                 summary, description, transcription)
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "Submit corrected translation text for Items", nickname = "updateItemTranslation", notes = "This method updates the translation of textual information available for transcribathon documents. \"storyId\" represents the identifier of the document in Transcribathon platform.\n"
            + " The parameter \"itemId\" further enables considering only specific story item. "
            + " The \"property\" parameter indicates the property which holds the original text, supported values: \"description\" or \"transcription\".\n")
    @PutMapping(value = "/enrichment/translation/{storyId}/{itemId}", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> updateItemTranslation(@PathVariable("storyId") String storyId,
            @PathVariable("itemId") String itemId, @RequestBody TranslationUpdateRequest body,
            HttpServletRequest request) throws Exception {

        verifyWriteAccess(Operations.UPDATE, request);

        // translation tool is optional
        if (StringUtils.isEmpty(body.getTranslationTool())) {
            body.setTranslationTool(EnrichmentConstants.defaultTranslationTool);
        }
        validateTranslationParams(storyId, itemId, body.getTranslationTool(), body.getProperty(), true);

        if (StringUtils.isEmpty(body.getText())) {
            throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentConstants.BODY,
                    body.getText());
        }
        
        //do not remove translation it is needed to save version
        ItemEntityImpl item = retrieveOrFetchItem(storyId, itemId, false);
        if(item==null) {
            throw new ResourceNotFoundException("item", EnrichmentUtils.buildResourcePath(storyId, itemId));
        } 
        // if(item==null) throw exception
        //remove previously computed enrichments (could be improved to verify first, if some exists)
        //do not remove translation it is needed to save version
        enrichmentStoryAndItemStorageService.removeItemEnrichments(item, EnrichmentConstants.TRANSCRIPTION, false);
        TranslationEntityImpl updateItemTranslation = enrichmentTranslationService.updateItemTranslation(item, body);
        String translation = null;
        if(updateItemTranslation != null) {
            translation = updateItemTranslation.getTranslatedText();
        }
        
        return new ResponseEntity<String>(translation, HttpStatus.OK);
    }

    /**
     * This method represents the /enrichment/translation/{storyId}/{itemId} end
     * point, where a translation request for an item will be processed. All
     * requests on this end point are processed here.
     * 
     * @param storyId  TP story id
     * @param itemId   TP item id
     * @param property indicating the property which holds the original text (e.g.
     *                 summary, description, transcription)
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "Submit corrected translation text for Stories", nickname = "updateStoryTranslation", notes = "This method updates the translation of textual information available in transcribathon documents. \"storyId\" represents the identifier of the document in Transcribathon platform.\n"
            + " The \"property\" parameter indicates the property which holds the original text, supported values: \"summary\" or \"description\".")
    @PutMapping(value = "/enrichment/translation/{storyId}", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> updateStoryTranslation(@PathVariable("storyId") String storyId,
            @RequestBody TranslationUpdateRequest body, HttpServletRequest request) throws Exception {

        verifyWriteAccess(Operations.UPDATE, request);

        // translation tool is optional
        if (StringUtils.isEmpty(body.getTranslationTool())) {
            body.setTranslationTool(EnrichmentConstants.defaultTranslationTool);
        }
        validateTranslationParams(storyId, null, body.getTranslationTool(), body.getProperty(), false);

        if (StringUtils.isEmpty(body.getText())) {
            throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentConstants.BODY,
                    body.getText());
        }

        TranslationEntityImpl updateItemTranslation = enrichmentTranslationService.updateStoryTranslation(storyId,
                body);

        return new ResponseEntity<String>(updateItemTranslation.getTranslatedText(), HttpStatus.OK);
    }

    @ApiOperation(value = "Get translated text (Google, eTranslation, DeepL) for Items", nickname = "getTranslationItem", notes = "This method retrieves the translated item elements. \"storyId\" and \"itemId\" enable the identification of the document in Transcribathon platform.\n"
            + " The \"property\" parameter indicates which textual information has been translated, supported values: \"description\" or \"transcription\".\n"
            + "The \"translationTool\" parameter indicates which machine translation tool has been used for performing the translation, supported value: \"Google\", \"eTranslation\", \"DeepL\".")
    @RequestMapping(value = "/enrichment/translation/{storyId}/{itemId}", method = {
            RequestMethod.GET }, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getTranslationItem(@PathVariable("storyId") String storyId,
            @PathVariable("itemId") String itemId,
            @RequestParam(value = "translationTool", required = true, defaultValue = "Google") String translationTool,
            @RequestParam(value = "property", required = false, defaultValue = "description") String property,
            @RequestParam(value = CommonApiConstants.PARAM_WSKEY) String wskey, HttpServletRequest request)
            throws Exception {

        verifyReadAccess(request);

        validateTranslationParams(storyId, itemId, translationTool, property, true);

        String result = null;
        ItemEntityImpl item = persistentItemEntityService.findItemEntity(storyId, itemId);
        if (item == null) {
            // TODO: import item;
        }
        if (item != null) {
            result = enrichmentTranslationService.translateItem(item, property, translationTool, false);
        }
        ResponseEntity<String> response = new ResponseEntity<String>(result, HttpStatus.OK);
        return response;
    }

}
