package main.java.com.excilys.dao;

import java.sql.Connection;

public abstract class Dao<T> {

	private Connection connection;
	
	
	public Dao(Connection conn) {
		this.connection = conn;
	}
	
	/**
	 * Stocke l'objet en base de donnée
	 * @param obj T
	 * @return true si ok sinon false
	 */
	public abstract boolean create(T obj);
	
	/**
	 * Supprime l'objet de la base de donnée
	 * @param obj T
	 * @return true si ok sinon false
	 */
	public abstract boolean delete(T obj);
	
	/**
	 * Met a jour l'objet en base de donnée
	 * @param obj T
	 * @return true si ok sinon false
	 */
	public abstract boolean update(T obj);
	
	/**
	 * Recupere un objet stockée en base de donnée
	 * @param id long
	 * @return objet T
	 */
	public abstract T find(Long id);

	public Connection getConnection() {
		return connection;
	}
	
	
}
