/*********************************************
 Classe per gestione oggetti COMMENTS
 Costruttore per la creazione dell'oggetto User estrapolando le informazioni dal DB

 I metodi che la classe espone sono
 SETTER
 1. setComment()		**Inserisce un comment in DB
 2. setNewsId()		**Aggiorna NEWS_ID tramite parametro commentId
 3. setUserId() 		**Aggiorna USER_ID tramite parametro commentId
 4. setText()		**Aggiorna COMMENT_TEXT tramite parametro commentId
 5. setMarked()		**Aggiorna COMMENT_MARKED tramite parametro commentId
 5. setDeleted()		**Imposta COMMENT_DELETED tramite parametro commentId
 GETTER
 1. getComment()		**Ritorna attributi commento
 2. getId()			**Ritorna COMMENT_ID
 3. getNewsId()		**Ritorna NEWS_ID
 4. getUserId()		**Ritorna USER_ID
 5. getMarked()		**Ritorna COMMENT_MARKED
 6. getText()		**Ritorna COMMENT_TEXT
 7. getDeleted()		**Ritorna COMMENT_DELETED

 *********************************************/

package dbObject;

public class Comment{

	private int commentId, userId, newsId;
	private String text;
	boolean marked, deleted;

	// COSTRUTTORE con ID da utilizzare quando si estrapolano dati dalla tabella
	public Comment(int commentId, String text, boolean marked, boolean deleted, int newsId, int userId) {
		this.commentId = commentId;
		this.text = text;
		this.marked = marked;
		this.newsId = newsId;
		this.userId = userId;
		this.deleted = deleted;
	}

	// ------------------SETTER per UPDATE -----------------
	// SET per COMMENT NEWS ID
	public void setNewsId(int newsId){
		this.newsId = newsId;
	}
	// SET per COMMENT USER ID
	public void setUserId(int userId) {
		this.userId = userId;
	}
	// SET per COMMENT TEXT
	public void setText(String text) {
		this.text = text;
	}
	// SET per COMMENT MARKED TRUE
	public void setMarked() {
		this.marked = true;
	}
	// SET per COMMENT MARKED FALSE
	public void removeMarked() {
		this.marked = false;
	}
	// SET per COMMENT DELETED
	public void setDeleted() {
		this.deleted = true;
	}

	// ------------------ GETTER ----------------
	// GETTER per COMMENT ID
	public int getId() {
		return commentId;
	}
	// GETTER per COMMENT NEWS ID
	public int getNewsId() {
		return newsId;
	}
	// GETTER per COMMENT USER ID
	public int getUserId() {
		return userId;
	}
	// GETTER per COMMENT MARKED
	public boolean getMarked() {
		return marked;
	}
	// GETTER per COMMENT DELETED
	public boolean getDeleted() {
		return deleted;
	}
	// GETTER per COMMENT TEXT
	public String getText() {
		return text;
	}
}	