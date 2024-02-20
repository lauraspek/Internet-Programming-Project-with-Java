package NabootPackage;

public class Conversation {

	// Parametri
	private int chatID;
	private ConversationStatus conversationStatus;
	private int lastMessageID;
	private int newsBookmark, rssBookmark, commentBookmark;
	private String dateBookmark;

	// Costruttore
	public Conversation (int chatID) {
		this.chatID = chatID;
		this.conversationStatus = ConversationStatus.START;
		this.lastMessageID = -1;
		newsBookmark = -1;
		rssBookmark = -1;
		commentBookmark = -1;
		dateBookmark = "";
	}

	// Getters e Setters
	public int getChatID() {return this.chatID;}
	public ConversationStatus getConversationStatus() {return this.conversationStatus;}
	public int getLastMessageID() {return this.lastMessageID;}
	public int getNewsBookmark() {return this.newsBookmark;}
	public int getRssBookmark() {return this.rssBookmark;}
	public int getCommentBookmark() {return this.commentBookmark;}
	public String getDateBookmark() {return this.dateBookmark;}

	public void setConversationStatus(ConversationStatus conversationStatus) {
		this.conversationStatus = conversationStatus;
	}
	public void setLastMessageID(int ID) {
		this.lastMessageID = ID;
	}
	public void setNewsBookmark(int newsBookmark) {
		this.newsBookmark = newsBookmark;
	}
	public void setRssBookmark(int rssBookmark) {
		this.rssBookmark = rssBookmark;
	}
	public void setCommentBookmark(int commentBookmark) {
		this.commentBookmark = commentBookmark;
	}
	public void setDateBookmark(String dateBookmark) {
		this.dateBookmark = dateBookmark;
	}


	// Metodi


}