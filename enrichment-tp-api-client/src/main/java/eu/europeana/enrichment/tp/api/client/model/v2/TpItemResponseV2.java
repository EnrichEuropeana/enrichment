package eu.europeana.enrichment.tp.api.client.model.v2;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class TpItemResponseV2 {

    boolean success;
    @JsonProperty("data")
    ItemV2 data;
    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }
    @JsonIgnore
    public ItemV2 getItem() {
        return data;
    }
    public ItemV2 getData() {
        return data;
    }
    
    public void setData(ItemV2 data) {
        this.data = data;
    }
}
