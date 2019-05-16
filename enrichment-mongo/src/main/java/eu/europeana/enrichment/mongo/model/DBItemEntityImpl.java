package eu.europeana.enrichment.mongo.model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.NotSaved;
import org.mongodb.morphia.annotations.Transient;
import org.springframework.data.annotation.Id;

import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.impl.ItemEntityImpl;

public class DBItemEntityImpl extends ItemEntityImpl{

	//id will be used for storing MongoDB _id
	@Id
    public String _id = new ObjectId().toString();
	@Transient
	@NotSaved
	private StoryEntity storyEntity;
	
	@Override
	public String getId() {
		return _id;
	}
	
	@Override
	public StoryEntity getStoryEntity() {
		return storyEntity;
	}

	@Override
	public void setStoryEntity(StoryEntity storyEntity) {
		this.storyEntity = storyEntity;
		if(storyEntity != null)
			setStoryId(storyEntity.getStoryId());
		else
			setStoryId(null);
	}
}
