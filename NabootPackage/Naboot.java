package NabootPackage;

import java.io.*;
import java.util.ArrayList;
import java.nio.charset.StandardCharsets;
import java.net.MalformedURLException;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

import dbObject.*;
import sample.*;

import org.json.*;

public class Naboot {

	// Parametri
	private String id;
	private ArrayList<Conversation> conversations;
	private ChatAnswersDictionary chatAnswersDictionary;
	private HandlerDb dbHandler;

	// Costruttore
	public Naboot (String BotID) {
		this.id = BotID;
		this.conversations = new ArrayList<Conversation>();
		this.chatAnswersDictionary = new ChatAnswersDictionary();
		this.dbHandler = new HandlerDb();
		dbHandler.openDb();
	}

	// Getters e Setters
	public String getID() {return this.id;}
	public ArrayList<Conversation> getConversations() {return this.conversations;}


	// Metodi

	public int checkConversation(int chatID) {
		//Verifica che la conversazione esista e, se non esiste, la aggiunge
		//Ritorna l'indice della conversazione, sia che l'abbia trovata sia che l'abbia aggiunta

		//Verifico che chi mi sta scrivendo non sia gia' fra le mie conversazioni in corso
		boolean chatExists = false;
		int conversationIndex = 0;
		for(int i = 0; i < conversations.size(); i++) {
			if(conversations.get(i).getChatID() == chatID) {
				chatExists = true;
				conversationIndex = i;
			}
		}

		//Se la chat non esiste, la aggiungo alle mie conversazioni
		if(!chatExists) {
			conversations.add(new Conversation(chatID));
			return conversations.size() - 1;
		} else {
			//Ritorna l'indice a cui ha trovato la conversazione
			return conversationIndex;
		}
	}

	public JSONObject getMe(){
		//Ritorna le caratteristiche del bot

		String url = "https://api.telegram.org/bot" + this.id + "/getMe";

		return callTelegramAPI(url, "").getJSONObject("result");

	}

	public JSONArray getUpdates(Boolean confirmUpdates){
		//Ritorna un JSON che tutti gli update pending dalle varie chat

		String url = "https://api.telegram.org/bot" + this.id + "/getUpdates";

		try {

			URL httpsURL = new URL(url);
			HttpsURLConnection connection = (HttpsURLConnection)httpsURL.openConnection();

			if(connection != null) {
				BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String tmpContent;
				String connectionContent = "";

				// non sono sicuro sia necessario fare una concat
				// probabilmente il payload e' sempre su una sola riga
				while((tmpContent = br.readLine()) != null) {
					connectionContent = connectionContent + tmpContent;
				}

				connection.getInputStream().close(); // questo chiude la connection senza che resti in cache
				br.close();

				JSONObject content = new JSONObject(connectionContent);

				if(content.getBoolean("ok")){

					if(confirmUpdates && content.getJSONArray("result").length() > 0) {
						URL confirmUrl = new URL(url + "?offset=" + (content.getJSONArray("result").getJSONObject(content.getJSONArray("result").length()-1).getInt("update_id") + 1));
						HttpsURLConnection confirmConnection = (HttpsURLConnection)confirmUrl.openConnection();
						confirmConnection.getInputStream().close(); // questo chiude la connection senza che resti in cache

						//DEBUG print the confirmationUrl
						//System.out.println(confirmUrl);
					}

					return content.getJSONArray("result");

				} else {
					return null;
				}

			} else {
				return null;
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}

	public void handleUpdates() {
		//Gestisce le conversazioni, rispondendo con le strutture chat adeguate
		// in base alla situazione

		JSONArray updatesArray = this.getUpdates(true);

		String message = "";
		int chatID = 0;
		int conversationIndex = 0;
		JSONObject messageResult;
		//boolean deleteResult;
		News tmpNews;

		//per ogni update (messaggio)
		for(int i = 0; i < updatesArray.length(); i++) {

			if(updatesArray.getJSONObject(i).has("callback_query")) {
				message = updatesArray.getJSONObject(i).getJSONObject("callback_query").getString("data");
				chatID = updatesArray.getJSONObject(i).getJSONObject("callback_query").getJSONObject("from").getInt("id");

				System.out.println("[CALLBACK]\t" + message); //DEBUG

				conversationIndex = checkConversation(chatID);

			} else if(updatesArray.getJSONObject(i).has("message")) {
				message = updatesArray.getJSONObject(i).getJSONObject("message").getString("text");
				chatID = updatesArray.getJSONObject(i).getJSONObject("message").getJSONObject("chat").getInt("id");

				System.out.println("[MESSAGE]\t" + message); //DEBUG

				conversationIndex = checkConversation(chatID);
			}

			//Verifico in che stato e' la conversazione
			ConversationStatus conversationStatus = conversations.get(conversationIndex).getConversationStatus();

			//Decido cosa rispondere per ogni stato
			switch(conversationStatus) {

				case START:
					if(message.equalsIgnoreCase("/start")) {
						if(dbHandler.getUser(Integer.toString(chatID)).size() > 0 &&
								dbHandler.getUser(Integer.toString(chatID)).get(0).getApproved()) {
							messageResult = sendCallback(chatAnswersDictionary.getChatAnswer("startmenu"),
									chatAnswersDictionary.getChatAnswer("startmenu_callback"), chatID);

							conversations.get(conversationIndex).setConversationStatus(ConversationStatus.STARTMENU);
						} else if (dbHandler.getUser(Integer.toString(chatID)).size() > 0 &&
								!dbHandler.getUser(Integer.toString(chatID)).get(0).getApproved()) {

							messageResult = sendMessage(chatAnswersDictionary.getChatAnswer("registrationsolicited"), chatID);

						} else {
							messageResult = sendMessage(chatAnswersDictionary.getChatAnswer("insertusername"), chatID);

							conversations.get(conversationIndex).setConversationStatus(ConversationStatus.USERREGISTRATION);
						}

					} else if(message.equalsIgnoreCase("/approveuser")) {
						dbHandler.updateApproveUser(dbHandler.getUser(Integer.toString(chatID)).get(0));

					}else {messageResult = sendMessage(chatAnswersDictionary.getChatAnswer("nocommand"), chatID);}
					break;

				case USERREGISTRATION:
					dbHandler.addUserFromBot(message, Integer.toString(chatID));
					messageResult = sendMessage(chatAnswersDictionary.getChatAnswer("registrationrequested"), chatID);

					conversations.get(conversationIndex).setConversationStatus(ConversationStatus.START);

					break;

				case STARTMENU:
					if(message.equalsIgnoreCase("newsoftheday")){
						tmpNews = dbHandler.getAllNews().get(0); //TODO random news

						//TODO sendPhotoCallback ma gli url delle foto non piacciono molto a Telegram
						messageResult = sendCallback(tmpNews.getTitle() + tmpNews.getDescription(),
								chatAnswersDictionary.getChatAnswer("newsoftheday_callback"), chatID);

						conversations.get(conversationIndex).setConversationStatus(ConversationStatus.NEWSOFTHEDAY);

					} else if(message.equalsIgnoreCase("filterfeed")){
						String tmpCallbackString = "{\"inline_keyboard\":[";

						for(Rss rss : dbHandler.getApprovedRss()) {
							// per ogni rss crea una riga con la descrizione del feed
							tmpCallbackString = tmpCallbackString + new JSONStringer().array().object()
									.key("text").value(rss.getDescription())
									.key("callback_data").value(rss.getId())
									.endObject().endArray().toString() + ",";
						}
						tmpCallbackString = tmpCallbackString.substring(0, tmpCallbackString.length() - 1) + "]}";

						messageResult = sendCallback(chatAnswersDictionary.getChatAnswer("choosefeed"),
								tmpCallbackString, chatID);

						conversations.get(conversationIndex).setConversationStatus(ConversationStatus.CHOOSINGFEED);

					} else if(message.equalsIgnoreCase("filterdate")){
						messageResult = sendMessage(chatAnswersDictionary.getChatAnswer("writedate"), chatID);

						conversations.get(conversationIndex).setConversationStatus(ConversationStatus.CHOOSINGDATE);

					} else if(message.equalsIgnoreCase("suggestfeed")){
						messageResult = sendMessage(chatAnswersDictionary.getChatAnswer("suggestfeed"),chatID);

						conversations.get(conversationIndex).setConversationStatus(ConversationStatus.SUGGESTINGFEED);

					} else if(message.equalsIgnoreCase("options")){
						messageResult = sendCallback(chatAnswersDictionary.getChatAnswer("options"),
								chatAnswersDictionary.getChatAnswer("options_callback"), chatID);

						conversations.get(conversationIndex).setConversationStatus(ConversationStatus.OPTIONS);

					} else {messageResult = sendMessage(chatAnswersDictionary.getChatAnswer("nocommand"), chatID);}
					break;

				case CHOOSINGFEED:
					conversations.get(conversationIndex).setNewsBookmark(0);
					conversations.get(conversationIndex).setRssBookmark(Integer.parseInt(message));
					conversations.get(conversationIndex).setDateBookmark("");
					conversations.get(conversationIndex).setCommentBookmark(-1);

					tmpNews = dbHandler.getNewsSpecificRss(conversations.get(conversationIndex).getRssBookmark()).get(conversations.get(conversationIndex).getNewsBookmark());

					messageResult = sendCallback(tmpNews.getTitle() + tmpNews.getDescription(),
							chatAnswersDictionary.getChatAnswer("news_callback"), chatID);

					conversations.get(conversationIndex).setConversationStatus(ConversationStatus.NEWS);
					break;

				case CHOOSINGDATE:
					if (!message.matches("^(19|20)\\d\\d[-](0[1-9]|1[012])[-](0[1-9]|[12][0-9]|3[01])$")) {
						messageResult = sendMessage(chatAnswersDictionary.getChatAnswer("writedate"), chatID);
					} else {
						conversations.get(conversationIndex).setNewsBookmark(0);
						conversations.get(conversationIndex).setRssBookmark(-1);
						conversations.get(conversationIndex).setDateBookmark(message);
						conversations.get(conversationIndex).setCommentBookmark(-1);

						if (dbHandler.getNewsSpecificDate(conversations.get(conversationIndex).getDateBookmark()).size() > 0) {
							tmpNews = dbHandler.getNewsSpecificDate(conversations.get(conversationIndex).getDateBookmark()).get(conversations.get(conversationIndex).getNewsBookmark());

							messageResult = sendCallback(tmpNews.getTitle() + tmpNews.getDescription(),
									chatAnswersDictionary.getChatAnswer("news_callback"), chatID);

							conversations.get(conversationIndex).setConversationStatus(ConversationStatus.NEWS);
						} else {
							messageResult = sendMessage(chatAnswersDictionary.getChatAnswer("nonewsfordate"), chatID);
							messageResult = sendCallback(chatAnswersDictionary.getChatAnswer("startmenu"),
									chatAnswersDictionary.getChatAnswer("startmenu_callback"), chatID);
							conversations.get(conversationIndex).setConversationStatus(ConversationStatus.STARTMENU);
						}
					}
					break;

				case NEWSOFTHEDAY:
					if(message.equalsIgnoreCase("votenews")){
						messageResult = sendMessage(chatAnswersDictionary.getChatAnswer("votenews"), chatID);

						conversations.get(conversationIndex).setConversationStatus(ConversationStatus.VOTING);

					} else if(message.equalsIgnoreCase("viewcommentsnews")){
						String comments = "News comments:";
						ArrayList<Comment> newsComments;
						newsComments = dbHandler.getNewsComments(dbHandler.getAllNews().get(0).getId());

						for (int j = 0; j < newsComments.size(); j++) {
							comments = comments + "\n\n" + newsComments.get(j).getId() + " - " +
									dbHandler.getUser(newsComments.get(j).getUserId()).get(0).getNickname() +
									"\n" + newsComments.get(j).getText();
						}

						messageResult = sendCallback(comments, chatAnswersDictionary.getChatAnswer("comments_callback"), chatID);

						conversations.get(conversationIndex).setConversationStatus(ConversationStatus.COMMENTS);

					} else if(message.equalsIgnoreCase("signalnews")){
						if(conversations.get(conversationIndex).getRssBookmark() > 0){
							tmpNews = dbHandler.getNewsSpecificRss(conversations.get(conversationIndex).getRssBookmark()).get(conversations.get(conversationIndex).getNewsBookmark());
						} else if(!conversations.get(conversationIndex).getDateBookmark().equals("")) {
							tmpNews = dbHandler.getNewsSpecificDate(conversations.get(conversationIndex).getDateBookmark()).get(conversations.get(conversationIndex).getNewsBookmark());
						} else {
							tmpNews = dbHandler.getAllNews().get(0); //TODO random news
						}

						dbHandler.markNews(tmpNews);

						messageResult = sendMessage(chatAnswersDictionary.getChatAnswer("signalnewssaved"), chatID);
						messageResult = sendCallback(chatAnswersDictionary.getChatAnswer("startmenu"),
								chatAnswersDictionary.getChatAnswer("startmenu_callback"), chatID);
						conversations.get(conversationIndex).setConversationStatus(ConversationStatus.STARTMENU);

					} else if(message.equalsIgnoreCase("backnews")){
						messageResult = sendCallback(chatAnswersDictionary.getChatAnswer("startmenu"),
								chatAnswersDictionary.getChatAnswer("startmenu_callback"), chatID);

						conversations.get(conversationIndex).setConversationStatus(ConversationStatus.STARTMENU);

					} else {messageResult = sendMessage(chatAnswersDictionary.getChatAnswer("nocommand"), chatID);}
					break;

				case NEWS:
					if(message.equalsIgnoreCase("previousnews")){
						if(conversations.get(conversationIndex).getNewsBookmark() > 0) {
							conversations.get(conversationIndex).setNewsBookmark(conversations.get(conversationIndex).getNewsBookmark() - 1);
						}

						if(conversations.get(conversationIndex).getRssBookmark() > 0){
							tmpNews = dbHandler.getNewsSpecificRss(conversations.get(conversationIndex).getRssBookmark()).get(conversations.get(conversationIndex).getNewsBookmark());
						} else if(!conversations.get(conversationIndex).getDateBookmark().equals("")) {
							tmpNews = dbHandler.getNewsSpecificDate(conversations.get(conversationIndex).getDateBookmark()).get(conversations.get(conversationIndex).getNewsBookmark());
						} else {
							tmpNews = dbHandler.getAllNews().get(0); //TODO random news
						}

						//TODO sendPhotoCallback ma gli url delle foto non piacciono molto a Telegram
						messageResult = sendCallback(tmpNews.getTitle() + tmpNews.getDescription(),
								chatAnswersDictionary.getChatAnswer("news_callback"), chatID);

						conversations.get(conversationIndex).setConversationStatus(ConversationStatus.NEWS);

					} else if(message.equalsIgnoreCase("nextnews")){
						if(conversations.get(conversationIndex).getRssBookmark() > 0){
							if(conversations.get(conversationIndex).getNewsBookmark() < dbHandler.getNewsSpecificRss(conversations.get(conversationIndex).getRssBookmark()).size() - 1) {
								conversations.get(conversationIndex).setNewsBookmark(conversations.get(conversationIndex).getNewsBookmark() + 1);
							}
							tmpNews = dbHandler.getNewsSpecificRss(conversations.get(conversationIndex).getRssBookmark()).get(conversations.get(conversationIndex).getNewsBookmark());
						} else if(!conversations.get(conversationIndex).getDateBookmark().equals("")) {
							if(conversations.get(conversationIndex).getNewsBookmark() < dbHandler.getNewsSpecificDate(conversations.get(conversationIndex).getDateBookmark()).size() - 1) {
								conversations.get(conversationIndex).setNewsBookmark(conversations.get(conversationIndex).getNewsBookmark() + 1);
							}
							tmpNews = dbHandler.getNewsSpecificDate(conversations.get(conversationIndex).getDateBookmark()).get(conversations.get(conversationIndex).getNewsBookmark());
						} else {
							tmpNews = dbHandler.getAllNews().get(0); //TODO random news
						}

						//TODO sendPhotoCallback ma gli url delle foto non piacciono molto a Telegram
						messageResult = sendCallback(tmpNews.getTitle() + tmpNews.getDescription(),
								chatAnswersDictionary.getChatAnswer("news_callback"), chatID);

						conversations.get(conversationIndex).setConversationStatus(ConversationStatus.NEWS);

					} else if(message.equalsIgnoreCase("votenews")){
						messageResult = sendMessage(chatAnswersDictionary.getChatAnswer("votenews"), chatID);

						conversations.get(conversationIndex).setConversationStatus(ConversationStatus.VOTING);

					} else if(message.equalsIgnoreCase("viewcommentsnews")){
						String comments = "News comments:";
						ArrayList<Comment> newsComments;
						if(conversations.get(conversationIndex).getRssBookmark() > 0){
							newsComments = dbHandler.getNewsComments(dbHandler.getNewsSpecificRss(conversations.get(conversationIndex).getRssBookmark()).get(conversations.get(conversationIndex).getNewsBookmark()).getId());
						} else if(!conversations.get(conversationIndex).getDateBookmark().equals("")) {
							newsComments = dbHandler.getNewsComments(dbHandler.getNewsSpecificDate(conversations.get(conversationIndex).getDateBookmark()).get(conversations.get(conversationIndex).getNewsBookmark()).getId());
						} else {
							newsComments = dbHandler.getNewsComments(dbHandler.getAllNews().get(0).getId()); //TODO random news
						}

						for (int j = 0; j < newsComments.size(); j++) {
							//DEBUG
							System.out.println("DEBUG:" + newsComments.get(j).getUserId());

							comments = comments + "\n\n" + newsComments.get(j).getId() + " - " +
									dbHandler.getUser(newsComments.get(j).getUserId()).get(0).getNickname() +
									"\n" + newsComments.get(j).getText();
						}

						messageResult = sendCallback(comments, chatAnswersDictionary.getChatAnswer("comments_callback"), chatID);

						conversations.get(conversationIndex).setConversationStatus(ConversationStatus.COMMENTS);

					} else if(message.equalsIgnoreCase("signalnews")){
						if(conversations.get(conversationIndex).getRssBookmark() > 0){
							tmpNews = dbHandler.getNewsSpecificRss(conversations.get(conversationIndex).getRssBookmark()).get(conversations.get(conversationIndex).getNewsBookmark());
						} else if(!conversations.get(conversationIndex).getDateBookmark().equals("")) {
							tmpNews = dbHandler.getNewsSpecificDate(conversations.get(conversationIndex).getDateBookmark()).get(conversations.get(conversationIndex).getNewsBookmark());
						} else {
							tmpNews = dbHandler.getAllNews().get(0); //TODO random news
						}

						dbHandler.markNews(tmpNews);

						messageResult = sendMessage(chatAnswersDictionary.getChatAnswer("signalnewssaved"), chatID);
						messageResult = sendCallback(chatAnswersDictionary.getChatAnswer("startmenu"),
								chatAnswersDictionary.getChatAnswer("startmenu_callback"), chatID);
						conversations.get(conversationIndex).setConversationStatus(ConversationStatus.STARTMENU);

					} else if(message.equalsIgnoreCase("backnews")){
						messageResult = sendCallback(chatAnswersDictionary.getChatAnswer("startmenu"),
								chatAnswersDictionary.getChatAnswer("startmenu_callback"),
								chatID);

						conversations.get(conversationIndex).setConversationStatus(ConversationStatus.STARTMENU);

					} else {messageResult = sendMessage(chatAnswersDictionary.getChatAnswer("nocommand"), chatID);}
					break;

				case VOTING:
					if(conversations.get(conversationIndex).getRssBookmark() > 0){
						tmpNews = dbHandler.getNewsSpecificRss(conversations.get(conversationIndex).getRssBookmark()).get(conversations.get(conversationIndex).getNewsBookmark());
					} else if(!conversations.get(conversationIndex).getDateBookmark().equals("")) {
						tmpNews = dbHandler.getNewsSpecificDate(conversations.get(conversationIndex).getDateBookmark()).get(conversations.get(conversationIndex).getNewsBookmark());
					} else {
						tmpNews = dbHandler.getAllNews().get(0); //TODO random news
					}

					try {
						int vote = Integer.parseInt(message);
						if (vote > 10 || vote < 1) {
							messageResult = sendMessage(chatAnswersDictionary.getChatAnswer("votenews"), chatID);
						} else {
							dbHandler.addRating(vote, tmpNews.getId(), dbHandler.getUser(Integer.toString(chatID)).get(0).getId());

							messageResult = sendMessage(chatAnswersDictionary.getChatAnswer("votenewssaved"), chatID);
							messageResult = sendCallback(chatAnswersDictionary.getChatAnswer("startmenu"),
									chatAnswersDictionary.getChatAnswer("startmenu_callback"), chatID);
							conversations.get(conversationIndex).setConversationStatus(ConversationStatus.STARTMENU);
						}

					} catch (NumberFormatException e) {
						messageResult = sendMessage(chatAnswersDictionary.getChatAnswer("votenews"), chatID);
					}
					break;

				case COMMENTS:
					if(message.equalsIgnoreCase("addcomment")){
						messageResult = sendMessage(chatAnswersDictionary.getChatAnswer("addcomment"), chatID);

						conversations.get(conversationIndex).setConversationStatus(ConversationStatus.COMMENTING);

					} else if(message.equalsIgnoreCase("signalcomment")){
						messageResult = sendMessage(chatAnswersDictionary.getChatAnswer("choosecommenttosignal"), chatID);

						conversations.get(conversationIndex).setConversationStatus(ConversationStatus.SIGNALINGCOMMENT);

					} else if(message.equalsIgnoreCase("backcomment")){
						if(conversations.get(conversationIndex).getRssBookmark() > 0){
							tmpNews = dbHandler.getNewsSpecificRss(conversations.get(conversationIndex).getRssBookmark()).get(conversations.get(conversationIndex).getNewsBookmark());
						} else if(!conversations.get(conversationIndex).getDateBookmark().equals("")) {
							tmpNews = dbHandler.getNewsSpecificDate(conversations.get(conversationIndex).getDateBookmark()).get(conversations.get(conversationIndex).getNewsBookmark());
						} else {
							tmpNews = dbHandler.getAllNews().get(0); //TODO random news
						}

						messageResult = sendCallback(tmpNews.getTitle() + tmpNews.getDescription(),
								chatAnswersDictionary.getChatAnswer("news_callback"), chatID);

						conversations.get(conversationIndex).setConversationStatus(ConversationStatus.NEWS);

					} else {messageResult = sendMessage(chatAnswersDictionary.getChatAnswer("nocommand"), chatID);}
					break;

				case COMMENTING:
					if(conversations.get(conversationIndex).getRssBookmark() > 0){
						tmpNews = dbHandler.getNewsSpecificRss(conversations.get(conversationIndex).getRssBookmark()).get(conversations.get(conversationIndex).getNewsBookmark());
					} else if(!conversations.get(conversationIndex).getDateBookmark().equals("")) {
						tmpNews = dbHandler.getNewsSpecificDate(conversations.get(conversationIndex).getDateBookmark()).get(conversations.get(conversationIndex).getNewsBookmark());
					} else {
						tmpNews = dbHandler.getAllNews().get(0); //TODO random news
					}
					dbHandler.addComment(message, tmpNews.getId(), dbHandler.getUser(Integer.toString(chatID)).get(0).getId());

					String comments = "News comments:";
					ArrayList<Comment> newsComments;
					if(conversations.get(conversationIndex).getRssBookmark() > 0){
						newsComments = dbHandler.getNewsComments(dbHandler.getNewsSpecificRss(conversations.get(conversationIndex).getRssBookmark()).get(conversations.get(conversationIndex).getNewsBookmark()).getId());
					} else if(!conversations.get(conversationIndex).getDateBookmark().equals("")) {
						newsComments = dbHandler.getNewsComments(dbHandler.getNewsSpecificDate(conversations.get(conversationIndex).getDateBookmark()).get(conversations.get(conversationIndex).getNewsBookmark()).getId());
					} else {
						newsComments = dbHandler.getNewsComments(dbHandler.getAllNews().get(0).getId()); //TODO random news
					}

					for (int j = 0; j < newsComments.size(); j++) {
						comments = comments + "\n\n" + newsComments.get(j).getId() + " - " +
								dbHandler.getUser(newsComments.get(j).getUserId()).get(0).getNickname() +
								"\n" + newsComments.get(j).getText();
					}

					messageResult = sendCallback(comments, chatAnswersDictionary.getChatAnswer("comments_callback"), chatID);

					conversations.get(conversationIndex).setConversationStatus(ConversationStatus.COMMENTS);

					break;

				case SIGNALINGCOMMENT:
					try {
						int commentID = Integer.parseInt(message);
						dbHandler.markComment(dbHandler.getComment(commentID).get(0));

						messageResult = sendMessage(chatAnswersDictionary.getChatAnswer("commentsignaled"), chatID);

						messageResult = sendCallback(chatAnswersDictionary.getChatAnswer("startmenu"),
								chatAnswersDictionary.getChatAnswer("startmenu_callback"), chatID);

						conversations.get(conversationIndex).setConversationStatus(ConversationStatus.STARTMENU);

					} catch (NumberFormatException e) {
						messageResult = sendMessage(chatAnswersDictionary.getChatAnswer("choosecommenttosignal"), chatID);
					}

					break;

				case SUGGESTINGFEED:
					if(true){ // TODO Qui si potrebbe pensare di mettere controlli sulla formattazione dell'URL
						sendMessage("Inviato il feed " + message + " per approvazione", chatID);
						dbHandler.addRssFromBot(message, message); //TODO gestire inserimento utente della descrizione

						messageResult = sendCallback(chatAnswersDictionary.getChatAnswer("startmenu"),
								chatAnswersDictionary.getChatAnswer("startmenu_callback"), chatID);

						conversations.get(conversationIndex).setConversationStatus(ConversationStatus.STARTMENU);

					} else {messageResult = sendMessage(chatAnswersDictionary.getChatAnswer("malformedURL"), chatID);}
					break;

				case OPTIONS:
					if(message.equalsIgnoreCase("deleteuser")){
						dbHandler.deleteUser(dbHandler.getUser(Integer.toString(chatID)).get(0));

						messageResult = sendMessage(chatAnswersDictionary.getChatAnswer("userdeleted"), chatID);
						conversations.get(conversationIndex).setConversationStatus(ConversationStatus.START);

					} else {messageResult = sendMessage(chatAnswersDictionary.getChatAnswer("nocommand"), chatID);}
					break;

				default:
					//TODO ERROR
					break;
			}

		}
	}

	public JSONObject sendPhotoCallback(String caption, String callbackJSONString, String photoUrl, int chatID) {
		// Invia un messaggio con callback in una chat

		String url = "https://api.telegram.org/bot" + this.id + "/sendPhoto";
		String urlParameters = "chat_id=" + chatID + "&caption=" + caption + "&photo=" + photoUrl +
				"&reply_markup=" + callbackJSONString + "&parse_mode=HTML";

		//DEBUG
		System.out.println(url);
		System.out.println(urlParameters);

		return callTelegramAPI(url, urlParameters).getJSONObject("result");

	}

	public JSONObject sendMessage(String text, int chatID) {
		// Invia un messaggio di puro testo in una chat

		String url = "https://api.telegram.org/bot" + this.id + "/sendMessage";
		String urlParameters = "chat_id=" + chatID + "&text=" + text + "&parse_mode=HTML";

		return callTelegramAPI(url, urlParameters).getJSONObject("result");

	}

	public JSONObject sendCallback(String text, String callbackJSONString, int chatID) {
		// Invia un messaggio con callback in una chat

		String url = "https://api.telegram.org/bot" + this.id + "/sendMessage";
		String urlParameters = "chat_id=" + chatID + "&text=" + text +
				"&reply_markup=" + callbackJSONString + "&parse_mode=HTML";

		return callTelegramAPI(url, urlParameters).getJSONObject("result");

	}

	public boolean deleteMessage(int chatID, int messageID) {
		// Cancella un messaggio da una chat

		String url = "https://api.telegram.org/bot" + this.id + "/deleteMessage";
		String urlParameters = "chat_id=" + chatID + "&message_id=" + messageID;

		return callTelegramAPI(url, urlParameters).getBoolean("result");
	}

	private JSONObject callTelegramAPI(String url, String urlParameters) {

		// Trasformo i parametri per essere gestiti tramite POST
		byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
		int postDataLength = postData.length;

		try {

			URL httpsURL = new URL(url);
			HttpsURLConnection connection = (HttpsURLConnection)httpsURL.openConnection();

			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("charset", "utf-8");
			connection.setRequestProperty("Content-Length", Integer.toString(postDataLength));
			connection.setUseCaches(false);

			if(connection != null) {
				OutputStream os = connection.getOutputStream();
				os.write(postData);

				BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String tmpContent;
				String connectionContent = "";

				// non sono sicuro sia necessario fare una concat
				// probabilmente il payload e' sempre su una sola riga
				while((tmpContent = br.readLine()) != null) {
					connectionContent = connectionContent + tmpContent;
				}

				connection.getInputStream().close(); // questo chiude la connection senza che resti in cache
				br.close();

				JSONObject content = new JSONObject(connectionContent);

				if(content.getBoolean("ok")){
					return content;
				} else {
					throw new IOException("Result not OK"); //TODO correggere l'errore
				}

			} else {
				return null;
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}

}