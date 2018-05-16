package com.excilys.service;

import java.sql.SQLException;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excilys.dao.CompanyDao;
import com.excilys.dao.DaoFactory;
import com.excilys.dao.DaoType;
import com.excilys.exception.DaoNotInitializeException;
import com.excilys.model.Company;
import com.excilys.util.Pages;

public class ServiceCompany implements ServiceCdb<Company> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceCompany.class);

    private static ServiceCompany serviceCompany;

    private CompanyDao companyDao;

    /**
     * Initialise le service.
     * @throws DaoNotInitializeException
     *             Si la DAO n'est pas initialisé
     */
    private ServiceCompany() throws DaoNotInitializeException {
        try {
            final DaoFactory daoFactory = DaoFactory.getInstance();
            companyDao = (CompanyDao) daoFactory.getDao(DaoType.COMPANY_DAO);
        } catch (SQLException e) {
            LOGGER.debug(e.getMessage());
            throw new DaoNotInitializeException();

        } catch (DaoNotInitializeException e1) {
            LOGGER.error(e1.getMessage());
        }
    }

    /**
     * Permet de recuperer l'instance du service.
     * @return ServiceCompany ServiceCompany
     * @throws DaoNotInitializeException
     *             Si la DAO n'est pas initialisé
     */
    public static ServiceCompany getInstance() throws DaoNotInitializeException {
        if (serviceCompany == null) {
            serviceCompany = new ServiceCompany();
        }
        return serviceCompany;
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
