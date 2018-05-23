package com.excilys.dao;

import java.util.Collection;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.excilys.util.Pages;

@Repository
public abstract class Dao<T> {

    protected final DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    private void initJdbcTemplate() {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * Constructeur de Dao.
     * @param dataSource
     *            DataSourceHikari
     */
    Dao(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Retourne l'ensemble des objets stockée en base de donnée.
     * @return Collection<T>
     */
    public abstract Collection<T> findAll();

    /**
     * Recupere un objet stockée en base de donnée.
     * @param id
     *            long
     * @return objet T
     */
    public abstract Optional<T> find(Long id);

    /**
     * Creer une page de resultat pour l'objet passé en parametre.
     * @param pageAndNumberResult
     *            Page courante et nombre de resultat si besoin
     * @return Page de résultat
     */
    public abstract Pages<T> findPerPage(int... pageAndNumberResult);

    /**
     * Supprime un objet stockée en base de données par son ID.
     * @param id
     *            ID de l'objet a supprimer
     * @return True si la suppresion réussie
     */
    public abstract boolean delete(Long id);

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
}
