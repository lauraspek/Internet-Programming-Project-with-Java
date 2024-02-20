/*********************************************
 Classe per gestione oggetti RSS
 Costruttore per la creazione dell'oggetto User estrapolando le informazioni dal DB

 I metodi che la classe espone sono
 SETTER
 1. setRss()			**Inserisce un rss in DB
 2. setDescription()	**Aggiorna RSS_DESCRIPTION tramite parametro rssId
 3. setUrl()			**Aggiorna RSS_URL tramite paramentro rssId
 4. setDeleted()		**Aggiorna RSS_PUBLISHED tramite parametro rssId
 GETTER
 1. getRss()			**Ritorna attributi RSS
 2. getId()			**Ritorna RSS_ID
 3. getDescription()	**Ritorna RSS_DESCRIPTION
 4. getUrl()			**Ritorna RSS_URL
 5. getPublished()	**Ritorna RSS_PUBLISHED
 *********************************************/

package dbObject;

public class Rss {
	private int rssId;
	private String url, description;
	private boolean approved, deleted;

	public Rss(int rssId, String description, String url, boolean approved, boolean deleted) {
		this.rssId = rssId;
		this.description = description;
		this.url = url;
		this.approved = approved;
		this.deleted = deleted;
	}

	//*************************************************************************************************************
	// ------------------SETTER -----------------
	// SETTER per RSS DESCRIPTION
	public void setDescription(String description) {
		this.description = description;
	}
	// SETTER per RSS URL
	public void setUrl(String url) {
		this.url = url;
	}
	// SETTER per RSS APPROVED
	public void setApproved(){
		this.approved = true;
	}
	// SETTER per RSS DELETED
	public void setDeleted(){
		this.deleted = true;
	}
	//*************************************************************************************************************
	// ------------------ GETTER ----------------
	// GETTER per RSS ID
	public int getId() {
		return rssId;
	}
	// GETTER per RSS DESCRIPTION
	public String getDescription() {
		return description;
	}
	// GETTER per RSS URL
	public String getUrl() {
		return url;
	}
	// GETTER per RSS APPROVED
	public boolean getApproved() {
		return approved;
	}
	// GETTER per RSS DELETED
	public boolean getDeleted() {
		return deleted;
	}
}