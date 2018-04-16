package main.java.com.excilys.dao;

import java.sql.Connection;
import java.util.Collection;

public abstract class Dao<T> {

	private Connection connection;
	
	
	public Dao(Connection conn) {
		this.connection = conn;
	}
	
	/**
	 * Retourne l'ensemble des objets stockée en base de donnée
	 * @return Collection<T>
	 */
	public abstract Collection<T> findAll();
	
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
