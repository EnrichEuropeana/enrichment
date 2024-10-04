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
public class TranscriptionV2 {
    @JsonProperty("UserId")
    public Integer userId;
    // plain text
    @JsonProperty("TranscriptionText")
    public String transcriptionText;
    // PageXml not needed for the time being
    // TranscriptionData
    @JsonProperty("CurrentVersion")
    boolean currentVersion;
    @JsonProperty("HtrDataId")
    public String htrDataId;
    @JsonProperty("HtrDataRevisionId")
    public String htrRevisionId;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone="UTC")
    @JsonProperty("LastUpdated")
    public Date lastUpdated;
    
    

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getTranscriptionText() {
        return transcriptionText;
    }

    public void setTranscriptionText(String transcriptionText) {
        this.transcriptionText = transcriptionText;
    }

    public boolean isCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(boolean currentVersion) {
        this.currentVersion = currentVersion;
    }

    public String getHtrDataId() {
        return htrDataId;
    }

    public void setHtrDataId(String htrDataId) {
        this.htrDataId = htrDataId;
    }

    public String getHtrRevisionId() {
        return htrRevisionId;
    }

    public void setHtrRevisionId(String htrRevisionId) {
        this.htrRevisionId = htrRevisionId;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

}
