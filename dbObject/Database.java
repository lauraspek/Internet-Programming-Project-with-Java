/*********************************************
 Classe per gestione oggetti DATABASE
 La classe espone un COSTRUTTORE per settare le variabili di istanza per la creazione delle tabelle del DB

 I metodi che la classe espone sono
 SETTER
 1. Create()			**Crea tutte le tabelle se non esistono gia
 2. setNewsId()		**Aggiorna NEWS_ID tramite parametro commentId
 3. setUserId() 		**Aggiorna USER_ID tramite parametro commentId
 4. setText()		**Aggiorna COMMENT_TEXT tramite parametro commentId
 5. setMarked()		**Aggiorna COMMENT_MARKED tramite parametro commentId
 OTHER
 1. clearUsers()		**Svuota la tabella USERS
 2. clearRss()		**Svuota la tabella RSS
 3. clearNews()		**Svuota la tabella NEWS
 4. clearRating()	**Svuota la tabella RATING
 5. clearComments()	**Svuota la tabella COMMENTS
 6. clearAll()		**Svuota tutte le tabelle
 *********************************************/

package dbObject;
import java.sql.*;

public class Database {

	private static String[] tables = new String[5];
	private static String[] tablesName = {"USERS","RSS","NEWS","RATING","COMMENTS"};

	public Database() {
		// TABLE USERS
		tables[0] = "CREATE TABLE "+ tablesName[0] +" "
				+ "(USER_ID INTEGER PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),"
				+ " USER_NICKNAME VARCHAR(100) NOT NULL,"
				+ " USER_PWD VARCHAR(100),"
				+ " USER_TELEGRAMID VARCHAR(100) NOT NULL,"
				+ " USER_ROLE VARCHAR(100) NOT NULL,"
				+ " USER_APPROVED BOOLEAN NOT NULL,"
				+ " USER_DELETED BOOLEAN NOT NULL)";
		// TABLE RSS
		tables[1] = "CREATE TABLE "+ tablesName[1] +" "
				+ " (RSS_ID INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY(START WITH 1, INCREMENT BY 1),"
				+ " RSS_DESCRIPTION VARCHAR(200) NOT NULL,"
				+ " RSS_URL VARCHAR(30000) NOT NULL,"
				+ " RSS_DELETED BOOLEAN NOT NULL)";
		// TABLE NEWS
		tables[2] = "CREATE TABLE "+ tablesName[2] +" "
				+ " (NEWS_ID INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY(START WITH 1, INCREMENT BY 	1),"
				+ " NEWS_TITLE VARCHAR(10000) NOT NULL,"
				+ " NEWS_DATE DATE NOT NULL,"
				+ " NEWS_DESCRIPTION VARCHAR(10000) NOT NULL,"
				+ " NEWS_AUTHOR VARCHAR(1000) NOT NULL,"
				+ " RSS_ID INTEGER NOT NULL,"
				+ " NEWS_URL VARCHAR(10000) NOT NULL,"
				+ " NEWS_IMAGEURL LONG VARCHAR NOT NULL,"
				+ " NEWS_DELETED BOOLEAN NOT NULL)";
		//+ " FOREIGN KEY (RSS_ID) REFERENCES RSS(RSS_ID))";
		// TABLE RATING
		tables[3] = "CREATE TABLE "+ tablesName[3] +" "
				+ " (RATING_ID INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY(START WITH 1, INCREMENT BY 	1),"
				+ " RATING_VOTE integer NOT NULL,"
				+ " NEWS_ID INTEGER NOT NULL,"
				+ " USER_ID INTEGER NOT NULL)";
		//+ " FOREIGN KEY (NEWS_ID) REFERENCES NEWS(NEWS_ID),"
		//+ " FOREIGN KEY (USER_ID) REFERENCES USERS(USER_ID))";
		// TABLE COMMENTS
		tables[4] = "CREATE TABLE "+ tablesName[4] +" "
				+ " (COMMENT_ID INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY(START WITH 1, INCREMENT BY 1),"
				+ " COMMENT_TEXT LONG VARCHAR NOT NULL,"
				+ " COMMENT_MARKED BOOLEAN,"
				+ " COMMENT_DELETED BOOLEAN,"
				+ " NEWS_ID INTEGER NOT NULL,"
				+ " USER_ID INTEGER NOT NULL)";
		//+ " FOREIGN KEY (NEWS_ID) REFERENCES NEWS(NEWS_ID),"
		//+ " FOREIGN KEY (USER_ID) REFERENCES USERS(USER_ID))";
	}
	//----------METODO CREAZIONE TABELLE CON CICLO FOR ----------
	public static void Create(Statement stmt) throws ClassNotFoundException, SQLException {
		for (int i=0; i<tables.length;i++) {
			try {
				stmt.execute(tables[i]);
				System.out.println("Table " + tablesName[i] + " created!");
			}
			catch (SQLException error) {
				if (error.getErrorCode() == 30000) System.out.println("Table " + tablesName[i] + " already exist");
				else System.out.println(error.getMessage());
			}
		}// FINE FOR
	}

	//---------- PULIZIA TABELLA USERS ----------
	public static void clearUsers(Statement stmt) {
		try { stmt.execute("TRUNCATE TABLE USERS");}
		catch (SQLException error) { System.out.println(error.getMessage());}
	}
	//---------- PULIZIA TABELLA RSS ----------
	public static void clearRss(Statement stmt) {
		try { stmt.execute("TRUNCATE TABLE RSS");}
		catch (SQLException error) { System.out.println(error.getMessage());}
	}
	//---------- PULIZIA TABELLA NEWS ----------
	public static void clearNews(Statement stmt) {
		try { stmt.execute("TRUNCATE TABLE NEWS");}
		catch (SQLException error) { System.out.println(error.getMessage());}
	}
	//---------- PULIZIA TABELLA RATING ----------
	public static void clearRating(Statement stmt) {
		try { stmt.execute("TRUNCATE TABLE RATING");}
		catch (SQLException error) { System.out.println(error.getMessage());}
	}
	//---------- PULIZIA TABELLA COMMENTS ----------
	public static void clearComments(Statement stmt) {
		try { stmt.execute("TRUNCATE TABLE COMMENTS");}
		catch (SQLException error) { System.out.println(error.getMessage());}
	}
	//---------- PULIZIA DI TUTTE LE TABELLE ----------
	public static void clearAll(Statement stmt) {
		for (int i=4; i>=0;i--) {
			System.out.println("Sto svuotando la tabella: " +tablesName[i]);
			try { stmt.execute("TRUNCATE TABLE "+tablesName[i]+"");}
			catch (SQLException error) { System.out.println(error.getMessage());}
		}
	}
}