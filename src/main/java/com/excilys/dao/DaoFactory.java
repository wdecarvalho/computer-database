package main.java.com.excilys.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public class DaoFactory {

	private static final Logger LOGGER = Logger.getLogger(DaoFactory.class.getName());
	
	private static DaoFactory factory;
	
	//fixme remove and put it to file properties
	private final String url = "jdbc:mysql://";
	private final String host = "localhost";
	private final String port = "3306";
	private final String database = "computer-database-db";
	private final String user = "admincdb";
	private final String password = "qwerty1234";
	
	private DaoFactory() {
		//default singleton
	}
	
	/**
	 * Crée une ComputerDao en initialisant sa connexion
	 * @throws SQLException La connexion n'a pas pu s'effectuer
	 * @return ComputerDao
	 */
	public ComputerDao getComputerDao() throws SQLException {
		final Connection conn = initConnexion();
		return new ComputerDao(conn);
	}
	
	/**
	 * Crée une CompanyDao en initialisation sa connexion
	 * @throws SQLException La connexion n'a pas pu s'effectuer
	 * @return CompanyDao
	 */
	public CompanyDao getCompanyDao() throws SQLException {
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
	 */
	private Connection initConnexion() throws SQLException {
		final StringBuilder sb = new StringBuilder(url);
		sb.append(host);
		sb.append(":");
		sb.append(port);
		sb.append("/");
		sb.append(database);
		final Connection conn = DriverManager.getConnection(sb.toString(), user, password);
		return conn;
	}
	
}
