/*********************************************
 Classe per gestione oggetti HANDLERDB
 Costruttore per inizializzazione array per creazione tabelle

 I metodi che la classe espone sono riepilogati nel file READ_ME_DB-OBJECT-CLASS

 *********************************************/

package sample;

import java.util.ArrayList;
//********** PER DB
import dbObject.*;
import java.sql.*;
//********** PER RSS
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import org.xml.sax.InputSource;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;

public class HandlerDb {
	// Dichiaro variabili d'istanza
	protected static Connection conn = null;
	protected static Statement stmt = null;
	private static final String JDBC_URL = "jdbc:derby:naboodb;create=true";
	protected static String[] tables = new String[5];
	protected static String[] tablesName = {"USERS","RSS","NEWS","RATING","COMMENTS"};

	public HandlerDb() {
		// TABLE USERS
		tables[0] = "CREATE TABLE "+ tablesName[0] + " "
				+ " (USER_ID INTEGER PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),"
				+ " USER_NICKNAME VARCHAR(100) NOT NULL,"
				+ " USER_PWD VARCHAR(100),"
				+ " USER_TELEGRAMID VARCHAR(100) NOT NULL,"
				+ " USER_ROLE VARCHAR(100) NOT NULL,"
				+ " USER_APPROVED BOOLEAN NOT NULL,"
				+ " USER_DELETED BOOLEAN NOT NULL)";

		// TABLE RSS
		tables[1] = "CREATE TABLE "+ tablesName[1]
				+ " (RSS_ID INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY(START WITH 1, INCREMENT BY 1),"
				+ " RSS_DESCRIPTION VARCHAR(200) NOT NULL,"
				+ " RSS_APPROVED BOOLEAN NOT NULL,"
				+ " RSS_URL VARCHAR(30000) NOT NULL,"
				+ " RSS_DELETED BOOLEAN NOT NULL)";

		// TABLE NEWS
		tables[2] = "CREATE TABLE "+ tablesName[2]
				+ " (NEWS_ID INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY(START WITH 1, INCREMENT BY 	1),"
				+ " NEWS_TITLE VARCHAR(10000) NOT NULL,"
				+ " NEWS_DATE DATE,"
				+ " NEWS_DESCRIPTION VARCHAR(10000),"
				+ " NEWS_AUTHOR VARCHAR(1000),"
				+ " RSS_ID INTEGER NOT NULL,"
				+ " NEWS_URL VARCHAR(10000),"
				+ " NEWS_IMAGEURL LONG VARCHAR,"
				+ " NEWS_MARKED BOOLEAN NOT NULL,"
				+ " NEWS_DELETED BOOLEAN NOT NULL)";

		// TABLE RATING
		tables[3] = "CREATE TABLE "+ tablesName[3] +" "
				+ " (RATING_ID INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY(START WITH 1, INCREMENT BY 	1),"
				+ " RATING_VOTE integer NOT NULL,"
				+ " NEWS_ID INTEGER NOT NULL,"
				+ " USER_ID INTEGER NOT NULL)";

		// TABLE COMMENTS
		tables[4] = "CREATE TABLE "+ tablesName[4]
				+ " (COMMENT_ID INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY(START WITH 1, INCREMENT BY 1),"
				+ " COMMENT_TEXT LONG VARCHAR NOT NULL,"
				+ " COMMENT_MARKED BOOLEAN,"
				+ " COMMENT_DELETED BOOLEAN,"
				+ " NEWS_ID INTEGER NOT NULL,"
				+ " USER_ID INTEGER NOT NULL)";
	}
	//*************************************************************************************************************
	//----------METODO CREAZIONE TABELLE CON CICLO FOR ----------
	public static boolean create() {
		boolean passed=false;
		for (int i=0; i<tables.length;i++) {
			try {
				stmt.execute(tables[i]);
				System.out.println("Table " + tablesName[i] + " created!");
				passed=true;
			}
			catch (SQLException error) {
				if (error.getErrorCode() == 30000) System.out.println("Table " + tablesName[i] + " already exist");
				else System.out.println(error.getMessage());
			}
		}// FINE FOR
		return passed;
	}
	//---------- PULIZIA TABELLA USERS ----------
	public static boolean clearUsers() {
		boolean passed=false;
		try { stmt.execute("TRUNCATE TABLE USERS"); passed=true;}
		catch (SQLException error) { System.out.println(error.getMessage());}
		return passed;
	}
	//---------- PULIZIA TABELLA RSS ----------
	public static boolean clearRss() {
		boolean passed=false;
		try { stmt.execute("TRUNCATE TABLE RSS"); passed=true;}
		catch (SQLException error) { System.out.println(error.getMessage());}
		return passed;
	}
	//---------- PULIZIA TABELLA NEWS ----------
	public static boolean clearNews() {
		boolean passed=false;
		try { stmt.execute("TRUNCATE TABLE NEWS");passed=true;}
		catch (SQLException error) { System.out.println(error.getMessage());}
		return passed;
	}
	//---------- PULIZIA TABELLA RATING ----------
	public static boolean clearRating() {
		boolean passed=false;
		try { stmt.execute("TRUNCATE TABLE RATING");passed=true;}
		catch (SQLException error) { System.out.println(error.getMessage());}
		return passed;
	}
	//---------- PULIZIA TABELLA COMMENTS ----------
	public static boolean clearComments() {
		boolean passed=false;
		try { stmt.execute("TRUNCATE TABLE COMMENTS");passed=true;}
		catch (SQLException error) { System.out.println(error.getMessage());}
		return passed;
	}
	//---------- PULIZIA DI TUTTE LE TABELLE ----------
	public static boolean clearAll() {
		boolean passed=false;
		for (int i=4; i>=0;i--) {
			System.out.println("Sto svuotando la tabella: " +tablesName[i]);
			try { stmt.execute("TRUNCATE TABLE "+tablesName[i]+""); passed=true;}
			catch (SQLException error) { System.out.println(error.getMessage());}
		}
		return passed;
	}
	//*************************************************************************************************************
	//---------- METODO PER APRIRE CONNESSIONE ----------
	public static boolean openDb() {
		boolean passed=false;
		try {
			conn = DriverManager.getConnection(JDBC_URL);
			System.setProperty("derby.language.sequence.preallocator", String.valueOf(1));
			stmt = conn.createStatement();
			passed=true;
		}
		catch (SQLException error) { System.out.println("Non sono riuscito a stabilire la connessione col DB, vedi errore: " + error);}
		return passed;
	}
	//---------- METODO PER CHIUDERE CONNESSIONE ----------
	public static boolean closeDb() {
		boolean passed=false;
		try {stmt.close(); passed=true;}
		catch (SQLException error) {System.out.println("Non sono riuscito a chiudere la connessione col DB, vedi errore: " +error);}
		return passed;
	}
	//*****************REPLACE ILLEGAL CHAR*****************************************************************************
	public static String replaceIllegalChar(String input) {
		input = input.replace("'","''").replace("\"","").replace("<br/>","").replace("’","''");
		return input;
	}
	//*********************************USER****************************************************************************
	// Aggiunta USER da BOT
	public static boolean addUserFromBot(String nickname, String telegramId) {
		boolean passed=false;
		String queryAdd = "INSERT INTO USERS VALUES (DEFAULT, '"+nickname+"','','"+telegramId+"','User',false,false)";
		try{	stmt.executeUpdate(queryAdd);passed=true;}
		catch (SQLException error) {	System.out.println(error);}
		return passed;
	}
	// Aggiunta USER da ADMIN PANEL
	public static boolean addUserFromAdmin(String nickname, String telegramId, String role) {
		boolean passed=false;
		String queryAdd = "INSERT INTO USERS VALUES (DEFAULT, '"+nickname+"','','"+telegramId+"','"+role+"',true,false)";
		try{	stmt.executeUpdate(queryAdd);passed=true;}
		catch (SQLException error) {	System.out.println(error);}
		return passed;
	}
	// METODO PER LOGIN
	public static Boolean login(String user, String pwd) {
		//throws ClassNotFoundException, SQLException
		ResultSet rs = null;
		Boolean passed;
		String querySelect = "SELECT USER_ID FROM USERS WHERE USER_NICKNAME='"+user+"' AND USER_PWD='"+pwd+"' AND USER_DELETED=false AND USER_ROLE<>'User'";
		try{
			rs = stmt.executeQuery(querySelect);
			if (!rs.next()) passed = false;
			else passed = true;
		}
		catch (SQLException error) { System.out.println(error.getMessage()); passed = null;}
		return passed;
	}
	// METODO PER CHIEDERE USER su SPECIFICO ID TELEGRAM
	public static ArrayList<User> getUser(String telegramId) {
		ResultSet rs = null;
		ArrayList<User> user = new ArrayList<User>();
		String querySelect = "SELECT * FROM USERS WHERE USER_DELETED=false AND  USER_TELEGRAMID='"+telegramId+"'";
		try{
			rs = stmt.executeQuery(querySelect);
			while (rs.next()) {
				user.add(new User(rs.getInt("USER_ID"),rs.getString("USER_NICKNAME"),rs.getString("USER_PWD"),rs.getString("USER_TELEGRAMID"),rs.getString("USER_ROLE"),rs.getBoolean("USER_APPROVED"),rs.getBoolean("USER_DELETED")));
			}
		}
		catch (SQLException error) {	System.out.println(error.getMessage());}
		return user;
	}

	// METODO PER CHIEDERE USER su SPECIFICO ID TELEGRAM
	public static ArrayList<User> getUser(int input) {
		ResultSet rs = null;
		ArrayList<User> user = new ArrayList<User>();
		String querySelect = "SELECT * FROM USERS WHERE USER_DELETED=false AND  USER_ID="+input+"";
		try{
			rs = stmt.executeQuery(querySelect);
			while (rs.next()) {
				user.add(new User(rs.getInt("USER_ID"),rs.getString("USER_NICKNAME"),rs.getString("USER_PWD"),rs.getString("USER_TELEGRAMID"),rs.getString("USER_ROLE"),rs.getBoolean("USER_APPROVED"),rs.getBoolean("USER_DELETED")));
			}
		}
		catch (SQLException error) {	System.out.println(error.getMessage());}
		return user;
	}
	// METODO PER CHIEDERE TUTTI GLI UTENTI ATTIVI
	public static ArrayList<User> getActiveUser() {
		ResultSet rs = null;
		ArrayList<User> allActiveUsers = new ArrayList<User>();
		String querySelect = "SELECT * FROM USERS WHERE USER_DELETED=false AND USER_ROLE<>'System'";
		try{
			rs = stmt.executeQuery(querySelect);
			while (rs.next()) {
				allActiveUsers.add(new User(rs.getInt("USER_ID"),rs.getString("USER_NICKNAME"),rs.getString("USER_PWD"),rs.getString("USER_TELEGRAMID"),rs.getString("USER_ROLE"),rs.getBoolean("USER_APPROVED"),rs.getBoolean("USER_DELETED")));
			}
		}
		catch (SQLException error) {	System.out.println(error.getMessage()); allActiveUsers=null;}
		return allActiveUsers;
	}
	// METODO PER CHIEDERE TUTTI GLI UTENTI DA APPROVARE
	public static ArrayList<User> getUserNotApproved() {
		ResultSet rs = null;
		ArrayList<User> allUsersNotApproved = new ArrayList<User>();
		String querySelect = "SELECT * FROM USERS WHERE USER_DELETED=false AND USER_APPROVED=false";
		try{
			rs = stmt.executeQuery(querySelect);
			while (rs.next()) {
				allUsersNotApproved.add(new User(rs.getInt("USER_ID"),rs.getString("USER_NICKNAME"),rs.getString("USER_PWD"),rs.getString("USER_TELEGRAMID"),rs.getString("USER_ROLE"),rs.getBoolean("USER_APPROVED"),rs.getBoolean("USER_DELETED")));
			}
		}
		catch (SQLException error) {	System.out.println(error.getMessage()); allUsersNotApproved=null;}
		return allUsersNotApproved;
	}
	// METODO PER AGGIORNARE A TRUE CAMPO USER_APPROVED
	public static boolean updateApproveUser(User input) {
		boolean passed=false;
		String queryUpdate = "UPDATE USERS SET USER_APPROVED=TRUE WHERE USER_ID="+input.getId()+"";
		try{stmt.executeUpdate(queryUpdate);passed=true;}
		catch (SQLException error) {System.out.println(error.getMessage());}
		return passed;
	}
	// METODO PER ELIMINARE UTENTE (soft deleted)
	public static boolean deleteUser(User input) {
		boolean passed=false;
		String queryUpdate = "UPDATE USERS SET USER_DELETED=TRUE WHERE USER_ID="+input.getId()+"";
		try{stmt.executeUpdate(queryUpdate); passed=true;}
		catch (SQLException error) {System.out.println(error.getMessage());}
		return passed;
	}
	// METODO PER CAMBIARE RUOLO UTENTE
	public static boolean changeUserRole(User input) {
		boolean passed=false;
		if (input.getRole().equals("Admin")) {
			String queryUpdate = "UPDATE USERS SET USER_ROLE='User' WHERE USER_ID="+input.getId()+"";
			try{stmt.executeUpdate(queryUpdate); passed=true;}
			catch (SQLException error) {System.out.println(error.getMessage());}
		}
		else {
			String queryUpdate = "UPDATE USERS SET USER_ROLE='Admin' WHERE USER_ID="+input.getId()+"";
			try{stmt.executeUpdate(queryUpdate); passed=true;}
			catch (SQLException error) {System.out.println(error.getMessage());}
		}
		return passed;
	}
	//*************************RSS************************************************************************************
	// Aggiunta di un RSS da BOT
	public static boolean addRssFromBot(String description, String url) {
		boolean passed=false;
		String queryAdd = "INSERT INTO RSS (RSS_DESCRIPTION,RSS_URL,RSS_APPROVED,RSS_DELETED) VALUES ('"+description+"', '"+url+"',FALSE, FALSE)";
		System.out.println("BOT: " +queryAdd);
		try{	stmt.executeUpdate(queryAdd);passed=true;}
		catch (SQLException error) {	System.out.println(error.getMessage());}
		return passed;
	}
	// Aggiunta di un RSS da ADMIN PANEL
	public static boolean addRssFromAdmin(String description, String url) {
		boolean passed=false;
		String queryAdd = "INSERT INTO RSS (RSS_DESCRIPTION,RSS_URL,RSS_APPROVED,RSS_DELETED) VALUES ('"+description+"', '"+url+"',"+true+","+false+")";
		System.out.println("ADMIN: "+queryAdd);
		try{	stmt.executeUpdate(queryAdd); passed=true;}
		catch (SQLException error) {	System.out.println(error.getMessage());}
		return passed;
	}
	//METODO PER VERIFICARE SE RSS ESISTE GIA'
	public static Boolean checkRss(String url) {
		Boolean passed=null;
		ResultSet rs = null;
		String queryCheck = "SELECT RSS_URL FROM RSS WHERE RSS_DELETED=false AND  RSS_URL='"+url+"'";
		try {	System.out.println("Sto per eseguire la query");
			rs = stmt.executeQuery(queryCheck);
			if (!rs.next()) return false;
			else return true;
		}
		catch (SQLException error) {System.out.println(error.getMessage());passed=null;}
		return passed;
	}

	// METODO PER CHIEDERE TUTTI GLI RSS
	public static ArrayList<Rss> getAllRss() {
		ResultSet rs = null;
		ArrayList<Rss> allRss = new ArrayList<Rss>();
		String querySelect = "SELECT * FROM RSS WHERE RSS_DELETED=FALSE";
		try{
			rs = stmt.executeQuery(querySelect);
			while (rs.next()) {
				allRss.add(new Rss(rs.getInt("RSS_ID"),rs.getString("RSS_DESCRIPTION"),rs.getString("RSS_URL"),rs.getBoolean("RSS_APPROVED"),rs.getBoolean("RSS_DELETED")));
			}
		}
		catch (SQLException error) { System.out.println(error.getMessage()); allRss=null;}
		return allRss;
	}
	// METODO PER CHIEDERE TUTTI GLI RSS APPROVATI
	public static ArrayList<Rss> getApprovedRss() {
		ResultSet rs = null;
		ArrayList<Rss> allRss = new ArrayList<Rss>();
		String querySelect = "SELECT * FROM RSS WHERE RSS_DELETED=FALSE AND RSS_APPROVED=TRUE";
		try{
			rs = stmt.executeQuery(querySelect);
			while (rs.next()) {
				allRss.add(new Rss(rs.getInt("RSS_ID"),rs.getString("RSS_DESCRIPTION"),rs.getString("RSS_URL"),rs.getBoolean("RSS_APPROVED"),rs.getBoolean("RSS_DELETED")));
			}
		}
		catch (SQLException error) { System.out.println(error.getMessage()); allRss=null;}
		return allRss;
	}

	// METODO PER APPROVARE UN RSS
	public static boolean updateRssApproved(Rss input) {
		boolean passed=false;
		String queryUpdate = "UPDATE RSS SET RSS_APPROVED=TRUE WHERE RSS_ID="+input.getId()+"";
		try{stmt.executeUpdate(queryUpdate); passed=true;}
		catch (SQLException error) {System.out.println(error.getMessage());}
		return passed;
	}
	// METODO PER ELIMINARE UN RSS
	public static boolean deleteRss(Rss input) {
		boolean passed=false;
		String queryUpdate = "UPDATE RSS SET RSS_DELETED=TRUE WHERE RSS_ID="+input.getId()+"";
		try{stmt.executeUpdate(queryUpdate); passed=true;}
		catch (SQLException error) {System.out.println(error.getMessage());}
		return passed;
	}
	//************************************NEWS*************************************************************************
	// Aggiunta di una NEWS da ADMIN PANEL
	public static boolean addNews(String title, Date date, String description, String author, int rssId, String url, String imageUrl) {
		boolean passed=false;
		String queryAdd = "INSERT INTO NEWS VALUES (DEFAULT, '"+title+"','"+date+"','"+description+"','"+author+"',"+rssId+",'"+url+"','"+imageUrl+"',FALSE, FALSE)";
		try{	stmt.executeUpdate(queryAdd); passed=true;}
		catch (SQLException error) { System.out.println(error.getMessage());}
		return passed;
	}

	//METODO PER VERIFICARE SE NEWS ESISTE GIA'
	public static Boolean checkNews(String title) {
		Boolean passed=null;
		ResultSet rs = null;
		title = replaceIllegalChar(title);
		String queryCheck = "SELECT NEWS_TITLE FROM NEWS WHERE NEWS_DELETED=false AND  NEWS_TITLE='"+title+"'";
		//System.out.println(queryCheck);
		try {	rs = stmt.executeQuery(queryCheck);
			if (!rs.next()) return false;
			else return true;
		}
		catch (SQLException error) {System.out.println(error.getMessage()); passed=null;}
		return passed;
	}
	//METODO PER AGGIORNARE TUTTE LE NEWS
	public static String newsCollector() {
		ResultSet rs = null;
		int added, notAdded, tot;
		String frase = "";
		ArrayList<Rss> allRss = getApprovedRss();
		if (allRss!=null & allRss.size()!=0) {

			for (int i=0; i<allRss.size(); i++) {
				added=0;
				notAdded=0;
				tot=0;
				System.out.println("I'm checking updates from RSS: "+allRss.get(i).getDescription()+ " ("+allRss.get(i).getUrl()+")");
				try {
					URL feedUrl = new URL(allRss.get(i).getUrl());
					SyndFeedInput input = new SyndFeedInput();
					try {
						//Dichiaro variabili per inserimento in DBs
						int year, month, day, hour, minutes;
						String title, description, link, imageLink, category, author;
						Date dates;
						SyndFeed feed = input.build(new InputSource(feedUrl.openStream()));
						List<SyndEntry> entries = feed.getEntries();
						Iterator<SyndEntry> itEntries = entries.iterator();
						while (itEntries.hasNext()) {
							SyndEntry entry = itEntries.next();
							title = replaceIllegalChar(entry.getTitle());
							//System.out.println("TITLE: "+title);
							//System.out.println(checkNews(entry.getTitle()));

							if (checkNews(entry.getTitle())==false) {
								title = replaceIllegalChar(entry.getTitle());
								//System.out.println("TITLE: "+title);
								dates = new Date(entry.getPublishedDate().getTime());
								//System.out.println("DATE italy: "+dates);
								description = replaceIllegalChar( entry.getDescription().getValue());
								//System.out.println("DESCRIPTION: " +description);
								link = entry.getLink();
								//System.out.println("LINK:"+link);
								if (entry.getEnclosures() != null)	imageLink = "nd";
								else imageLink = entry.getEnclosures().get(0).getUrl();
								//System.out.println("IMAGE LINK: "+imageLink);
								author = entry.getAuthor();
								//System.out.println("AUTHOR: "+author);
								//System.out.println("Ok allora inserisco la notizia");
								addNews(title, dates, description, author, allRss.get(i).getId(), link, imageLink);
								tot +=1;
								added +=1;
							}
							else { /*System.out.println("La notizia esiste gia'");*/
								tot +=1;
								notAdded +=1;
							}
						}
					}
					catch (IllegalArgumentException | FeedException | IOException e) {
						// Errore lettura feed
						e.printStackTrace();
					}
				}
				catch (MalformedURLException e) {
					// Errore lettura feed
					e.printStackTrace();
				}
				frase = frase+"RSS: "+allRss.get(i).getDescription()+" total news: "+ tot+"\n";
				frase = frase+"News updated: "+added+", News already exist in DB: "+notAdded+"\n\n";

			}
		}// fine IF
		else frase= "There no active RSS to updated!";
		return frase;
	}

	//---------- METODO PER CHIEDERE TUTTE LE NEWS ----------
	public static ArrayList<News> getAllNews() {
		ResultSet rs = null;
		ArrayList<News> allNews = new ArrayList<News>();
		String querySelect = "SELECT * FROM NEWS WHERE NEWS_DELETED=false";
		try{
			rs = stmt.executeQuery(querySelect);
			while (rs.next()) {
				allNews.add(new News(rs.getInt("NEWS_ID"),rs.getString("NEWS_TITLE"),rs.getDate("NEWS_DATE"),rs.getString("NEWS_DESCRIPTION"),rs.getString("NEWS_AUTHOR"),rs.getInt("RSS_ID"),rs.getString("NEWS_URL"),rs.getString("NEWS_IMAGEURL"),rs.getBoolean("NEWS_MARKED"),rs.getBoolean("NEWS_DELETED")));
			}
		}
		catch (SQLException error) {
			System.out.println(error.getMessage());
		}
		return allNews;
	}
	//---------- METODO PER CHIEDERE NEWS con ID Specifico ----------
	public static ArrayList<News> getNews(int input) {
		ResultSet rs = null;
		ArrayList<News> news = new ArrayList<News>();
		String querySelect = "SELECT * FROM NEWS WHERE NEWS_DELETED=false AND  NEWS_ID="+input+"";
		try{
			rs = stmt.executeQuery(querySelect);
			while (rs.next()) {
				news.add(new News(rs.getInt("NEWS_ID"),rs.getString("NEWS_TITLE"),rs.getDate("NEWS_DATE"),rs.getString("NEWS_DESCRIPTION"),rs.getString("NEWS_AUTHOR"),rs.getInt("RSS_ID"),rs.getString("NEWS_URL"),rs.getString("NEWS_IMAGEURL"),rs.getBoolean("NEWS_MARKED"),rs.getBoolean("NEWS_DELETED")));
			}
		}
		catch (SQLException error) {
			System.out.println(error.getMessage());
		}
		return news;
	}
	//---------- METODO PER CHIEDERE TUTTE LE NEWS DI UN CERTO RSS ----------
	public static ArrayList<News> getNewsSpecificRss(Rss input) {
		ResultSet rs = null;
		ArrayList<News> allNews = new ArrayList<News>();
		String querySelect = "SELECT * FROM NEWS WHERE NEWS_DELETED=false AND RSS_ID="+input.getId()+"";
		try{
			rs = stmt.executeQuery(querySelect);
			while (rs.next()) {
				allNews.add(new News(rs.getInt("NEWS_ID"),rs.getString("NEWS_TITLE"),rs.getDate("NEWS_DATE"),rs.getString("NEWS_DESCRIPTION"),rs.getString("NEWS_AUTHOR"),rs.getInt("RSS_ID"),rs.getString("NEWS_URL"),rs.getString("NEWS_IMAGEURL"),rs.getBoolean("NEWS_MARKED"),rs.getBoolean("NEWS_DELETED")));
			}
		}
		catch (SQLException error) {
			System.out.println(error.getMessage());
		}
		return allNews;
	}
	//---------- METODO PER CHIEDERE TUTTE LE NEWS DI UN CERTO RSS CON L'ID RSS ----------
	public static ArrayList<News> getNewsSpecificRss(int input) {
		ResultSet rs = null;
		ArrayList<News> allNews = new ArrayList<News>();
		String querySelect = "SELECT * FROM NEWS WHERE NEWS_DELETED=false AND RSS_ID="+input+"";
		try{
			rs = stmt.executeQuery(querySelect);
			while (rs.next()) {
				allNews.add(new News(rs.getInt("NEWS_ID"),rs.getString("NEWS_TITLE"),rs.getDate("NEWS_DATE"),rs.getString("NEWS_DESCRIPTION"),rs.getString("NEWS_AUTHOR"),rs.getInt("RSS_ID"),rs.getString("NEWS_URL"),rs.getString("NEWS_IMAGEURL"),rs.getBoolean("NEWS_MARKED"),rs.getBoolean("NEWS_DELETED")));
			}
		}
		catch (SQLException error) {
			System.out.println(error.getMessage());
		}
		return allNews;
	}

	//---------- METODO PER CHIEDERE TUTTE LE NEWS DI UNA CERTA DATA ----------
	public static ArrayList<News> getNewsSpecificDate(String input) {
		ResultSet rs = null;
		ArrayList<News> allNews = new ArrayList<News>();
		String querySelect = "SELECT * FROM NEWS WHERE NEWS_DELETED=false AND NEWS_DATE='"+input+"'";
		try{
			rs = stmt.executeQuery(querySelect);
			while (rs.next()) {
				allNews.add(new News(rs.getInt("NEWS_ID"),rs.getString("NEWS_TITLE"),rs.getDate("NEWS_DATE"),rs.getString("NEWS_DESCRIPTION"),rs.getString("NEWS_AUTHOR"),rs.getInt("RSS_ID"),rs.getString("NEWS_URL"),rs.getString("NEWS_IMAGEURL"),rs.getBoolean("NEWS_MARKED"),rs.getBoolean("NEWS_DELETED")));
			}
		}
		catch (SQLException error) {
			System.out.println(error.getMessage());
		}
		return allNews;
	}
	//---------- METODO PER CHIEDERE TUTTE LE NEWS DEGLI RSS ATTIVI ----------
	public static ArrayList<News> getNewsActiveRss() {
		ResultSet rs = null;
		ArrayList<News> newsActiveRss = new ArrayList<News>();
		String querySelect = "SELECT * FROM NEWS WHERE RSS_ID IN (SELECT RSS_ID FROM RSS WHERE RSS_DELETED=false)";
		try{
			rs = stmt.executeQuery(querySelect);
			while (rs.next()) {
				newsActiveRss.add(new News(rs.getInt("NEWS_ID"),rs.getString("NEWS_TITLE"),rs.getDate("NEWS_DATE"),rs.getString("NEWS_DESCRIPTION"),rs.getString("NEWS_AUTHOR"),rs.getInt("RSS_ID"),rs.getString("NEWS_URL"),rs.getString("NEWS_IMAGEURL"),rs.getBoolean("NEWS_MARKED"),rs.getBoolean("NEWS_DELETED")));
			}
		}
		catch (SQLException error) {
			System.out.println(error.getMessage());
		}
		return newsActiveRss;
	}
	//---------- METODO PER TOTALE COMMENTI DI UNA NEWS ----------
	public static ArrayList<Comment> getNewsComments(int newsId) {
		ResultSet rs = null;
		ArrayList<Comment> allComments = new ArrayList<Comment>();
		String querySelect = "SELECT * FROM COMMENTS WHERE COMMENT_DELETED=false AND COMMENT_MARKED=false AND NEWS_ID="+newsId+"";
		try{
			rs = stmt.executeQuery(querySelect);
			while (rs.next()) {
				allComments.add(new Comment(rs.getInt("COMMENT_ID"),rs.getString("COMMENT_TEXT"),rs.getBoolean("COMMENT_MARKED"),rs.getBoolean("COMMENT_DELETED"),rs.getInt("NEWS_ID"),rs.getInt("USER_ID")));
			}
		}
		catch (SQLException error) {
			System.out.println(error.getMessage());
		}
		return allComments;
	}
	//---------- METODO PER NUMERO TOTALE COMMENTI DI UNA NEWS ----------
	public static int getNumberCommentsNews(int newsId) {
		ResultSet rs = null;
		int count=0;
		ArrayList<Comment> allActiveComments = new ArrayList<Comment>();
		String querySelect = "SELECT COUNT(comment_id) AS countComment FROM COMMENTS WHERE NEWS_ID="+newsId+"";
		try{
			rs = stmt.executeQuery(querySelect);
			while (rs.next()) {
				count = rs.getInt("countComment");
			}
		}
		catch (SQLException error) {
			System.out.println(error.getMessage());
		}
		return count;
	}
	//---------- METODO PER NUMERO TOTALE RATING DI UNA NEWS ----------
	public static int getNumberRatingNews(int newsId) {
		ResultSet rs = null;
		int count=0;
		ArrayList<Rating> allRating = new ArrayList<Rating>();
		String querySelect = "SELECT COUNT(rating_id) AS countRating FROM RATING WHERE NEWS_ID="+newsId+"";
		try{
			rs = stmt.executeQuery(querySelect);
			while (rs.next()) {
				count = rs.getInt("countRating");
			}
		}
		catch (SQLException error) {
			System.out.println(error.getMessage());
		}
		return count;
	}

	// METODO PER IMPOSTARE MARKED TRUE NELLA NEWS
	public static boolean markNews(News input) {
		boolean passed = false;
		String queryUpdate = "UPDATE NEWS SET NEWS_MARKED=TRUE WHERE NEWS_ID="+input.getId()+"";
		try{stmt.executeUpdate(queryUpdate); passed=true;}
		catch (SQLException error) {System.out.println(error.getMessage());}
		return passed;
	}
	// METODO PER IMPOSTARE MARKED FALSE NELLA NEWS
	public static boolean deleteMarkNews(News input) {
		boolean passed = false;
		String queryUpdate = "UPDATE NEWS SET NEWS_MARKED=FALSE WHERE NEWS_ID="+input.getId()+"";
		try{stmt.executeUpdate(queryUpdate); passed=true;}
		catch (SQLException error) {System.out.println(error.getMessage());}
		return passed;
	}
	// METODO PER ELIMINARE NEWS (soft delete)
	public static boolean deleteNews(News input) {
		boolean passed=false;
		String queryUpdate = "UPDATE NEWS SET NEWS_DELETED=TRUE WHERE NEWS_ID="+input.getId()+"";
		try{stmt.executeUpdate(queryUpdate); passed=true;}
		catch (SQLException error) {System.out.println(error.getMessage());}
		return passed;
	}
	//************************************COMMENT*************************************************************************
	// Aggiunta di un COMMENTO da BOT
	public static boolean addComment(String text, int newsId, int userId) {
		boolean passed = false;
		String queryAdd = "INSERT INTO COMMENTS VALUES (DEFAULT, '"+replaceIllegalChar(text)+"',FALSE,FALSE,"+newsId+","+userId+")";
		try{	stmt.executeUpdate(queryAdd); passed=true;}
		catch (SQLException error) {	System.out.println(error.getMessage());}
		return passed;
	}
	// METODO PER CHIEDERE TUTTI I COMMENTIs
	public static ArrayList<Comment> getAllComments() {
		ResultSet rs = null;
		ArrayList<Comment> allComments = new ArrayList<Comment>();
		String querySelect = "SELECT * FROM COMMENTS WHERE COMMENT_DELETED=false";
		try{
			rs = stmt.executeQuery(querySelect);
			while (rs.next()) {
				allComments.add(new Comment(rs.getInt("COMMENT_ID"),rs.getString("COMMENT_TEXT"),rs.getBoolean("COMMENT_MARKED"),rs.getBoolean("COMMENT_DELETED"),rs.getInt("NEWS_ID"),rs.getInt("USER_ID")));
			}
		}
		catch (SQLException error) {
			System.out.println(error.getMessage());
		}
		return allComments;
	}

	// METODO PER CHIEDERE TUTTI I COMMENTI NOT MARKED
	public static ArrayList<Comment> getAllCommentsNotMarked() {
		ResultSet rs = null;
		ArrayList<Comment> allComments = new ArrayList<Comment>();
		String querySelect = "SELECT * FROM COMMENTS WHERE COMMENT_DELETED=false AND COMMENT_MARKED=false";
		try{
			rs = stmt.executeQuery(querySelect);
			while (rs.next()) {
				allComments.add(new Comment(rs.getInt("COMMENT_ID"),rs.getString("COMMENT_TEXT"),rs.getBoolean("COMMENT_MARKED"),rs.getBoolean("COMMENT_DELETED"),rs.getInt("NEWS_ID"),rs.getInt("USER_ID")));
			}
		}
		catch (SQLException error) {
			System.out.println(error.getMessage());
		}
		return allComments;
	}
	// METODO PER CHIEDERE il COMMENTO con ID Specificato
	public static ArrayList<Comment> getComment(int input) {
		ResultSet rs = null;
		ArrayList<Comment> comment = new ArrayList<Comment>();
		String querySelect = "SELECT * FROM COMMENTS WHERE COMMENT_DELETED=false AND  COMMENT_ID="+input+"";
		try{
			rs = stmt.executeQuery(querySelect);
			while (rs.next()) {
				comment.add(new Comment(rs.getInt("COMMENT_ID"),rs.getString("COMMENT_TEXT"),rs.getBoolean("COMMENT_MARKED"),rs.getBoolean("COMMENT_DELETED"),rs.getInt("NEWS_ID"),rs.getInt("USER_ID")));
			}
		}
		catch (SQLException error) {
			System.out.println(error.getMessage());
		}
		return comment;
	}
	// METODO PER CHIEDERE TUTTI I COMMENTI DEGLI UTENTI ATTIVI
	public static ArrayList<Comment> getCommentActiveUser() {
		ResultSet rs = null;
		ArrayList<Comment> allActiveComments = new ArrayList<Comment>();
		String querySelect = "SELECT * FROM COMMENTS WHERE USER_ID IN(SELECT USER_ID FROM USERS WHERE USER_DELETED=false AND USER_APPROVED=true)";
		try{
			rs = stmt.executeQuery(querySelect);
			while (rs.next()) {
				allActiveComments.add(new Comment(rs.getInt("COMMENT_ID"),rs.getString("COMMENT_TEXT"),rs.getBoolean("COMMENT_MARKED"),rs.getBoolean("COMMENT_DELETED"),rs.getInt("NEWS_ID"),rs.getInt("USER_ID")));
			}
		}
		catch (SQLException error) {
			System.out.println(error.getMessage());
		}
		return allActiveComments;
	}
	// METODO PER SEGNALARE UN COMMENTO
	public static boolean markComment(Comment input) {
		boolean passed=false;
		String queryUpdate = "UPDATE COMMENTS SET COMMENT_MARKED=TRUE WHERE COMMENT_ID="+input.getId()+"";
		try{stmt.executeUpdate(queryUpdate); passed=true;}
		catch (SQLException error) {System.out.println(error.getMessage());}
		return passed;
	}
	// METODO PER RIMUOVERE SEGNALAZIONE DI UN COMMENTO
	public static boolean deleteMarkComment(Comment input) {
		boolean passed=false;
		String queryUpdate = "UPDATE COMMENTS SET COMMENT_MARKED=FALSE WHERE COMMENT_ID="+input.getId()+"";
		try{stmt.executeUpdate(queryUpdate); passed=true;}
		catch (SQLException error) {System.out.println(error.getMessage());}
		return passed;
	}
	// METODO PER RIMUOVERE UN COMMENTO
	public static boolean deleteComment(Comment input) {
		boolean passed=false;
		String queryUpdate = "UPDATE COMMENTS SET COMMENT_DELETED=TRUE WHERE COMMENT_ID="+input.getId()+"";
		try{stmt.executeUpdate(queryUpdate); passed=true;}
		catch (SQLException error) {System.out.println(error.getMessage());}
		return passed;
	}
	// METODO PER RICAVARE L'UTENTE DEL COMMENTO
	public static String getCommentUser(Comment input) {
		String result = null;
		ResultSet rs = null;
		String queryUpdate = "SELECT USER_NICKNAME FROM USERS WHERE USER_ID IN (SELECT USER_ID FROM COMMENTS WHERE COMMENT_ID="+input.getId()+")";
		try{
			rs = stmt.executeQuery(queryUpdate);
			while (rs.next()) {
				result = rs.getString("USER_NICKNAME");
			}
		}
		catch (SQLException error) {System.out.println(error.getMessage());}
		return result;
	}
	// METODO PER RICAVARE TITOLO NEWS DEL COMMENTO
	public static String getCommentNewsTitle(Comment input) {
		String result = null;
		ResultSet rs = null;
		String queryUpdate = "SELECT NEWS_TITLE FROM NEWS WHERE NEWS_ID IN (SELECT NEWS_ID FROM COMMENTS WHERE COMMENT_ID="+input.getId()+")";
		try{
			rs = stmt.executeQuery(queryUpdate);
			while (rs.next()) {
				result = rs.getString("NEWS_TITLE");
			}
		}
		catch (SQLException error) {System.out.println(error.getMessage());}
		return result;
	}
	//************************************RATING*************************************************************************
	// Aggiunta di un RATING da BOT
	public static boolean addRating(int vote, int newsId, int userId) {
		boolean passed=false;
		String queryAdd = "INSERT INTO RATING VALUES (DEFAULT,"+vote+","+newsId+","+userId+")";
		try{	stmt.executeUpdate(queryAdd);passed=true;}
		catch (SQLException error) {	System.out.println(error.getMessage());}
		return passed;
	}
	// METODO PER RICAVARE TUTTI I RATING
	public static ArrayList<Rating> getAllRatings() {
		ArrayList<Rating> allRating = new ArrayList<Rating>();
		ResultSet rs = null;
		String queryUpdate = "SELECT * FROM RATING";
		try{
			rs = stmt.executeQuery(queryUpdate);
			while (rs.next()) {
				allRating.add(new Rating(rs.getInt("RATING_ID"),rs.getInt("RATING_VOTE"),rs.getInt("NEWS_ID"),rs.getInt("USER_ID")));
			}
		}
		catch (SQLException error) {System.out.println(error.getMessage());}
		return allRating;
	}
	// METODO PER RICAVARE RATING da ID Specificato
	public static ArrayList<Rating> getRating(int input) {
		ArrayList<Rating> rating = new ArrayList<Rating>();
		ResultSet rs = null;
		String queryUpdate = "SELECT * FROM RATING WHERE RATING_ID="+input+"";
		try{
			rs = stmt.executeQuery(queryUpdate);
			while (rs.next()) {
				rating.add(new Rating(rs.getInt("RATING_ID"),rs.getInt("RATING_VOTE"),rs.getInt("NEWS_ID"),rs.getInt("USER_ID")));
			}
		}
		catch (SQLException error) {System.out.println(error.getMessage());}
		return rating;
	}
	// METODO PER RICAVARE NEWS TITLE DEL RATING
	public static String getRatingNewsTitle(Rating input) {
		String result = null;
		ResultSet rs = null;
		String queryUpdate = "SELECT NEWS_TITLE FROM NEWS WHERE NEWS_ID_ID IN (SELECT NEWS_ID FROM RATING WHERE RATING_ID="+input.getId()+")";
		try{
			rs = stmt.executeQuery(queryUpdate);
			while (rs.next()) {
				result = rs.getString("NEWS_TITLE");
			}
		}
		catch (SQLException error) {System.out.println(error.getMessage());}
		return result;
	}
	// METODO PER RICAVARE USER del RATING
	public static String getRatingUser(Rating input) {
		String result = null;
		ResultSet rs = null;
		String queryUpdate = "SELECT USER_NICKNAME FROM USERS WHERE USER_ID IN (SELECT USER_ID FROM RATING WHERE RATING_ID="+input.getId()+")";
		try{
			rs = stmt.executeQuery(queryUpdate);
			while (rs.next()) {
				result = rs.getString("NEWS_TITLE");
			}
		}
		catch (SQLException error) {System.out.println(error.getMessage());}
		return result;
	}
}