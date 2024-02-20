package NabootPackage;

public class NabootThread extends Thread {

	//Parametri
	private Naboot bot;
	
	//Costruttore
	public NabootThread (Naboot bot) {
		this.bot = bot;	
	}
	
	//Getter e setter
	public Naboot getBot() {return this.bot;}
	
	//Metodi
	@Override
	public void run() {
		while(true) {
			bot.handleUpdates();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}