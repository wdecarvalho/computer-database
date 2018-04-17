package main.java.com.excilys.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import main.java.com.excilys.exception.DaoNotInitializeException;

public class DaoFactory {
	
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
	 * La factory fourni la DAO demandé et si on lui demande une DAO qu'elle ne possede pas throw une exception
	 * @param type Type de la DAO demandé
	 * @return DAO requise
	 * @throws SQLException
	 * @throws DaoNotInitializeException Une DAO non géré a été demandé
	 */
	public Dao<?> getDao(DaoType type) throws SQLException, DaoNotInitializeException {

		switch(type) {
			case COMPUTER_DAO :
				return getComputerDao();
			case COMPANY_DAO :
				return getCompanyDao();
			default :
				throw new DaoNotInitializeException("");
		}
	}
	
	/**
	 * Crée une ComputerDao en initialisant sa connexion
	 * @throws SQLException La connexion n'a pas pu s'effectuer
	 * @return ComputerDao
	 */
	private ComputerDao getComputerDao() throws SQLException {
		final Connection conn = initConnexion();
		return new ComputerDao(conn);
	}
	
	/**
	 * Crée une CompanyDao en initialisation sa connexion
	 * @throws SQLException La connexion n'a pas pu s'effectuer
	 * @return CompanyDao
	 */
	private CompanyDao getCompanyDao() throws SQLException {
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
