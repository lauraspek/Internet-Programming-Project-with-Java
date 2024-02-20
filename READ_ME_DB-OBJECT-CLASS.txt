/****************************************************/
Classe HandlerDb
	Serve per gestire tutte le interazioni con il database
	* è precaricato un utente con ruolo SYSTEM che non può essere modificato da nessuna interfaccia. Questo garantisce di avere un super user nel caso per errore non ci siano più utenti "Admin"
	L'eliminazione di un qualsiasi dato e' logica e non fisica
Riepilogo metodi della classe
//**Connessione e creazione DB
(tutte le funzioni boolean restituiscono TRUE se tutto ok)
	1. boolean create()				**Crea tutte le tabelle.
	2. boolean clearUsers()				**Pulisce tabella USERS.
	3. boolean clearNews()				**Pulisce tabella NEWS.
	4. boolean clearRating()			**Pulisce tabella RATING.
	5. boolean clearComments()			**Pulisce tabella COMMENTS.
	6. boolean clearAll()				**Pulisce tutte le tabelle.
	7. boolean openDb()				**Apre la connessione col DB e se non esiste lo crea
	8. boolean closeDb()				**Chiude la connessione col DB
	9. String replaceIllegalChar(String)	**Sostituisce i caratteri non consentiti dalla stringa per l'inserimento in DB
	10. boolean addUserFromBot(String,String)	**Inserisce utente (imposta lo stato approvato a FALSE)
	11. boolean addUserFromAdmin(String,String,String)	**Inserisce utente (imposta lo stato approvato a TRUE)
	12. Boolean login(String user, String pwd)**Permette il login restituisce TRUE se OK, FALSE se inserimento sbagliato, NULL se cattura exception)
	13. ArrayList<User> getActiveUser()		**Restituisce tutti gli utenti attivi e non di sistema (* vedi inizio)
	14. ArrayList<User> getUserNotApproved()	**Restituisce tutti gli utenti attivi ma non approvati
	15. boolean updateApproveUser(User)		**Aggiorna a TRUE lo stato approvato dell'utente
	16. boolean deleteUser(User)			**Elimina l'utente
	17. boolean changeUserRole(User)		**Aggiorna a Admin o User il ruolo dell'utente
	18. boolean addRssFromBot(String,String)	**Aggiunge un RSS e imposta lo stato approved a FALSE
	19. boolean addRssFromAdmin(StringString)	**Aggiunge un RSS e imposta lo stato approved a TRUE
	20. Boolean checkRss(String)			**Verifica se l'RSS esiste gia', restituisce TRUE se si, FALSE se no, NULL se cattura exception
	21. ArrayList<Rss> getAllRss()		**Restituisce tutti gli RSS attivi
	22. ArrayList<Rss> getApprovedRss()		**Restituisce tutti gli RSS attivi e approvati
	23. boolean updateRssApproved(Rss)		**Aggiorna a TRUE lo stato approved
	24. boolean deleteRss(Rss)			**Elimina l'RSS
	25. boolean addNews(String,Date,String,String,int,String,String)	**Aggiunge una notizia
	26. Boolean checkNews(String)			**Verifica se la notizia esiste gia', restituisce TRUE se si, FALSE se no, NULL se cattura exception
	27. String newsCollector()			**Restituisce stringa con l'elenco delle notizie aggiornate
	28. ArrayList<News> getAllNews()		**Restituisce tutte le news attive
	28. ArrayList<News> getNews(int)		**Restituisce la news con id specificato
	29. ArrayList<News> getNewsSpecificRss(Rss)	**Restituisce tutte le News di uno specifico RSS (l'input e' l'RSS)
	30. ArrayList<News> getNewsSpecificRss(int)	**Restituisce tutte le News di uno specifico RSS (l'input e' l'RSS_ID)
	31. ArrayList<News> getNewsSpecificDate(String)	**Restituisce tutte le News di una certa data (inserire data nel formato yyyy-mm-dd)
	32. ArrayList<News> getNewsActiveRss()	**Restituisce tutte le news degli RSS attivi e approvati
	33. int getNumberCommentsNews(int)		**Restituisce il numero di commenti di una certa news
	34. int getNumberRatingNews(int)		**Restituisce il numero di rating di una certa news
	35. boolean markNews(News)			**Aggiorna a TRUE lo stato MARKED (segnalazione)
	36. boolean deleteMarkNews(News)		**Aggiorna a TRUE lo stato MARKED (segnalazione)
	37. boolean deleteNews(News)			**Elimina la news
	38. boolean addComment(String,int,int)	**Aggiunge un commento (imposta a FALSE lo stato MARKED, segnalato)
	39. ArrayList<Comment> getAllComments()	**Restituisce tutti i commenti attivi
	39. ArrayList<Comment> getComment(int)	**Restituisce commento con id specificato
	40. ArrayList<Comment> getCommentActiveUser()	****Restituisce tutti i commenti degli utenti attivi
	41. boolean markComment(Comment)		**Aggiorna a TRUE lo stato MARKED (segnalato)
	42. boolean deleteMarkComment(Comment)	**Aggiorna a FALSE lo stato MARKED (segnalato)
	43. boolean deleteComment(Comment)		**Elimina commento
	44. String getCommentUser(Comment)		**Restituisce nickname dell'utente che ha fatto il commento
	45. String getCommentNewsTitle(Comment)	**Restituisce il titolo della news del commento
	46. boolean addRating(int, int, int)	**Aggiunge un rating
	46. String getRating(int)			**Restituisce rating su id specificato
	47. String getRatingNewsTitle(Rating)	**Restituisce il titolo della News relativo al rating
	48. String getRatingUser(Rating)		**Restituisce l'utente che ha fatto il rating
=================================================================================================