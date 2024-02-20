/*********************************************
 Classe per gestione oggetti RATING
 Costruttore per la creazione dell'oggetto User estrapolando le informazioni dal DB

 I metodi che la classe espone sono
 SETTER
 1. setRating()	**Inserisce un rating in DB
 2. setVote()	**Aggiorna RATING_VOTE tramite parametro ratingId
 GETTER
 1. getRating()	**Restituisce attributi rating
 2. getId()		**Restituisce RATING_ID
 3. getVote()	**Restitutisce RATING_VOTE
 4. getNewsId()	**Restituisce NEWS_ID
 5. getUserId()	**Restituisce USER_ID
 *********************************************/

package dbObject;

public class Rating {

	private int ratingId, vote, newsId, userId;

	public Rating (int ratingId, int vote, int newsId, int userId) {
		this.ratingId = ratingId;
		this.vote = vote;
		this.newsId = newsId;
		this.userId = userId;
	}

	// ------------------SETTER -----------------
	public void setVote(int vote) {
		this.vote = vote;
	}
	// ------------------ GETTER ----------------
	// GETTER per RATING ID
	public int getId() {
		return ratingId;
	}
	// GETTER per RATING VOTE
	public int getVote() {
		return vote;
	}
	// GETTER per RATING NEWS ID
	public int getNewsId() {
		return newsId;
	}
	// GETTER per RATING USER ID
	public int getUserId() {
		return userId;
	}
}