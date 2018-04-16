import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Logger;

import main.java.com.excilys.dao.ComputerDao;
import main.java.com.excilys.dao.DaoFactory;
import main.java.com.excilys.model.Computer;

public class Main {

	private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
	
	public static void main(String... args) {
		final DaoFactory dFactory = DaoFactory.getInstance();
		final ComputerDao computerDao;
		try {
			computerDao = dFactory.getComputerDao();
			LOGGER.info(computerDao.find(1L).getName());
			LOGGER.info(computerDao.find(2L).getName());
			computerDao.create(new Computer("name",new Timestamp(System.currentTimeMillis()),null,null));
			if(computerDao.find(900L) != null) {
				LOGGER.fine("error");
			}
		} catch (SQLException e) {
			LOGGER.severe(e.getMessage());
		}
	}

}
