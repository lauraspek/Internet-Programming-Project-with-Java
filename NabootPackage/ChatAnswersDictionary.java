package NabootPackage;

import java.util.HashMap;
import org.json.*;

public class ChatAnswersDictionary {

	// Parametri
	private HashMap<String, String> answers;

	// Costruttore
	public ChatAnswersDictionary() {
		this.answers = new HashMap<String, String>();

		this.answers.put("insertusername", "You are not registered, insert your username");
		this.answers.put("registrationrequested", "A registration request was submitted to the admins. Come back later.");
		this.answers.put("registrationsolicited", "Your registration request is not approved yet. Come back later.");

		this.answers.put("startmenu", "What do you want to do now with the Naboo bot?");
		this.answers.put("startmenu_callback",new JSONStringer()
				.object()
				.key("inline_keyboard").array()
				.array() //riga 1
				.object() //bottone "News of the day"
				.key("text").value("Get the news of the day")
				.key("callback_data").value("newsoftheday")
				.endObject()
				.endArray()
				.array() //riga 2
				.object() //bottone "Feed filter"
				.key("text").value("Feed Filter")
				.key("callback_data").value("filterfeed")
				.endObject()
				.object() //bottone "Date filter"
				.key("text").value("Date Filter")
				.key("callback_data").value("filterdate")
				.endObject()
				.endArray()
				.array() //riga 3
				.object() //bottone "Suggest Feed"
				.key("text").value("Suggest Feed")
				.key("callback_data").value("suggestfeed")
				.endObject()
				.endArray()
				.array() //riga 4
				.object() //bottone "Options"
				.key("text").value("Options")
				.key("callback_data").value("options")
				.endObject()
				.endArray()
				.endArray()
				.endObject().toString());

		this.answers.put("news_callback", new JSONStringer()
				.object()
				.key("inline_keyboard").array()
				.array() //riga 1
				.object() //bottone "Vote"
				.key("text").value("Vote")
				.key("callback_data").value("votenews")
				.endObject()
				.object() //bottone "Comment"
				.key("text").value("View comments")
				.key("callback_data").value("viewcommentsnews")
				.endObject()
				.endArray()
				.array() //riga 2
				.object() //bottone "Previous"
				.key("text").value("<<")
				.key("callback_data").value("previousnews")
				.endObject()
				.object() //bottone "Next"
				.key("text").value(">>")
				.key("callback_data").value("nextnews")
				.endObject()
				.endArray()
				.array() //riga 3
				.object() //bottone "Signal news as inappropriate"
				.key("text").value("Signal news as inappropriate")
				.key("callback_data").value("signalnews")
				.endObject()
				.endArray()
				.array() //riga 4
				.object() //bottone 1 "Back"
				.key("text").value("Back")
				.key("callback_data").value("backnews")
				.endObject()
				.endArray()
				.endArray()
				.endObject().toString());

		this.answers.put("newsoftheday_callback", new JSONStringer()
				.object()
				.key("inline_keyboard").array()
				.array() //riga 1
				.object() //bottone "Vote"
				.key("text").value("Vote")
				.key("callback_data").value("votenews")
				.endObject()
				.object() //bottone "Comment"
				.key("text").value("View comments")
				.key("callback_data").value("viewcommentsnews")
				.endObject()
				.endArray()
				.array() //riga 2
				.object() //bottone "Signal news as inappropriate"
				.key("text").value("Signal news as inappropriate")
				.key("callback_data").value("signalnews")
				.endObject()
				.endArray()
				.array() //riga 3
				.object() //bottone 1 "Back"
				.key("text").value("Back")
				.key("callback_data").value("backnews")
				.endObject()
				.endArray()
				.endArray()
				.endObject().toString());

		this.answers.put("comments_callback", new JSONStringer()
				.object()
				.key("inline_keyboard").array()
				.array() //riga 1
				.object() //bottone "Add comment"
				.key("text").value("Add comment")
				.key("callback_data").value("addcomment")
				.endObject()
				.endArray()
				.array() //riga 2
				.object() //bottone "Signal comment as inappropriate"
				.key("text").value("Signal comment as inappropriate")
				.key("callback_data").value("signalcomment")
				.endObject()
				.endArray()
				.array() //riga 3
				.object() //bottone "Back"
				.key("text").value("Back")
				.key("callback_data").value("backcomment")
				.endObject()
				.endArray()
				.endArray()
				.endObject().toString());

		this.answers.put("choosefeed", "Choose what feed to read news about:");

		this.answers.put("writedate", "Write the date to be searched in a yyyy-mm-dd format:");
		this.answers.put("nonewsfordate", "There are no news for this date, going back to the main menu");

		this.answers.put("suggestfeed", "Write down the feed URL:");

		this.answers.put("votenews", "Insert a vote between 1 and 10:");
		this.answers.put("votenewssaved", "Thanks for voting, going back to the main menu");

		this.answers.put("signalnewssaved", "Thanks for signaling this news, it will not be showed again unless administrators revoke the signaling, going back to the main menu");

		this.answers.put("options", "Options menu:");
		this.answers.put("options_callback", new JSONStringer()
				.object()
				.key("inline_keyboard").array()
				.array() //riga 1
				.object() //bottone "Delete User"
				.key("text").value("Delete User")
				.key("callback_data").value("deleteuser")
				.endObject()
				.endArray()
				.endArray()
				.endObject().toString());

		this.answers.put("userdeleted", "User deleted, please restart the bot to register again");

		this.answers.put("addcomment", "Write your comment:");

		this.answers.put("choosecommenttosignal", "Insert the ID of the comment you want to signal:");
		this.answers.put("commentsignaled", "Thanks for signaling this comment, it will not be showed again unless administrators revoke the signaling, going back to the main menu");

		this.answers.put("nocommand", "Command not recognised");
		this.answers.put("malformedURL", "URL not formatted correctly");
	}

	// Getter e Setter
	public String getChatAnswer(String key) {return this.answers.get(key);}

	// Metodi

}