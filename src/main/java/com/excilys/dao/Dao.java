package main.java.com.excilys.dao;

import java.sql.Connection;
import java.util.Collection;
import java.util.Optional;

import main.java.com.excilys.util.Pages;

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
	public abstract Optional<T> find(Long id);
	
	/**
	 * Creer une page de resultat pour l'objet passé en parametre
	 * @param page Page courante
	 * @return Page de résultat
	 */
	public abstract Pages<T> findPerPage(int page);

	public Connection getConnection() {
		return connection;
	}
	
}
