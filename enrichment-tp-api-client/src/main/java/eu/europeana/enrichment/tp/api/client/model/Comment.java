package eu.europeana.enrichment.tp.api.client.model;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class Comment {
	public Integer CommentId;
	public String Text; 
	public Integer UserId; 
	public Integer ItemId; 
	public Timestamp Timestamp;
	
	public void setCommentId (Integer CommentId) {
		this.CommentId = CommentId;
	}
	
	public void setText (String Text) {
		this.Text = Text;
	}
	
	public void setUserId (Integer UserId) {
		this.UserId = UserId;
	}
	
	public void setItemId (Integer ItemId) {
		this.ItemId = ItemId;
	}
	
	public void setTimestamp (Timestamp Timestamp) {
		this.Timestamp = Timestamp;
	}

}
