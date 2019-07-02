package eu.europeana.enrichment.ner.service;

import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.model.NamedEntity;
import eu.europeana.enrichment.model.PositionEntity;
import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.TranslationEntity;
import eu.europeana.enrichment.mongo.model.DBPositionEntityImpl;
import eu.europeana.enrichment.ner.exception.NERAnnotateException;

public interface NERService {

	/**
	 * Getter for the service endpoint.
	 * @return
	 */
	
	public String getEnpoint ();
	
	/**
	 * Setter for the NER service endpoint.
	 * @param endpoint
	 */
	
	public void setEndpoint (String endpoint);
	
	/**
	 * This method identifies named entities based on the translated text.
	 * 
	 * @param text					translated text in English
	 * @return 						a TreeMap based on the classification type
	 * 								including all named entities findings
	 * @throws NERAnnotateException	
	 */
	public TreeMap<String, List<NamedEntity>> identifyNER(String text);
	
	/**
	 * This methods is the default implementation for getting the positions of the NER entities
	 * on the original text
	 * 
	 * @param namedEntity
	 * @param storyEntity
	 * @param itemEntity
	 * @param translationEntity
	 */
	default void getPositions(NamedEntity namedEntity, StoryEntity storyEntity, ItemEntity itemEntity, TranslationEntity translationEntity){
		//TODO: report named entities which we could not find in the original text
		
		String text;
		if(storyEntity != null)
			text = storyEntity.getTranscription();
		else if(translationEntity != null)
			text = translationEntity.getTranslatedText();
		else {
			//TODO: proper exception handling
			return;
		}

		PositionEntity posEntity = null;
		if(storyEntity != null) {
			List<PositionEntity> positions = namedEntity.getPositionEntities().stream().filter(x -> x.getStoryId()
					.equals(storyEntity.getStoryId())).collect(Collectors.toList());
			if(positions.size() == 0) {
				posEntity = new DBPositionEntityImpl();
				posEntity.setStoryEntity(storyEntity);
				positions.add(posEntity);
			}
			else {
				for(PositionEntity posItemEntity : positions)
				{
					if(posItemEntity.getItemId().compareTo(itemEntity.getItemId())==0)
					{
						posEntity = posItemEntity;
						break;
					}
				}
				
			}
		}
		else {
			List<PositionEntity> positions = namedEntity.getPositionEntities().stream().filter(x -> x.getTranslationKey()
					.equals(translationEntity.getKey())).collect(Collectors.toList());
			if(positions.size() == 0) {
				posEntity = new DBPositionEntityImpl();
				posEntity.setTranslationEntity(translationEntity);
				positions.add(posEntity);
			}
			else {
				posEntity = positions.get(0);
			}
		}
		String entityKey = namedEntity.getKey();
		int index = text.indexOf(entityKey);
		while(index >= 0) {
			posEntity.addOfssetsTranslatedText(index);
			index = text.indexOf(entityKey, index+entityKey.length());
		}

	}
}
