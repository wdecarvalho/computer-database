package com.excilys.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.excilys.dao.CompanyDao;
import com.excilys.exception.DaoNotInitializeException;
import com.excilys.model.Company;
import com.excilys.util.Pages;

@Service
public class ServiceCompany implements ServiceCdb<Company> {

    // private static final Logger LOGGER =
    // LoggerFactory.getLogger(ServiceCompany.class);

    @Autowired
    private CompanyDao companyDao;

    /**
     * Initialise le service.
     * @throws DaoNotInitializeException
     *             Si la DAO n'est pas initialisé
     */
    private ServiceCompany() {

    }

    /**
     * Verofoe l'existence d'une company en BD.
     * @param id
     *            ID de la company a verifier
     * @return true si elle existe
     */

    public boolean isExistCompany(final Long id) {
        return companyDao.find(id).isPresent();
    }

    @Override
    public Collection<Company> getAll() {
        return companyDao.findAll();
    }

    @Override
    public boolean deleteOne(final Long id) {
        return companyDao.delete(id);
    }

    @Override
    public Pages<Company> findByPage(final int... page) {
        return companyDao.findPerPage(page);
    }
}
