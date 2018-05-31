package com.excilys.dao;

import java.util.Collection;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.excilys.model.Company;

@Repository
public interface CompanyDAO extends PagingAndSortingRepository<Company, Long> {

    /**
     * Retourne une collection contenant toutes les companies de la base de données.
     * @return Collection<Company>
     */
    Collection<Company> findAll();

}
