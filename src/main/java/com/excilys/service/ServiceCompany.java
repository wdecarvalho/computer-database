package com.excilys.service;

import java.util.Collection;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.excilys.dao.CompanyDao;
import com.excilys.model.Company;
import com.excilys.util.Pages;

@Service
@Transactional
public class ServiceCompany implements ServiceCdb<Company> {

    private final CompanyDao companyDao;

    /**
     * Initialise le service.
     * @param companyDao a injecter
     */
    private ServiceCompany(CompanyDao companyDao) {
        this.companyDao = companyDao;
    }

    /**
     * Verofoe l'existence d'une company en BD.
     * @param id
     *            ID de la company a verifier
     * @return true si elle existe
     */
    @Transactional(readOnly = true)
    public boolean isExistCompany(final Long id) {
        return companyDao.find(id).isPresent();
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Company> getAll() {
        return companyDao.findAll();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteOne(final Long id) {
        return companyDao.delete(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Pages<Company> findByPage(final int... page) {
        return companyDao.findPerPage(page);
    }
}
