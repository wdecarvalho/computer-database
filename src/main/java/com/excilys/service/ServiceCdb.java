package com.excilys.service;

import java.sql.SQLException;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excilys.dao.CompanyDao;
import com.excilys.dao.ComputerDao;
import com.excilys.dao.DaoFactory;
import com.excilys.dao.DaoType;
import com.excilys.exception.CompanyNotFoundException;
import com.excilys.exception.ComputerException;
import com.excilys.exception.ComputerNotFoundException;
import com.excilys.exception.ComputerNotUpdatedException;
import com.excilys.exception.DaoNotInitializeException;
import com.excilys.model.Company;
import com.excilys.model.Computer;
import com.excilys.util.Pages;
import com.excilys.validation.ComputerValidation;

public class ServiceCdb {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceCdb.class);

    private static ServiceCdb serviceCdb;

    private CompanyDao companyDao;

    private ComputerDao computerDao;

    /**
     * Initialise le service.
     * @throws DaoNotInitializeException
     *             Si la DAO n'est pas initialisé
     */
    private ServiceCdb() throws DaoNotInitializeException {
        try {
            final DaoFactory daoFactory = DaoFactory.getInstance();
            companyDao = (CompanyDao) daoFactory.getDao(DaoType.COMPANY_DAO);
            computerDao = (ComputerDao) daoFactory.getDao(DaoType.COMPUTER_DAO);
        } catch (SQLException e) {
            LOGGER.debug(e.getMessage());
            throw new DaoNotInitializeException();

        } catch (DaoNotInitializeException e1) {
            LOGGER.error(e1.getMessage());
        }

    }

    /**
     * Permet de recuperer l'instance du service.
     * @return ServiceCdb
     * @throws DaoNotInitializeException
     *             Si la DAO n'est pas initialisé
     */
    public static ServiceCdb getInstance() throws DaoNotInitializeException {
        if (serviceCdb == null) {
            serviceCdb = new ServiceCdb();
        }
        return serviceCdb;
    }

    /**
     * Recupere la liste des computers.
     * @return Collection de computer
     */
    public Collection<Computer> getListComputers() {
        return computerDao.findAll();
    }

    /**
     * Recupere la liste des compagnies.
     * @return Colleciton de company
     */
    public Collection<Company> getListCompanies() {
        return companyDao.findAll();
    }

    /**
     * Recupere les information d'un computer.
     * @param id
     *            ID du computer recherché
     * @return Computer
     * @throws ComputerNotFoundException
     *             Si le computer n'est pas présent
     */
    public Computer getComputerDaoDetails(final Long id) throws ComputerNotFoundException {
        return computerDao.find(id).orElseThrow(() -> new ComputerNotFoundException("" + id));

    }

    /**
     * Demande a la DAO de crée un computer.
     * @param c
     *            Computer à sauvegarder
     * @return L'ID du computer crée ou -1L si a echoué
     * @throws ComputerException
     *             Si une regle propre au computer échoue
     * @throws CompanyNotFoundException
     *             Si la company n'existe pas
     */
    public Long createComputer(final Computer c) throws ComputerException, CompanyNotFoundException {
        if (c.getCompany() != null && !isExistCompany(c.getCompany().getId())) {
            throw new CompanyNotFoundException(c.getCompany().getId().toString());
        }
        ComputerValidation.nameIsRequiredForComputer(c.getName());
        ComputerValidation.dateIntroMinorThanDateDiscon(c.getIntroduced(), c.getDiscontinued());
        return computerDao.create(c);

    }

    /**
     * /** Demande a la DAO de mettre a jour un computer.
     * @param c
     *            Computer à mettre a jour
     * @return Le computer qui a été mit a jour.
     * @throws ComputerException
     *             Si une regle propre au computer échoue.
     */
    public Computer updateComputer(final Computer c) throws ComputerException {
        ComputerValidation.idIsRequiredForComputerUpdate(c.getId());
        ComputerValidation.nameIsRequiredForComputer(c.getName());
        ComputerValidation.dateIntroMinorThanDateDiscon(c.getIntroduced(), c.getDiscontinued());
        return computerDao.update(c).orElseThrow(() -> new ComputerNotUpdatedException(c.getId().toString()));
    }

    /**
     * Demande a la DAO de supprimer un computer.
     * @param id
     *            ID du Computer à supprimer
     * @return True si réussi
     */
    public boolean deleteComputer(final Long id) {
        return computerDao.delete(id);
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

    /**
     * Retourne les computer par pages.
     * @param pageAndNumberResult
     *            Numero de page courante et nombre de resultat a afficher
     * @return Page de computer
     */
    public Pages<Computer> findByPagesComputer(int... pageAndNumberResult) {
        if (pageAndNumberResult.length == 1) {
            return computerDao.findPerPage(pageAndNumberResult[0]);
        } else {
            return computerDao.findPerPage(pageAndNumberResult[0], pageAndNumberResult[1]);
        }
    }

    /**
     * Retourne les computers par pages.
     * @param page
     *            Page de resultat
     * @return Page de company
     */
    public Pages<Company> findByPagesCompany(int page) {
        return companyDao.findPerPage(page);
    }

}
