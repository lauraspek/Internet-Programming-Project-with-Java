/*********************************************
 Classe per gestione oggetti NEWS
 Costruttore per la creazione dell'oggetto User estrapolando le informazioni dal DB

 I metodi che la classe espone sono
 SETTER
 1. setNews()			**Inserisce news in DB
 2. setTitle()			**Aggiorna NEWS_TITLE tramite attributo newsId
 3. setDate()			**Aggiorna NEWS_DATE tramite attributo newsId
 4. setDescription()		**Aggiorna NEWS_DESCRIPTION tramite attributo newsId
 5. setAuthor()			**Aggiorna NEWS_AUTHOR tramite attributo newsId
 6. setRssId() 			-- DA VERFIFICARE ora non attiva
 7. setUrl()				**Aggiorna NEWS_URL tramite attributo newsId
 8. setImageUrl			**Aggiorna NEWS_IMAGEURL tramite attributo newsId
 9. setDeleted()			**Aggiorna NEWS_DELETED tramite l'attributo newsId
 GETTER
 2. getId()				**Restituisce NEWS_ID
 3. getTitle()			**Restituisce NEWS_TITLE
 4. getDate()			**Restituisce NEWS_DATE
 5. getDescription()		**Restituisce NEWS_DESCRIPTION
 6. getAuthor()			**Restituisce NEWS_AUTHOR
 7. getRssId()			**Restituisce RSS_ID
 8. getUrl()				**Restituisce NEWS_URL
 9. getImageUrl()		**Restituisce NEWS_IMAGEURL
 10. getDeleted()			**Restituisce NEWS_DELETED
 *********************************************/
package dbObject;
import java.util.Date;

public class News{

	private int newsId, rssId;
	private String title, description, author, url, imageUrl;
	private Date date;
	private boolean marked, deleted;

	public News(int newsId, String title, Date date, String description, String author, int rssId, String url, String imageUrl, boolean marked, boolean deleted) {
		this.newsId = newsId;
		this.title = title;
		this.date = date;
		this.description = description;
		this.author = author;
		this.rssId = rssId;
		this.url = url;
		this.imageUrl = imageUrl;
		this.marked = marked;
		this.deleted = deleted;
	}
	// ------------------SETTER -----------------
	// SETTER per NEWS TITLE
	public void setTitle(String title) {
		this.title = title;
	}
	// SETTER per NEWS DATE
	public void setDate(Date date) {
		this.date = date;
	}
	// SETTER per NEWS DESCRIPTION
	public void setDescription(String description) {
		this.description = description;
	}
	// SETTER per NEWS AUTHOR
	public void setAuthor(String author) {
		this.author = author;
	}
	// SETTER per NEWS URL
	public void setUrl(String url) {
		this.url = url;
	}
	// SETTER per NEWS IMAGE URL
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	// SETTER per NEWS MARKED
	public void setMarked() {
		this.marked = true;
	}
	// SETTER per NEWS DELETED
	public void setDeleted() {
		this.deleted = true;
	}
	// ------------------ GETTER ----------------
	// GETTER per NEWS ID
	public int getId() {
		return newsId;
	}
	// GETTER per NEWS TITLE
	public String getTitle() {
		return title;
	}
	// GETTER per NEWS ID
	public Date getDate() {
		return date;
	}
	// GETTER per NEWS DESCRIPTION
	public String getDescription() {
		return description;
	}
	// GETTER per NEWS AUTHOR
	public String getAuthor() {
		return author;
	}
	// GETTER per NEWS RSS_ID
	public int getRssId() {
		return rssId;
	}
	// GETTER per NEWS URL
	public String getUrl() {
		return url;
	}
	// GETTER per NEWS IMAGE URL
	public String getImageUrl() {
		return imageUrl;
	}
	// GETTER per NEWS MARKED
	public boolean getMarked() {
		return marked;
	}
	// GETTER per NEWS ACTIVE
	public boolean getDeleted() {
		return deleted;
	}
}