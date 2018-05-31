package com.excilys.service.company;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.excilys.model.Company;
import com.excilys.service.ServiceCdb;

@Service
@Transactional
public interface ServiceCdbCompany extends ServiceCdb<Company> {

    /**
     * Verifie que la companie existe bien.
     * @param id ID de la companie a tester
     * @return True si il existe
     */
    @Transactional(readOnly = true)
    boolean isExists(Long id);

}
