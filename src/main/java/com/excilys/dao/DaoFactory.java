package main.java.com.excilys.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.omg.CORBA.PRIVATE_MEMBER;

import sun.security.jca.GetInstance;

public class DaoFactory {

	private DaoFactory factory;
	
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
	 * @return ComputerDao
	 */
	public ComputerDao getComputerDao() {
		Connection conn = null;
		try {
			conn = initConnexion();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ComputerDao(conn);
	}

	
	
	/**
	 * Crée une CompanyDao en initialisation sa connexion
	 * @return CompanyDao
	 */
	public CompanyDao getCompanyDao() {
		Connection conn = null;
		try {
			conn = initConnexion();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new CompanyDao(null);
	}
	
	/**
	 * Retourne la factory et la crée si elle n'existe pas
	 * @return
	 */
	public DaoFactory getInstance() {
		if(factory == null) {
			factory = new DaoFactory();
		}
		return factory;
		
	}
	
	private Connection initConnexion() throws SQLException {
		final StringBuilder sb = new StringBuilder(url);
		sb.append(host);
		sb.append(":");
		sb.append(port);
		sb.append("/");
		sb.append(database);
		Connection conn = DriverManager.getConnection(sb.toString(), user, password);
		return conn;
	}
	
}
