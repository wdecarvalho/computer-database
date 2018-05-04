package com.excilys.dao;

import java.util.Collection;
import java.util.Optional;

import com.excilys.util.Pages;

public abstract class Dao<T> {

    /**
     * Constructeur de Dao.
    */
    public Dao() {
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

}
