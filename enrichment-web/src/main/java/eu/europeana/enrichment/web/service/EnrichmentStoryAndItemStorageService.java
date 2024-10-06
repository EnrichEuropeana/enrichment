package eu.europeana.enrichment.web.service;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import eu.europeana.enrichment.definitions.exceptions.EntityRetrievalException;
import eu.europeana.enrichment.definitions.model.impl.ItemEntityImpl;
import eu.europeana.enrichment.definitions.model.impl.StoryEntityImpl;

public interface EnrichmentStoryAndItemStorageService {

    public StoryEntityImpl updateStoryFromTranscribathon(String storyId, List<String> fieldsToUpdate, boolean removeTranslations)
            throws EntityRetrievalException;

    public ItemEntityImpl updateItemFromTranscribathon(String storyId, String itemId, boolean removeTranslation) throws EntityRetrievalException;

    public void updateStoriesFromInput(StoryEntityImpl[] stories);

    public void updateItemsFromInput(ItemEntityImpl[] items)
            throws NoSuchAlgorithmException, UnsupportedEncodingException;

    void removeItemEnrichments(ItemEntityImpl dbItem, String field, boolean removeTranslation);

    void removeStoryEnrichments(StoryEntityImpl story, String field, boolean removeTranslation);

}
