package eu.europeana.enrichment.tp.api.client.model.v2;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.morphia.annotations.Embedded;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
@Embedded
public class ItemV2 {
    @JsonProperty("ItemId")
    private Integer ItemId;
    @JsonProperty("Title")
    private String Title;
    @JsonProperty("Description")
    private String Description;
    @JsonProperty("DescriptionLanguage")
    private Integer DescriptionLanguage;
    @JsonProperty("StoryId")
    private Integer StoryId;
    @JsonProperty("TranscriptionSource")
    private String TranscriptionSource;
    @JsonProperty("Transcription")
    private TranscriptionV2 Transcription;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone="UTC")
    @JsonProperty("LastUpdated")
    public Date lastUpdated;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone="UTC")
    @JsonProperty("Timestamp")
    public Date timestamp;
    
    
    public Integer getItemId() {
        return ItemId;
    }
    public void setItemId(Integer itemId) {
        ItemId = itemId;
    }
    public String getTitle() {
        return Title;
    }
    public void setTitle(String title) {
        Title = title;
    }
    public String getDescription() {
        return Description;
    }
    public void setDescription(String description) {
        Description = description;
    }
    public Integer getDescriptionLanguage() {
        return DescriptionLanguage;
    }
    public void setDescriptionLanguage(Integer descriptionLanguage) {
        DescriptionLanguage = descriptionLanguage;
    }
    public Integer getStoryId() {
        return StoryId;
    }
    public void setStoryId(Integer storyId) {
        StoryId = storyId;
    }
    public String getTranscriptionSource() {
        return TranscriptionSource;
    }
    public void setTranscriptionSource(String transcriptionSource) {
        TranscriptionSource = transcriptionSource;
    }
    public TranscriptionV2 getTranscription() {
        return Transcription;
    }
    public void setTranscription(TranscriptionV2 transcription) {
        Transcription = transcription;
    }
    public Date getLastUpdated() {
        return lastUpdated;
    }
    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    public Date getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }


   

}
