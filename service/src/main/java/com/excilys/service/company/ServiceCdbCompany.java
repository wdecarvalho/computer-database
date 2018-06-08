package com.excilys.service.company;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.excilys.exception.company.CompanyNotFoundException;
import com.excilys.model.Company;
import com.excilys.service.ServiceCdb;

@Service
@Transactional
public interface ServiceCdbCompany extends ServiceCdb<Company> {

    /**
     * Verifie que la companie existe bien.
     * @param id
     *            ID de la companie a tester
     * @return True si il existe
     */
    @Transactional(readOnly = true)
    boolean isExists(Long id);

    /**
     * Recupere le nom d'une companie par son ID .
     * @param id
     *            ID de la companie
     * @return Nom de la companie
     * @throws CompanyNotFoundException
     *             Si la companie n'existe pas
     */
    @Transactional(readOnly = true)
    String getCompanyNameById(Long id) throws CompanyNotFoundException;
    
    /**
     * Reciêre une company par son ID
     * @param id ID de la company recherché
     * @return Company
     * @throws CompanyNotFoundException 
     */
    @Transactional(readOnly = true)
    Company findOneById(Long id) throws CompanyNotFoundException;

}
