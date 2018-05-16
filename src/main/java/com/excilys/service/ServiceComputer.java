package com.excilys.service;

import java.sql.SQLException;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excilys.dao.ComputerDao;
import com.excilys.dao.DaoFactory;
import com.excilys.dao.DaoType;
import com.excilys.exception.CompanyNotFoundException;
import com.excilys.exception.ComputerException;
import com.excilys.exception.ComputerNotFoundException;
import com.excilys.exception.ComputerNotUpdatedException;
import com.excilys.exception.DaoNotInitializeException;
import com.excilys.exception.DateTruncationException;
import com.excilys.model.Computer;
import com.excilys.util.Pages;
import com.excilys.validation.ComputerValidation;

public class ServiceComputer implements ServiceCdb<Computer> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceComputer.class);

    private static ServiceComputer serviceComputer;

    private ComputerDao computerDao;

    private static ServiceCompany serviceCompany;

    /**
     * Initialise le service.
     * @throws DaoNotInitializeException
     *             Si la DAO n'est pas initialisé
     */
    private ServiceComputer() throws DaoNotInitializeException {
        try {
            final DaoFactory daoFactory = DaoFactory.getInstance();
            serviceCompany = ServiceCompany.getInstance();
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
     * @return ServiceComputer
     * @throws DaoNotInitializeException
     *             Si la DAO n'est pas initialisé
     */
    public static ServiceComputer getInstance() throws DaoNotInitializeException {
        if (serviceComputer == null) {
            serviceComputer = new ServiceComputer();
        }
        return serviceComputer;
    }

    @Override
    public Collection<Computer> getAll() {
        return computerDao.findAll();
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
     * @throws DateTruncationException
     *             Lorsque une date invalide essaye de se stocker en BD
     */
    public Long createComputer(final Computer c)
            throws ComputerException, CompanyNotFoundException, DateTruncationException {
        if (c.getCompany() != null && !serviceCompany.isExistCompany(c.getCompany().getId())) {
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
     * @throws DateTruncationException
     *             Lorsque une date invalide essaye de se stocker en BD
     * @throws CompanyNotFoundException
     *             Si la companie n'existe pas
     */
    public Computer updateComputer(final Computer c)
            throws ComputerException, DateTruncationException, CompanyNotFoundException {
        if (c.getCompany() != null && !serviceCompany.isExistCompany(c.getCompany().getId())) {
            throw new CompanyNotFoundException(c.getCompany().getId().toString());
        }
        ComputerValidation.idIsRequiredForComputerUpdate(c.getId());
        ComputerValidation.nameIsRequiredForComputer(c.getName());
        ComputerValidation.dateIntroMinorThanDateDiscon(c.getIntroduced(), c.getDiscontinued());
        return computerDao.update(c).orElseThrow(() -> new ComputerNotUpdatedException(c.getId().toString()));
    }

    @Override
    public boolean deleteOne(final Long id) {
        return computerDao.delete(id);
    }

    /**
     * Demande a la DAO de supprimer une liste de computer.
     * @param ids
     *            ID des computers à supprimer
     * @return True si réussi
     */
    public boolean deleteComputer(final String ids) {
        return computerDao.delete(ids);
    }

    @Override
    public Pages<Computer> findByPage(int... pageAndNumberResult) {
        return computerDao.findPerPage(pageAndNumberResult);
    }

    /**
     * Retourne les computer par pages et par recherche.
     * @param search
     *            Computer ou company name a recherché
     * @param pageAndNumberResult
     *            Numero de page courante et nombre de resultat a afficher
     * @return Page de computer
     */
    public Pages<Computer> findByPagesComputer(final String search, int... pageAndNumberResult) {
        return computerDao.findPerPage(search, pageAndNumberResult);
    }
}
