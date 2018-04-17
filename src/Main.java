import java.util.logging.Logger;

import main.java.com.excilys.ui.ControleurCdb;

public class Main {

	private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
	
	public static void main(String... args) {
			new ControleurCdb().core(); 
	}
}
