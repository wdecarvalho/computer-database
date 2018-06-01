package com.excilys.service;

import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.excilys.exception.company.CompanyNotFoundException;
import com.excilys.exception.computer.ComputerNotDeletedException;

@Service
@Transactional
public interface ServiceCdb<T> {

    int NB_PAGE = 10;

    /**
     * Recupere tout les objets de type T.
     * @return Collection<T>
     */
    @Transactional(readOnly = true)
    Collection<T> getAll();

    /**
     * Recupere par pagination les objets de type T.
     * @param page
     *            Numero de page courant
     * @return Pages<T>
     */
    @Transactional(readOnly = true)
    Page<T> findByPage(int... page);

    /**
     * Recupere le nombre de T en base de données.
     * @return Nombre en base de données
     */
    @Transactional(readOnly = true)
    Long getCountInDatabase();

    /**
     * Supprime un objet de type T.
     * @param id
     *            de l'objet a supprimer
     * @throws ComputerNotDeletedException
     *             ComputerNotDeletedException
     * @throws CompanyNotFoundException
     *             Si la companie n'existe pas
     */
    @Transactional(rollbackFor = { ComputerNotDeletedException.class, CompanyNotFoundException.class })
    void deleteOne(Long id) throws ComputerNotDeletedException, CompanyNotFoundException;

}
