package com.excilys.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.excilys.dao.ComputerDao;
import com.excilys.exception.ComputerException;
import com.excilys.exception.company.CompanyNotFoundException;
import com.excilys.exception.computer.ComputerNotDeletedException;
import com.excilys.exception.computer.ComputerNotFoundException;
import com.excilys.exception.computer.ComputerNotUpdatedException;
import com.excilys.exception.date.DateTruncationException;
import com.excilys.model.Computer;
import com.excilys.util.Pages;
import com.excilys.validation.ComputerValidation;

@Service
@Transactional
public class ServiceComputer implements ServiceCdb<Computer> {

    @Autowired
    private ComputerDao computerDao;

    @Autowired
    private ServiceCompany serviceCompany;

    /**
     * Constructeur de ServiceComputer [Spring].
     */
    private ServiceComputer() {
    }

    @Override
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public Computer getComputerDaoDetails(final Long id) throws ComputerNotFoundException {
        return computerDao.find(id).orElseThrow(() -> new ComputerNotFoundException("" + id));

    }

    /**
     * Demande a la DAO de crée un computer.
     * @param c
     *            Computer à sauvegarder
     * @param validation
     *            True si on a besoin d'appelé le validator javax
     * @return L'ID du computer crée ou -1L si a echoué
     * @throws ComputerException
     *             Si une regle propre au computer échoue
     * @throws CompanyNotFoundException
     *             Si la company n'existe pas
     * @throws DateTruncationException
     *             Lorsque une date invalide essaye de se stocker en BD
     */
    public Long createComputer(final Computer c, final boolean validation)
            throws ComputerException, CompanyNotFoundException, DateTruncationException {
        if (c.getCompany() != null && !serviceCompany.isExistCompany(c.getCompany().getId())) {
            throw new CompanyNotFoundException(c.getCompany().getId().toString());
        }
        if (validation) {
            ComputerValidation.validateComputerIntegrityAndDate(c);
        } else {
            ComputerValidation.dateIntroMinorThanDateDiscon(c.getIntroduced(), c.getDiscontinued());
        }
        return computerDao.create(c);
    }

    /**
     * /** Demande a la DAO de mettre a jour un computer.
     * @param c
     *            Computer à mettre a jour
     * @param validation
     *            True si on a besoin d'appelé le validator javax
     * @return Le computer qui a été mit a jour.
     * @throws ComputerException
     *             Si une regle propre au computer échoue.
     * @throws DateTruncationException
     *             Lorsque une date invalide essaye de se stocker en BD
     * @throws CompanyNotFoundException
     *             Si la companie n'existe pas
     */
    public Computer updateComputer(final Computer c, final boolean validation)
            throws ComputerException, DateTruncationException, CompanyNotFoundException {
        if (c.getCompany() != null && !serviceCompany.isExistCompany(c.getCompany().getId())) {
            throw new CompanyNotFoundException(c.getCompany().getId().toString());
        }
        if (validation) {
            ComputerValidation.validateComputerIntegrityAndVerifyPresenceId(c);
        } else {
            ComputerValidation.validateComputerAndVerifyPresenceId(c);
        }
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
     * @throws ComputerNotDeletedException
     *             Si un ou plusieurs ordinateurs n'arrivent pas a être supprimé
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean deleteComputer(final String ids) throws ComputerNotDeletedException {
        return computerDao.delete(ids);
    }

    @Override
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public Pages<Computer> findByPagesComputer(final String search, int... pageAndNumberResult) {
        return computerDao.findPerPage(search, pageAndNumberResult);
    }
}
