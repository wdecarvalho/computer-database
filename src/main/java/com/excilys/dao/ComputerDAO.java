package com.excilys.dao;

import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.excilys.model.Computer;

@Repository
public interface ComputerDAO extends PagingAndSortingRepository<Computer, Long> {

    /**
     * Recherche un computer par son nom ou par le nom de la companie.
     * @param nameComputer
     *            Nom du computer
     * @param nameCompany
     *            Nom de la companie
     * @param pageable
     *            Pageable
     * @return Page<Computer>
     */
    Page<Computer> findByNameContainingOrCompanyNameContainingOrderByName(String nameComputer, String nameCompany,
            Pageable pageable);

    /**
     * Recupere le nombre de computer recupérée par la recherche.
     * @param nameComputer
     *            Nom du computer
     * @param nameCompany
     *            Nom de la companie
     * @return Nombre de computer
     */
    Long countByNameContainingOrCompanyNameContaining(String nameComputer, String nameCompany);

    /**
     * Supprime tout les computers par leurs ID.
     * @param computerstoDelete
     *            Iterable<Long>
     * @return Nobmre de computer supprimés
     */
    Long deleteByIdIn(Iterable<Long> computerstoDelete);

    /**
     * Supprime tous les computers qui ont cette companie.
     * @param companyID companie ID
     * @return Nombre de company supprimé
     */
    Long deleteByCompanyId(Long companyID);

    /**
     * Retourne une collection contenant tous les ordinateurs de la base de données.
     * @return Collection<Computer>
     */
    Collection<Computer> findAll();
}
