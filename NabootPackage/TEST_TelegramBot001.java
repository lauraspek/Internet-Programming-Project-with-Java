package NabootPackage;

import java.sql.SQLException;

public class TEST_TelegramBot001{

	public static void main(String[] args) throws InterruptedException, ClassNotFoundException, SQLException{

		Naboot bot = new Naboot("5444520458:AAHSSxCaQoRMXISV04tB5h6KtzCTl44sfcQ");

		System.out.println(bot.getMe());

		// DEBUG cancella tutti gli updates arrivati prima che si accendesse il bot
		// Sono tutti prevalentemente test per verificare che il processo del bot sia stato killato a dovere
		bot.getUpdates(true);

		while(true){
			System.out.println(bot.getUpdates(false));
			Thread.sleep(50);
			bot.handleUpdates();
			Thread.sleep(50);
		}

	}

}