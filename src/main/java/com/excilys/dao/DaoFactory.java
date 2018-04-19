package main.java.com.excilys.dao;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import main.java.com.excilys.exception.DaoNotInitializeException;

public class DaoFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(DaoFactory.class);
	
	private static DaoFactory factory;

	private DaoFactory() {
		//default singleton
	}

	/**
	 * La factory fourni la DAO demandé et si on lui demande une DAO qu'elle ne possede pas throw une exception
	 * @param type Type de la DAO demandé
	 * @return DAO requise
	 * @throws SQLException
	 * @throws DaoNotInitializeException Une DAO non géré a été demandé
	 */
	public static Dao<?> getDao(DaoType type) throws SQLException, DaoNotInitializeException {

		switch(type) {
		case COMPUTER_DAO :
			return getComputerDao();
		case COMPANY_DAO :
			return getCompanyDao();
		default :
			throw new DaoNotInitializeException();
		}
	}

	/**
	 * Crée une ComputerDao en initialisant sa connexion
	 * @throws SQLException La connexion n'a pas pu s'effectuer
	 * @return ComputerDao
	 */
	private static ComputerDao getComputerDao() throws SQLException {
		final Connection conn = initConnexion();
		return new ComputerDao(conn);
	}

	/**
	 * Crée une CompanyDao en initialisation sa connexion
	 * @throws SQLException La connexion n'a pas pu s'effectuer
	 * @return CompanyDao
	 */
	private static CompanyDao getCompanyDao() throws SQLException {
		final Connection conn = initConnexion();
		return new CompanyDao(conn);
	}

	/**
	 * Retourne la factory et la crée si elle n'existe pas
	 * @return
	 */
	public static DaoFactory getInstance() {
		if(factory == null) {
			factory = new DaoFactory();
		}
		return factory;

	}

	/**
	 * Initiliase la connexion a la base de donnée
	 * @return Connection
	 * @throws SQLException La connexion n'a pas pu s'effectuer
	 * @throws IOException 
	 */
	private static Connection initConnexion() throws SQLException {
		final Properties aProperties = new Properties();
		final InputStream path = ClassLoader.getSystemResourceAsStream("main/resources/app.properties");
		try {
			aProperties.load(path);
		} catch (FileNotFoundException e) {
			LOGGER.error(e.getMessage());
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}
		final Connection conn = DriverManager.getConnection(
				aProperties.getProperty("url"),
				aProperties.getProperty("user"),
				aProperties.getProperty("password"));
		return conn;

	}
}
