package eu.europeana.enrichment.tp.api.client.model;

public class CompletionStatus {
	public Integer CompletionStatusId;
	public String Name; 
	public String ColorCode; 
	public String ColorCodeGradient; 
	public Integer Amount;
	
	public void setCompletionStatusId (Integer CompletionStatusId) {
		this.CompletionStatusId = CompletionStatusId;
	}
	
	public void setName (String Name) {
		this.Name = Name;
	}
	public void setColorCode(String colorCode) {
		ColorCode = colorCode;
	}

	public void setColorCodeGradient(String colorCodeGradient) {
		ColorCodeGradient = colorCodeGradient;
	}

	public void setAmount(Integer amount) {
		Amount = amount;
	}
}
