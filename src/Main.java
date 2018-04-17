import java.sql.SQLException;
import java.time.LocalDate;
import java.util.logging.Logger;

import main.java.com.excilys.dao.ComputerDao;
import main.java.com.excilys.dao.DaoFactory;
import main.java.com.excilys.dao.DaoType;
import main.java.com.excilys.exception.ComputerNotFoundException;
import main.java.com.excilys.exception.DaoNotInitializeException;
import main.java.com.excilys.model.Computer;

public class Main {

	private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
	
	public static void main(String... args) {
		final DaoFactory dFactory = DaoFactory.getInstance();
		final ComputerDao computerDao;
		try {
			computerDao = (ComputerDao) dFactory.getDao(DaoType.COMPUTER_DAO);
			computerDao.find(1L).ifPresent(c -> LOGGER.info(c.toString()));
			computerDao.find(2L).ifPresent(c -> LOGGER.info(c.toString()));
			computerDao.create(new Computer("name",LocalDate.now(),null,null));
			try {
				computerDao.find(900L).orElseThrow(() -> new ComputerNotFoundException("900L")).getName();
		
			} catch (ComputerNotFoundException e) {
				LOGGER.severe(e.getMessage());
			}
			LOGGER.info(computerDao.findAll().size()+"");
		} catch (SQLException e) {
			LOGGER.severe(e.getMessage());
		} catch (DaoNotInitializeException e1) {
			LOGGER.severe(e1.getMessage());
		}
		
	}

}
