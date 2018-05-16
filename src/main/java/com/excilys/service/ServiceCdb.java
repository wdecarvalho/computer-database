package com.excilys.service;

import java.util.Collection;

import com.excilys.util.Pages;

public interface ServiceCdb<T> {

    /**
     * Recupere tout les objets de type T.
     * @return Collection<T>
     */
    Collection<T> getAll();

    /**
     * Supprime un objet de type T.
     * @param id de l'objet a supprimer
     * @return True si réussi / False sinon
     */
    boolean deleteOne(Long id);

    /**
     * Recupere par pagination les objets de type T.
     * @param page Numero de page courant
     * @return Pages<T>
     */
    Pages<T> findByPage(int... page);
}
