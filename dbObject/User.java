/*********************************************
Classe per gestione oggetti USER

I metodi che la classe espone sono
	SETTER
	1. void setUser()		**Inserisce un user in DB
	2. void setNickname()	**Aggiorna USER_NICKNAME nel DB e variabile locale tramite parametro userId
	3. void setPassword()	**Aggiorna USER_PASSWORD nel DB e variabile locale  tramite parametro userId
	4. void setTelegramId()**Aggiorna USER_TELEGRAMID nel DB e variabile locale tramite parametro userId
	5. void setRole()		**Aggiorna USER_ROLE nel DB e variabile locale  tramite parametro userId
	6. void setApproved()	**Imposta a TRUE USER_APPROVED nel DB e variabile locale  tramite parametro userId
	7. void setDeleted()	**Imposta a TRUE USER_DELETED nel DB e variabile locale  tramite parametro userId 
	GETTER
	1. void		getUser()		**Stampa attributi utente
	2. int		getId()			**Ritorna USER_ID (variabile locale)
	3. String	getNickname()	**Ritorna USER_NICKNAME (variabile locale)
	4. String	getPassword()	**Ritorna USER_PASSWORD (variabile locale)
	5. String	getTelegramId()	**Ritorna USER_TELEGRAMID (variabile locale)
	6. String	getRole()		**Ritorna USER_ROLE (variabile locale)
	7. Boolean	getApproved()	**Ritorna USER_APPROVED (variabile locale)
	8. Boolean	getDeleted()	**Ritorna USER_DELETED (variabile locale)
*********************************************/

package dbObject;

public class User {
	private int userId;
	private String nickname, password, telegramId, role;
	private boolean approved, deleted;
	
	public User(int userId, String nickname, String password, String telegramId, String role, boolean approved, boolean deleted) {
		this.userId = userId;
		this.nickname = nickname;
		this.password = password;
		this.telegramId = telegramId;
		this.role = role;
		this.approved = approved;
		this.deleted = deleted;
	}
	
	// ------------------SETTER -----------------
	// SETTER per USER NICKNAME
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	// SETTER per USER PASSWORD
	public void setPassword(String password) {
		this.password = password;
	}
	// SETTER per USER TELEGRAM ID
	public void setTelegramId(String telegramId) {
		this.telegramId = telegramId;
	}
	// SETTER per USER ROLE
	public void setRole(String role) {
		this.role = role;
	}
	// SETTER per USER STATUS
	public void setApproved() { this.approved = true; }
	// SETTER per USER DELETED
	public void setDeleted() {
		this.deleted = true;
	}
	// ------------------ GETTER ----------------
	// GETTER per USER ID
	public int getId() {
		return userId;
	}
	// GETTER per USER NICKNAME
	public String getNickname() {
		return nickname;
	}
	// GETTER per USER PASSWORD
	public String getPassword() {
		return password;
	}
	// GETTER per USER TELEGRAM ID
	public String getTelegramId() {
		return telegramId;
	}
	// GETTER per USER ROLE
	public String getRole() {
		return role;
	}
	// GETTER per USER STATUS
	public boolean getApproved() {
		return approved;
	}
	// GETTER per USER DELETED
	public boolean getDeleted() {
		return deleted;
	}
}